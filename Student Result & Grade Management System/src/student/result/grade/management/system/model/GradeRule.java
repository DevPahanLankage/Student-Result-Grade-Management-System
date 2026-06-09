package student.result.grade.management.system.model;

public class GradeRule {
    private final String letter;
    private final double minMark;
    private final double maxMark;
    private final double gradePoint;
    private final boolean pass;

    public GradeRule(String letter, double minMark, double maxMark, double gradePoint, boolean pass) {
        this.letter = letter;
        this.minMark = minMark;
        this.maxMark = maxMark;
        this.gradePoint = gradePoint;
        this.pass = pass;
    }

    public boolean matches(double mark) {
        return mark >= minMark && mark <= maxMark;
    }

    public String getLetter() {
        return letter;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public boolean isPass() {
        return pass;
    }
}
