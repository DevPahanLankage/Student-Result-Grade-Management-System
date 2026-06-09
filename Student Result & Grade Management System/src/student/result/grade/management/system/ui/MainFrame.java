package student.result.grade.management.system.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import student.result.grade.management.system.model.CourseModule;
import student.result.grade.management.system.model.Enrollment;
import student.result.grade.management.system.model.Result;
import student.result.grade.management.system.model.ResultRow;
import student.result.grade.management.system.model.Role;
import student.result.grade.management.system.model.Student;
import student.result.grade.management.system.model.User;
import student.result.grade.management.system.repository.DataStore;
import student.result.grade.management.system.service.JasperReportService;
import student.result.grade.management.system.service.ResultService;
import student.result.grade.management.system.service.ValidationException;

public class MainFrame extends JFrame {
    private final DataStore store;
    private final ResultService service;
    private final JasperReportService jasperReports = new JasperReportService();
    private final User user;
    private final JTabbedPane tabs = new JTabbedPane();
    private final JLabel dashboard = new JLabel();
    private JTable studentTable;
    private JTable moduleTable;
    private JTable enrollmentTable;
    private JTable resultTable;
    private JTable atRiskTable;

    public MainFrame(DataStore store, ResultService service, User user) {
        super("Student Result & Grade Management System - " + user.getRole());
        this.store = store;
        this.service = service;
        this.user = user;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setLocationRelativeTo(null);
        build();
        refreshAll();
    }

