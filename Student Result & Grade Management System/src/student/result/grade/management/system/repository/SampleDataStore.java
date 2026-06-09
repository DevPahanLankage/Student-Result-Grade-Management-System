package student.result.grade.management.system.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import student.result.grade.management.system.model.CourseModule;
import student.result.grade.management.system.model.Enrollment;
import student.result.grade.management.system.model.GradeRule;
import student.result.grade.management.system.model.Result;
import student.result.grade.management.system.model.ResultRow;
import student.result.grade.management.system.model.Role;
import student.result.grade.management.system.model.Student;
import student.result.grade.management.system.model.User;
import student.result.grade.management.system.service.ValidationException;

public class SampleDataStore implements DataStore {
    private final Map<String, User> users = new LinkedHashMap<>();
    private final Map<String, Student> students = new LinkedHashMap<>();
    private final Map<String, CourseModule> modules = new LinkedHashMap<>();
    private final Map<Integer, Enrollment> enrollments = new LinkedHashMap<>();
    private final Map<Integer, Result> results = new LinkedHashMap<>();
    private final List<GradeRule> gradeRules = new ArrayList<>();
    private int nextEnrollmentId = 1;

    public SampleDataStore() {
        seed();
    }

    private void seed() {
        users.put("admin", new User("admin", "admin123", Role.ADMIN, null));
        users.put("lecturer", new User("lecturer", "lect123", Role.LECTURER, null));
        users.put("student", new User("student", "stud123", Role.STUDENT, "CODSE251P-015"));

        gradeRules.add(new GradeRule("A", 80, 100, 4.0, true));
        gradeRules.add(new GradeRule("B", 65, 79.99, 3.0, true));
        gradeRules.add(new GradeRule("C", 50, 64.99, 2.0, true));
        gradeRules.add(new GradeRule("D", 40, 49.99, 1.0, true));
        gradeRules.add(new GradeRule("F", 0, 39.99, 0.0, false));

        addStudent(new Student("CODSE251P-001", "Ayesha Perera", LocalDate.of(2004, 3, 12), "Software Engineering", 2025));
        addStudent(new Student("CODSE251P-002", "Kasun Fernando", LocalDate.of(2003, 11, 8), "Software Engineering", 2025));
        addStudent(new Student("CODSE251P-015", "Pahan Student", LocalDate.of(2004, 7, 21), "Software Engineering", 2025));

        addModule(new CourseModule("EAD101", "Enterprise Application Development", 4, 1, 2026));
        addModule(new CourseModule("DBS101", "Database Systems", 3, 1, 2026));
        addModule(new CourseModule("OOP101", "Object Oriented Programming", 3, 1, 2026));

        addEnrollment("CODSE251P-001", "EAD101", 1, 2026);
        addEnrollment("CODSE251P-001", "DBS101", 1, 2026);
        addEnrollment("CODSE251P-002", "EAD101", 1, 2026);
        addEnrollment("CODSE251P-002", "OOP101", 1, 2026);
        addEnrollment("CODSE251P-015", "EAD101", 1, 2026);
        addEnrollment("CODSE251P-015", "DBS101", 1, 2026);
    }

    public Optional<User> authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.passwordMatches(password)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public List<Student> getStudents() {
        return students.values().stream().sorted(Comparator.comparing(Student::getId)).toList();
    }

    public void addStudent(Student student) {
        requireUnique(!students.containsKey(student.getId()), "Student ID already exists.");
        students.put(student.getId(), student);
    }

    public void updateStudent(Student student) {
        requireUnique(students.containsKey(student.getId()), "Student ID was not found.");
        students.put(student.getId(), student);
    }

    public void deleteStudent(String studentId) {
        boolean hasEnrollment = enrollments.values().stream().anyMatch(e -> e.getStudentId().equals(studentId));
        requireUnique(!hasEnrollment, "Cannot delete a student with enrollments.");
        students.remove(studentId);
    }

    public List<CourseModule> getModules() {
        return modules.values().stream().sorted(Comparator.comparing(CourseModule::getCode)).toList();
    }

    public void addModule(CourseModule module) {
        requireUnique(!modules.containsKey(module.getCode()), "Module code already exists.");
        modules.put(module.getCode(), module);
    }

    public void updateModule(CourseModule module) {
        requireUnique(modules.containsKey(module.getCode()), "Module code was not found.");
        modules.put(module.getCode(), module);
    }

    public void deleteModule(String moduleCode) {
        boolean hasEnrollment = enrollments.values().stream().anyMatch(e -> e.getModuleCode().equals(moduleCode));
        requireUnique(!hasEnrollment, "Cannot delete a module with enrollments.");
        modules.remove(moduleCode);
    }

    public List<Enrollment> getEnrollments() {
        return enrollments.values().stream().sorted(Comparator.comparingInt(Enrollment::getId)).toList();
    }

    public Enrollment addEnrollment(String studentId, String moduleCode, int semester, int academicYear) {
        requireUnique(students.containsKey(studentId), "Select a valid student.");
        requireUnique(modules.containsKey(moduleCode), "Select a valid module.");
        boolean duplicate = enrollments.values().stream().anyMatch(e -> e.getStudentId().equals(studentId)
                && e.getModuleCode().equals(moduleCode)
                && e.getSemester() == semester
                && e.getAcademicYear() == academicYear);
        requireUnique(!duplicate, "This enrollment already exists.");
        Enrollment enrollment = new Enrollment(nextEnrollmentId++, studentId, moduleCode, semester, academicYear);
        enrollments.put(enrollment.getId(), enrollment);
        return enrollment;
    }

    @Override
    public void deleteEnrollment(int enrollmentId) {
        results.remove(enrollmentId);
        enrollments.remove(enrollmentId);
    }

    public List<GradeRule> getGradeRules() {
        return new ArrayList<>(gradeRules);
    }

    public void saveResult(Result result) {
        requireUnique(enrollments.containsKey(result.getEnrollmentId()), "Enrollment was not found.");
        results.put(result.getEnrollmentId(), result);
    }

    @Override
    public void deleteResult(int enrollmentId) {
        results.remove(enrollmentId);
    }

    public List<ResultRow> getResultRows() {
        List<ResultRow> rows = new ArrayList<>();
        for (Enrollment enrollment : getEnrollments()) {
            rows.add(new ResultRow(
                    students.get(enrollment.getStudentId()),
                    modules.get(enrollment.getModuleCode()),
                    enrollment,
                    results.get(enrollment.getId())));
        }
        return rows;
    }

    public Optional<Result> getResult(int enrollmentId) {
        return Optional.ofNullable(results.get(enrollmentId));
    }

    public Optional<Student> findStudent(String studentId) {
        return Optional.ofNullable(students.get(studentId));
    }

    public Optional<CourseModule> findModule(String moduleCode) {
        return Optional.ofNullable(modules.get(moduleCode));
    }

    private void requireUnique(boolean condition, String message) {
        if (!condition) {
            throw new ValidationException(message);
        }
    }
}
