package student.result.grade.management.system.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import student.result.grade.management.system.repository.DatabaseConnection;

public class JasperReportService {
    private final Path batchReport = Path.of("reports", "batch_performance_summary.jrxml");
    private final Path studentReport = Path.of("reports", "individual_student_result.jrxml");

    public Path generateBatchHtml(Path outputFile) {
        return generateHtml(batchReport, Map.of(), outputFile);
    }

    public Path generateBatchPdf(Path outputFile) {
        return generatePdf(batchReport, Map.of(), outputFile);
    }

    public Path generateStudentHtml(String studentId, Path outputFile) {
        Map<String, Object> params = new HashMap<>();
        params.put("STUDENT_ID", studentId);
        return generateHtml(studentReport, params, outputFile);
    }

    public Path generateStudentPdf(String studentId, Path outputFile) {
        Map<String, Object> params = new HashMap<>();
        params.put("STUDENT_ID", studentId);
        return generatePdf(studentReport, params, outputFile);
    }

    private Path generateHtml(Path reportSource, Map<String, Object> parameters, Path outputFile) {
        ensureParentDirectory(outputFile);
        try (Connection connection = DatabaseConnection.open()) {
            JasperReport report = JasperCompileManager.compileReport(reportSource.toString());
            JasperPrint print = JasperFillManager.fillReport(report, parameters, connection);
            JasperExportManager.exportReportToHtmlFile(print, outputFile.toString());
            return outputFile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Jasper report: " + e.getMessage(), e);
        }
    }

    private Path generatePdf(Path reportSource, Map<String, Object> parameters, Path outputFile) {
        ensureParentDirectory(outputFile);
        try (Connection connection = DatabaseConnection.open()) {
            JasperReport report = JasperCompileManager.compileReport(reportSource.toString());
            JasperPrint print = JasperFillManager.fillReport(report, parameters, connection);
            JasperExportManager.exportReportToPdfFile(print, outputFile.toString());
            return outputFile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Jasper report: " + e.getMessage(), e);
        }
    }

    private void ensureParentDirectory(Path outputFile) {
        Path parent = outputFile.toAbsolutePath().getParent();
        if (parent == null) {
            return;
        }
        try {
            Files.createDirectories(parent);
        } catch (IOException e) {
            throw new RuntimeException("Could not create report directory: " + parent, e);
        }
    }
}