    private void build() {
        add(header(), BorderLayout.NORTH);
        tabs.add("Dashboard", dashboardPanel());
        if (user.getRole() == Role.ADMIN) {
            tabs.add("Students", studentsPanel());
            tabs.add("Modules", modulesPanel());
            tabs.add("Enrollments", enrollmentsPanel());
        }
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.LECTURER) {
            tabs.add("Result Entry", resultsPanel());
            tabs.add("At-Risk", atRiskPanel());
            tabs.add("Reports", reportsPanel());
        }
        if (user.getRole() == Role.STUDENT) {
            tabs.add("My Results", studentResultsPanel());
        }
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("  Logged in as " + user.getUsername() + " (" + user.getRole() + ")"), BorderLayout.WEST);
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            new LoginFrame(store, service).setVisible(true);
            dispose();
        });
        panel.add(logout, BorderLayout.EAST);
        return panel;
    }

    private JPanel dashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        dashboard.setFont(dashboard.getFont().deriveFont(16f));
        panel.add(dashboard, BorderLayout.CENTER);
        return panel;
    }

    private JPanel studentsPanel() {
        studentTable = table("ID", "Name", "DOB", "Pathway", "Batch");
        JPanel form = formPanel("ID", "Name", "DOB YYYY-MM-DD", "Pathway", "Batch");
        JTextField id = (JTextField) form.getClientProperty("ID");
        JTextField name = (JTextField) form.getClientProperty("Name");
        JTextField dob = (JTextField) form.getClientProperty("DOB YYYY-MM-DD");
        JTextField pathway = (JTextField) form.getClientProperty("Pathway");
        JTextField batch = (JTextField) form.getClientProperty("Batch");
        JButton add = new JButton("Add");
        add.addActionListener(e -> runSafe(() -> {
            store.addStudent(service.buildStudent(id.getText(), name.getText(), dob.getText(), pathway.getText(), batch.getText()));
            refreshAll();
        }));
        JButton update = new JButton("Update");
        update.addActionListener(e -> runSafe(() -> {
            store.updateStudent(service.buildStudent(id.getText(), name.getText(), dob.getText(), pathway.getText(), batch.getText()));
            refreshAll();
        }));
        JButton delete = new JButton("Delete");
        delete.addActionListener(e -> runSafe(() -> {
            if (!confirm("Delete student " + id.getText().trim() + "?")) return;
            store.deleteStudent(id.getText().trim());
            refreshAll();
        }));
        addButtons(form, add, update, delete);
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            int r = studentTable.getSelectedRow();
            if (r >= 0) {
                id.setText(value(studentTable, r, 0));
                name.setText(value(studentTable, r, 1));
                dob.setText(value(studentTable, r, 2));
                pathway.setText(value(studentTable, r, 3));
                batch.setText(value(studentTable, r, 4));
            }
        });
        return split(form, studentTable);
    }

    private JPanel modulesPanel() {
        moduleTable = table("Code", "Name", "Credits", "Semester", "Year");
        JPanel form = formPanel("Code", "Name", "Credits", "Semester", "Year");
        JTextField code = (JTextField) form.getClientProperty("Code");
        JTextField name = (JTextField) form.getClientProperty("Name");
        JTextField credits = (JTextField) form.getClientProperty("Credits");
        JTextField semester = (JTextField) form.getClientProperty("Semester");
        JTextField year = (JTextField) form.getClientProperty("Year");
        JButton add = new JButton("Add");
        add.addActionListener(e -> runSafe(() -> {
            store.addModule(service.buildModule(code.getText(), name.getText(), credits.getText(), semester.getText(), year.getText()));
            refreshAll();
        }));
        JButton update = new JButton("Update");
        update.addActionListener(e -> runSafe(() -> {
            store.updateModule(service.buildModule(code.getText(), name.getText(), credits.getText(), semester.getText(), year.getText()));
            refreshAll();
        }));
        JButton delete = new JButton("Delete");
        delete.addActionListener(e -> runSafe(() -> {
            if (!confirm("Delete module " + code.getText().trim() + "?")) return;
            store.deleteModule(code.getText().trim());
            refreshAll();
        }));
        addButtons(form, add, update, delete);
        moduleTable.getSelectionModel().addListSelectionListener(e -> {
            int r = moduleTable.getSelectedRow();
            if (r >= 0) {
                code.setText(value(moduleTable, r, 0));
                name.setText(value(moduleTable, r, 1));
                credits.setText(value(moduleTable, r, 2));
                semester.setText(value(moduleTable, r, 3));
                year.setText(value(moduleTable, r, 4));
            }
        });
        return split(form, moduleTable);
    }

    private JPanel enrollmentsPanel() {
        enrollmentTable = table("ID", "Student", "Module", "Semester", "Year");
        JComboBox<String> studentBox = new JComboBox<>();
        JComboBox<String> moduleBox = new JComboBox<>();
        JTextField semester = new JTextField("1", 8);
        JTextField year = new JTextField("2026", 8);
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.putClientProperty("studentBox", studentBox);
        form.putClientProperty("moduleBox", moduleBox);
        form.add(new JLabel("Student"));
        form.add(studentBox);
        form.add(new JLabel("Module"));
        form.add(moduleBox);
        form.add(new JLabel("Semester"));
        form.add(semester);
        form.add(new JLabel("Year"));
        form.add(year);
        JButton add = new JButton("Enroll");
        add.addActionListener(e -> runSafe(() -> {
            String studentId = selectedKey(studentBox);
            String moduleCode = selectedKey(moduleBox);
            store.addEnrollment(studentId, moduleCode, Integer.parseInt(semester.getText().trim()), Integer.parseInt(year.getText().trim()));
            refreshAll();
        }));
        form.add(add);
        JButton delete = new JButton("Delete Selected");
        delete.addActionListener(e -> runSafe(() -> {
            int row = enrollmentTable.getSelectedRow();
            if (row < 0) throw new ValidationException("Select an enrollment to delete.");
            int enrollmentId = Integer.parseInt(value(enrollmentTable, row, 0));
            if (!confirm("Delete enrollment " + enrollmentId + " and its result?")) return;
            store.deleteEnrollment(enrollmentId);
            refreshAll();
        }));
        form.add(delete);
        return split(form, enrollmentTable);
    }

    private JPanel resultsPanel() {
        resultTable = table("Enrollment", "Student", "Module", "CW", "Mid", "Final", "Total", "Grade", "Status");
        JComboBox<String> enrollmentBox = new JComboBox<>();
        JTextField cw = new JTextField("75", 6);
        JTextField mid = new JTextField("70", 6);
        JTextField fin = new JTextField("80", 6);
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.putClientProperty("enrollmentBox", enrollmentBox);
        form.add(new JLabel("Enrollment"));
        form.add(enrollmentBox);
        form.add(new JLabel("Coursework"));
        form.add(cw);
        form.add(new JLabel("Mid-term"));
        form.add(mid);
        form.add(new JLabel("Final"));
        form.add(fin);
        JButton save = new JButton("Calculate & Save");
        save.addActionListener(e -> runSafe(() -> {
            int enrollmentId = Integer.parseInt(selectedKey(enrollmentBox));
            service.recordResult(enrollmentId, Double.parseDouble(cw.getText()), Double.parseDouble(mid.getText()), Double.parseDouble(fin.getText()));
            refreshAll();
        }));
        form.add(save);
        JButton delete = new JButton("Delete Selected Result");
        delete.addActionListener(e -> runSafe(() -> {
            int row = resultTable.getSelectedRow();
            if (row < 0) throw new ValidationException("Select a result to delete.");
            int enrollmentId = Integer.parseInt(value(resultTable, row, 0));
            if (!confirm("Delete result for enrollment " + enrollmentId + "?")) return;
            store.deleteResult(enrollmentId);
            refreshAll();
        }));
        form.add(delete);
        return split(form, resultTable);
    }

    private JPanel atRiskPanel() {
        atRiskTable = table("ID", "Name", "Pathway", "GPA", "Reason");
        return new JPanel(new BorderLayout()) {{
            add(new JScrollPane(atRiskTable), BorderLayout.CENTER);
        }};
    }

    private JPanel reportsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.LECTURER) {
            JComboBox<String> studentBox = new JComboBox<>();
            panel.putClientProperty("studentBox", studentBox);
            panel.add(new JLabel("Student"));
            panel.add(studentBox);

            JButton batchReport = new JButton("Generate Batch Jasper Report");
            batchReport.addActionListener(e -> chooseAndRunAndOpen("batch-performance-summary.html",
                path -> jasperReports.generateBatchHtml(path)));
            JButton studentReport = new JButton("Generate Student Jasper Report");
            studentReport.addActionListener(e -> chooseAndRunAndOpen("student-result.html",
                path -> jasperReports.generateStudentHtml(selectedKey(studentBox), path)));
            panel.add(batchReport);
            panel.add(studentReport);
        }
        JButton csv = new JButton("Export At-Risk CSV");
        csv.addActionListener(e -> chooseAndRun("at-risk-students.csv", path -> service.exportAtRiskCsv(path)));
        JButton html = new JButton("Generate Batch HTML Report");
        html.addActionListener(e -> chooseAndRunAndOpen("batch-performance-summary.html", path -> service.exportBatchHtml(path)));
        JButton batchJasper = new JButton("Open Batch Design");
        batchJasper.addActionListener(e -> openFile(Path.of("reports", "batch_performance_summary.jrxml")));
        JButton studentJasper = new JButton("Open Student Design");
        studentJasper.addActionListener(e -> openFile(Path.of("reports", "individual_student_result.jrxml")));
        panel.add(csv);
        panel.add(html);
        panel.add(batchJasper);
        panel.add(studentJasper);
        return panel;
    }

    private JPanel studentResultsPanel() {
        resultTable = table("Enrollment", "Student", "Module", "CW", "Mid", "Final", "Total", "Grade", "Status");
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton report = new JButton("Generate My Jasper Report");
        report.addActionListener(e -> chooseAndRunAndOpen("my-result.html",
                path -> jasperReports.generateStudentHtml(user.getLinkedStudentId(), path)));
        actions.add(report);
        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        return panel;
    }

    private void refreshAll() {
        refreshDashboard();
        refreshStudents();
        refreshModules();
        refreshEnrollments();
        refreshResults();
        refreshAtRisk();
        refreshCombos();
    }

    private void refreshDashboard() {
        long entered = service.completedResultCount();
        double passRate = service.passRate();
        if (user.getRole() == Role.STUDENT) {
            dashboard.setText("<html><div style='padding:25px'><h1>My Academic Dashboard</h1>"
                    + "<p>Student ID: " + user.getLinkedStudentId() + "</p>"
                    + "<p>GPA: " + service.format(service.calculateGpa(user.getLinkedStudentId())) + "</p>"
                    + "<p>Standing: " + (service.isAtRisk(user.getLinkedStudentId()) ? "AT RISK" : "GOOD STANDING")
                    + "</p></div></html>");
            return;
        }
        dashboard.setText("<html><div style='padding:25px'>"
                + "<h1>Dashboard</h1>"
                + "<p>Students: " + store.getStudents().size() + "</p>"
                + "<p>Modules: " + store.getModules().size() + "</p>"
                + "<p>Enrollments: " + store.getEnrollments().size() + "</p>"
                + "<p>Results Entered: " + entered + "</p>"
                + "<p>Pass Rate: " + service.format(passRate) + "%</p>"
                + "<p>Class Average: " + service.format(service.classAverage()) + "</p>"
                + "<p>Highest / Lowest: " + service.format(service.highestMark()) + " / " + service.format(service.lowestMark()) + "</p>"
                + "<p>At-Risk Students: " + service.getAtRiskStudents().size() + "</p>"
                + "</div></html>");
    }

    private void refreshStudents() {
        if (studentTable == null) return;
        DefaultTableModel model = model(studentTable);
        model.setRowCount(0);
        for (Student s : store.getStudents()) {
            model.addRow(new Object[]{s.getId(), s.getFullName(), s.getDateOfBirth(), s.getPathway(), s.getBatchYear()});
        }
    }

    private void refreshModules() {
        if (moduleTable == null) return;
        DefaultTableModel model = model(moduleTable);
        model.setRowCount(0);
        for (CourseModule m : store.getModules()) {
            model.addRow(new Object[]{m.getCode(), m.getName(), m.getCreditHours(), m.getSemester(), m.getAcademicYear()});
        }
    }

    private void refreshEnrollments() {
        if (enrollmentTable == null) return;
        DefaultTableModel model = model(enrollmentTable);
        model.setRowCount(0);
        for (Enrollment e : store.getEnrollments()) {
            model.addRow(new Object[]{e.getId(), e.getStudentId(), e.getModuleCode(), e.getSemester(), e.getAcademicYear()});
        }
    }

    private void refreshResults() {
        if (resultTable == null) return;
        DefaultTableModel model = model(resultTable);
        model.setRowCount(0);
        for (ResultRow row : store.getResultRows()) {
            if (user.getRole() == Role.STUDENT && !row.getStudent().getId().equals(user.getLinkedStudentId())) {
                continue;
            }
            Result r = row.getResult();
            model.addRow(new Object[]{
                row.getEnrollment().getId(),
                row.getStudent().getId(),
                row.getModule().getCode(),
                r == null ? "" : service.format(r.getCourseworkMark()),
                r == null ? "" : service.format(r.getMidtermMark()),
                r == null ? "" : service.format(r.getFinalExamMark()),
                r == null ? "Pending" : service.format(r.getTotalMark()),
                r == null ? "-" : r.getGradeLetter(),
                r == null ? "Pending" : r.getStatus()
            });
        }
    }

    private void refreshAtRisk() {
        if (atRiskTable == null) return;
        DefaultTableModel model = model(atRiskTable);
        model.setRowCount(0);
        for (Student s : service.getAtRiskStudents()) {
            model.addRow(new Object[]{s.getId(), s.getFullName(), s.getPathway(), service.format(service.calculateGpa(s.getId())), service.reason(s.getId())});
        }
    }

    private void refreshCombos() {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            refreshCombosIn(tabs.getComponentAt(i));
        }
    }

    @SuppressWarnings("unchecked")
    private void refreshCombosIn(Object component) {
        if (!(component instanceof JPanel panel)) return;
        Object sBox = panel.getClientProperty("studentBox");
        if (sBox instanceof JComboBox<?> box) {
            JComboBox<String> combo = (JComboBox<String>) box;
            combo.removeAllItems();
            store.getStudents().forEach(s -> combo.addItem(s.getId() + " - " + s.getFullName()));
        }
        Object mBox = panel.getClientProperty("moduleBox");
        if (mBox instanceof JComboBox<?> box) {
            JComboBox<String> combo = (JComboBox<String>) box;
            combo.removeAllItems();
            store.getModules().forEach(m -> combo.addItem(m.getCode() + " - " + m.getName()));
        }
        Object eBox = panel.getClientProperty("enrollmentBox");
        if (eBox instanceof JComboBox<?> box) {
            JComboBox<String> combo = (JComboBox<String>) box;
            combo.removeAllItems();
            store.getEnrollments().forEach(e -> combo.addItem(service.describeEnrollment(e)));
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                refreshCombosIn(child);
            }
        }
    }

    private JPanel formPanel(String... labels) {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        for (String label : labels) {
            JTextField field = new JTextField();
            form.add(new JLabel(label));
            form.add(field);
            form.putClientProperty(label, field);
        }
        return form;
    }

    private void addButtons(JPanel form, JButton... buttons) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton button : buttons) {
            row.add(button);
        }
        form.add(new JLabel(""));
        form.add(row);
    }

    private JPanel split(JPanel form, JTable table) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JTable table(String... columns) {
        return new JTable(new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    private DefaultTableModel model(JTable table) {
        return (DefaultTableModel) table.getModel();
    }

    private String value(JTable table, int row, int column) {
        Object value = table.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private String selectedKey(JComboBox<String> combo) {
        if (combo.getSelectedItem() == null) {
            throw new ValidationException("Please select a value.");
        }
        return combo.getSelectedItem().toString().split(" - | \\| ")[0].trim();
    }

    private void runSafe(Runnable action) {
        try {
            action.run();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirm", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    private void openFile(Path path) {
        runSafe(() -> {
            try {
                Desktop.getDesktop().open(path.toFile());
            } catch (Exception e) {
                throw new ValidationException("Could not open " + path + ". Open it manually in Jaspersoft Studio.");
            }
        });
    }

    private interface FileAction {
        void run(Path path) throws Exception;
    }

    private void chooseAndRun(String defaultName, FileAction action) {
        chooseAndRun(defaultName, action, false);
    }

    private void chooseAndRunAndOpen(String defaultName, FileAction action) {
        chooseAndRun(defaultName, action, true);
    }

    private void chooseAndRun(String defaultName, FileAction action, boolean openAfter) {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setSelectedFile(new File(defaultName));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path path = chooser.getSelectedFile().toPath();
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                action.run(path);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (openAfter) {
                        openFile(path);
                    }
                    JOptionPane.showMessageDialog(MainFrame.this, "Export completed:\n" + path);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), "Export failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
