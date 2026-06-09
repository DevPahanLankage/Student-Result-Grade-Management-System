package student.result.grade.management.system.model;

public class Enrollment {
    private final int id;
    private final String studentId;
    private final String moduleCode;
    private final int semester;
    private final int academicYear;

    public Enrollment(int id, String studentId, String moduleCode, int semester, int academicYear) {
        this.id = id;
        this.studentId = studentId;
        this.moduleCode = moduleCode;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    public int getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public int getSemester() {
        return semester;
    }

    public int getAcademicYear() {
        return academicYear;
    }
}
