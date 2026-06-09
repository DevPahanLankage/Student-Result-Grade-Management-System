CREATE DATABASE IF NOT EXISTS student_result_system;
USE student_result_system;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'LECTURER', 'STUDENT') NOT NULL,
    linked_student_id VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE students (
    student_id VARCHAR(20) PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    date_of_birth DATE NOT NULL,
    pathway VARCHAR(80) NOT NULL,
    batch_year INT NOT NULL
);

ALTER TABLE users
    ADD CONSTRAINT fk_user_student FOREIGN KEY (linked_student_id) REFERENCES students(student_id);

CREATE TABLE lecturers (
    lecturer_id VARCHAR(20) PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL
);

CREATE TABLE modules (
    module_code VARCHAR(20) PRIMARY KEY,
    module_name VARCHAR(120) NOT NULL,
    credit_hours INT NOT NULL,
    semester INT NOT NULL,
    academic_year INT NOT NULL
);

CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    module_code VARCHAR(20) NOT NULL,
    semester INT NOT NULL,
    academic_year INT NOT NULL,
    UNIQUE KEY uq_enrollment (student_id, module_code, semester, academic_year),
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES students(student_id),
    CONSTRAINT fk_enrollment_module FOREIGN KEY (module_code) REFERENCES modules(module_code)
);

CREATE TABLE grade_rules (
    grade_letter VARCHAR(2) PRIMARY KEY,
    min_mark DECIMAL(5,2) NOT NULL,
    max_mark DECIMAL(5,2) NOT NULL,
    grade_point DECIMAL(3,2) NOT NULL,
    pass_grade BOOLEAN NOT NULL
);

CREATE TABLE results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL UNIQUE,
    coursework_mark DECIMAL(5,2) NOT NULL,
    midterm_mark DECIMAL(5,2) NOT NULL,
    final_exam_mark DECIMAL(5,2) NOT NULL,
    total_mark DECIMAL(5,2) NOT NULL,
    grade_letter VARCHAR(2) NOT NULL,
    grade_point DECIMAL(3,2) NOT NULL,
    status ENUM('PASS', 'FAIL', 'REPEAT') NOT NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_result_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id),
    CONSTRAINT fk_result_grade FOREIGN KEY (grade_letter) REFERENCES grade_rules(grade_letter)
);
