package com.example.fullstackcrudreact.fullstackbackend.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.fullstackcrudreact.fullstackbackend.model.User;
import com.example.fullstackcrudreact.fullstackbackend.repository.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

     @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public byte[] exportUsersToExcel() throws IOException {
        // Fetch all users from the repository
        List<User> users = userRepository.findAll();

        // Create a new Workbook and Sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Username", "Name", "Email", "Created On", "Updated On"};
        
        // Adding headers to the first row
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Fill sheet with user data
        int rowNum = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getUsername());
            row.createCell(2).setCellValue(user.getName());
            row.createCell(3).setCellValue(user.getEmail());

            // Check if `createdOn` is null before calling `.toString()`
        if (user.getCreatedOn() != null) {
            row.createCell(4).setCellValue(user.getCreatedOn().toString());
        } else {
            row.createCell(4).setCellValue("N/A"); 
        }

        // Check if `createdOn` is null before calling `.toString()`
        if (user.getUpdatedOn() != null) {
            row.createCell(5).setCellValue(user.getUpdatedOn().toString());
        } else {
            row.createCell(5).setCellValue("N/A"); //
        }

          
        }

        // Auto-size columns for all content
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to byte array and return
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        }
    }


}
