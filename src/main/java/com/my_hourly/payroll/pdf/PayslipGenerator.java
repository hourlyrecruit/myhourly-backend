package com.my_hourly.payroll.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.my_hourly.payroll.entity.Payroll;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Generates a professionally formatted payslip PDF per Design Document (Sections 10, 16).
 *
 * Layout:
 *  1. Header  - Company name + "PAYSLIP STATEMENT"
 *  2. Meta    - Payroll Number, Month, Version, Status
 *  3. Employee & Department Details
 *  4. Bank / Payment Details
 *  5. Attendance Summary Table
 *  6. Earnings & Deductions Table (with LOP Deduction explicitly listed)
 *  7. Summary Box  – Gross Salary | Total Deductions | Net Payable
 *  8. Footer  – system-generated disclaimer
 */
public class PayslipGenerator {

    // ── Colour Palette ──────────────────────────────────────────────────────────
    private static final Color BRAND_DARK   = new Color(31, 41, 64);   // Navy
    private static final Color BRAND_ACCENT = new Color(59, 130, 246); // Blue
    private static final Color HEADER_BG    = new Color(238, 242, 255);
    private static final Color ROW_ALT_BG   = new Color(248, 250, 252);
    private static final Color TOTAL_BG     = new Color(224, 231, 255);
    private static final Color TEXT_GRAY    = new Color(100, 116, 139);
    private static final Color WHITE        = Color.WHITE;

    // ── Fonts ───────────────────────────────────────────────────────────────────
    private static Font heading1(float size)  { return FontFactory.getFont(FontFactory.HELVETICA_BOLD,  size, BRAND_DARK);   }
    private static Font heading2(float size)  { return FontFactory.getFont(FontFactory.HELVETICA_BOLD,  size, BRAND_ACCENT); }
    private static Font bold(float size)      { return FontFactory.getFont(FontFactory.HELVETICA_BOLD,  size, BRAND_DARK);   }
    private static Font normal(float size)    { return FontFactory.getFont(FontFactory.HELVETICA,        size, BRAND_DARK);   }
    private static Font muted(float size)     { return FontFactory.getFont(FontFactory.HELVETICA,        size, TEXT_GRAY);   }
    private static Font boldWhite(float size) { return FontFactory.getFont(FontFactory.HELVETICA_BOLD,  size, WHITE);        }

    private static final DateTimeFormatter MONTH_FMT =
            DateTimeFormatter.ofPattern("MMMM yyyy");

    // ── Public API ───────────────────────────────────────────────────────────────

