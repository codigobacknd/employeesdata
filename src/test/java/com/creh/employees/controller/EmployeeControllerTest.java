package com.creh.employees.controller;

import com.creh.employees.model.Employee;
import com.creh.employees.service.EmployeeService;
import com.creh.employees.service.FileService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private FileService fileService;

    private List<Employee> employeeList;

    @BeforeEach
    public void setUp() {
        Employee employee1 = new Employee(1L, "Alejandro", "Villamizar", "villamizar@gmail.com", 1234567890L, new HashSet<>());
        Employee employee2 = new Employee(2L, "Maria", "Burgos", "mburgos@gmail.com", 1234567891L, new HashSet<>());
        employeeList = Arrays.asList(employee1, employee2);
    }

    private MockMultipartFile createMockExcelFile() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Nombre");
        header.createCell(1).setCellValue("Apellido");
        header.createCell(2).setCellValue("Correo");
        header.createCell(3).setCellValue("Phone");
        header.createCell(4).setCellValue("Roles");

        Object[][] data = {
                {"Alejandro", "Villamizar", "villamizar@gmail.com", 3045461464L, "admin, user"},
                {"María", "Burgos", "mburgos@gmail.com", 3045461465L, "client, user"},
                {"Maya", "Villamizar", "mvillamizar@gmail.com", 3045461466L, "client, user"},
                {"Mia", "Burgos", "miaburgos@gmail.com", 3045461467L, "admin, client"}
        };

        int rowCount = 1;
        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowCount++);
            int colCount = 0;
            for (Object field : rowData) {
                Cell cell = row.createCell(colCount++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Long) {
                    cell.setCellValue((Long) field);
                }
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return new MockMultipartFile("file", "employees.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", inputStream);
    }

    @Test
    public void testGetAllEmployees() throws Exception {
        Mockito.when(employeeService.findAll()).thenReturn(employeeList);

        mockMvc.perform(get("/api/employees/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Employees retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Alejandro"))
                .andExpect(jsonPath("$.data[1].name").value("Maria"));
    }

    @Test
    public void testUploadFile() throws Exception {
        MockMultipartFile file = createMockExcelFile();

        Mockito.doNothing().when(fileService).uploadFile(any(MultipartFile.class));

        mockMvc.perform(multipart("/api/employees/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("File loaded successfully"));
    }

    @Test
    public void testUploadFileWithException() throws Exception {
        MockMultipartFile file = createMockExcelFile();

        Mockito.doThrow(new IOException("File processing error")).when(fileService).uploadFile(any(MultipartFile.class));

        mockMvc.perform(multipart("/api/employees/upload").file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("File processing error"))
                .andExpect(jsonPath("$.detailResponse.type").value("IOException"))
                .andExpect(jsonPath("$.detailResponse.details").value("File processing error"));
    }
}