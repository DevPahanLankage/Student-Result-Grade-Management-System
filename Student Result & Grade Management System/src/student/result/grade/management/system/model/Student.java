package student.result.grade.management.system.model;

import java.time.LocalDate;

public class Student {
    private String id;
    private String fullName;
    private LocalDate dateOfBirth;
    private String pathway;
    private int batchYear;

    public Student(String id, String fullName, LocalDate dateOfBirth, String pathway, int batchYear) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.pathway = pathway;
        this.batchYear = batchYear;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPathway() {
        return pathway;
    }

    public int getBatchYear() {
        return batchYear;
    }
}
