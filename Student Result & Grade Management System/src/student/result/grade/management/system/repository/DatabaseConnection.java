package student.result.grade.management.system.repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {
    private static final Path CONFIG = Path.of("config", "db.properties");
    private static final Properties PROPERTIES = loadProperties();

    private DatabaseConnection() {
    }

    public static Connection open() throws SQLException {
        return DriverManager.getConnection(
                PROPERTIES.getProperty("db.url"),
                PROPERTIES.getProperty("db.username"),
                PROPERTIES.getProperty("db.password"));
    }

    public static void test() {
        try (Connection ignored = open()) {
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to MySQL. Check config/db.properties and confirm MySQL is running.", e);
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        if (!Files.exists(CONFIG)) {
            throw new DatabaseException("Missing config/db.properties. Copy db.properties.example and add your MySQL credentials.", null);
        }
        try (InputStream input = Files.newInputStream(CONFIG)) {
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new DatabaseException("Could not read config/db.properties.", e);
        }
    }
}
