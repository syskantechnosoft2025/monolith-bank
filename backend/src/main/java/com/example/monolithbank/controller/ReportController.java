package com.example.monolithbank.controller;

import com.example.monolithbank.domain.Transaction;
import com.example.monolithbank.domain.TransactionType;
import com.example.monolithbank.service.TransactionService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final TransactionService transactionService;

    @Autowired
    public ReportController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/excel")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<byte[]> transactionExcel(@RequestParam(required = false) LocalDateTime start,
                                                   @RequestParam(required = false) LocalDateTime end,
                                                   @RequestParam(required = false) String type,
                                                   @RequestParam(required = false) Boolean debit) throws Exception {
        TransactionType transactionType = type == null ? null : TransactionType.valueOf(type.toUpperCase());
        List<Transaction> transactions = transactionService.searchTransactions(start, end, transactionType, debit);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("transactions");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Account");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Amount");
            header.createCell(4).setCellValue("Debit");
            header.createCell(5).setCellValue("Date");
            header.createCell(6).setCellValue("Remarks");

            int rowIdx = 1;
            for (Transaction tr : transactions) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(tr.getId());
                row.createCell(1).setCellValue(tr.getAccount().getAccountNumber());
                row.createCell(2).setCellValue(tr.getTransactionType().name());
                row.createCell(3).setCellValue(tr.getAmount().doubleValue());
                row.createCell(4).setCellValue(tr.isDebit());
                row.createCell(5).setCellValue(tr.getTransactionDate().toString());
                row.createCell(6).setCellValue(tr.getRemarks());
            }
            workbook.write(out);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        }
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<byte[]> transactionPdf(@RequestParam(required = false) LocalDateTime start,
                                                 @RequestParam(required = false) LocalDateTime end,
                                                 @RequestParam(required = false) String type,
                                                 @RequestParam(required = false) Boolean debit) throws Exception {
        TransactionType transactionType = type == null ? null : TransactionType.valueOf(type.toUpperCase());
        List<Transaction> transactions = transactionService.searchTransactions(start, end, transactionType, debit);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            document.add(new Paragraph("Transaction Report"));
            for (Transaction tr : transactions) {
                document.add(new Paragraph(String.format("%d: %s %s %s %s %s", tr.getId(), tr.getAccount().getAccountNumber(), tr.getTransactionType(), tr.getAmount(), tr.isDebit() ? "debit" : "credit", tr.getTransactionDate())));
            }
            document.close();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(out.toByteArray());
        }
    }
}
