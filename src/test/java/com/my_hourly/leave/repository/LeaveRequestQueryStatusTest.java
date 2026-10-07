package com.my_hourly.leave.repository;

import com.my_hourly.leave.enums.LeaveStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Keeps three definitions of the leave status lifecycle from drifting apart:
 *
 * <ol>
 *   <li>the {@link LeaveStatus} enum,</li>
 *   <li>the status literals hardcoded inside {@link LeaveRequestRepository} JPQL,</li>
 *   <li>the {@code leave_requests.status} check constraint in the Flyway baseline.</li>
 * </ol>
 *
 * <p>This exists because those three did drift: the queries filtered on
 * {@code 'HR_APPROVED'}, a two-stage approval state that had been deleted from
 * the enum and was rejected by the check constraint. The queries therefore
 * always matched nothing, and the month-end scheduler expired unused guideline
 * days for employees who had already used their approved leave.</p>
 *
 * <p>These are pure string/schema assertions: they need no database, so they
 * also work in CI without a PostgreSQL instance.</p>
 */
@DisplayName("Leave status consistency")
class LeaveRequestQueryStatusTest {

    private static final String BASELINE_MIGRATION = "db/migration/V1__baseline_schema.sql";

    private static final Pattern STATUS_EQUALS =
            Pattern.compile("status\\s*=\\s*'([A-Za-z_]+)'", Pattern.CASE_INSENSITIVE);

    private static final Pattern STATUS_IN =
            Pattern.compile("status\\s+in\\s*\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE);

    @Test
    @DisplayName("Every status filtered by LeaveRequestRepository JPQL is a LeaveStatus value")
    void jpqlStatusLiteralsAreDeclaredLeaveStatusValues() {

        Set<String> declared = declaredStatusNames();
        List<String> checked = new ArrayList<>();
        List<String> offenders = new ArrayList<>();

        for (Method method : LeaveRequestRepository.class.getDeclaredMethods()) {

            Query query = method.getAnnotation(Query.class);
            if (query == null) {
                continue;
            }

            // @Query(value = "...") - joined in case the query was split across lines.
            String jpql = String.join(" ", query.value());

            Matcher equals = STATUS_EQUALS.matcher(jpql);
            while (equals.find()) {
                collect(equals.group(1), method.getName(), declared, checked, offenders);
            }

            Matcher in = STATUS_IN.matcher(jpql);
            while (in.find()) {
                for (String value : in.group(1).split(",")) {
                    collect(value, method.getName(), declared, checked, offenders);
                }
            }
        }

        assertTrue(offenders.isEmpty(),
                "LeaveRequestRepository filters on status value(s) that LeaveStatus does not declare "
                        + offenders + ". Declared values: " + declared
                        + ". A status the enum cannot produce matches no rows, so the query "
                        + "silently returns nothing.");

        // Fail loudly if the scan stops finding status literals, so that this
        // test can never quietly become a no-op.
        assertTrue(checked.contains(LeaveStatus.APPROVED.name()),
                "Expected the JPQL scan to find an '" + LeaveStatus.APPROVED
                        + "' status filter, but it found " + checked
                        + ". If the query was rewritten, update this scan.");
    }

    @Test
    @DisplayName("The leave_requests check constraint accepts exactly the LeaveStatus values")
    void checkConstraintMatchesTheEnum() {

        String block = leaveRequestsTableBlock();

        Matcher check = STATUS_IN.matcher(block);
        assertTrue(check.find(),
                "Could not find the status check constraint inside the leave_requests "
                        + "definition in " + BASELINE_MIGRATION);

        Set<String> storable = Arrays.stream(check.group(1).split(","))
                .map(value -> value.trim().replace("'", ""))
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toSet());

        assertEquals(declaredStatusNames(), storable,
                "The leave_requests.status check constraint and the LeaveStatus enum have drifted "
                        + "apart. Every status the code can write must be storable, and every storable "
                        + "status must be a real enum value - otherwise queries filter on states that "
                        + "can never exist.");
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static void collect(String rawValue,
                               String methodName,
                               Set<String> declared,
                               List<String> checked,
                               List<String> offenders) {

        String value = rawValue.trim().replace("'", "");

        // ':status' style bind parameters are typed by the method signature.
        if (value.isEmpty() || value.startsWith(":")) {
            return;
        }

        checked.add(value);

        if (!declared.contains(value)) {
            offenders.add(methodName + " -> '" + value + "'");
        }
    }

    private static Set<String> declaredStatusNames() {

        return Arrays.stream(LeaveStatus.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    /**
     * Extracts the {@code create table leave_requests (...)} body from the
     * baseline migration.
     */
    private static String leaveRequestsTableBlock() {

        String sql = readClasspathResource(BASELINE_MIGRATION);

        Matcher table = Pattern.compile(
                        "create table leave_requests\\s*\\((.*?)\\);",
                        Pattern.DOTALL | Pattern.CASE_INSENSITIVE)
                .matcher(sql);

        assertTrue(table.find(),
                "Could not find the leave_requests table definition in " + BASELINE_MIGRATION);

        return table.group(1);
    }

    private static String readClasspathResource(String resource) {

        try (InputStream in = LeaveRequestQueryStatusTest.class
                .getClassLoader()
                .getResourceAsStream(resource)) {

            assertNotNull(in, resource + " is not on the test classpath");
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + resource, e);
        }
    }
}
