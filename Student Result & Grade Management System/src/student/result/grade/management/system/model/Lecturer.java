package student.result.grade.management.system.model;

public class Lecturer {
    private final String lecturerId;
    private final String fullName;
    private final String email;

    public Lecturer(String lecturerId, String fullName, String email) {
        this.lecturerId = lecturerId;
        this.fullName = fullName;
        this.email = email;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return lecturerId + " - " + fullName;
    }
}