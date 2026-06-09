USE student_result_system;

ALTER TABLE users
    ADD CONSTRAINT fk_user_student FOREIGN KEY (linked_student_id) REFERENCES students(student_id);

UPDATE users SET password_hash=SHA2(password_hash, 256)
WHERE CHAR_LENGTH(password_hash) <> 64;
