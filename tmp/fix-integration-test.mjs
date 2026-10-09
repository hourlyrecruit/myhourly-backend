import { readFileSync, writeFileSync } from 'fs';
const f = 'src/test/java/com/my_hourly/leave/LeaveMonthlyUsageIntegrationTest.java';
let src = readFileSync(f, 'utf8');

// 1) add import for PersistenceContext after the SpringBootTest import line
src = src.replace(
  'import org.springframework.boot.test.context.SpringBootTest;\n',
  'import org.springframework.boot.test.context.SpringBootTest;\nimport jakarta.persistence.PersistenceContext;\n'
);

// 2) add field after the last @Autowired field (JobTitleRepository jobTitleRepository;)
src = src.replace(
  '    @Autowired\n    private JobTitleRepository jobTitleRepository;\n',
  '    @Autowired\n    private JobTitleRepository jobTitleRepository;\n\n    @PersistenceContext\n    private javax.persistence.EntityManager entityManager;\n'
);

// 3) replace the failing test method body to call entityManager.clear() after delete
const oldMethod = `    void allocationRowsSurviveOnlyWhileTheirRequestExists() {

        LeaveRequest cross = approvedRequest(
                LocalDate.of(2026, 10, 29), LocalDate.of(2026, 11, 3), 4, 4);
        allocate(cross, OCTOBER, 2, 2);
        allocate(cross, NOVEMBER, 2, 2);

        Long requestId = cross.getId();
        leaveRequestRepository.delete(cross);

        List<LeaveRequestMonthAllocation> orphans = allocationRepository.findAll()
                .stream()
                .filter(row -> row.getLeaveRequest().getId().equals(requestId))
                .toList();

        assertTrue(orphans.isEmpty(),
                "Allocation rows must be removed with their request (ON DELETE CASCADE), "
                        + "so a hard-deleted leave type cannot leave orphans behind");
    }`;

const newMethod = `    void allocationRowsSurviveOnlyWhileTheirRequestExists() {

        LeaveRequest cross = approvedRequest(
                LocalDate.of(2026, 10, 29), LocalDate.of(2026, 11, 3), 4, 4);
        allocate(cross, OCTOBER, 2, 2);
        allocate(cross, NOVEMBER, 2, 2);

        Long requestId = cross.getId();
        leaveRequestRepository.delete(cross);
        entityManager.clear();

        List<LeaveRequestMonthAllocation> orphans = allocationRepository.findAll()
                .stream()
                .filter(row -> row.getLeaveRequest().getId().equals(requestId))
                .toList();

        assertTrue(orphans.isEmpty(),
                "Allocation rows must be removed with their request (ON DELETE CASCADE), "
                        + "so a hard-deleted leave type cannot leave orphans behind");
    }`;

if (!src.includes(oldMethod)) {
  console.error('OLD METHOD NOT FOUND');
  process.exit(2);
}
src = src.replace(oldMethod, newMethod);
writeFileSync(f, src, 'utf8');
console.log('patched OK');
