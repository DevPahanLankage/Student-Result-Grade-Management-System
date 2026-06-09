package student.result.grade.management.system;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import student.result.grade.management.system.repository.DataStore;
import student.result.grade.management.system.repository.DatabaseException;
import student.result.grade.management.system.repository.MySqlDataStore;
import student.result.grade.management.system.service.ResultService;
import student.result.grade.management.system.ui.LoginFrame;

public class StudentResultGradeManagementSystem {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            try {
                DataStore store = new MySqlDataStore();
                ResultService resultService = new ResultService(store);
                new LoginFrame(store, resultService).setVisible(true);
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Database Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
