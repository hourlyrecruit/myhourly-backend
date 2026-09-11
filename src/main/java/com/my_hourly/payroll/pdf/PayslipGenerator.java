package com.my_hourly.payroll.pdf;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.my_hourly.payroll.entity.Payroll;
import com.my_hourly.settings.company.entity.CompanySettings;
import com.my_hourly.settings.company.repository.CompanySettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayslipGenerator {

    private final CompanySettingsRepository companySettingsRepository;

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private static final String LOGO_RESOURCE = "/payslip/hourlyrecruit-logo.png";
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    // -------------------------------------------------------------------------
    // Color Palette — exact match to the sample image
    // -------------------------------------------------------------------------

    private static final Color BRAND_TEXT       = new Color(15,  23,  42);
    private static final Color BRAND_BLUE       = new Color(37,  99,  235);
    private static final Color HEADER_BG        = new Color(241, 245, 249);
    private static final Color CARD_HEADER_BG   = new Color(226, 232, 240);
    private static final Color GROSS_ROW_BG     = new Color(238, 242, 255);
    private static final Color NET_PAY_BG       = new Color(219, 234, 254);
    private static final Color MUTED_TEXT       = new Color(100, 116, 139);
    private static final Color CARD_BORDER      = new Color(226, 232, 240);
    private static final Color TABLE_BORDER     = new Color(226, 232, 240);
    private static final Color SEPARATOR_LINE   = new Color(148, 163, 184);
    private static final Color WHITE            = Color.WHITE;

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public byte[] generate(Payroll payroll) {
        if (payroll == null) {
            log.error("Payslip generation failed: payroll is null");
            throw new PayslipGenerationException("Payroll cannot be null");
        }

        Long payrollId = payroll.getId();
        log.info("Starting payslip generation. payrollId={}", payrollId);

        validatePayroll(payroll);
        CompanySettings settings = getActiveCompanySettings(payrollId);

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.LETTER, 30, 30, 22, 24);
            PdfWriter writer = PdfWriter.getInstance(document, output);
            writer.setCompressionLevel(9);
            writer.setFullCompression();
            writer.setViewerPreferences(PdfWriter.HideToolbar);
            document.open();

            try {
                addHeader(document, settings);
                addSeparator(document);

                addTitleAndPayrollCard(document, payroll);
                addEmployeeAndBankCards(document, payroll);
                addAttendanceSummary(document, payroll);
                addEarningsAndDeductions(document, payroll);
                addNetPayable(document, payroll);
                addAmountInWords(document, payroll);
                addFooter(document, settings);

                document.close();
                byte[] pdf = output.toByteArray();
                if (pdf.length == 0) {
                    throw new PayslipGenerationException("Generated payslip PDF is empty");
                }

                log.info(
                        "Payslip generated successfully. payrollId={}, size={} bytes",
                        payrollId, pdf.length
                );
                return pdf;

            } catch (DocumentException | IOException e) {
                closeQuietly(document);
                throw new PayslipGenerationException("Unable to create payslip PDF", e);
            } catch (PayslipGenerationException e) {
                closeQuietly(document);
                throw e;
            } catch (Exception e) {
                closeQuietly(document);
                log.error("Unexpected error while drawing payslip. payrollId={}", payrollId, e);
                throw new PayslipGenerationException("Unexpected error while generating payslip", e);
            }

        } catch (PayslipGenerationException e) {
            log.error("Payslip generation failed. payrollId={}", payrollId, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected payslip generation failure. payrollId={}", payrollId, e);
            throw new PayslipGenerationException("Failed to generate payslip PDF", e);
        }
    }

    // -------------------------------------------------------------------------
    // Validation & Settings
    // -------------------------------------------------------------------------

    private void validatePayroll(Payroll payroll) {
        if (payroll.getPayrollMonth() == null) {
            log.error("Payroll month is null. payrollId={}", payroll.getId());
            throw new PayslipGenerationException("Payroll month cannot be null");
        }
        log.debug("Payroll validation successful. payrollId={}", payroll.getId());
    }

    private CompanySettings getActiveCompanySettings(Long payrollId) {
        return companySettingsRepository.findFirstByActiveTrue()
                .orElseThrow(() -> {
                    log.error("Active company settings not found. payrollId={}", payrollId);
                    return new PayslipGenerationException("Active company settings not found");
                });
    }

    // -------------------------------------------------------------------------
    // 1. Header — Logo left, Address right
    // -------------------------------------------------------------------------

    private void addHeader(Document document, CompanySettings settings)
            throws DocumentException, IOException, BadElementException {

        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{3f, 2.2f});
        header.setSpacingAfter(4);

        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        logoCell.setPaddingRight(6);

        try {
            Image logo = loadLogoImage();
            logo.scaleToFit(160, 50);
            logo.setAlignment(Element.ALIGN_LEFT);
            logoCell.addElement(logo);
        } catch (PayslipGenerationException e) {
            log.warn("Falling back to text-only brand header");
            Paragraph brandName = new Paragraph("HourlyRecruit", brandNameFont(18));
            brandName.setAlignment(Element.ALIGN_LEFT);
            logoCell.addElement(brandName);

            Paragraph tagline = new Paragraph("Build People Grow Together", mutedFont(9));
            tagline.setAlignment(Element.ALIGN_LEFT);
            logoCell.addElement(tagline);
        }
        header.addCell(logoCell);

        PdfPCell addrCell = new PdfPCell();
        addrCell.setBorder(Rectangle.NO_BORDER);
        addrCell.setVerticalAlignment(Element.ALIGN_TOP);
        addrCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        addrCell.setPaddingLeft(6);

        String line1 = buildAddressLine1(settings);
        String line2 = buildAddressLine2(settings);
        String line3 = buildAddressLine3(settings);

        if (!line1.isEmpty()) {
            Paragraph p1 = new Paragraph(line1, bodyFont(9));
            p1.setAlignment(Element.ALIGN_RIGHT);
            addrCell.addElement(p1);
        }
        if (!line2.isEmpty()) {
            Paragraph p2 = new Paragraph(line2, bodyFont(9));
            p2.setAlignment(Element.ALIGN_RIGHT);
            addrCell.addElement(p2);
        }
        if (!line3.isEmpty()) {
            Paragraph p3 = new Paragraph(line3, bodyFont(9));
            p3.setAlignment(Element.ALIGN_RIGHT);
            addrCell.addElement(p3);
        }
        header.addCell(addrCell);

        document.add(header);
    }

    private Image loadLogoImage() throws IOException, BadElementException {
        try (InputStream in = PayslipGenerator.class.getResourceAsStream(LOGO_RESOURCE)) {
            if (in == null) {
                throw new PayslipGenerationException("Logo not found: " + LOGO_RESOURCE);
            }
            BufferedImage src = ImageIO.read(in);
            if (src == null) {
                throw new PayslipGenerationException("Unable to decode logo: " + LOGO_RESOURCE);
            }

            int maxW = 450;
            int maxH = 120;
            int targetW = Math.min(src.getWidth(), maxW);
            int targetH = (int) Math.round(src.getHeight() * (targetW / (double) src.getWidth()));
            if (targetH > maxH) {
                targetH = maxH;
                targetW = (int) Math.round(src.getWidth() * (maxH / (double) src.getHeight()));
            }
            if (targetW <= 0) targetW = 1;
            if (targetH <= 0) targetH = 1;

            BufferedImage scaled = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = scaled.createGraphics();
            try {
                g2.setPaint(new java.awt.Color(255, 255, 255));
                g2.fillRect(0, 0, targetW, targetH);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(src, 0, 0, targetW, targetH, null);
            } finally {
                g2.dispose();
            }

            byte[] jpegBytes = encodeAsJpeg(scaled, 0.85f);
            return Image.getInstance(jpegBytes);
        }
    }

    private byte[] encodeAsJpeg(BufferedImage image, float quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            throw new IOException("No JPEG ImageWriter available");
        }
        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
            ios.flush();
            return baos.toByteArray();
        } finally {
            writer.dispose();
        }
    }

    private void addSeparator(Document document) throws DocumentException {
        LineSeparator sep = new LineSeparator(1.0f, 100, SEPARATOR_LINE, Element.ALIGN_CENTER, -2);
        document.add(new Chunk(sep));
    }

    // -------------------------------------------------------------------------
    // 2. Title ("Payslip for July 2026") + Payroll No. card (right)
    // -------------------------------------------------------------------------

    private void addTitleAndPayrollCard(Document document, Payroll payroll)
            throws DocumentException {

        PdfPTable row = new PdfPTable(2);
        row.setSpacingBefore(10);
        row.setWidthPercentage(100);
        row.setWidths(new float[]{3f, 1.8f});
        row.setSpacingAfter(10);

        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        String month = payroll.getPayrollMonth().format(MONTH_FORMAT);
        Paragraph title = new Paragraph("Payslip for " + month, titleFont(22));
        title.setSpacingBefore(4);
        title.setSpacingAfter(4);
        title.setAlignment(Element.ALIGN_LEFT);
        titleCell.addElement(title);
        row.addCell(titleCell);

        PdfPCell prCell = new PdfPCell();
        prCell.setBorder(Rectangle.BOX);
        prCell.setBorderColor(CARD_BORDER);
        prCell.setBackgroundColor(HEADER_BG);
        prCell.setPadding(8);
        prCell.setPaddingLeft(12);
        prCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        Paragraph label = new Paragraph("Payroll No.", mutedFont(9));
        label.setSpacingAfter(2);
        prCell.addElement(label);

        Paragraph value = new Paragraph(safe(payroll.getPayrollNumber()), boldBodyFont(13));
        prCell.addElement(value);
        row.addCell(prCell);

        document.add(row);
    }

    // -------------------------------------------------------------------------
    // 3. Employee Details + Bank Details cards (side by side)
    // -------------------------------------------------------------------------

    private void addEmployeeAndBankCards(Document document, Payroll payroll)
            throws DocumentException {

        PdfPTable row = new PdfPTable(2);
        row.setWidthPercentage(100);
        row.setWidths(new float[]{1f, 1f});
        row.setSpacingAfter(10);

        row.addCell(buildCard(
                "Employee Details",
                new String[][]{
                        {"Name",          safe(payroll.getEmployeeName())},
                        {"Employee ID",   safe(payroll.getEmployeeCode())},
                        {"Department",    safeOrDash(payroll.getDepartmentName())},
                        {"Designation",   safeOrDash(payroll.getDesignationName())},
                        {"UAN",           safeOrDash(payroll.getUanNumber())},
                        {"PAN",           safeOrDash(payroll.getPanNumber())}
                }
        ));

        row.addCell(buildCard(
                "Bank Details",
                new String[][]{
                        {"Bank Name",   safeOrDash(payroll.getBankName())},
                        {"A/C No.",     maskAccountNumber(payroll.getAccountNumber())},
                        {"IFSC Code",   safeOrDash(payroll.getIfscCode())}
                }
        ));

        document.add(row);
    }

    private PdfPCell buildCard(String title, String[][] rows) {
        PdfPCell outer = new PdfPCell();
        outer.setBorder(Rectangle.BOX);
        outer.setBorderColor(CARD_BORDER);
        outer.setPadding(0);

        PdfPTable inner = new PdfPTable(1);
        inner.setWidthPercentage(100);

        PdfPCell header = new PdfPCell();
        header.setBorder(Rectangle.NO_BORDER);
        header.setBackgroundColor(CARD_HEADER_BG);
        header.setPadding(6);
        header.setPaddingLeft(10);
        header.setPaddingRight(10);

        Paragraph titleP = new Paragraph(title, boldBodyFont(11));
        titleP.setSpacingBefore(0);
        titleP.setSpacingAfter(0);
        header.addElement(titleP);
        inner.addCell(header);

        PdfPCell body = new PdfPCell();
        body.setBorder(Rectangle.NO_BORDER);
        body.setPadding(8);
        body.setPaddingLeft(10);
        body.setPaddingRight(10);

        for (String[] pair : rows) {
            body.addElement(labelValuePair(pair[0], pair[1]));
        }
        inner.addCell(body);

        outer.addElement(inner);
        return outer;
    }

    private PdfPTable labelValuePair(String label, String value) {

        PdfPTable row = new PdfPTable(3);
        row.setWidthPercentage(100);
        row.setWidths(new float[]{1.6f, 0.15f, 2.5f});

        // Label
        PdfPCell labelCell = new PdfPCell(
                new Phrase(safe(label), mutedFont(9))
        );
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(0);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        // Colon
        PdfPCell colonCell = new PdfPCell(
                new Phrase(":", mutedFont(9))
        );
        colonCell.setBorder(Rectangle.NO_BORDER);
        colonCell.setPadding(0);
        colonCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        colonCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        // Value
        PdfPCell valueCell = new PdfPCell(
                new Phrase(safe(value), bodyFont(9))
        );
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(0);
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        row.addCell(labelCell);
        row.addCell(colonCell);
        row.addCell(valueCell);

        return row;
    }

    // -------------------------------------------------------------------------
    // 4. Attendance Summary
    // -------------------------------------------------------------------------

    private void addAttendanceSummary(Document document, Payroll payroll)
            throws DocumentException {

        PdfPTable outer = new PdfPTable(1);
        outer.setWidthPercentage(100);
        outer.setSpacingAfter(10);
        PdfPCell outerCell = new PdfPCell();
        outerCell.setBorder(Rectangle.BOX);
        outerCell.setBorderColor(CARD_BORDER);
        outerCell.setPadding(0);

        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setBackgroundColor(CARD_HEADER_BG);
        titleCell.setPadding(6);
        titleCell.setPaddingLeft(10);
        titleCell.addElement(new Paragraph("Attendance Summary", boldBodyFont(11)));
        outer.addCell(titleCell);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 1f, 1f, 1f});

        tableHeaderCell(table, "TOTAL DAYS");
        tableHeaderCell(table, "WORKED DAYS");
        tableHeaderCell(table, "PAYABLE DAYS");
        tableHeaderCell(table, "LOP DAYS");

        tableBodyCell(table, valueOrZero(payroll.getTotalWorkingDays()), HEADER_BG);
        tableBodyCell(table, valueOrZero(payroll.getWorkedDays()),       HEADER_BG);
        tableBodyCell(table, valueOrZero(payroll.getPayableDays()),      HEADER_BG);
        tableBodyCell(table, valueOrZero(payroll.getLopDays()),          HEADER_BG);

        outerCell.addElement(table);
        outer.addCell(outerCell);
        document.add(outer);
    }

    // -------------------------------------------------------------------------
    // 5. Earnings & Deductions (with Gross / Total Deductions built in)
    // -------------------------------------------------------------------------

    private void addEarningsAndDeductions(Document document, Payroll payroll)
            throws DocumentException {

        PdfPTable outer = new PdfPTable(1);
        outer.setWidthPercentage(100);
        outer.setSpacingAfter(10);

        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.BOX);
        titleCell.setBorderColor(CARD_BORDER);
        titleCell.setBackgroundColor(CARD_HEADER_BG);
        titleCell.setPadding(6);
        titleCell.setPaddingLeft(10);
        titleCell.addElement(new Paragraph("Earnings & Deductions", boldBodyFont(11)));
        outer.addCell(titleCell);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.6f, 1.4f, 2.6f, 1.4f});

        subHeaderCell(table, "EARNINGS");
        subHeaderCell(table, "AMOUNT (₹)");
        subHeaderCell(table, "DEDUCTIONS");
        subHeaderCell(table, "AMOUNT (₹)");

        edDataRow(table, false, "Basic",              formatAmount(payroll.getBasicSalary()),
                            "TDS",                formatAmount(payroll.getIncomeTax()));
        edDataRow(table, true,  "HRA",                formatAmount(payroll.getHra()),
                            "Employee PF",        formatAmount(payroll.getPf()));
        edDataRow(table, false, "Conveyance",         formatAmount(payroll.getTravelAllowance()),
                            "ESI",                formatAmount(payroll.getEsi()));
        edDataRow(table, true,  "Special Allowance",  formatAmount(payroll.getSpecialAllowance()),
                            "Professional Tax",   formatAmount(payroll.getProfessionalTax()));
        edDataRow(table, false, "Medical Allowance",  formatAmount(payroll.getMedicalAllowance()),
                            "LOP Deduction",      formatAmount(payroll.getLopAmount()));
        edDataRow(table, true,  "Bonus",              formatAmount(payroll.getBonus(), true),
                            "Others",             formatAmount(payroll.getOtherDeduction()));
        edDataRow(table, false, "Other Allowance",    formatAmount(payroll.getOtherAllowance()),
                            "",                   "");

        grossRow(table,
                "GROSS SALARY",       formatAmount(payroll.getGrossSalary()),
                "TOTAL DEDUCTIONS",   formatAmount(payroll.getTotalDeduction()));

        PdfPCell bodyWrap = new PdfPCell();
        bodyWrap.setBorder(Rectangle.BOX);
        bodyWrap.setBorderColor(CARD_BORDER);
        bodyWrap.setPadding(0);
        bodyWrap.addElement(table);
        outer.addCell(bodyWrap);

        document.add(outer);
    }

    private void edDataRow(
            PdfPTable table, boolean alt,
            String eName, String eVal,
            String dName, String dVal
    ) {
        Color bg = alt ? WHITE : HEADER_BG;
        leftBodyCell(table, eName, bg);
        rightBodyCell(table, eVal, bg);
        leftBodyCell(table, dName, bg);
        rightBodyCell(table, dVal, bg);
    }

    private void grossRow(
            PdfPTable table,
            String eName, String eVal,
            String dName, String dVal
    ) {
        grossLeftCell(table, eName);
        grossRightCell(table, eVal);
        grossLeftCell(table, dName);
        grossRightCell(table, dVal);
    }

    // -------------------------------------------------------------------------
    // 6. Net Payable banner
    // -------------------------------------------------------------------------

    private void addNetPayable(Document document, Payroll payroll)
            throws DocumentException {

        PdfPTable row = new PdfPTable(2);
        row.setWidthPercentage(100);
        row.setWidths(new float[]{1f, 1f});
        row.setSpacingAfter(8);

        PdfPCell labelCell = new PdfPCell();
        labelCell.setBorder(Rectangle.BOX);
        labelCell.setBorderColor(NET_PAY_BG);
        labelCell.setBackgroundColor(NET_PAY_BG);
        labelCell.setPadding(10);
        labelCell.setPaddingLeft(14);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph labelP = new Paragraph("NET PAYABLE (₹)", netPayLabelFont(10));
        labelP.setAlignment(Element.ALIGN_LEFT);
        labelCell.addElement(labelP);
        row.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell();
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setBorderColor(NET_PAY_BG);
        valueCell.setBackgroundColor(NET_PAY_BG);
        valueCell.setPadding(10);
        valueCell.setPaddingRight(14);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph valueP = new Paragraph(formatAmount(payroll.getNetPayable()), netPayValueFont(10));
        valueP.setAlignment(Element.ALIGN_RIGHT);
        valueCell.addElement(valueP);
        row.addCell(valueCell);

        document.add(row);
    }

    // -------------------------------------------------------------------------
    // 7. Amount in Words — single bordered row
    // -------------------------------------------------------------------------

    private void addAmountInWords(Document document, Payroll payroll)
            throws DocumentException {

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 0.25f, 4f});
        table.setSpacingAfter(10);

        PdfPCell lCell = new PdfPCell();
        lCell.setBorder(Rectangle.BOX);
        lCell.setBorderColor(CARD_BORDER);
        lCell.setBackgroundColor(HEADER_BG);
        lCell.setPadding(8);
        lCell.setPaddingLeft(12);
        lCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        lCell.addElement(new Phrase("Amount in Words", mutedFont(9)));
        table.addCell(lCell);

        PdfPCell colon = new PdfPCell();
        colon.setBorder(Rectangle.BOX);
        colon.setBorderColor(CARD_BORDER);
        colon.setBackgroundColor(HEADER_BG);
        colon.setPadding(8);
        colon.setHorizontalAlignment(Element.ALIGN_CENTER);
        colon.setVerticalAlignment(Element.ALIGN_MIDDLE);
        colon.addElement(new Phrase(":", mutedFont(9)));
        table.addCell(colon);

        PdfPCell vCell = new PdfPCell();
        vCell.setBorder(Rectangle.BOX);
        vCell.setBorderColor(CARD_BORDER);
        vCell.setBackgroundColor(WHITE);
        vCell.setPadding(8);
        vCell.setPaddingLeft(12);
        vCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        vCell.addElement(new Phrase(
                NumberToWordsConverter.convertAsRupees(payroll.getNetPayable()),
                bodyFont(10)
        ));
        table.addCell(vCell);

        document.add(table);
    }

    // -------------------------------------------------------------------------
    // 8. Footer — separator, disclaimer left, brand right
    // -------------------------------------------------------------------------

    private void addFooter(Document document, CompanySettings settings)
            throws DocumentException {

        LineSeparator sep = new LineSeparator(1.0f, 100, SEPARATOR_LINE, Element.ALIGN_CENTER, -2);
        document.add(new Chunk(sep));

        PdfPTable row = new PdfPTable(2);
        row.setWidthPercentage(100);
        row.setWidths(new float[]{3f, 1.2f});
        row.setSpacingBefore(2);

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        Paragraph note = new Paragraph(
                "This is a system generated payslip and does not require signature.",
                mutedFont(9)
        );
        note.setAlignment(Element.ALIGN_LEFT);
        leftCell.addElement(note);
        row.addCell(leftCell);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        Paragraph brand = new Paragraph(
                settings.getCompanyName() +": "+ settings.getAddressLine1() +" "+ settings.getAddressLine2(),
                boldBodyFont(8)
        );
        brand.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(brand);
        row.addCell(rightCell);

        document.add(row);
    }

    // -------------------------------------------------------------------------
    // Table cell helpers
    // -------------------------------------------------------------------------

    private void tableHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, boldBodyFont(9)));
        cell.setBackgroundColor(HEADER_BG);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(TABLE_BORDER);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void tableBodyCell(PdfPTable table, String text, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, bodyFont(9.5f)));
        cell.setBackgroundColor(bg);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(TABLE_BORDER);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void subHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, boldBodyFont(9)));
        cell.setBackgroundColor(HEADER_BG);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(TABLE_BORDER);
        cell.setPadding(5);
        cell.setPaddingLeft(8);
        cell.setPaddingRight(8);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void leftBodyCell(PdfPTable table, String text, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, bodyFont(9.5f)));
        cell.setBackgroundColor(bg);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(TABLE_BORDER);
        cell.setPadding(5);
        cell.setPaddingLeft(8);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void rightBodyCell(PdfPTable table, String text, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, bodyFont(9.5f)));
        cell.setBackgroundColor(bg);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(TABLE_BORDER);
        cell.setPadding(5);
        cell.setPaddingRight(10);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void grossLeftCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, boldBodyFont(10.5f)));
        cell.setBackgroundColor(GROSS_ROW_BG);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(TABLE_BORDER);
        cell.setPadding(6);
        cell.setPaddingLeft(8);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void grossRightCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, boldBodyFont(10.5f)));
        cell.setBackgroundColor(GROSS_ROW_BG);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(TABLE_BORDER);
        cell.setPadding(6);
        cell.setPaddingRight(10);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    // -------------------------------------------------------------------------
    // Fonts
    // -------------------------------------------------------------------------

    private static Font titleFont(float size) {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, size, BRAND_TEXT);
    }

    private static Font netPayLabelFont(float size) {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, size, BRAND_BLUE);
    }

    private static Font netPayValueFont(float size) {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, size, BRAND_BLUE);
    }

    private static Font brandNameFont(float size) {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, size, BRAND_TEXT);
    }

    private static Font boldBodyFont(float size) {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, size, BRAND_TEXT);
    }

    private static Font bodyFont(float size) {
        return FontFactory.getFont(FontFactory.HELVETICA, size, BRAND_TEXT);
    }

    private static Font mutedFont(float size) {
        return FontFactory.getFont(FontFactory.HELVETICA, size, MUTED_TEXT);
    }

    // -------------------------------------------------------------------------
    // Value Helpers
    // -------------------------------------------------------------------------

    private String formatAmount(BigDecimal value) {
        return formatAmount(value, false);
    }

    private String formatAmount(BigDecimal value, boolean blankZero) {
        if (value == null) {
            return "0.00";
        }
        if (blankZero && value.compareTo(BigDecimal.ZERO) == 0) {
            return "";
        }
        return String.format("%,.2f", value);
    }

    private String valueOrZero(Integer value) {
        return value == null ? "0" : value.toString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }

    private String safeOrDash(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }

    private String maskAccountNumber(String account) {
        if (account == null || account.isBlank()) {
            return "-";
        }
        String trimmed = account.trim();
        int visibleLen = Math.min(4, trimmed.length());
        if (trimmed.length() <= visibleLen) {
            return trimmed;
        }
        String stars = "*".repeat(trimmed.length() - visibleLen);
        return stars + trimmed.substring(trimmed.length() - visibleLen);
    }

    // -------------------------------------------------------------------------
    // Address lines (3 lines matching the sample layout)
    // -------------------------------------------------------------------------

    private String buildAddressLine1(CompanySettings s) {
        String line1 = safe(s.getAddressLine1()).trim();
        return line1.isEmpty() ? "" : line1;
    }

    private String buildAddressLine2(CompanySettings s) {
        String line2 = safe(s.getAddressLine2()).trim();
        return line2.isEmpty() ? "" : line2;
    }

    private String buildAddressLine3(CompanySettings s) {
        String city = safe(s.getCity()).trim();
        String state = safe(s.getState()).trim();
        String postal = safe(s.getPostalCode()).trim();

        StringBuilder sb = new StringBuilder();
        if (!city.isEmpty()) sb.append(city);
        if (!state.isEmpty()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(state);
        }
        if (!postal.isEmpty()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(postal);
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Misc
    // -------------------------------------------------------------------------

    private void closeQuietly(Document document) {
        if (document != null && document.isOpen()) {
            try {
                document.close();
            } catch (Exception e) {
                log.warn("Error while closing PDF document", e);
            }
        }
    }
}
