package com.example.demo.service.iml;

import com.example.demo.model.dto.UserResponseDTO;
import com.example.demo.service.service.ExportExcelService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ExportExcelIml implements ExportExcelService {
    public void exportToExcel(List<UserResponseDTO> users, HttpServletResponse response) throws IOException {
        // Create a new workbook
        Workbook workbook = new XSSFWorkbook();
        // Create a sheet within the workbook
        Sheet sheet = workbook.createSheet("Users");

        // Create a header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Mobile Phone");
        headerRow.createCell(3).setCellValue("Email");
        headerRow.createCell(4).setCellValue("Role");

        // Populate data rows
        int rowNum = 1;
        for (UserResponseDTO user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getUserId());
            row.createCell(1).setCellValue(user.getName());
            row.createCell(2).setCellValue(user.getMobilePhone());
            row.createCell(3).setCellValue(user.getEmail());
            row.createCell(4).setCellValue(user.getRole().toString());
        }

        // Set the response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=users.xlsx");

        // Write the workbook to the response output stream
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
