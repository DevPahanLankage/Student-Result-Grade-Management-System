package student.result.grade.management.system.repository;

import java.util.List;
import java.util.Optional;
import student.result.grade.management.system.model.CourseModule;
import student.result.grade.management.system.model.Enrollment;
import student.result.grade.management.system.model.GradeRule;
import student.result.grade.management.system.model.Result;
import student.result.grade.management.system.model.ResultRow;
import student.result.grade.management.system.model.Student;
import student.result.grade.management.system.model.User;

public interface DataStore {
    Optional<User> authenticate(String username, String password);
    List<Student> getStudents();
    void addStudent(Student student);
    void updateStudent(Student student);
    void deleteStudent(String studentId);
    List<CourseModule> getModules();
    void addModule(CourseModule module);
    void updateModule(CourseModule module);
    void deleteModule(String moduleCode);
    List<Enrollment> getEnrollments();
    Enrollment addEnrollment(String studentId, String moduleCode, int semester, int academicYear);
    void deleteEnrollment(int enrollmentId);
    List<GradeRule> getGradeRules();
    void saveResult(Result result);
    void deleteResult(int enrollmentId);
    List<ResultRow> getResultRows();
    Optional<Result> getResult(int enrollmentId);
    Optional<Student> findStudent(String studentId);
    Optional<CourseModule> findModule(String moduleCode);
}
