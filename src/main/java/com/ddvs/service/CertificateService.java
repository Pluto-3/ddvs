package com.ddvs.service;

import com.ddvs.entity.Document;
import com.ddvs.repository.DocumentRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final DocumentRepository documentRepository;

    public byte[] generateCertificate(String verificationCode) throws Exception {
        Document doc = documentRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        com.itextpdf.text.Document pdf = new com.itextpdf.text.Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(pdf, outputStream);
        pdf.open();

        PdfContentByte canvas = writer.getDirectContent();

        canvas.setColorFill(new BaseColor(245, 247, 250));
        canvas.rectangle(0, 0, pdf.getPageSize().getWidth(), pdf.getPageSize().getHeight());
        canvas.fill();

        canvas.setColorStroke(new BaseColor(26, 60, 110));
        canvas.setLineWidth(4f);
        canvas.rectangle(20, 20, pdf.getPageSize().getWidth() - 40, pdf.getPageSize().getHeight() - 40);
        canvas.stroke();

        canvas.setColorStroke(new BaseColor(46, 95, 163));
        canvas.setLineWidth(1.5f);
        canvas.rectangle(28, 28, pdf.getPageSize().getWidth() - 56, pdf.getPageSize().getHeight() - 56);
        canvas.stroke();

        float pageWidth = pdf.getPageSize().getWidth();
        float pageHeight = pdf.getPageSize().getHeight();

        canvas.setColorFill(new BaseColor(26, 60, 110));
        canvas.rectangle(20, pageHeight - 100, pageWidth - 40, 80);
        canvas.fill();

        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("DIGITAL DOCUMENT VERIFICATION SYSTEM",
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE)),
                pageWidth / 2, pageHeight - 55, 0);

        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("OFFICIAL CERTIFICATE",
                        FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.WHITE)),
                pageWidth / 2, pageHeight - 78, 0);

        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("CERTIFICATE OF " + doc.getDocumentType().toUpperCase(),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new BaseColor(26, 60, 110))),
                pageWidth / 2, pageHeight - 150, 0);

        canvas.setColorStroke(new BaseColor(46, 95, 163));
        canvas.setLineWidth(1f);
        canvas.moveTo(80, pageHeight - 165);
        canvas.lineTo(pageWidth - 80, pageHeight - 165);
        canvas.stroke();

        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("This is to certify that",
                        FontFactory.getFont(FontFactory.HELVETICA, 13, new BaseColor(100, 100, 100))),
                pageWidth / 2, pageHeight - 200, 0);

        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase(doc.getOwnerName().toUpperCase(),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, new BaseColor(26, 60, 110))),
                pageWidth / 2, pageHeight - 240, 0);

        canvas.setColorStroke(new BaseColor(26, 60, 110));
        canvas.setLineWidth(0.8f);
        canvas.moveTo(pageWidth / 2 - 160, pageHeight - 248);
        canvas.lineTo(pageWidth / 2 + 160, pageHeight - 248);
        canvas.stroke();

        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("has been issued the following document:",
                        FontFactory.getFont(FontFactory.HELVETICA, 13, new BaseColor(100, 100, 100))),
                pageWidth / 2, pageHeight - 275, 0);

        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase(doc.getTitle(),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(46, 95, 163))),
                pageWidth / 2, pageHeight - 305, 0);

        float leftX = 100;
        float rightX = pageWidth / 2 + 40;
        float detailY = pageHeight - 355;
        float lineSpacing = 30;

        drawDetail(canvas, "Issued By:", doc.getIssuer().getName(), leftX, detailY);
        drawDetail(canvas, "Issued Date:", doc.getIssuedDate().toString(), leftX, detailY - lineSpacing);
        drawDetail(canvas, "Expiration Date:", doc.getExpirationDate() != null ? doc.getExpirationDate().toString() : "No Expiration", leftX, detailY - lineSpacing * 2);
        drawDetail(canvas, "Status:", doc.getStatus().name(), leftX, detailY - lineSpacing * 3);
        drawDetail(canvas, "Verification Code:", doc.getVerificationCode(), leftX, detailY - lineSpacing * 4);

        // QR Code
        String verificationUrl = "https://verify.gov.tz/" + doc.getVerificationCode();
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrWriter.encode(
                verificationUrl,
                BarcodeFormat.QR_CODE,
                250, 250,
                Map.of(EncodeHintType.MARGIN, 1)
        );
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream qrStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", qrStream);
        byte[] qrImageBytes = qrStream.toByteArray();

        Image qrImage = Image.getInstance(qrImageBytes);
        qrImage.scaleToFit(110, 110);
        qrImage.setAbsolutePosition(rightX + 60, pageHeight - 470);
        canvas.addImage(qrImage);

        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("Scan to verify",
                        FontFactory.getFont(FontFactory.HELVETICA, 9, new BaseColor(120, 120, 120))),
                rightX + 115, pageHeight - 480, 0);

        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase(verificationUrl,
                        FontFactory.getFont(FontFactory.HELVETICA, 8, new BaseColor(120, 120, 120))),
                rightX + 115, pageHeight - 493, 0);

        canvas.setColorFill(new BaseColor(26, 60, 110));
        canvas.rectangle(20, 20, pageWidth - 40, 42);
        canvas.fill();

        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("This document is digitally verifiable. Visit verify.gov.tz to confirm authenticity.",
                        FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.WHITE)),
                pageWidth / 2, 37, 0);

        pdf.close();
        return outputStream.toByteArray();
    }

    private void drawDetail(PdfContentByte canvas, String label, String value, float x, float y) {
        try {
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                    new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new BaseColor(80, 80, 80))),
                    x, y, 0);
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                    new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA, 11, new BaseColor(26, 60, 110))),
                    x + 140, y, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}