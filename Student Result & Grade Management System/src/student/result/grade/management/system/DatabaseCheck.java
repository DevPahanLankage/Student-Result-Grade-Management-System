package student.result.grade.management.system;

import student.result.grade.management.system.repository.DataStore;
import student.result.grade.management.system.repository.MySqlDataStore;

public class DatabaseCheck {
    public static void main(String[] args) {
        DataStore store = new MySqlDataStore();
        System.out.println("MySQL connection successful.");
        System.out.println("Students: " + store.getStudents().size());
        System.out.println("Modules: " + store.getModules().size());
        System.out.println("Enrollments: " + store.getEnrollments().size());
        requireLogin(store, "admin", "admin123");
        requireLogin(store, "lecturer", "lect123");
        requireLogin(store, "student", "stud123");
        System.out.println("Role authentication successful.");
    }

    private static void requireLogin(DataStore store, String username, String password) {
        if (store.authenticate(username, password).isEmpty()) {
            throw new IllegalStateException("Authentication failed for " + username);
        }
    }
}
