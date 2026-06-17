package student.result.grade.management.system.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import student.result.grade.management.system.model.CourseModule;
import student.result.grade.management.system.model.Enrollment;
import student.result.grade.management.system.model.GradeRule;
import student.result.grade.management.system.model.Lecturer;
import student.result.grade.management.system.model.Result;
import student.result.grade.management.system.model.ResultRow;
import student.result.grade.management.system.model.ResultStatus;
import student.result.grade.management.system.model.Student;
import student.result.grade.management.system.repository.DataStore;

public class ResultService {
    private final DataStore store;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public ResultService(DataStore store) {
        this.store = store;
    }

    public Result recordResult(int enrollmentId, double coursework, double midterm, double finalExam) {
        validateMark(coursework, "Coursework");
        validateMark(midterm, "Mid-term");
        validateMark(finalExam, "Final exam");
        double total = coursework * 0.30 + midterm * 0.20 + finalExam * 0.50;
        GradeRule rule = store.getGradeRules().stream()
                .filter(r -> r.matches(total))
                .findFirst()
                .orElseThrow(() -> new ValidationException("No grade rule found for total mark."));
        ResultStatus status = rule.isPass() ? ResultStatus.PASS : ResultStatus.REPEAT;
        Result result = new Result(enrollmentId, coursework, midterm, finalExam, total,
                rule.getLetter(), rule.getGradePoint(), status);
        store.saveResult(result);
        return result;
    }

    public double calculateGpa(String studentId) {
        return calculateCgpa(studentId);
    }

    public double calculateSemesterGpa(String studentId, int semester, int academicYear) {
        double weightedPoints = 0;
        int totalCredits = 0;
        for (ResultRow row : store.getResultRows()) {
            if (row.getStudent().getId().equals(studentId)
                    && row.getEnrollment().getSemester() == semester
                    && row.getEnrollment().getAcademicYear() == academicYear
                    && row.getResult() != null) {
                weightedPoints += row.getResult().getGradePoint() * row.getModule().getCreditHours();
                totalCredits += row.getModule().getCreditHours();
            }
        }
        return totalCredits == 0 ? 0 : weightedPoints / totalCredits;
    }

    public double calculateCgpa(String studentId) {
        double weightedPoints = 0;
        int totalCredits = 0;
        for (ResultRow row : store.getResultRows()) {
            if (row.getStudent().getId().equals(studentId) && row.getResult() != null) {
                weightedPoints += row.getResult().getGradePoint() * row.getModule().getCreditHours();
                totalCredits += row.getModule().getCreditHours();
            }
        }
        return totalCredits == 0 ? 0 : weightedPoints / totalCredits;
    }

    public double latestSemesterGpa(String studentId) {
        int latestYear = Integer.MIN_VALUE;
        int latestSemester = Integer.MIN_VALUE;
        for (ResultRow row : store.getResultRows()) {
            if (row.getStudent().getId().equals(studentId) && row.getResult() != null) {
                int year = row.getEnrollment().getAcademicYear();
                int semester = row.getEnrollment().getSemester();
                if (year > latestYear || (year == latestYear && semester > latestSemester)) {
                    latestYear = year;
                    latestSemester = semester;
                }
            }
        }
        if (latestYear == Integer.MIN_VALUE) {
            return 0;
        }
        return calculateSemesterGpa(studentId, latestSemester, latestYear);
    }

    public boolean isAtRisk(String studentId) {
        List<ResultRow> completed = store.getResultRows().stream()
                .filter(row -> row.getStudent().getId().equals(studentId) && row.getResult() != null)
                .toList();
        if (completed.isEmpty()) {
            return false;
        }
        boolean failed = completed.stream().anyMatch(row -> row.getResult().getStatus() != ResultStatus.PASS);
        return failed || calculateCgpa(studentId) < 2.0;
    }

    public List<Student> getAtRiskStudents() {
        return store.getStudents().stream().filter(s -> isAtRisk(s.getId())).toList();
    }

    public void validateStudentInput(String id, String name, String dob, String pathway, String batchYear) {
        requireText(id, "Student ID");
        requireText(name, "Full name");
        requireText(pathway, "Pathway");
        parseDate(dob);
        parseInt(batchYear, "Batch year");
    }

    public Student buildStudent(String id, String name, String dob, String pathway, String batchYear) {
        validateStudentInput(id, name, dob, pathway, batchYear);
        return new Student(id.trim(), name.trim(), parseDate(dob), pathway.trim(), parseInt(batchYear, "Batch year"));
    }

    public CourseModule buildModule(String code, String name, String credits, String semester, String year) {
        requireText(code, "Module code");
        requireText(name, "Module name");
        int creditValue = parseInt(credits, "Credit hours");
        int semesterValue = parseInt(semester, "Semester");
        int yearValue = parseInt(year, "Academic year");
        if (creditValue <= 0) {
            throw new ValidationException("Credit hours must be greater than zero.");
        }
        return new CourseModule(code.trim(), name.trim(), creditValue, semesterValue, yearValue);
    }

