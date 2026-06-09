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
