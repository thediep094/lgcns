package com.example.demo.service.service;

import com.example.demo.model.dto.UserResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface ExportExcelService {
    void exportToExcel(Page<UserResponseDTO> users, HttpServletResponse response) throws IOException;
}
