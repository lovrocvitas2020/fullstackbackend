package com.example.fullstackcrudreact.fullstackbackend.financial;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.example.fullstackcrudreact.fullstackbackend.model.PaymentSlip;

public class PaymentSlipGenerator {

     public static void createPaymentSlipPDF(PaymentSlip slip, String outputPdf, String templatePath) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        PDImageXObject background = PDImageXObject.createFromFile(templatePath, document);
        
        contentStream.drawImage(background, 0, 0, PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight());
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        //contentStream.setNonStrokingColor(Color.);

        // Overlaying text at specific positions (adjust X, Y coordinates as needed)
        addText(contentStream, slip.getPayerName(), 100, 600);
        addText(contentStream, slip.getRecipientName(), 100, 570);
        addText(contentStream, slip.getAmount() + " " + slip.getCurrencyCode(), 100, 540);
        addText(contentStream, slip.getRecipientAccount(), 100, 510);
        addText(contentStream, slip.getPurposeCode(), 100, 480);
        
        contentStream.close();
        document.save(outputPdf);
        document.close();
    }

     private static void addText(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    public static PaymentSlip fetchPaymentSlipFromDB(int id) {
        PaymentSlip slip = null;

        // TODO write correct logic

            /* 
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fullstack", "root", "admin");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM payment_slips WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            
            if (resultSet.next()) {
                slip = new PaymentSlip(
                    resultSet.getString("payer_name"),
                    resultSet.getString("recipient_name"),
                    resultSet.getString("amount"),
                    resultSet.getString("currency_code"),
                    resultSet.getString("recipient_account"),
                    resultSet.getString("purpose_code")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */

        return slip;
    }


}
