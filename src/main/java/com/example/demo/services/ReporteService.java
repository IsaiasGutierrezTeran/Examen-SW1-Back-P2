package com.example.demo.services;

import com.example.demo.dto.ReporteRequest;
import com.example.demo.models.Reporte;
import com.example.demo.models.Tramite;
import com.example.demo.repositories.ReporteRepository;
import com.example.demo.repositories.TramiteRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private TramiteRepository tramiteRepository;

    private static final String STORAGE_DIR = "./uploads/reportes/";

    public Reporte generarReporte(ReporteRequest request, String adminId) throws Exception {
        Path dirPath = Paths.get(STORAGE_DIR);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String formato = request.getFormato() != null ? request.getFormato() : "CSV";
        String fileName = UUID.randomUUID() + "." + formato.toLowerCase();
        Path filePath = dirPath.resolve(fileName);

        List<Tramite> tramites = filtrarTramites(request.getFiltros());

        if ("CSV".equalsIgnoreCase(formato)) {
            generarCSV(tramites, filePath);
        } else if ("PDF".equalsIgnoreCase(formato)) {
            generarPDF(tramites, filePath);
        } else if ("EXCEL".equalsIgnoreCase(formato) || "XLSX".equalsIgnoreCase(formato)) {
            generarExcel(tramites, filePath);
        } else {
            throw new IllegalArgumentException("Formato no soportado: " + formato);
        }

        Reporte reporte = new Reporte();
        reporte.setGeneradoPorId(adminId);
        reporte.setTipo(request.getTipo());
        reporte.setFiltros(request.getFiltros());
        reporte.setFormato(formato);
        reporte.setUrlArchivo(filePath.toString());
        reporte.setFechaGeneracion(LocalDateTime.now());

        return reporteRepository.save(reporte);
    }

    private List<Tramite> filtrarTramites(Map<String, Object> filtros) {
        List<Tramite> base = new ArrayList<>(tramiteRepository.findAll());
        if (filtros == null) {
            return base;
        }

        Object estado = filtros.get("estado");
        if (estado instanceof String estadoStr && !estadoStr.isBlank()) {
            base.removeIf(t -> !estadoStr.equalsIgnoreCase(t.getEstadoActual()));
        }

        return base;
    }

    private void generarCSV(List<Tramite> tramites, Path filePath) throws Exception {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Estado,ClienteID,FechaInicio\n");
        for (Tramite t : tramites) {
            csv.append(t.getId()).append(",")
                    .append(t.getEstadoActual()).append(",")
                    .append(t.getClienteId()).append(",")
                    .append(t.getFechaInicio() != null ? t.getFechaInicio() : "")
                    .append("\n");
        }
        Files.writeString(filePath, csv.toString());
    }

    /** Reporte en PDF usando iText. */
    private void generarPDF(List<Tramite> tramites, Path filePath) throws Exception {
        com.itextpdf.layout.Document doc = new com.itextpdf.layout.Document(
                new com.itextpdf.kernel.pdf.PdfDocument(
                        new com.itextpdf.kernel.pdf.PdfWriter(filePath.toString())));
        try {
            doc.add(new com.itextpdf.layout.element.Paragraph("Reporte de Tramites")
                    .setFontSize(16));
            doc.add(new com.itextpdf.layout.element.Paragraph(
                    "Generado: " + LocalDateTime.now() + "  ·  Total: " + tramites.size())
                    .setFontSize(9));

            com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(
                    com.itextpdf.layout.properties.UnitValue.createPercentArray(new float[]{3, 2, 3, 3}))
                    .useAllAvailableWidth();
            for (String h : new String[]{"ID", "Estado", "Cliente ID", "Fecha Inicio"}) {
                table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                        .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY)
                        .add(new com.itextpdf.layout.element.Paragraph(h)));
            }
            for (Tramite t : tramites) {
                table.addCell(pdfCell(t.getId()));
                table.addCell(pdfCell(t.getEstadoActual()));
                table.addCell(pdfCell(t.getClienteId()));
                table.addCell(pdfCell(t.getFechaInicio() != null ? t.getFechaInicio().toString() : ""));
            }
            doc.add(table);
        } finally {
            doc.close();
        }
    }

    private com.itextpdf.layout.element.Cell pdfCell(String texto) {
        return new com.itextpdf.layout.element.Cell()
                .add(new com.itextpdf.layout.element.Paragraph(texto != null ? texto : ""));
    }

    /** Reporte en Excel (XLSX) usando Apache POI. */
    private void generarExcel(List<Tramite> tramites, Path filePath) throws Exception {
        try (Workbook wb = new XSSFWorkbook();
             OutputStream os = Files.newOutputStream(filePath)) {
            Sheet sheet = wb.createSheet("Tramites");

            Font bold = wb.createFont();
            bold.setBold(true);
            CellStyle headStyle = wb.createCellStyle();
            headStyle.setFont(bold);

            String[] cols = {"ID", "Estado", "Cliente ID", "Fecha Inicio"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headStyle);
            }

            int r = 1;
            for (Tramite t : tramites) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(t.getId() != null ? t.getId() : "");
                row.createCell(1).setCellValue(t.getEstadoActual() != null ? t.getEstadoActual() : "");
                row.createCell(2).setCellValue(t.getClienteId() != null ? t.getClienteId() : "");
                row.createCell(3).setCellValue(t.getFechaInicio() != null ? t.getFechaInicio().toString() : "");
            }
            for (int i = 0; i < cols.length; i++) {
                sheet.autoSizeColumn(i);
            }
            wb.write(os);
        }
    }

    public byte[] descargarReporte(String reporteId) throws Exception {
        Reporte reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));

        Path path = Paths.get(reporte.getUrlArchivo());
        return Files.readAllBytes(path);
    }
}
