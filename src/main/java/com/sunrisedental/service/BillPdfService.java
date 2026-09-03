package com.sunrisedental.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.sunrisedental.dto.BillResponse;
import com.sunrisedental.dto.PaymentResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BillPdfService {

    public byte[] generateBillPdf(BillResponse bill, List<PaymentResponse> payments) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Fonts
            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, new java.awt.Color(0, 82, 155));
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, new java.awt.Color(51, 51, 51));
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL, new java.awt.Color(80, 80, 80));
            Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD, new java.awt.Color(51, 51, 51));
            Font totalFont = new Font(Font.HELVETICA, 12, Font.BOLD, new java.awt.Color(180, 0, 0));

            // ========== CLINIC HEADER ==========
            Paragraph clinicName = new Paragraph("SUNRISE DENTAL CLINIC", titleFont);
            clinicName.setAlignment(Element.ALIGN_CENTER);
            document.add(clinicName);

            Paragraph clinicAddr = new Paragraph("123 Dental Avenue, Colombo, Sri Lanka", normalFont);
            clinicAddr.setAlignment(Element.ALIGN_CENTER);
            document.add(clinicAddr);

            Paragraph clinicContact = new Paragraph("Tel: +94 11 234 5678  |  info@sunrisedental.lk", normalFont);
            clinicContact.setAlignment(Element.ALIGN_CENTER);
            document.add(clinicContact);

            document.add(Chunk.NEWLINE);

            // ========== BILL TITLE ==========
            Paragraph billTitle = new Paragraph("BILL / INVOICE", new Font(Font.HELVETICA, 16, Font.BOLD, new java.awt.Color(0, 82, 155)));
            billTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(billTitle);
            document.add(Chunk.NEWLINE);

            // ========== BILL INFO ==========
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 1});

            addInfoRow(infoTable, "Bill Number:", bill.getBillNumber(), boldFont, normalFont);
            addInfoRow(infoTable, "Date:", formatDateTime(bill.getCreatedAt()), boldFont, normalFont);
            addInfoRow(infoTable, "Status:", bill.getStatus() != null ? bill.getStatus().toString() : "-", boldFont, normalFont);
            addInfoRow(infoTable, "Appointment:", bill.getAppointmentNumber() != null ? bill.getAppointmentNumber() : "-", boldFont, normalFont);

            document.add(infoTable);
            document.add(Chunk.NEWLINE);

            // ========== PATIENT INFO ==========
            Paragraph patientHeader = new Paragraph("Patient Information", headerFont);
            document.add(patientHeader);
            document.add(Chunk.NEWLINE);

            PdfPTable patientTable = new PdfPTable(2);
            patientTable.setWidthPercentage(100);
            patientTable.setWidths(new float[]{1, 1});

            addInfoRow(patientTable, "Patient Name:", bill.getPatientName(), boldFont, normalFont);
            addInfoRow(patientTable, "Patient ID:", String.valueOf(bill.getPatientId()), boldFont, normalFont);

            document.add(patientTable);
            document.add(Chunk.NEWLINE);

            // ========== TREATMENT DETAILS ==========
            Paragraph treatmentHeader = new Paragraph("Treatment Details", headerFont);
            document.add(treatmentHeader);
            document.add(Chunk.NEWLINE);

            PdfPTable treatmentTable = new PdfPTable(2);
            treatmentTable.setWidthPercentage(100);
            treatmentTable.setWidths(new float[]{1, 1});

            addInfoRow(treatmentTable, "Treatment:", bill.getTreatmentName(), boldFont, normalFont);
            addInfoRow(treatmentTable, "Treatment Amount:", "LKR " + formatMoney(bill.getTreatmentAmount()), boldFont, normalFont);
            addInfoRow(treatmentTable, "Consultation Fee:", "LKR " + formatMoney(bill.getConsultationAmount()), boldFont, normalFont);

            document.add(treatmentTable);
            document.add(Chunk.NEWLINE);

            // ========== FINANCIAL SUMMARY ==========
            Paragraph financialHeader = new Paragraph("Financial Summary", headerFont);
            document.add(financialHeader);
            document.add(Chunk.NEWLINE);

            PdfPTable financialTable = new PdfPTable(2);
            financialTable.setWidthPercentage(60);
            financialTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            financialTable.setWidths(new float[]{2, 1});

            addMoneyRow(financialTable, "Treatment Amount:", bill.getTreatmentAmount(), normalFont, false);
            addMoneyRow(financialTable, "Consultation Amount:", bill.getConsultationAmount(), normalFont, false);
            addMoneyRow(financialTable, "Total Amount:", bill.getTotalAmount(), boldFont, false);
            addMoneyRow(financialTable, "Paid Amount:", bill.getPaidAmount(), normalFont, false);
            addMoneyRow(financialTable, "Remaining Balance:", bill.getRemainingAmount(), totalFont, true);

            document.add(financialTable);
            document.add(Chunk.NEWLINE);

            // ========== PAYMENT HISTORY ==========
            if (payments != null && !payments.isEmpty()) {
                Paragraph paymentHeader = new Paragraph("Payment History", headerFont);
                document.add(paymentHeader);
                document.add(Chunk.NEWLINE);

                PdfPTable paymentTable = new PdfPTable(4);
                paymentTable.setWidthPercentage(100);
                paymentTable.setWidths(new float[]{1.5f, 2.5f, 2f, 1.5f});

                addTableHeader(paymentTable, "Payment #", boldFont);
                addTableHeader(paymentTable, "Date", boldFont);
                addTableHeader(paymentTable, "Method", boldFont);
                addTableHeader(paymentTable, "Amount", boldFont);

                for (PaymentResponse p : payments) {
                    addTableCell(paymentTable, p.getPaymentNumber(), normalFont, Element.ALIGN_CENTER);
                    addTableCell(paymentTable, formatDateTime(p.getPaymentDate()), normalFont, Element.ALIGN_CENTER);
                    addTableCell(paymentTable, p.getPaymentMethod() != null ? p.getPaymentMethod().toString() : "-", normalFont, Element.ALIGN_CENTER);
                    addTableCell(paymentTable, "LKR " + formatMoney(p.getAmount()), normalFont, Element.ALIGN_RIGHT);
                }

                document.add(paymentTable);
                document.add(Chunk.NEWLINE);
            }

            // ========== FOOTER ==========
            Paragraph footer = new Paragraph("Thank you for choosing Sunrise Dental Clinic!", new Font(Font.HELVETICA, 10, Font.ITALIC, new java.awt.Color(120, 120, 120)));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            Paragraph footer2 = new Paragraph("This is a computer-generated bill and does not require a signature.", new Font(Font.HELVETICA, 8, Font.NORMAL, new java.awt.Color(150, 150, 150)));
            footer2.setAlignment(Element.ALIGN_CENTER);
            document.add(footer2);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    // ========== HELPERS ==========

    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void addMoneyRow(PdfPTable table, String label, BigDecimal amount, Font font, boolean highlight) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(6);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        if (highlight) {
            labelCell.setBackgroundColor(new java.awt.Color(255, 245, 245));
        }
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase("LKR " + formatMoney(amount), font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(6);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        if (highlight) {
            valueCell.setBackgroundColor(new java.awt.Color(255, 245, 245));
        }
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new java.awt.Color(240, 240, 240));
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0.00";
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatDateTime(java.time.LocalDateTime dt) {
        if (dt == null) return "-";
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}