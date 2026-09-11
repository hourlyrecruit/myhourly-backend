package com.my_hourly.payroll.pdf.sample;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.my_hourly.payroll.entity.Payroll;
import com.my_hourly.payroll.pdf.PayslipGenerationException;
import com.my_hourly.payroll.pdf.PayslipGenerator;
import com.my_hourly.settings.company.entity.CompanySettings;
import com.my_hourly.settings.company.repository.CompanySettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Generates the payslip PDF.
 *
 * Logo:
 * src/main/resources/payslip/hourlyrecruit-logo.png
 *
 * PDF:
 * Letter size - 612 x 792 pt
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SlipSample {

    private final CompanySettingsRepository companySettingsRepository;

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private static final float PAGE_W =
            PageSize.LETTER.getWidth();

    private static final float PAGE_H =
            PageSize.LETTER.getHeight();

    private static final float LEFT = 28f;
    private static final float RIGHT = 586f;
    private static final float TOP = 698f;

    // Box height must equal the sum of every internal row's height
    // (header 66 + info 108 + earnings/deductions header 18 + rows 120
    //  + rupees-in-words 18 + note 22 = 352). Previously this was 353f,
    // 7pt short, which pushed the Note row's border below the outer box.
    private static final float BOTTOM = 346f;

    private static final Color BLACK =
            Color.BLACK;

    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MMMM yyyy");

    /**
     * Classpath location of company logo.
     *
     * File:
     * src/main/resources/payslip/hourlyrecruit-logo.png
     */
    private static final String LOGO_RESOURCE =
            "/payslip/hourlyrecruit-logo.png";


    // -------------------------------------------------------------------------
    // Generate PDF
    // -------------------------------------------------------------------------

    /**
     * Generates a payslip PDF.
     *
     * @param payroll payroll entity
     * @return PDF as byte array
     */
    @Transactional(readOnly = true)
    public byte[] generate(Payroll payroll) {

        if (payroll == null) {

            log.error(
                    "Payslip generation failed: payroll is null"
            );

            throw new PayslipGenerationException(
                    "Payroll cannot be null"
            );
        }

        Long payrollId =
                payroll.getId();

        log.info(
                "Starting payslip generation. payrollId={}",
                payrollId
        );

        validatePayroll(payroll);

        CompanySettings settings =
                getActiveCompanySettings(payrollId);

        try (
                ByteArrayOutputStream output =
                        new ByteArrayOutputStream()
        ) {

            Document document = null;

            try {

                // -------------------------------------------------------------
                // Create document
                // -------------------------------------------------------------

                document =
                        new Document(
                                PageSize.LETTER,
                                0,
                                0,
                                0,
                                0
                        );

                PdfWriter writer =
                        PdfWriter.getInstance(
                                document,
                                output
                        );

                document.open();

                PdfContentByte canvas =
                        writer.getDirectContent();

                // -------------------------------------------------------------
                // Fonts
                // -------------------------------------------------------------

                BaseFont font =
                        createNormalFont();

                BaseFont bold =
                        createBoldFont();

                // -------------------------------------------------------------
                // Draw payslip
                // -------------------------------------------------------------

                drawLogo(canvas);

                drawPayslip(
                        canvas,
                        payroll,
                        font,
                        bold,
                        settings
                );

                drawBottomCompanyAddress(
                        canvas,
                        bold,
                        font,
                        settings
                );

                log.debug(
                        "Payslip content drawn successfully. payrollId={}",
                        payrollId
                );

            } catch (DocumentException e) {

                log.error(
                        "PDF document error. payrollId={}",
                        payrollId,
                        e
                );

                throw new PayslipGenerationException(
                        "Unable to create payslip PDF",
                        e
                );

            } catch (IOException e) {

                log.error(
                        "I/O error while generating payslip. payrollId={}",
                        payrollId,
                        e
                );

                throw new PayslipGenerationException(
                        "Unable to load payslip resources",
                        e
                );

            } catch (PayslipGenerationException e) {

                throw e;

            } catch (Exception e) {

                log.error(
                        "Unexpected error while drawing payslip. payrollId={}",
                        payrollId,
                        e
                );

                throw new PayslipGenerationException(
                        "Unexpected error while generating payslip",
                        e
                );

            } finally {

                if (document != null &&
                        document.isOpen()) {

                    try {

                        document.close();

                    } catch (Exception e) {

                        log.warn(
                                "Error while closing PDF document. payrollId={}",
                                payrollId,
                                e
                        );
                    }
                }
            }

            byte[] pdf =
                    output.toByteArray();

            if (pdf.length == 0) {

                log.error(
                        "Generated payslip PDF is empty. payrollId={}",
                        payrollId
                );

                throw new PayslipGenerationException(
                        "Generated payslip PDF is empty"
                );
            }

            log.info(
                    "Payslip generated successfully. payrollId={}, size={} bytes",
                    payrollId,
                    pdf.length
            );

            return pdf;

        } catch (PayslipGenerationException e) {

            log.error(
                    "Payslip generation failed. payrollId={}",
                    payrollId,
                    e
            );

            throw e;

        } catch (Exception e) {

            log.error(
                    "Unexpected payslip generation failure. payrollId={}",
                    payrollId,
                    e
            );

            throw new PayslipGenerationException(
                    "Failed to generate payslip PDF",
                    e
            );
        }
    }


    // -------------------------------------------------------------------------
    // Company settings
    // -------------------------------------------------------------------------

    private CompanySettings getActiveCompanySettings(
            Long payrollId) {

        try {

            return companySettingsRepository
                    .findFirstByActiveTrue()
                    .orElseThrow(() -> {

                        log.error(
                                "Active company settings not found. payrollId={}",
                                payrollId
                        );

                        return new PayslipGenerationException(
                                "Active company settings not found"
                        );
                    });

        } catch (PayslipGenerationException e) {

            throw e;

        } catch (Exception e) {

            log.error(
                    "Error while loading company settings. payrollId={}",
                    payrollId,
                    e
            );

            throw new PayslipGenerationException(
                    "Unable to load company settings",
                    e
            );
        }
    }


    // -------------------------------------------------------------------------
    // Payroll validation
    // -------------------------------------------------------------------------

    private void validatePayroll(
            Payroll payroll) {

        Long payrollId =
                payroll.getId();

        if (payroll.getPayrollMonth() == null) {

            log.error(
                    "Payroll month is null. payrollId={}",
                    payrollId
            );

            throw new PayslipGenerationException(
                    "Payroll month cannot be null"
            );
        }

        log.debug(
                "Payroll validation successful. payrollId={}",
                payrollId
        );
    }


    // -------------------------------------------------------------------------
    // Fonts
    // -------------------------------------------------------------------------

    private BaseFont createNormalFont()
            throws DocumentException, IOException {

        return BaseFont.createFont(
                BaseFont.HELVETICA,
                BaseFont.WINANSI,
                BaseFont.NOT_EMBEDDED
        );
    }


    private BaseFont createBoldFont()
            throws DocumentException, IOException {

        return BaseFont.createFont(
                BaseFont.HELVETICA_BOLD,
                BaseFont.WINANSI,
                BaseFont.NOT_EMBEDDED
        );
    }


    // -------------------------------------------------------------------------
    // Main payslip
    // -------------------------------------------------------------------------

    private void drawPayslip(
            PdfContentByte c,
            Payroll p,
            BaseFont font,
            BaseFont bold,
            CompanySettings settings) {

        try {

            final float left =
                    LEFT;

            final float right =
                    RIGHT;

            final float top =
                    TOP;

            final float bottom =
                    BOTTOM;

            final float innerX =
                    LEFT + 34f;

            final float centerVerticalX =
                    LEFT + (RIGHT - LEFT) / 2f + 3f;


            // =================================================================
            // OUTER BORDER
            // =================================================================

            line(
                    c,
                    left,
                    top,
                    right,
                    top
            );

            line(
                    c,
                    left,
                    bottom,
                    right,
                    bottom
            );

            line(
                    c,
                    left,
                    top,
                    left,
                    bottom
            );

            line(
                    c,
                    right,
                    top,
                    right,
                    bottom
            );


            // =================================================================
            // HEADER
            // =================================================================

            float y =
                    top;


            String companyName =
                    safe(settings.getCompanyName());

            String address =
                    buildAddress(
                            settings.getAddressLine1(),
                            settings.getAddressLine2()
                    );

            String city =
                    safe(settings.getCity());

            String postalCode =
                    safe(settings.getPostalCode());


            // Company name
            y =
                    horizontalRow(
                            c,
                            y,
                            17
                    );

            centerText(
                    c,
                    companyName,
                    bold,
                    9.5f,
                    centerVerticalX,
                    y + 6
            );


            // Address
            y =
                    horizontalRow(
                            c,
                            y,
                            15
                    );

            centerText(
                    c,
                    address,
                    bold,
                    8,
                    centerVerticalX,
                    y + 5
            );


            // City + Postal Code
            y =
                    horizontalRow(
                            c,
                            y,
                            15
                    );

            centerText(
                    c,
                    city + " " + postalCode,
                    bold,
                    8,
                    centerVerticalX,
                    y + 5
            );


            // Payslip month
            y =
                    horizontalRow(
                            c,
                            y,
                            19
                    );

            String payrollMonth =
                    p.getPayrollMonth()
                            .format(MONTH_FORMAT)
                            .toUpperCase();

            centerText(
                    c,
                    "PAYSLIP FOR THE MONTH OF "
                            + payrollMonth,
                    bold,
                    10,
                    centerVerticalX,
                    y + 6
            );


            // =================================================================
            // EMPLOYEE INFORMATION
            // =================================================================

            float infoTop =
                    y;

            float rowH =
                    18;


            // Six rows:
            //
            // 1. Employee Name
            // 2. EMP.ID
            // 3. UAN
            //
            // 4. Date of Joining
            // 5. Designation
            // 6. Bank A/C Details
            //

            float nameColX =
                    innerX;

            // Aligns with the EARNINGS/DEDUCTIONS divider drawn below,
            // so the middle vertical line runs continuously instead of
            // jogging sideways between the two sections.
            float nameEndX =
                    centerVerticalX;

            // Right edge of the row; the value is right-aligned a few
            // points in from here (see rightLabelValue below).
            float amountColX =
                    right;


            // -------------------------------------------------------------
            // Horizontal lines
            // -------------------------------------------------------------

            for (int i = 0; i <= 6; i++) {

                line(
                        c,
                        left,
                        infoTop - i * rowH,
                        right,
                        infoTop - i * rowH
                );
            }


            // -------------------------------------------------------------
            // Vertical divider → two columns: labels | values
            // -------------------------------------------------------------

            line(
                    c,
                    nameEndX,
                    infoTop,
                    nameEndX,
                    infoTop - 6 * rowH
            );


            float yy =
                    infoTop - rowH + 5;


            // =================================================================
            // ROW 1 - EMPLOYEE NAME
            // =================================================================

            labelValue(
                    c,
                    "Employee Name",
                    safe(p.getEmployeeName()),
                    left + 6,
                    yy,
                    bold,
                    font,
                    110f
            );

            rightLabelValue(
                    c,
                    "Total Days",
                    valueOrZero(
                            p.getTotalWorkingDays()
                    ),
                    nameEndX + 6,
                    amountColX - 6,
                    yy,
                    bold,
                    font
            );


            // =================================================================
            // ROW 2 - EMPLOYEE ID
            // =================================================================

            yy -= rowH;

            labelValue(
                    c,
                    "EMP.ID",
                    safe(p.getEmployeeCode()),
                    left + 6,
                    yy,
                    bold,
                    font,
                    110f
            );

            rightLabelValue(
                    c,
                    "Working Days",
                    valueOrZero(
                            p.getWorkedDays()
                    ),
                    nameEndX + 6,
                    amountColX - 6,
                    yy,
                    bold,
                    font
            );


            // =================================================================
            // ROW 3 - UAN
            // =================================================================

            yy -= rowH;

            labelValue(
                    c,
                    "UAN",
                    safe(p.getUanNumber()),
                    left + 6,
                    yy,
                    bold,
                    font,
                    110f
            );

            rightLabelValue(
                    c,
                    "LOP",
                    valueOrZero(
                            p.getLopDays()
                    ),
                    nameEndX + 6,
                    amountColX - 6,
                    yy,
                    bold,
                    font
            );


            // =================================================================
            // ROW 4 - DATE OF JOINING
            // =================================================================

            yy -= rowH;

            labelValue(
                    c,
                    "Date of Joining",
                    safe(p.getDateOfJoining()),
                    left + 6,
                    yy,
                    bold,
                    font,
                    110f
            );


            // =================================================================
            // ROW 5 - DESIGNATION
            // =================================================================

            yy -= rowH;

            labelValue(
                    c,
                    "Designation",
                    safe(p.getDesignationName()),
                    left + 6,
                    yy,
                    bold,
                    font,
                    110f
            );


            // =================================================================
            // ROW 6 - BANK ACCOUNT
            // =================================================================

            yy -= rowH;

            labelValue(
                    c,
                    "Bank A/C Details",
                    safe(p.getAccountNumber()),
                    left + 6,
                    yy,
                    bold,
                    font,
                    110f
            );


            // =================================================================
            // EARNINGS / DEDUCTIONS
            // =================================================================

            float edTop =
                    infoTop - 6 * rowH;

            float edHeaderH =
                    18;


            line(
                    c,
                    left,
                    edTop - edHeaderH,
                    right,
                    edTop - edHeaderH
            );


            // Vertical divider: left (company info) | right (earnings/deductions)
            line(
                    c,
                    centerVerticalX,
                    edTop,
                    centerVerticalX,
                    bottom
            );


            // Column header: EARNINGS first half | DEDUCTIONS second half
            centerText(
                    c,
                    "EARNINGS",
                    bold,
                    8,
                    (left + centerVerticalX) / 2,
                    edTop - 12
            );

            centerText(
                    c,
                    "DEDUCTIONS",
                    bold,
                    8,
                    (centerVerticalX + right) / 2,
                    edTop - 12
            );


            // =================================================================
            // TABLE COLUMNS
            // =================================================================

            float eNameX =
                    left;

            float eAmountX =
                    left + (centerVerticalX - left) - 72f;

            float eEnd =
                    centerVerticalX - 6f;


            float dNameX =
                    centerVerticalX + 4f;

            float dAmountX =
                    centerVerticalX + (right - centerVerticalX) - 60f;

            float dEnd =
                    right - 6f;


            float tableTop =
                    edTop - edHeaderH;

            float itemH =
                    20;


            // =================================================================
            // EARNINGS
            // =================================================================

            String[] earningNames = {

                    "BASIC",
                    "HRA",
                    "CONVEYANCE",
                    "SPECIAL ALLOWANCE",
                    "Relocation Bonus",
                    "TOTAL"
            };


            String[] earningValues = {

                    amount(p.getBasicSalary()),

                    amount(p.getHra()),

                    amount(p.getTravelAllowance()),

                    amount(p.getSpecialAllowance()),

                    zeroAsBlank(p.getBonus()),

                    amount(p.getGrossSalary())
            };


            // =================================================================
            // DEDUCTIONS
            // =================================================================

            String[] deductionNames = {

                    "TDS",
                    "Employee PF",
                    "Others",
                    "",
                    "TOTAL\nDEDUCTIONS",
                    "AMOUNT PAYABLE"
            };


            String[] deductionValues = {

                    amount(p.getIncomeTax()),

                    amount(p.getPf()),

                    amount(p.getOtherDeduction()),

                    "",

                    amount(p.getTotalDeduction()),

                    amount(p.getNetPayable())
            };


            // =================================================================
            // TABLE ROWS
            // =================================================================

            for (int i = 0; i < 6; i++) {

                float rowBottom =
                        tableTop -
                                (i + 1) * itemH;


                // Row line
                line(
                        c,
                        left,
                        rowBottom,
                        right,
                        rowBottom
                );


                // Earnings amount column
                line(
                        c,
                        eAmountX,
                        tableTop -
                                i * itemH,
                        eAmountX,
                        rowBottom
                );


                // Deduction amount column
                line(
                        c,
                        dAmountX,
                        tableTop -
                                i * itemH,
                        dAmountX,
                        rowBottom
                );


                BaseFont rowFont =
                        i == 5
                                ? bold
                                : font;


                float size =
                        i == 5
                                ? 9
                                : 8.5f;


                // Earnings name
                multiLineLeft(
                        c,
                        earningNames[i],
                        rowFont,
                        size,
                        eNameX + 6,
                        rowBottom + 6,
                        13
                );


                // Earnings amount
                rightText(
                        c,
                        earningValues[i],
                        rowFont,
                        size,
                        eEnd - 7,
                        rowBottom + 6
                );


                // Deduction name
                multiLineLeft(
                        c,
                        deductionNames[i],
                        rowFont,
                        size,
                        dNameX + 6,
                        rowBottom + 6,
                        11
                );


                // Deduction amount
                rightText(
                        c,
                        deductionValues[i],
                        rowFont,
                        size,
                        dEnd - 7,
                        rowBottom + 6
                );
            }


            // =================================================================
            // RUPEES IN WORDS
            // =================================================================

            float wordsY =
                    tableTop -
                            6 * itemH -
                            18;


            line(
                    c,
                    left,
                    wordsY,
                    right,
                    wordsY
            );


            leftText(
                    c,
                    "Rupees "
                            + numberToWords(
                            toLong(
                                    p.getNetPayable()
                            )
                    )
                            + " Only /-",
                    bold,
                    9,
                    left + 6,
                    wordsY + 6
            );


            // =================================================================
            // NOTE
            // =================================================================

            float noteY =
                    wordsY - 22;


            line(
                    c,
                    left,
                    noteY,
                    right,
                    noteY
            );


            leftText(
                    c,
                    "Note: This is a system generated payslip and does not require signature.",
                    font,
                    8,
                    left + 6,
                    noteY + 7
            );


        } catch (Exception e) {

            log.error(
                    "Error while drawing payslip. payrollId={}",
                    p.getId(),
                    e
            );

            throw new PayslipGenerationException(
                    "Unable to draw payslip",
                    e
            );
        }
    }


    // -------------------------------------------------------------------------
    // Logo
    // -------------------------------------------------------------------------

    private void drawLogo(
            PdfContentByte c)
            throws IOException {

        log.debug(
                "Loading payslip logo. resource={}",
                LOGO_RESOURCE
        );

        Image logo =
                loadImage(
                        LOGO_RESOURCE
                );

        /*
         * Adjust dimensions if required.
         */
        logo.scaleAbsolute(
                170,
                43
        );

        logo.setAbsolutePosition(
                25,
                PAGE_H - 67
        );

        try {

            c.addImage(logo);

        } catch (DocumentException e) {

            log.error(
                    "Unable to add company logo to PDF. resource={}",
                    LOGO_RESOURCE,
                    e
            );

            throw new PayslipGenerationException(
                    "Unable to add company logo",
                    e
            );
        }

        log.debug(
                "Payslip logo added successfully"
        );
    }


    // -------------------------------------------------------------------------
    // Load classpath image
    // -------------------------------------------------------------------------

    private Image loadImage(
            String resource)
            throws IOException {

        if (resource == null ||
                resource.isBlank()) {

            log.error(
                    "Image resource path is null or empty"
            );

            throw new IllegalArgumentException(
                    "Image resource path cannot be empty"
            );
        }


        try (
                InputStream in =
                        PayslipGenerator.class
                                .getResourceAsStream(
                                        resource
                                )
        ) {

            if (in == null) {

                log.error(
                        "Payslip image resource not found. resource={}",
                        resource
                );

                throw new PayslipGenerationException(
                        "Missing payslip image resource: "
                                + resource
                );
            }


            byte[] imageBytes =
                    in.readAllBytes();


            if (imageBytes.length == 0) {

                log.error(
                        "Payslip image resource is empty. resource={}",
                        resource
                );

                throw new PayslipGenerationException(
                        "Payslip image is empty: "
                                + resource
                );
            }


            try {

                return Image.getInstance(
                        imageBytes
                );

            } catch (Exception e) {

                log.error(
                        "Invalid image resource. resource={}",
                        resource,
                        e
                );

                throw new PayslipGenerationException(
                        "Invalid payslip image: "
                                + resource,
                        e
                );
            }

        } catch (PayslipGenerationException e) {

            throw e;

        } catch (IOException e) {

            log.error(
                    "Unable to read payslip image. resource={}",
                    resource,
                    e
            );

            throw e;

        } catch (Exception e) {

            log.error(
                    "Unexpected error while loading payslip image. resource={}",
                    resource,
                    e
            );

            throw new PayslipGenerationException(
                    "Unable to load payslip image",
                    e
            );
        }
    }


    // -------------------------------------------------------------------------
    // Bottom company address
    // -------------------------------------------------------------------------

    private void drawBottomCompanyAddress(
            PdfContentByte c,
            BaseFont bold,
            BaseFont font,
            CompanySettings settings) {

        try {

            leftText(
                    c,
                    safe(
                            settings.getCompanyName()
                    ),
                    bold,
                    9,
                    12,
                    20
            );


            String address =
                    buildAddress(
                            settings.getAddressLine1(),
                            settings.getAddressLine2()
                    );


            leftText(
                    c,
                    address,
                    font,
                    7.5f,
                    12,
                    10
            );

        } catch (Exception e) {

            log.error(
                    "Error while drawing company address",
                    e
            );

            throw new PayslipGenerationException(
                    "Unable to draw company address",
                    e
            );
        }
    }


    // -------------------------------------------------------------------------
    // Drawing helpers
    // -------------------------------------------------------------------------

    private float horizontalRow(
            PdfContentByte c,
            float top,
            float height) {

        float y =
                top - height;

        line(
                c,
                LEFT,
                y,
                RIGHT,
                y
        );

        return y;
    }


    private void line(
            PdfContentByte c,
            float x1,
            float y1,
            float x2,
            float y2) {

        c.saveState();

        try {

            c.setColorStroke(
                    BLACK
            );

            c.setLineWidth(
                    0.55f
            );

            c.moveTo(
                    x1,
                    y1
            );

            c.lineTo(
                    x2,
                    y2
            );

            c.stroke();

        } finally {

            c.restoreState();
        }
    }


    private void leftText(
            PdfContentByte c,
            String text,
            BaseFont font,
            float size,
            float x,
            float y) {

        c.beginText();

        try {

            c.setFontAndSize(
                    font,
                    size
            );

            c.setColorFill(
                    BLACK
            );

            c.setTextMatrix(
                    x,
                    y
            );

            c.showText(
                    text == null
                            ? ""
                            : text
            );

        } finally {

            c.endText();
        }
    }


    private void centerText(
            PdfContentByte c,
            String text,
            BaseFont font,
            float size,
            float x,
            float y) {

        c.beginText();

        try {

            c.setFontAndSize(
                    font,
                    size
            );

            c.setColorFill(
                    BLACK
            );

            c.showTextAligned(
                    PdfContentByte.ALIGN_CENTER,
                    text == null
                            ? ""
                            : text,
                    x,
                    y,
                    0
            );

        } finally {

            c.endText();
        }
    }


    private void rightText(
            PdfContentByte c,
            String text,
            BaseFont font,
            float size,
            float x,
            float y) {

        c.beginText();

        try {

            c.setFontAndSize(
                    font,
                    size
            );

            c.setColorFill(
                    BLACK
            );

            c.showTextAligned(
                    PdfContentByte.ALIGN_RIGHT,
                    text == null
                            ? ""
                            : text,
                    x,
                    y,
                    0
            );

        } finally {

            c.endText();
        }
    }


    private void multiLineLeft(
            PdfContentByte c,
            String text,
            BaseFont font,
            float size,
            float x,
            float y,
            float leading) {

        if (text == null ||
                text.isBlank()) {

            return;
        }


        String[] lines =
                text.split("\\n");


        for (int i = 0;
             i < lines.length;
             i++) {

            leftText(
                    c,
                    lines[i],
                    font,
                    size,
                    x,
                    y +
                            (
                                    lines.length -
                                            1 -
                                            i
                            ) * leading
            );
        }
    }


    private void labelValue(
            PdfContentByte c,
            String label,
            String value,
            float x,
            float y,
            BaseFont bold,
            BaseFont normal,
            float labelWidth) {

        leftText(
                c,
                safe(label),
                bold,
                8.2f,
                x,
                y
        );

        leftText(
                c,
                safe(value),
                normal,
                8.2f,
                x + labelWidth,
                y
        );
    }


    private void rightLabelValue(
            PdfContentByte c,
            String label,
            String value,
            float labelLeft,
            float valueRight,
            float y,
            BaseFont bold,
            BaseFont normal) {

        leftText(
                c,
                safe(label),
                bold,
                8.2f,
                labelLeft,
                y
        );

        rightText(
                c,
                safe(value),
                normal,
                8.2f,
                valueRight,
                y
        );}


    // -------------------------------------------------------------------------
    // Value helpers
    // -------------------------------------------------------------------------

    private String amount(
            BigDecimal value) {

        if (value == null) {
            return "0";
        }

        try {

            return String.format(
                    "%,.0f",
                    value
            );

        } catch (Exception e) {

            log.warn(
                    "Unable to format payroll amount",
                    e
            );

            return "0";
        }
    }


    private String zeroAsBlank(
            BigDecimal value) {

        if (value == null ||
                value.compareTo(
                        BigDecimal.ZERO
                ) == 0) {

            return "";
        }

        return amount(value);
    }


    private String valueOrZero(
            Integer value) {

        return value == null
                ? "0"
                : value.toString();
    }


    private String safe(
            String value) {

        return value == null
                ? ""
                : value;
    }


    private String safe(
            Object value) {

        return value == null
                ? ""
                : value.toString();
    }


    private long toLong(
            BigDecimal value) {

        return value == null
                ? 0L
                : value.longValue();
    }


    private String buildAddress(
            String address1,
            String address2) {

        String first =
                safe(address1)
                        .trim();

        String second =
                safe(address2)
                        .trim();


        if (first.isEmpty()) {
            return second;
        }


        if (second.isEmpty()) {
            return first;
        }


        return first +
                ", " +
                second;
    }


    // -------------------------------------------------------------------------
    // Number to words
    // -------------------------------------------------------------------------

    private String numberToWords(
            long number) {

        if (number == 0) {
            return "Zero";
        }


        if (number < 0) {

            return "Minus " +
                    numberToWords(
                            -number
                    );
        }


        StringBuilder result =
                new StringBuilder();


        // Crore
        if (number / 10000000 > 0) {

            result.append(
                            numberToWords(
                                    number / 10000000
                            )
                    )
                    .append(
                            " Crore "
                    );

            number %= 10000000;
        }


        // Lakh
        if (number / 100000 > 0) {

            result.append(
                            numberToWords(
                                    number / 100000
                            )
                    )
                    .append(
                            " Lakh "
                    );

            number %= 100000;
        }


        // Thousand
        if (number / 1000 > 0) {

            result.append(
                            numberToWords(
                                    number / 1000
                            )
                    )
                    .append(
                            " Thousand "
                    );

            number %= 1000;
        }


        // Hundred
        if (number / 100 > 0) {

            result.append(
                            numberToWords(
                                    number / 100
                            )
                    )
                    .append(
                            " Hundred "
                    );

            number %= 100;
        }


        if (number > 0) {

            if (result.length() > 0) {

                result.append(
                        "and "
                );
            }


            result.append(
                    twoDigitWords(
                            (int) number
                    )
            );
        }


        return result
                .toString()
                .trim()
                .replaceAll(
                        "\\s+",
                        " "
                );
    }


    private String twoDigitWords(
            int number) {

        String[] ones = {

                "",
                "One",
                "Two",
                "Three",
                "Four",
                "Five",
                "Six",
                "Seven",
                "Eight",
                "Nine",
                "Ten",
                "Eleven",
                "Twelve",
                "Thirteen",
                "Fourteen",
                "Fifteen",
                "Sixteen",
                "Seventeen",
                "Eighteen",
                "Nineteen"
        };


        String[] tens = {

                "",
                "",
                "Twenty",
                "Thirty",
                "Forty",
                "Fifty",
                "Sixty",
                "Seventy",
                "Eighty",
                "Ninety"
        };


        if (number < 20) {

            return ones[number];
        }


        return tens[number / 10] +
                (
                        number % 10 == 0
                                ? ""
                                : " " +
                                  ones[
                                  number % 10
                                  ]
                );
    }
}