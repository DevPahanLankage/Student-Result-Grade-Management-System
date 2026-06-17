USE student_result_system;

INSERT INTO grade_rules (grade_letter, min_mark, max_mark, grade_point, pass_grade) VALUES
('A', 80, 100, 4.00, TRUE),
('B', 65, 79.99, 3.00, TRUE),
('C', 50, 64.99, 2.00, TRUE),
('D', 40, 49.99, 1.00, TRUE),
('F', 0, 39.99, 0.00, FALSE);

INSERT INTO students (student_id, full_name, date_of_birth, pathway, batch_year) VALUES
('CODSE251P-001', 'Ayesha Perera', '2004-03-12', 'Software Engineering', 2025),
('CODSE251P-002', 'Kasun Fernando', '2003-11-08', 'Software Engineering', 2025),
('CODSE251P-015', 'Pahan Student', '2004-07-21', 'Software Engineering', 2025);

INSERT INTO lecturers (lecturer_id, full_name, email) VALUES
('LEC001', 'Dr. N. Silva', 'n.silva@nibm.lk');

INSERT INTO modules (module_code, module_name, credit_hours, semester, academic_year) VALUES
('EAD101', 'Enterprise Application Development', 4, 1, 2026),
('DBS101', 'Database Systems', 3, 1, 2026),
('OOP101', 'Object Oriented Programming', 3, 1, 2026);

INSERT INTO enrollments (student_id, module_code, semester, academic_year) VALUES
('CODSE251P-001', 'EAD101', 1, 2026),
('CODSE251P-001', 'DBS101', 1, 2026),
('CODSE251P-002', 'EAD101', 1, 2026),
('CODSE251P-002', 'OOP101', 1, 2026),
('CODSE251P-015', 'EAD101', 1, 2026),
('CODSE251P-015', 'DBS101', 1, 2026);

INSERT INTO users (username, password_hash, role, linked_student_id) VALUES
('admin', SHA2('admin123', 256), 'ADMIN', NULL),
('lecturer', SHA2('lect123', 256), 'LECTURER', NULL),
('student', SHA2('stud123', 256), 'STUDENT', 'CODSE251P-015');

-- Demo results spanning grades A through F (30% coursework, 20% mid-term, 50% final)
INSERT INTO results (enrollment_id, coursework_mark, midterm_mark, final_exam_mark, total_mark, grade_letter, grade_point, status)
SELECT e.enrollment_id, 85, 82, 88, 85.90, 'A', 4.00, 'PASS'
FROM enrollments e WHERE e.student_id = 'CODSE251P-001' AND e.module_code = 'EAD101';

INSERT INTO results (enrollment_id, coursework_mark, midterm_mark, final_exam_mark, total_mark, grade_letter, grade_point, status)
SELECT e.enrollment_id, 72, 70, 74, 72.60, 'B', 3.00, 'PASS'
FROM enrollments e WHERE e.student_id = 'CODSE251P-001' AND e.module_code = 'DBS101';

INSERT INTO results (enrollment_id, coursework_mark, midterm_mark, final_exam_mark, total_mark, grade_letter, grade_point, status)
SELECT e.enrollment_id, 58, 55, 52, 54.40, 'C', 2.00, 'PASS'
FROM enrollments e WHERE e.student_id = 'CODSE251P-002' AND e.module_code = 'EAD101';

INSERT INTO results (enrollment_id, coursework_mark, midterm_mark, final_exam_mark, total_mark, grade_letter, grade_point, status)
SELECT e.enrollment_id, 48, 45, 42, 44.40, 'D', 1.00, 'PASS'
FROM enrollments e WHERE e.student_id = 'CODSE251P-002' AND e.module_code = 'OOP101';

INSERT INTO results (enrollment_id, coursework_mark, midterm_mark, final_exam_mark, total_mark, grade_letter, grade_point, status)
SELECT e.enrollment_id, 35, 32, 30, 31.90, 'F', 0.00, 'REPEAT'
FROM enrollments e WHERE e.student_id = 'CODSE251P-015' AND e.module_code = 'EAD101';

INSERT INTO results (enrollment_id, coursework_mark, midterm_mark, final_exam_mark, total_mark, grade_letter, grade_point, status)
SELECT e.enrollment_id, 90, 88, 92, 90.60, 'A', 4.00, 'PASS'
FROM enrollments e WHERE e.student_id = 'CODSE251P-015' AND e.module_code = 'DBS101';
