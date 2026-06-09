package student.result.grade.management.system;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import student.result.grade.management.system.repository.DatabaseConnection;

public class DatabaseUpgrade {
    public static void main(String[] args) throws Exception {
        try (Connection connection = DatabaseConnection.open(); Statement statement = connection.createStatement()) {
            try {
                statement.executeUpdate("ALTER TABLE users ADD CONSTRAINT fk_user_student "
                        + "FOREIGN KEY (linked_student_id) REFERENCES students(student_id)");
                System.out.println("Added users-to-students foreign key.");
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("duplicate")) throw e;
                System.out.println("Foreign key already exists.");
            }
            int upgraded = statement.executeUpdate("UPDATE users SET password_hash=SHA2(password_hash, 256) "
                    + "WHERE CHAR_LENGTH(password_hash) <> 64");
            System.out.println("Upgraded password hashes: " + upgraded);
        }
    }
}
