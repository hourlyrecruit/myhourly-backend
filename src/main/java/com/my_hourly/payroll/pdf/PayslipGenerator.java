package com.my_hourly.payroll.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.my_hourly.payroll.entity.Payroll;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Generates a professionally formatted payslip PDF for an employee.
 *
 * <p>The generated payslip contains:
 * <ol>
 *     <li>Company header and pay period</li>
 *     <li>Payroll metadata</li>
 *     <li>Employee information</li>
 *     <li>Payment and bank information</li>
 *     <li>Attendance summary</li>
 *     <li>Earnings and deductions</li>
 *     <li>Salary summary</li>
 *     <li>System-generated footer</li>
 * </ol>
 */
public class PayslipGenerator {

    // -------------------------------------------------------------------------
    // Color Palette
    // -------------------------------------------------------------------------

    private static final Color BRAND_DARK = new Color(31, 41, 64);
    private static final Color BRAND_ACCENT = new Color(59, 130, 246);
    private static final Color HEADER_BACKGROUND = new Color(238, 242, 255);
    private static final Color ALTERNATE_ROW_BACKGROUND = new Color(248, 250, 252);
    private static final Color TOTAL_BACKGROUND = new Color(224, 231, 255);
    private static final Color TEXT_GRAY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(203, 213, 225);
    private static final Color CELL_BORDER_COLOR = new Color(226, 232, 240);
    private static final Color WHITE = Color.WHITE;

    // -------------------------------------------------------------------------
    // Fonts
    // -------------------------------------------------------------------------

