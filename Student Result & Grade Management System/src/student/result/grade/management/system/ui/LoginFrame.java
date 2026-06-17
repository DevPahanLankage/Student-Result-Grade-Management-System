package student.result.grade.management.system.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import student.result.grade.management.system.model.User;
import student.result.grade.management.system.repository.DataStore;
import student.result.grade.management.system.service.ResultService;

public class LoginFrame extends JFrame {
    private final DataStore store;
    private final ResultService resultService;
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);

    public LoginFrame(DataStore store, ResultService resultService) {
        super("Student Result & Grade Management System");
        this.store = store;
        this.resultService = resultService;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 260);
        setLocationRelativeTo(null);
        build();
    }

    private void build() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Student Result & Grade Management System");
        title.setFont(title.getFont().deriveFont(18f));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        form.add(title, c);

        c.gridwidth = 1;
        c.gridy++;
        form.add(new JLabel("Username"), c);
        c.gridx = 1;
        form.add(usernameField, c);

        c.gridx = 0;
        c.gridy++;
        form.add(new JLabel("Password"), c);
        c.gridx = 1;
        form.add(passwordField, c);

        JButton login = new JButton("Login");
        login.addActionListener(e -> login());
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        form.add(login, c);

        JLabel hint = new JLabel("Demo: admin/admin123, lecturer/lect123, student/stud123");
        c.gridy++;
        form.add(hint, c);
        add(form, BorderLayout.CENTER);
        getRootPane().setDefaultButton(login);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        User user = store.authenticate(username, password).orElse(null);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new MainFrame(store, resultService, user).setVisible(true);
        dispose();
    }
}