    public Lecturer buildLecturer(String id, String name, String email) {
        requireText(id, "Lecturer ID");
        requireText(name, "Full name");
        requireText(email, "Email");
        String trimmedEmail = email.trim();
        if (!trimmedEmail.contains("@") || trimmedEmail.indexOf('@') == trimmedEmail.length() - 1) {
            throw new ValidationException("Email must be a valid address.");
        }
        return new Lecturer(id.trim(), name.trim(), trimmedEmail);
    }

    public String format(double value) {
        return df.format(value);
    }

    public void exportAtRiskCsv(Path file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write("Student ID,Full Name,Pathway,GPA,Reason");
            writer.newLine();
            for (Student student : getAtRiskStudents()) {
                writer.write(csv(student.getId()) + "," + csv(student.getFullName()) + ","
                        + csv(student.getPathway()) + "," + format(calculateCgpa(student.getId()))
                        + "," + csv(reason(student.getId())));
                writer.newLine();
            }
        }
    }

    public void exportBatchHtml(Path file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write("<html><head><title>Batch Performance Summary</title>");
            writer.write("<style>body{font-family:Arial}table{border-collapse:collapse;width:100%}td,th{border:1px solid #bbb;padding:6px}th{background:#e8eef7}</style>");
            writer.write("</head><body><h1>Batch Performance Summary Report</h1>");
            writer.write("<p><b>Completed results:</b> " + completedResultCount()
                    + " &nbsp; <b>Class average:</b> " + format(classAverage())
                    + " &nbsp; <b>Highest:</b> " + format(highestMark())
                    + " &nbsp; <b>Lowest:</b> " + format(lowestMark())
                    + " &nbsp; <b>Pass rate:</b> " + format(passRate()) + "%</p><table>");
            writer.write("<tr><th>Student</th><th>Module</th><th>Credits</th><th>Total</th><th>Grade</th><th>Status</th></tr>");
            for (ResultRow row : store.getResultRows()) {
                Result result = row.getResult();
                writer.write("<tr><td>" + esc(row.getStudent().getId() + " - " + row.getStudent().getFullName()) + "</td>");
                writer.write("<td>" + esc(row.getModule().getCode() + " - " + row.getModule().getName()) + "</td>");
                writer.write("<td>" + row.getModule().getCreditHours() + "</td>");
                writer.write("<td>" + (result == null ? "Pending" : format(result.getTotalMark())) + "</td>");
                writer.write("<td>" + (result == null ? "-" : esc(result.getGradeLetter())) + "</td>");
                writer.write("<td>" + (result == null ? "Pending" : result.getStatus()) + "</td></tr>");
            }
            writer.write("</table></body></html>");
        }
    }

    public String reason(String studentId) {
        boolean failed = store.getResultRows().stream()
                .anyMatch(row -> row.getStudent().getId().equals(studentId)
                && row.getResult() != null
                && row.getResult().getStatus() != ResultStatus.PASS);
        if (failed) {
            return "Failed or repeat module";
        }
        return "CGPA below 2.0";
    }

    public long completedResultCount() {
        return store.getResultRows().stream().filter(row -> row.getResult() != null).count();
    }

    public double classAverage() {
        return store.getResultRows().stream().filter(row -> row.getResult() != null)
                .mapToDouble(row -> row.getResult().getTotalMark()).average().orElse(0);
    }

    public double highestMark() {
        return store.getResultRows().stream().filter(row -> row.getResult() != null)
                .mapToDouble(row -> row.getResult().getTotalMark()).max().orElse(0);
    }

    public double lowestMark() {
        return store.getResultRows().stream().filter(row -> row.getResult() != null)
                .mapToDouble(row -> row.getResult().getTotalMark()).min().orElse(0);
    }

    public double passRate() {
        long completed = completedResultCount();
        long passed = store.getResultRows().stream().filter(row -> row.getResult() != null
                && row.getResult().getStatus() == ResultStatus.PASS).count();
        return completed == 0 ? 0 : passed * 100.0 / completed;
    }

    public String describeEnrollment(Enrollment enrollment) {
        Student student = store.findStudent(enrollment.getStudentId()).orElse(null);
        CourseModule module = store.findModule(enrollment.getModuleCode()).orElse(null);
        String studentText = student == null ? enrollment.getStudentId() : student.getId() + " - " + student.getFullName();
        String moduleText = module == null ? enrollment.getModuleCode() : module.getCode() + " - " + module.getName();
        return enrollment.getId() + " | " + studentText + " | " + moduleText;
    }

    private void validateMark(double mark, String label) {
        if (mark < 0 || mark > 100) {
            throw new ValidationException(label + " mark must be between 0 and 100.");
        }
    }

    private void requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(label + " is required.");
        }
    }

    private int parseInt(String value, String label) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            throw new ValidationException(label + " must be a whole number.");
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            throw new ValidationException("Date of birth must use YYYY-MM-DD format.");
        }
    }

    private String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String esc(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