    private static Font headingFont(float size) {
        return FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                size,
                BRAND_DARK
        );
    }

    private static Font sectionHeadingFont(float size) {
        return FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                size,
                BRAND_ACCENT
        );
    }

    private static Font boldFont(float size) {
        return FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                size,
                BRAND_DARK
        );
    }

    private static Font normalFont(float size) {
        return FontFactory.getFont(
                FontFactory.HELVETICA,
                size,
                BRAND_DARK
        );
    }

    private static Font mutedFont(float size) {
        return FontFactory.getFont(
                FontFactory.HELVETICA,
                size,
                TEXT_GRAY
        );
    }

    private static Font whiteBoldFont(float size) {
        return FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                size,
                WHITE
        );
    }

    // -------------------------------------------------------------------------
    // Date Formatting
    // -------------------------------------------------------------------------

    private static final DateTimeFormatter PAY_PERIOD_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM yyyy");

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Generates a payslip PDF for the supplied payroll record.
     *
     * @param payroll payroll information used to generate the payslip
     * @return generated PDF as a byte array
     * @throws RuntimeException if PDF generation fails
     */
    public static byte[] generate(Payroll payroll) {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Document document = new Document(
                    PageSize.A4,
                    40,
                    40,
                    36,
                    36
            );

            PdfWriter writer = PdfWriter.getInstance(
                    document,
                    outputStream
            );

            document.open();

            addHeader(document, payroll);
            addPayrollMetadata(document, payroll);
            addSectionDivider(document);

            addEmployeeAndPaymentDetails(document, payroll);
            addSectionDivider(document);

            addAttendanceSummary(document, payroll);
            addEarningsAndDeductions(document, payroll);
            addSalarySummary(document, payroll);

            addFooter(document);

            document.close();

            return outputStream.toByteArray();

        } catch (Exception exception) {
            throw new RuntimeException(
                    "Failed to generate payslip PDF",
                    exception
            );
        }
    }

    // -------------------------------------------------------------------------
    // Header
    // -------------------------------------------------------------------------

    private static void addHeader(
            Document document,
            Payroll payroll
    ) throws DocumentException {

        PdfPTable banner = new PdfPTable(1);
        banner.setWidthPercentage(100);
        banner.setSpacingAfter(4);

        PdfPCell companyCell = new PdfPCell();
        companyCell.setBackgroundColor(BRAND_DARK);
        companyCell.setPadding(14);
        companyCell.setBorder(Rectangle.NO_BORDER);

        Paragraph companyName = new Paragraph(
                "MyHourly",
                whiteBoldFont(18)
        );
        companyName.setAlignment(Element.ALIGN_LEFT);

        companyCell.addElement(companyName);

        Paragraph payslipTitle = new Paragraph(
                "Payslip Statement",
                mutedFont(10)
        );
        payslipTitle.setAlignment(Element.ALIGN_LEFT);

        companyCell.addElement(payslipTitle);

        banner.addCell(companyCell);
        document.add(banner);

        Paragraph payPeriod = new Paragraph(
                "Pay Period:  " +
                        payroll.getPayrollMonth().format(PAY_PERIOD_FORMATTER),
                headingFont(13)
        );

        payPeriod.setSpacingBefore(10);
        payPeriod.setSpacingAfter(2);

        document.add(payPeriod);
    }

    // -------------------------------------------------------------------------
    // Payroll Metadata
    // -------------------------------------------------------------------------

    private static void addPayrollMetadata(
            Document document,
            Payroll payroll
    ) throws DocumentException {

        PdfPTable metadataTable = new PdfPTable(4);
        metadataTable.setWidthPercentage(100);
        metadataTable.setSpacingAfter(8);

        addMetadataCell(
                metadataTable,
                "Payroll No.",
                payroll.getPayrollNumber()
        );

        addMetadataCell(
                metadataTable,
                "Version",
                "v" + payroll.getVersion()
        );

        addMetadataCell(
                metadataTable,
                "Status",
                payroll.getStatus().name()
        );

        addMetadataCell(
                metadataTable,
                "Pay Date",
                payroll.getPaymentDate() != null
                        ? payroll.getPaymentDate().toString()
                        : "—"
        );

        document.add(metadataTable);
    }

    private static void addMetadataCell(
            PdfPTable table,
            String label,
            String value
    ) {

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(HEADER_BACKGROUND);
        cell.setPadding(8);

        cell.addElement(
                new Phrase(label, mutedFont(8))
        );

        cell.addElement(
                new Phrase(value, boldFont(10))
        );

        table.addCell(cell);
    }

    // -------------------------------------------------------------------------
    // Section Divider
    // -------------------------------------------------------------------------

    private static void addSectionDivider(
            Document document
    ) throws DocumentException {

        LineSeparator separator = new LineSeparator(
                0.5f,
                100,
                BRAND_ACCENT,
                Element.ALIGN_CENTER,
                -2
        );

        document.add(new Chunk(separator));
        document.add(Chunk.NEWLINE);
    }

    // -------------------------------------------------------------------------
    // Employee & Payment Details
    // -------------------------------------------------------------------------

    private static void addEmployeeAndPaymentDetails(
            Document document,
            Payroll payroll
    ) throws DocumentException {

        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setSpacingAfter(10);
        detailsTable.setWidths(new float[]{1f, 1f});

        // Employee Details
        PdfPCell employeeCell = createDetailsCell();

        employeeCell.addElement(
                createSectionTitle("Employee Details")
        );

        employeeCell.addElement(
                createLabelValue(
                        "Name",
                        safe(payroll.getEmployeeName())
                )
        );

        employeeCell.addElement(
                createLabelValue(
                        "Code",
                        safe(payroll.getEmployeeCode())
                )
        );

        employeeCell.addElement(
                createLabelValue(
                        "Department",
                        safe(payroll.getDepartmentName())
                )
        );

        employeeCell.addElement(
                createLabelValue(
                        "Designation",
                        safe(payroll.getDesignationName())
                )
        );

        employeeCell.addElement(
                createLabelValue(
                        "PAN",
                        safe(payroll.getPanNumber(), "Not provided")
                )
        );

        employeeCell.addElement(
                createLabelValue(
                        "UAN",
                        safe(payroll.getUanNumber(), "Not provided")
                )
        );

        detailsTable.addCell(employeeCell);

        // Payment Details
        PdfPCell paymentCell = createDetailsCell();

        paymentCell.addElement(
                createSectionTitle("Payment Details")
        );

        paymentCell.addElement(
                createLabelValue(
                        "Bank Name",
                        safe(payroll.getBankName())
                )
        );

        paymentCell.addElement(
                createLabelValue(
                        "Account Number",
                        safe(payroll.getAccountNumber())
                )
        );

        paymentCell.addElement(
                createLabelValue(
                        "IFSC Code",
                        safe(payroll.getIfscCode())
                )
        );

        paymentCell.addElement(
                createLabelValue(
                        "Payment Mode",
                        "Bank Transfer"
                )
        );

        paymentCell.addElement(
                createLabelValue(
                        "Payment Ref.",
                        safe(payroll.getPaymentReference(), "NA")
                )
        );

        detailsTable.addCell(paymentCell);

        document.add(detailsTable);
    }

    private static PdfPCell createDetailsCell() {

        PdfPCell cell = new PdfPCell();

        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(BORDER_COLOR);
        cell.setPadding(10);

        return cell;
    }

    // -------------------------------------------------------------------------
    // Attendance Summary
    // -------------------------------------------------------------------------

    private static void addAttendanceSummary(
            Document document,
            Payroll payroll
    ) throws DocumentException {

        document.add(
                createSectionTitle("Attendance Summary")
        );

        document.add(Chunk.NEWLINE);

        PdfPTable attendanceTable = new PdfPTable(3);

        attendanceTable.setWidthPercentage(60);
        attendanceTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        attendanceTable.setSpacingAfter(12);

        addTableHeader(
                attendanceTable,
                "Total Working Days",
                "Worked Days",
                "LOP Days"
        );

        addTableRow(
                attendanceTable,
                valueOrZero(payroll.getTotalWorkingDays()),
                valueOrZero(payroll.getWorkedDays()),
                valueOrZero(payroll.getLopDays()),
                false
        );

        document.add(attendanceTable);
    }

    // -------------------------------------------------------------------------
    // Earnings & Deductions
    // -------------------------------------------------------------------------

    private static void addEarningsAndDeductions(
            Document document,
            Payroll payroll
    ) throws DocumentException {

        document.add(
                createSectionTitle("Earnings & Deductions")
        );

        document.add(Chunk.NEWLINE);

        PdfPTable salaryTable = new PdfPTable(4);

        salaryTable.setWidthPercentage(100);
        salaryTable.setSpacingAfter(8);
        salaryTable.setWidths(
                new float[]{2.5f, 1.5f, 2.5f, 1.5f}
        );

        addTableHeader(
                salaryTable,
                "Earnings",
                "Amount (₹)",
                "Deductions",
                "Amount (₹)"
        );

        addEarningsAndDeductionsRow(
                salaryTable,
                "Basic Salary",
                formatAmount(payroll.getBasicSalary()),
                "Provident Fund (PF)",
                formatAmount(payroll.getPf()),
                false
        );

        addEarningsAndDeductionsRow(
                salaryTable,
                "HRA",
                formatAmount(payroll.getHra()),
                "ESI",
                formatAmount(payroll.getEsi()),
                true
        );

        addEarningsAndDeductionsRow(
                salaryTable,
                "Special Allowance",
                formatAmount(payroll.getSpecialAllowance()),
                "Professional Tax",
                formatAmount(payroll.getProfessionalTax()),
                false
        );

        addEarningsAndDeductionsRow(
                salaryTable,
                "Medical Allowance",
                formatAmount(payroll.getMedicalAllowance()),
                "Income Tax (TDS)",
                formatAmount(payroll.getIncomeTax()),
                true
        );

        addEarningsAndDeductionsRow(
                salaryTable,
                "Travel Allowance",
                formatAmount(payroll.getTravelAllowance()),
                "Other Deductions",
                formatAmount(payroll.getOtherDeduction()),
                false
        );

        addEarningsAndDeductionsRow(
                salaryTable,
                "Bonus",
                formatAmount(payroll.getBonus()),
                "LOP Deduction",
                formatAmount(payroll.getLopAmount()),
                true
        );

        addEarningsAndDeductionsRow(
                salaryTable,
                "Other Allowance",
                formatAmount(payroll.getOtherAllowance()),
                "",
                "",
                false
        );

        document.add(salaryTable);
    }

    private static void addEarningsAndDeductionsRow(
            PdfPTable table,
            String earningLabel,
            String earningValue,
            String deductionLabel,
            String deductionValue,
            boolean alternateRow
    ) {

        Color background =
                alternateRow
                        ? ALTERNATE_ROW_BACKGROUND
                        : WHITE;

        addSimpleCell(
                table,
                earningLabel,
                background,
                Element.ALIGN_LEFT
        );

        addSimpleCell(
                table,
                earningValue,
                background,
                Element.ALIGN_RIGHT
        );

        addSimpleCell(
                table,
                deductionLabel,
                background,
                Element.ALIGN_LEFT
        );

        addSimpleCell(
                table,
                deductionValue,
                background,
                Element.ALIGN_RIGHT
        );
    }

    // -------------------------------------------------------------------------
    // Salary Summary
    // -------------------------------------------------------------------------

    private static void addSalarySummary(
            Document document,
            Payroll payroll
    ) throws DocumentException {

        PdfPTable summaryTable = new PdfPTable(3);

        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(4);
        summaryTable.setSpacingAfter(16);

        addSummaryCell(
                summaryTable,
                "Gross Salary",
                formatAmount(payroll.getGrossSalary()),
                HEADER_BACKGROUND
        );

        addSummaryCell(
                summaryTable,
                "Total Deductions",
                formatAmount(payroll.getTotalDeduction()),
                new Color(254, 226, 226)
        );

        addSummaryCell(
                summaryTable,
                "Net Payable",
                formatAmount(payroll.getNetPayable()),
                TOTAL_BACKGROUND
        );

        document.add(summaryTable);
    }

    private static void addSummaryCell(
            PdfPTable table,
            String label,
            String value,
            Color background
    ) {

        PdfPCell cell = new PdfPCell();

        cell.setBackgroundColor(background);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(BORDER_COLOR);
        cell.setPadding(12);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        cell.addElement(
                new Paragraph(label, mutedFont(9))
        );

        Paragraph amount = new Paragraph(
                value,
                boldFont(13)
        );

        amount.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(amount);

        table.addCell(cell);
    }

    // -------------------------------------------------------------------------
    // Footer
    // -------------------------------------------------------------------------

    private static void addFooter(
            Document document
    ) throws DocumentException {

        addSectionDivider(document);

        Paragraph disclaimer = new Paragraph(
                "This is a system-generated payslip. " +
                        "No signature is required.",
                mutedFont(8)
        );

        disclaimer.setAlignment(Element.ALIGN_CENTER);
        disclaimer.setSpacingBefore(4);

        document.add(disclaimer);

        Paragraph generatedBy = new Paragraph(
                "Generated by MyHourly HRMS",
                mutedFont(7)
        );

        generatedBy.setAlignment(Element.ALIGN_CENTER);

        document.add(generatedBy);
    }

    // -------------------------------------------------------------------------
    // Table Helpers
    // -------------------------------------------------------------------------

    private static void addTableHeader(
            PdfPTable table,
            String... headers
    ) {

        for (String header : headers) {

            PdfPCell cell = new PdfPCell(
                    new Phrase(header, whiteBoldFont(9))
            );

            cell.setBackgroundColor(BRAND_DARK);
            cell.setPadding(7);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(cell);
        }
    }

    private static void addTableRow(
            PdfPTable table,
            String firstColumn,
            String secondColumn,
            String thirdColumn,
            boolean alternateRow
    ) {

        Color background =
                alternateRow
                        ? ALTERNATE_ROW_BACKGROUND
                        : WHITE;

        addSimpleCell(
                table,
                firstColumn,
                background,
                Element.ALIGN_CENTER
        );

        addSimpleCell(
                table,
                secondColumn,
                background,
                Element.ALIGN_CENTER
        );

        addSimpleCell(
                table,
                thirdColumn,
                background,
                Element.ALIGN_CENTER
        );
    }

    private static void addSimpleCell(
            PdfPTable table,
            String text,
            Color background,
            int alignment
    ) {

        PdfPCell cell = new PdfPCell(
                new Phrase(
                        text != null ? text : "",
                        normalFont(9)
                )
        );

        cell.setBackgroundColor(background);
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(CELL_BORDER_COLOR);

        table.addCell(cell);
    }

    // -------------------------------------------------------------------------
    // Paragraph Helpers
    // -------------------------------------------------------------------------

    private static Paragraph createSectionTitle(
            String title
    ) {

        Paragraph paragraph = new Paragraph(
                title,
                sectionHeadingFont(10)
        );

        paragraph.setSpacingBefore(6);
        paragraph.setSpacingAfter(4);

        return paragraph;
    }

    private static Paragraph createLabelValue(
            String label,
            String value
    ) {

        Paragraph paragraph = new Paragraph();

        paragraph.add(
                new Chunk(
                        label + ":  ",
                        mutedFont(9)
                )
        );

        paragraph.add(
                new Chunk(
                        value,
                        boldFont(9)
                )
        );

        paragraph.setSpacingAfter(3);

        return paragraph;
    }

    // -------------------------------------------------------------------------
    // Utility Methods
    // -------------------------------------------------------------------------

    private static String formatAmount(BigDecimal value) {

        if (value == null) {
            return "0.00";
        }

        return String.format("%,.2f", value);
    }

    private static String valueOrZero(Integer value) {

        return value == null
                ? "0"
                : value.toString();
    }

    private static String safe(String value) {

        return value != null
                ? value
                : "—";
    }

    private static String safe(
            String value,
            String fallback
    ) {

        return value != null && !value.isBlank()
                ? value
                : fallback;
    }
}
