package student.result.grade.management.system.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import student.result.grade.management.system.model.CourseModule;
import student.result.grade.management.system.model.Enrollment;
import student.result.grade.management.system.model.GradeRule;
import student.result.grade.management.system.model.Lecturer;
import student.result.grade.management.system.model.Result;
import student.result.grade.management.system.model.ResultRow;
import student.result.grade.management.system.model.ResultStatus;
import student.result.grade.management.system.model.Role;
import student.result.grade.management.system.model.Student;
import student.result.grade.management.system.model.User;

public class MySqlDataStore implements DataStore {

    public MySqlDataStore() {
        DatabaseConnection.test();
    }

    @Override
    public Optional<User> authenticate(String username, String password) {
        String sql = "SELECT username, password_hash, role, linked_student_id FROM users WHERE username=? AND password_hash=SHA2(?, 256)";
        try (Connection c = DatabaseConnection.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(user(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public List<Student> getStudents() {
        String sql = "SELECT student_id, full_name, date_of_birth, pathway, batch_year FROM students ORDER BY student_id";
        List<Student> items = new ArrayList<>();
        try (Connection c = DatabaseConnection.open(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) items.add(student(rs));
            return items;
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public void addStudent(Student s) {
        execute("INSERT INTO students(student_id, full_name, date_of_birth, pathway, batch_year) VALUES(?,?,?,?,?)",
                ps -> setStudent(ps, s));
    }

    @Override
    public void updateStudent(Student s) {
        execute("UPDATE students SET full_name=?, date_of_birth=?, pathway=?, batch_year=? WHERE student_id=?", ps -> {
            ps.setString(1, s.getFullName());
            ps.setDate(2, Date.valueOf(s.getDateOfBirth()));
            ps.setString(3, s.getPathway());
            ps.setInt(4, s.getBatchYear());
            ps.setString(5, s.getId());
        });
    }

    @Override
    public void deleteStudent(String studentId) {
        execute("DELETE FROM students WHERE student_id=?", ps -> ps.setString(1, studentId));
    }

    @Override
    public List<CourseModule> getModules() {
        String sql = "SELECT module_code, module_name, credit_hours, semester, academic_year FROM modules ORDER BY module_code";
        List<CourseModule> items = new ArrayList<>();
        try (Connection c = DatabaseConnection.open(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) items.add(module(rs));
            return items;
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public void addModule(CourseModule m) {
        execute("INSERT INTO modules(module_code, module_name, credit_hours, semester, academic_year) VALUES(?,?,?,?,?)",
                ps -> setModule(ps, m));
    }

    @Override
    public void updateModule(CourseModule m) {
        execute("UPDATE modules SET module_name=?, credit_hours=?, semester=?, academic_year=? WHERE module_code=?", ps -> {
            ps.setString(1, m.getName());
            ps.setInt(2, m.getCreditHours());
            ps.setInt(3, m.getSemester());
            ps.setInt(4, m.getAcademicYear());
            ps.setString(5, m.getCode());
        });
    }

    @Override
    public void deleteModule(String moduleCode) {
        execute("DELETE FROM modules WHERE module_code=?", ps -> ps.setString(1, moduleCode));
    }

    @Override
    public List<Lecturer> getLecturers() {
        String sql = "SELECT lecturer_id, full_name, email FROM lecturers ORDER BY lecturer_id";
        List<Lecturer> items = new ArrayList<>();
        try (Connection c = DatabaseConnection.open(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) items.add(lecturer(rs));
            return items;
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public void addLecturer(Lecturer lecturer) {
        execute("INSERT INTO lecturers(lecturer_id, full_name, email) VALUES(?,?,?)", ps -> {
            ps.setString(1, lecturer.getLecturerId());
            ps.setString(2, lecturer.getFullName());
            ps.setString(3, lecturer.getEmail());
        });
    }

    @Override
    public void updateLecturer(Lecturer lecturer) {
        execute("UPDATE lecturers SET full_name=?, email=? WHERE lecturer_id=?", ps -> {
            ps.setString(1, lecturer.getFullName());
            ps.setString(2, lecturer.getEmail());
            ps.setString(3, lecturer.getLecturerId());
        });
    }

    @Override
    public void deleteLecturer(String lecturerId) {
        execute("DELETE FROM lecturers WHERE lecturer_id=?", ps -> ps.setString(1, lecturerId));
    }

    @Override
    public List<Enrollment> getEnrollments() {
        String sql = "SELECT enrollment_id, student_id, module_code, semester, academic_year FROM enrollments ORDER BY enrollment_id";
        List<Enrollment> items = new ArrayList<>();
        try (Connection c = DatabaseConnection.open(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) items.add(enrollment(rs));
            return items;
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public Enrollment addEnrollment(String studentId, String moduleCode, int semester, int academicYear) {
        String sql = "INSERT INTO enrollments(student_id, module_code, semester, academic_year) VALUES(?,?,?,?)";
        try (Connection c = DatabaseConnection.open();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, studentId);
            ps.setString(2, moduleCode);
            ps.setInt(3, semester);
            ps.setInt(4, academicYear);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return new Enrollment(keys.getInt(1), studentId, moduleCode, semester, academicYear);
            }
            throw new DatabaseException("Enrollment was saved but no ID was returned.", null);
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public void deleteEnrollment(int enrollmentId) {
        try (Connection c = DatabaseConnection.open()) {
            c.setAutoCommit(false);
            try (PreparedStatement result = c.prepareStatement("DELETE FROM results WHERE enrollment_id=?");
                    PreparedStatement enrollment = c.prepareStatement("DELETE FROM enrollments WHERE enrollment_id=?")) {
                result.setInt(1, enrollmentId);
                result.executeUpdate();
                enrollment.setInt(1, enrollmentId);
                if (enrollment.executeUpdate() == 0) throw new DatabaseException("Enrollment was not found.", null);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public List<GradeRule> getGradeRules() {
        String sql = "SELECT grade_letter, min_mark, max_mark, grade_point, pass_grade FROM grade_rules ORDER BY min_mark DESC";
        List<GradeRule> items = new ArrayList<>();
        try (Connection c = DatabaseConnection.open(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) items.add(new GradeRule(rs.getString(1), rs.getDouble(2), rs.getDouble(3), rs.getDouble(4), rs.getBoolean(5)));
            return items;
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public void saveResult(Result r) {
        String sql = """
            INSERT INTO results(enrollment_id, coursework_mark, midterm_mark, final_exam_mark, total_mark, grade_letter, grade_point, status)
            VALUES(?,?,?,?,?,?,?,?)
            ON DUPLICATE KEY UPDATE coursework_mark=VALUES(coursework_mark), midterm_mark=VALUES(midterm_mark),
            final_exam_mark=VALUES(final_exam_mark), total_mark=VALUES(total_mark), grade_letter=VALUES(grade_letter),
            grade_point=VALUES(grade_point), status=VALUES(status), recorded_at=CURRENT_TIMESTAMP
            """;
        execute(sql, ps -> {
            ps.setInt(1, r.getEnrollmentId());
            ps.setDouble(2, r.getCourseworkMark());
            ps.setDouble(3, r.getMidtermMark());
            ps.setDouble(4, r.getFinalExamMark());
            ps.setDouble(5, r.getTotalMark());
            ps.setString(6, r.getGradeLetter());
            ps.setDouble(7, r.getGradePoint());
            ps.setString(8, r.getStatus().name());
        });
    }

    @Override
    public void deleteResult(int enrollmentId) {
        execute("DELETE FROM results WHERE enrollment_id=?", ps -> ps.setInt(1, enrollmentId));
    }

    @Override
    public List<ResultRow> getResultRows() {
        String sql = """
            SELECT s.student_id, s.full_name, s.date_of_birth, s.pathway, s.batch_year,
                   m.module_code, m.module_name, m.credit_hours, m.semester AS module_semester, m.academic_year AS module_year,
                   e.enrollment_id, e.semester AS enrollment_semester, e.academic_year AS enrollment_year,
                   r.coursework_mark, r.midterm_mark, r.final_exam_mark, r.total_mark, r.grade_letter, r.grade_point, r.status
            FROM enrollments e
            JOIN students s ON s.student_id=e.student_id
            JOIN modules m ON m.module_code=e.module_code
            LEFT JOIN results r ON r.enrollment_id=e.enrollment_id
            ORDER BY s.student_id, m.module_code
            """;
        List<ResultRow> items = new ArrayList<>();
        try (Connection c = DatabaseConnection.open(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Student s = new Student(rs.getString("student_id"), rs.getString("full_name"),
                        rs.getDate("date_of_birth").toLocalDate(), rs.getString("pathway"), rs.getInt("batch_year"));
                CourseModule m = new CourseModule(rs.getString("module_code"), rs.getString("module_name"),
                        rs.getInt("credit_hours"), rs.getInt("module_semester"), rs.getInt("module_year"));
                Enrollment e = new Enrollment(rs.getInt("enrollment_id"), s.getId(), m.getCode(),
                        rs.getInt("enrollment_semester"), rs.getInt("enrollment_year"));
                Result r = rs.getString("grade_letter") == null ? null : result(rs, e.getId());
                items.add(new ResultRow(s, m, e, r));
            }
            return items;
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public Optional<Result> getResult(int enrollmentId) {
        String sql = "SELECT coursework_mark, midterm_mark, final_exam_mark, total_mark, grade_letter, grade_point, status FROM results WHERE enrollment_id=?";
        try (Connection c = DatabaseConnection.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(result(rs, enrollmentId)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw db(e);
        }
    }

    @Override
    public Optional<Student> findStudent(String studentId) {
        return getStudents().stream().filter(s -> s.getId().equals(studentId)).findFirst();
    }

    @Override
    public Optional<CourseModule> findModule(String moduleCode) {
        return getModules().stream().filter(m -> m.getCode().equals(moduleCode)).findFirst();
    }

    @Override
    public Optional<Lecturer> findLecturer(String lecturerId) {
        return getLecturers().stream().filter(l -> l.getLecturerId().equals(lecturerId)).findFirst();
    }

    private User user(ResultSet rs) throws SQLException {
        return new User(rs.getString("username"), rs.getString("password_hash"),
                Role.valueOf(rs.getString("role")), rs.getString("linked_student_id"));
    }

    private Student student(ResultSet rs) throws SQLException {
        return new Student(rs.getString("student_id"), rs.getString("full_name"),
                rs.getDate("date_of_birth").toLocalDate(), rs.getString("pathway"), rs.getInt("batch_year"));
    }

    private CourseModule module(ResultSet rs) throws SQLException {
        return new CourseModule(rs.getString("module_code"), rs.getString("module_name"),
                rs.getInt("credit_hours"), rs.getInt("semester"), rs.getInt("academic_year"));
    }

    private Lecturer lecturer(ResultSet rs) throws SQLException {
        return new Lecturer(rs.getString("lecturer_id"), rs.getString("full_name"), rs.getString("email"));
    }

    private Enrollment enrollment(ResultSet rs) throws SQLException {
        return new Enrollment(rs.getInt("enrollment_id"), rs.getString("student_id"),
                rs.getString("module_code"), rs.getInt("semester"), rs.getInt("academic_year"));
    }

    private Result result(ResultSet rs, int enrollmentId) throws SQLException {
        return new Result(enrollmentId, rs.getDouble("coursework_mark"), rs.getDouble("midterm_mark"),
                rs.getDouble("final_exam_mark"), rs.getDouble("total_mark"), rs.getString("grade_letter"),
                rs.getDouble("grade_point"), ResultStatus.valueOf(rs.getString("status")));
    }

    private void setStudent(PreparedStatement ps, Student s) throws SQLException {
        ps.setString(1, s.getId());
        ps.setString(2, s.getFullName());
        ps.setDate(3, Date.valueOf(s.getDateOfBirth()));
        ps.setString(4, s.getPathway());
        ps.setInt(5, s.getBatchYear());
    }

    private void setModule(PreparedStatement ps, CourseModule m) throws SQLException {
        ps.setString(1, m.getCode());
        ps.setString(2, m.getName());
        ps.setInt(3, m.getCreditHours());
        ps.setInt(4, m.getSemester());
        ps.setInt(5, m.getAcademicYear());
    }

    private void execute(String sql, StatementSetter setter) {
        try (Connection c = DatabaseConnection.open(); PreparedStatement ps = c.prepareStatement(sql)) {
            setter.set(ps);
            int changed = ps.executeUpdate();
            if (changed == 0) throw new DatabaseException("No database record was changed.", null);
        } catch (SQLException e) {
            throw db(e);
        }
    }

    private DatabaseException db(SQLException e) {
        return new DatabaseException("Database operation failed: " + e.getMessage(), e);
    }

    @FunctionalInterface
    private interface StatementSetter {
        void set(PreparedStatement statement) throws SQLException;
    }
}
