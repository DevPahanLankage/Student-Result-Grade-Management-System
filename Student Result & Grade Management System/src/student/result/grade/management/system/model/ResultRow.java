package student.result.grade.management.system.model;

public class ResultRow {
    private final Student student;
    private final CourseModule module;
    private final Enrollment enrollment;
    private final Result result;

    public ResultRow(Student student, CourseModule module, Enrollment enrollment, Result result) {
        this.student = student;
        this.module = module;
        this.enrollment = enrollment;
        this.result = result;
    }

    public Student getStudent() {
        return student;
    }

    public CourseModule getModule() {
        return module;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public Result getResult() {
        return result;
    }
}