    public static byte[] generate(Payroll payroll) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4, 40, 40, 36, 36);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, payroll);
            addMetaRow(doc, payroll);
            addSectionDivider(doc);

            addTwoColumnSection(doc, payroll);
            addSectionDivider(doc);

            addAttendanceTable(doc, payroll);
            addSalaryTable(doc, payroll);
            addSummarBox(doc, payroll);

            addFooter(doc, writer);

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate payslip PDF", e);
        }
    }

    // ── 1. Header ────────────────────────────────────────────────────────────────

    private static void addHeader(Document doc, Payroll payroll) throws DocumentException {

        // Company bar
        PdfPTable banner = new PdfPTable(1);
        banner.setWidthPercentage(100);
        banner.setSpacingAfter(4);

        PdfPCell companyCell = new PdfPCell();
        companyCell.setBackgroundColor(BRAND_DARK);
        companyCell.setPadding(14);
        companyCell.setBorder(Rectangle.NO_BORDER);

        Paragraph companyName = new Paragraph("MyHourly", boldWhite(18));
        companyName.setAlignment(Element.ALIGN_LEFT);
        companyCell.addElement(companyName);

        Paragraph tagline = new Paragraph("Payslip Statement", muted(10));
        tagline.setAlignment(Element.ALIGN_LEFT);
        companyCell.addElement(tagline);

        banner.addCell(companyCell);
        doc.add(banner);

        // Payroll month heading
        Paragraph monthLine = new Paragraph(
                "Pay Period:  " + payroll.getPayrollMonth().format(MONTH_FMT),
                heading1(13));
        monthLine.setSpacingBefore(10);
        monthLine.setSpacingAfter(2);
        doc.add(monthLine);
    }

    // ── 2. Meta Row ──────────────────────────────────────────────────────────────

    private static void addMetaRow(Document doc, Payroll payroll) throws DocumentException {

        PdfPTable meta = new PdfPTable(4);
        meta.setWidthPercentage(100);
        meta.setSpacingAfter(8);

        addMetaCell(meta, "Payroll No.",     payroll.getPayrollNumber());
        addMetaCell(meta, "Version",         "v" + payroll.getVersion());
        addMetaCell(meta, "Status",          payroll.getStatus().name());
        addMetaCell(meta, "Pay Date",
                payroll.getPaymentDate() != null
                        ? payroll.getPaymentDate().toString()
                        : "—");

        doc.add(meta);
    }

    private static void addMetaCell(PdfPTable table, String label, String value) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(HEADER_BG);
        cell.setPadding(8);
        cell.addElement(new Phrase(label, muted(8)));
        cell.addElement(new Phrase(value,  bold(10)));
        table.addCell(cell);
    }

    // ── 3. Section Divider ───────────────────────────────────────────────────────

    private static void addSectionDivider(Document doc) throws DocumentException {
        LineSeparator line = new LineSeparator(0.5f, 100, BRAND_ACCENT, Element.ALIGN_CENTER, -2);
        doc.add(new Chunk(line));
        doc.add(Chunk.NEWLINE);
    }

    // ── 4. Two-column: Employee Details | Bank / Payment Details ────────────────

    private static void addTwoColumnSection(Document doc, Payroll payroll) throws DocumentException {

        PdfPTable outer = new PdfPTable(2);
        outer.setWidthPercentage(100);
        outer.setSpacingAfter(10);
        outer.setWidths(new float[]{1f, 1f});

        // Employee Details
        PdfPCell empCell = new PdfPCell();
        empCell.setBorder(Rectangle.BOX);
        empCell.setBorderColor(new Color(203, 213, 225));
        empCell.setPadding(10);

        empCell.addElement(sectionTitle("Employee Details"));
        empCell.addElement(labelValue("Name",        safe(payroll.getEmployeeName())));
        empCell.addElement(labelValue("Code",        safe(payroll.getEmployeeCode())));
        empCell.addElement(labelValue("Department",  safe(payroll.getDepartmentName())));
        empCell.addElement(labelValue("Designation", safe(payroll.getDesignationName())));
        empCell.addElement(labelValue("PAN",         safe(payroll.getPanNumber(), "Not provided")));
        empCell.addElement(labelValue("UAN",         safe(payroll.getUanNumber(), "Not provided")));
        outer.addCell(empCell);

        // Bank / Payment Details
        PdfPCell bankCell = new PdfPCell();
        bankCell.setBorder(Rectangle.BOX);
        bankCell.setBorderColor(new Color(203, 213, 225));
        bankCell.setPadding(10);

        bankCell.addElement(sectionTitle("Payment Details"));
        bankCell.addElement(labelValue("Bank Name",       safe(payroll.getBankName())));
        bankCell.addElement(labelValue("Account Number",  safe(payroll.getAccountNumber())));
        bankCell.addElement(labelValue("IFSC Code",       safe(payroll.getIfscCode())));
        bankCell.addElement(labelValue("Payment Mode",    "Bank Transfer"));
        bankCell.addElement(labelValue("Payment Ref.",
                safe(payroll.getPaymentReference(), "NA")));

        outer.addCell(bankCell);
        doc.add(outer);
    }

    // ── 5. Attendance Table ──────────────────────────────────────────────────────

    private static void addAttendanceTable(Document doc, Payroll payroll) throws DocumentException {

        doc.add(sectionTitle("Attendance Summary"));
        doc.add(Chunk.NEWLINE);

        PdfPTable tbl = new PdfPTable(3);
        tbl.setWidthPercentage(60);
        tbl.setHorizontalAlignment(Element.ALIGN_LEFT);
        tbl.setSpacingAfter(12);

        addTableHeader(tbl, "Total Working Days", "Worked Days", "LOP Days");
        addTableRow(tbl,
                str(payroll.getTotalWorkingDays()),
                str(payroll.getWorkedDays()),
                str(payroll.getLopDays()),
                false);

        doc.add(tbl);
    }

    // ── 6. Earnings & Deductions Table ───────────────────────────────────────────

    private static void addSalaryTable(Document doc, Payroll payroll) throws DocumentException {

        doc.add(sectionTitle("Earnings & Deductions"));
        doc.add(Chunk.NEWLINE);

        PdfPTable tbl = new PdfPTable(4);
        tbl.setWidthPercentage(100);
        tbl.setSpacingAfter(8);
        tbl.setWidths(new float[]{2.5f, 1.5f, 2.5f, 1.5f});

        addTableHeader(tbl, "Earnings", "Amount (₹)", "Deductions", "Amount (₹)");

        addEarningsDeductionsRow(tbl, "Basic Salary",       fmt(payroll.getBasicSalary()),
                "Provident Fund (PF)",              fmt(payroll.getPf()), false);
        addEarningsDeductionsRow(tbl, "HRA",                fmt(payroll.getHra()),
                "ESI",                              fmt(payroll.getEsi()), true);
        addEarningsDeductionsRow(tbl, "Special Allowance",  fmt(payroll.getSpecialAllowance()),
                "Professional Tax",                 fmt(payroll.getProfessionalTax()), false);
        addEarningsDeductionsRow(tbl, "Medical Allowance",  fmt(payroll.getMedicalAllowance()),
                "Income Tax (TDS)",                 fmt(payroll.getIncomeTax()), true);
        addEarningsDeductionsRow(tbl, "Travel Allowance",   fmt(payroll.getTravelAllowance()),
                "Other Deductions",                 fmt(payroll.getOtherDeduction()), false);
        addEarningsDeductionsRow(tbl, "Bonus",              fmt(payroll.getBonus()),
                "LOP Deduction",                    fmt(payroll.getLopAmount()), true);
        addEarningsDeductionsRow(tbl, "Other Allowance",    fmt(payroll.getOtherAllowance()),
                "",                                 "", false);

        doc.add(tbl);
    }

    // ── 7. Summary Box ───────────────────────────────────────────────────────────

    private static void addSummarBox(Document doc, Payroll payroll) throws DocumentException {

        PdfPTable summary = new PdfPTable(3);
        summary.setWidthPercentage(100);
        summary.setSpacingAfter(16);
        summary.setSpacingBefore(4);

        addSummaryCell(summary, "Gross Salary",    fmt(payroll.getGrossSalary()),    HEADER_BG);
        addSummaryCell(summary, "Total Deductions", fmt(payroll.getTotalDeduction()), new Color(254, 226, 226));
        addSummaryCell(summary, "Net Payable",      fmt(payroll.getNetPayable()),     TOTAL_BG);

        doc.add(summary);
    }

    private static void addSummaryCell(PdfPTable table, String label, String value, Color bg) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bg);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(new Color(203, 213, 225));
        cell.setPadding(12);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.addElement(new Paragraph(label, muted(9)));
        Paragraph val = new Paragraph(value, bold(13));
        val.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(val);
        table.addCell(cell);
    }

    // ── 8. Footer ────────────────────────────────────────────────────────────────

    private static void addFooter(Document doc, PdfWriter writer) throws DocumentException {

        addSectionDivider(doc);

        Paragraph footer = new Paragraph(
                "This is a system-generated payslip. No signature is required.", muted(8));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(4);
        doc.add(footer);

        Paragraph generated = new Paragraph(
                "Generated by MyHourly HRMS", muted(7));
        generated.setAlignment(Element.ALIGN_CENTER);
        doc.add(generated);
    }

    // ── Helper: Table ────────────────────────────────────────────────────────────

    private static void addTableHeader(PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, boldWhite(9)));
            cell.setBackgroundColor(BRAND_DARK);
            cell.setPadding(7);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private static void addTableRow(PdfPTable table, String c1, String c2, String c3, boolean alt) {
        Color bg = alt ? ROW_ALT_BG : WHITE;
        addSimpleCell(table, c1, bg, Element.ALIGN_CENTER);
        addSimpleCell(table, c2, bg, Element.ALIGN_CENTER);
        addSimpleCell(table, c3, bg, Element.ALIGN_CENTER);
    }

    private static void addEarningsDeductionsRow(
            PdfPTable table,
            String earningLabel, String earningValue,
            String deductionLabel, String deductionValue,
            boolean alt) {

        Color bg = alt ? ROW_ALT_BG : WHITE;
        addSimpleCell(table, earningLabel,   bg, Element.ALIGN_LEFT);
        addSimpleCell(table, earningValue,   bg, Element.ALIGN_RIGHT);
        addSimpleCell(table, deductionLabel, bg, Element.ALIGN_LEFT);
        addSimpleCell(table, deductionValue, bg, Element.ALIGN_RIGHT);
    }

    private static void addSimpleCell(PdfPTable table, String text, Color bg, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", normal(9)));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        cell.setHorizontalAlignment(align);
        cell.setBorderColor(new Color(226, 232, 240));
        table.addCell(cell);
    }

    // ── Helper: Paragraphs ───────────────────────────────────────────────────────

    private static Paragraph sectionTitle(String title) {
        Paragraph p = new Paragraph(title, heading2(10));
        p.setSpacingBefore(6);
        p.setSpacingAfter(4);
        return p;
    }

    private static Paragraph labelValue(String label, String value) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + ":  ", muted(9)));
        p.add(new Chunk(value, bold(9)));
        p.setSpacingAfter(3);
        return p;
    }

    // ── Utility ──────────────────────────────────────────────────────────────────

    private static String fmt(java.math.BigDecimal value) {
        if (value == null) return "0.00";
        return String.format("%,.2f", value);
    }

    private static String str(Integer value) {
        return value == null ? "0" : value.toString();
    }

    private static String safe(String value) {
        return value != null ? value : "—";
    }

    private static String safe(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }
}
