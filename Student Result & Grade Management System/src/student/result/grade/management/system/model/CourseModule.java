package student.result.grade.management.system.model;

public class CourseModule {
    private final String code;
    private final String name;
    private final int creditHours;
    private final int semester;
    private final int academicYear;

    public CourseModule(String code, String name, int creditHours, int semester, int academicYear) {
        this.code = code;
        this.name = name;
        this.creditHours = creditHours;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public int getSemester() {
        return semester;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}
