# Student Result & Grade Management System

Java Swing coursework application for EAD-1. The system supports student and module management, enrollments, result entry, grade/GPA calculation, at-risk detection, CSV export, and Jasper report generation.

## Quick Run (NetBeans)

1. Open `Student Result & Grade Management System` in NetBeans.
2. Set up MySQL (see below) and `config/db.properties`.
3. Press **Run** (F6).

Default logins:

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `admin123` |
| Lecturer | `lecturer` | `lect123` |
| Student | `student` | `stud123` |

## Database Setup

MySQL scripts are in `Student Result & Grade Management System/database/`.

1. Create the schema with `database/schema.sql`.
2. Load demo data with `database/seed.sql` (includes sample results for grades A–F).
3. Copy `config/db.properties.example` to `config/db.properties` and enter the MySQL password.
4. Connector/J and JasperReports JARs belong in the project `lib/` directory (configured in NetBeans).

Verify without the GUI:

```powershell
cd "Student Result & Grade Management System"
java -cp "lib/*;build/classes" student.result.grade.management.system.DatabaseCheck
java -cp "lib/*;build/classes" student.result.grade.management.system.LogicCheck
```

## Deployment

See [`DEPLOYMENT.md`](Student%20Result%20&%20Grade%20Management%20System/DEPLOYMENT.md) for build and JAR instructions.

After **Clean and Build**:

```powershell
cd "Student Result & Grade Management System"
.\run.ps1
```

The working directory must contain `config/` and `reports/`.

## JasperReports

- `reports/batch_performance_summary.jrxml` — decision report with grade distribution
- `reports/individual_student_result.jrxml` — per-student sheet with GPA and standing

Generate from the Reports tab or preview in Jaspersoft Studio using the same JDBC settings as `config/db.properties`.

## Coursework Coverage

- User interfaces: login, dashboard, data input, transaction, output/report screens
- OOP: model, service, repository, UI packages
- Exception handling: validation and service exceptions shown in dialogs
- IO streams: CSV and HTML report export
- Multithreading: reports run in a `SwingWorker`
- Data store: normalized MySQL schema and seed data
- Design pattern: Repository pattern via `DataStore` interface

## Documentation

- [`docs/COURSEWORK_REPORT.md`](Student%20Result%20&%20Grade%20Management%20System/docs/COURSEWORK_REPORT.md) — written report (export to PDF for LMS)
- [`submission/README.md`](submission/README.md) — submission checklist
- [`SUBMISSION_CHECKLIST.md`](Student%20Result%20&%20Grade%20Management%20System/SUBMISSION_CHECKLIST.md) — pre-submission steps

## Demonstration Flow

1. Log in as Admin — student/module CRUD, enrollments, lecturers
2. Log in as Lecturer — enter results, view at-risk list
3. Export at-risk CSV and batch Jasper PDF (grade distribution)
4. Log in as Student — personal dashboard, My Results, Jasper PDF
