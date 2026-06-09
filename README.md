# Student Result & Grade Management System

Java Swing coursework application for EAD-1. The system supports student and module management, enrollments, result entry, grade/GPA calculation, at-risk detection, CSV export, and report generation.

## Quick Run

Open `Student Result & Grade Management System` in NetBeans, or run from that project folder:

```powershell
cd "Student Result & Grade Management System"
javac -d build/classes (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp build/classes student.result.grade.management.system.StudentResultGradeManagementSystem
```

Default logins:

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `admin123` |
| Lecturer | `lecturer` | `lect123` |
| Student | `student` | `stud123` |

## Database Setup

MySQL scripts are in `Student Result & Grade Management System/database/`.

1. Create the schema with `database/schema.sql`.
2. Load demo data with `database/seed.sql`.
3. Copy `config/db.properties.example` to `config/db.properties` and enter the MySQL password.
4. Connector/J is bundled in the project `lib/` directory and already configured in NetBeans.

The application uses MySQL through JDBC. Keep MySQL running and ensure `config/db.properties` contains the correct credentials before starting the app.

To verify the connection without opening the GUI, run:

```powershell
java -cp "path\to\mysql-connector-j-9.7.0.jar;build\classes" student.result.grade.management.system.DatabaseCheck
```

## JasperReports

The project contains two Jasper designs:

- `reports/batch_performance_summary.jrxml`
- `reports/individual_student_result.jrxml`

Open them through the application's Reports tab or directly in Jaspersoft Studio. Create a JDBC data adapter using the same connection details as `config/db.properties`, then preview/export the reports as PDF.

The app also generates an HTML batch report and CSV exports from the Reports tab so the report workflow is demonstrable even before Jasper libraries are linked.

## Coursework Coverage

- User interfaces: login, dashboard, data input, transaction, output/report screens.
- OOP: model, service, repository, UI, utility packages.
- Exception handling: validation and service exceptions shown in dialogs.
- IO streams: CSV and HTML report export.
- Multithreading: reports run in a `SwingWorker`.
- Data store: normalized MySQL schema and seed data.

## Verification

Run these classes from NetBeans:

- `DatabaseCheck`: confirms live MySQL connectivity and row counts.
- `DatabaseUpgrade`: safely upgrades an existing database relationship and password hashes.
- `LogicCheck`: verifies grade boundaries and invalid-mark handling.

## Demonstration Flow

1. Log in as Admin and demonstrate student/module CRUD and enrollment.
2. Log in as Lecturer and enter results.
3. Show automatic totals, grades, dashboard statistics, and at-risk students.
4. Export the at-risk CSV and batch HTML report.
5. Open both Jasper designs and preview/export them in Jaspersoft Studio.
6. Log in as Student and show the student-specific dashboard and results.
