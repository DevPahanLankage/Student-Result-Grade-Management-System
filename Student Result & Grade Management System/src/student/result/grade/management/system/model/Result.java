package student.result.grade.management.system.model;

public class Result {
    private final int enrollmentId;
    private final double courseworkMark;
    private final double midtermMark;
    private final double finalExamMark;
    private final double totalMark;
    private final String gradeLetter;
    private final double gradePoint;
    private final ResultStatus status;

    public Result(int enrollmentId, double courseworkMark, double midtermMark, double finalExamMark,
            double totalMark, String gradeLetter, double gradePoint, ResultStatus status) {
        this.enrollmentId = enrollmentId;
        this.courseworkMark = courseworkMark;
        this.midtermMark = midtermMark;
        this.finalExamMark = finalExamMark;
        this.totalMark = totalMark;
        this.gradeLetter = gradeLetter;
        this.gradePoint = gradePoint;
        this.status = status;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public double getCourseworkMark() {
        return courseworkMark;
    }

    public double getMidtermMark() {
        return midtermMark;
    }

    public double getFinalExamMark() {
        return finalExamMark;
    }

    public double getTotalMark() {
        return totalMark;
    }

    public String getGradeLetter() {
        return gradeLetter;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public ResultStatus getStatus() {
        return status;
    }
}
