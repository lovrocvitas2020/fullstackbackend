package com.example.fullstackcrudreact.fullstackbackend.batch.batch1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

public class WorklogWriter implements ItemWriter<Worklog>, ItemStream {


    private Document document;
    private PdfDocument pdfDoc;
    private static final DateTimeFormatter DATE_FORMAT = 
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    public void write(Chunk<? extends Worklog> chunk) throws Exception {
        for (Worklog worklog : chunk) {
            addWorklogEntry(worklog);
        }
    }

    // Keep the rest of the implementation the same as before
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            String dest = "worklog_report_" + timestamp + ".pdf";
            pdfDoc = new PdfDocument(new PdfWriter(dest));
            document = new Document(pdfDoc);
            addHeader();
        } catch (Exception e) {
            throw new ItemStreamException("Error opening PDF document", e);
        }
    }

    private void addHeader() {
        Paragraph header = new Paragraph("Worklog Report")
            .setTextAlignment(TextAlignment.CENTER)
            .setBold()
            .setFontSize(16);
        document.add(header);
        document.add(new Paragraph("\n"));
    }

    private void addWorklogEntry(Worklog worklog) {
    // Format date and time
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    // Header with ID
    Paragraph header = new Paragraph("Worklog Entry #" + worklog.getId())
        .setBold()
        .setFontSize(14)
        .setMarginBottom(5f);
    document.add(header);

    // Create a 2-column table for better layout
    float[] columnWidths = {1, 4};
    Table table = new Table(columnWidths);
    
    // Add rows with information
    addTableRow(table, "User Name:", worklog.getName());
    addTableRow(table, "Date:", worklog.getWorkDate().format(dateFormatter));
    addTableRow(table, "Start Time:", worklog.getStartHour().format(timeFormatter));
    addTableRow(table, "End Time:", worklog.getEndHour().format(timeFormatter));
    addTableRow(table, "Total Hours:", worklog.getHourSum() + " hours");
    
    // Description with wrapping
    String description = worklog.getWorkDescription() != null && !worklog.getWorkDescription().isEmpty()
        ? worklog.getWorkDescription()
        : "No description provided";
    
    Cell descCell = new Cell(1, 2)
        .add(new Paragraph("Description:")
            .setBold()
            .setMarginBottom(3f))
        .add(new Paragraph(description)
            .setMarginBottom(5f));
    table.addCell(descCell);

    document.add(table);
    
    // Add separator line
    document.add(new Paragraph("\n")
        .setBorderBottom(new SolidBorder(1f))
        .setMarginBottom(10f));
}

private void addTableRow(Table table, String label, String value) {
    table.addCell(new Cell().add(new Paragraph(label).setBold()));
    table.addCell(new Cell().add(new Paragraph(value)));
}

    @Override
    public void close() throws ItemStreamException {
        try {
            if (document != null) {
                document.close();
            }
            if (pdfDoc != null) {
                pdfDoc.close();
            }
        } catch (Exception e) {
            throw new ItemStreamException("Error closing PDF document", e);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // Not needed for this implementation
    }



}
