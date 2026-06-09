package student.result.grade.management.system.model;

public class User {
    private final String username;
    private final String password;
    private final Role role;
    private final String linkedStudentId;

    public User(String username, String password, Role role, String linkedStudentId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.linkedStudentId = linkedStudentId;
    }

    public String getUsername() {
        return username;
    }

    public boolean passwordMatches(String input) {
        return password.equals(input);
    }

    public Role getRole() {
        return role;
    }

    public String getLinkedStudentId() {
        return linkedStudentId;
    }
}
