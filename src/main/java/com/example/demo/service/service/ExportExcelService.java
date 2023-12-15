package com.example.demo.service.service;

import com.example.demo.model.dto.UserResponseDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface ExportExcelService {
    void exportToExcel(List<UserResponseDTO> users, HttpServletResponse response) throws IOException;
}
