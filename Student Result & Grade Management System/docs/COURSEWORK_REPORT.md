# Student Result & Grade Management System — Coursework Report

**Student ID:** CODSE251P-015  
**Module:** EAD-1 (Enterprise Application Development)  
**Programme:** DSE 25.1P  
**Institution:** National Institute of Business Management — School of Computing and Engineering

---

## 1. Introduction

This report documents the design, implementation, and deployment of the **Student Result & Grade Management System**, a desktop enterprise application developed as coursework for EAD-1. The approved proposal scenario is **Student Result Processing**, covering registration of students and modules, enrollment, lecturer result entry, automated grade calculation, GPA analytics, at-risk identification, and decision-oriented reporting.

The system replaces manual result handling with a centralized, role-controlled solution using **Java Swing**, **MySQL**, and **JasperReports**, developed in **NetBeans**.

---

## 2. Requirements Summary (from Proposal)

### Input
- Student registration (ID, name, DOB, pathway, batch year)
- Module configuration (code, name, credits, semester, year)
- Examination marks (coursework, mid-term, final)
- User authentication (Admin, Lecturer, Student)

### Process
- Weighted total mark and letter grade (A–F)
- Semester GPA and CGPA (credit-weighted)
- Pass / repeat determination
- Validation and exception handling

### Output
- Individual student result sheet (Jasper)
- Batch performance summary — decision report (Jasper)
- At-risk student list and CSV export

---

## 3. System Architecture

### 3.1 Layered Design

```
┌─────────────────────────────────────────┐
│  UI Layer: LoginFrame, MainFrame        │
├─────────────────────────────────────────┤
│  Service Layer: ResultService,          │
│                 JasperReportService     │
├─────────────────────────────────────────┤
│  Repository Layer: DataStore interface  │
│                    MySqlDataStore       │
├─────────────────────────────────────────┤
│  MySQL Database: student_result_system  │
└─────────────────────────────────────────┘
```

The **Repository pattern** (`DataStore` interface with `MySqlDataStore` implementation) decouples business logic from persistence. `ResultService` encapsulates grading rules and analytics. The UI layer delegates all validation and computation to services.

### 3.2 Database ERD (Normalized Schema)

**Tables:** `users`, `students`, `lecturers`, `modules`, `enrollments`, `grade_rules`, `results`

**Key relationships:**
- `users.linked_student_id` → `students.student_id` (student login)
- `enrollments` links `students` and `modules`
- `results.enrollment_id` → `enrollments` (one result per enrollment)
- `results.grade_letter` → `grade_rules`

Foreign keys enforce referential integrity. Passwords stored as SHA-256 hashes.

### 3.3 Role-Based Access

| Feature | Admin | Lecturer | Student |
|---------|-------|----------|---------|
| Student/Module/Lecturer CRUD | Yes | No | No |
| Enrollments | Yes | No | No |
| Result entry | Yes | Yes | No |
| At-risk & batch reports | Yes | Yes | No |
| Personal results & report | No | No | Yes |

---

## 4. User Interfaces

### 4.1 Data Input Screens
- **Login** — username/password with role routing
- **Students / Modules / Lecturers** — CRUD forms with validation
- **Enrollments** — student + module selection
- **Result Entry** — coursework, mid-term, final marks

### 4.2 Data Process (Transaction) Screens
- **Enroll** — creates enrollment record with duplicate prevention
- **Calculate & Save** — computes weighted total, grade, GPA impact, persists result

### 4.3 Data Output Screens
- **Dashboard** — class statistics or personal GPA/standing
- **At-Risk** — filtered list with reasons
- **Reports** — Jasper HTML/PDF, CSV, HTML batch export
- **My Results** — student-specific result table

---

## 5. EAD Core Concepts Applied

| Concept | Implementation |
|---------|----------------|
| **OOP** | Model classes (`Student`, `Result`, `Lecturer`), service and repository layers, encapsulation |
| **Multithreading** | `SwingWorker` in `MainFrame` for non-blocking report generation |
| **IO Streams** | `BufferedWriter` for CSV and HTML export in `ResultService` |
| **Exception Handling** | `ValidationException`, `DatabaseException`, user-friendly JOptionPane messages |
| **Design Patterns** | Repository pattern (`DataStore`), layered architecture, dependency injection via constructors |
| **Database Operations** | JDBC CRUD, parameterized queries, transactions on enrollment delete |
| **Reports** | JasperReports with multi-table SQL joins for decision making |

---

## 6. Grading Logic

**Total mark** = coursework × 0.30 + mid-term × 0.20 + final × 0.50

| Grade | Range | GPA | Pass |
|-------|-------|-----|------|
| A | 80–100 | 4.0 | Yes |
| B | 65–79.99 | 3.0 | Yes |
| C | 50–64.99 | 2.0 | Yes |
| D | 40–49.99 | 1.0 | Yes |
| F | 0–39.99 | 0.0 | No (REPEAT) |

**CGPA** = Σ(grade_point × credit_hours) / Σ(credit_hours)

**At-risk:** any non-PASS result OR CGPA < 2.0

---

## 7. Reports

### 7.1 Batch Performance Summary (Decision Report)
Joins `students`, `enrollments`, `modules`, and `results`. Summary includes class average, highest/lowest marks, pass rate, and **grade distribution (A–F counts)**.

### 7.2 Individual Student Result Sheet
Per-student module breakdown with **Semester GPA, CGPA, and academic standing** in the summary band.

Both reports export to HTML and PDF from the application or Jaspersoft Studio.

---

## 8. Deployment

1. Install MySQL and run `database/schema.sql` + `database/seed.sql`
2. Copy `config/db.properties.example` → `config/db.properties`
3. NetBeans **Clean and Build** → `dist/Student_Result___Grade_Management_System.jar`
4. Run from project root: `.\run.ps1` or `java -jar dist/Student_Result___Grade_Management_System.jar`

See `DEPLOYMENT.md` for full instructions.

**Verification utilities:**
- `DatabaseCheck` — connectivity and row counts
- `LogicCheck` — grade boundary validation

---

## 9. Testing Evidence

Demo seed data includes results spanning grades **A, B, C, D, and F**:
- CODSE251P-001: A and B grades
- CODSE251P-002: C and D grades
- CODSE251P-015: F (repeat) and A — triggers at-risk (CGPA below 2.0)

Run `LogicCheck` and `DatabaseCheck` after database setup to confirm.

---

## 10. Screenshots Required

Capture and insert the following (see `submission/SCREENSHOTS.md`):

1. Login screen
2. Admin dashboard
3. Student CRUD
4. Result entry with calculated grade
5. At-risk list
6. Batch Jasper PDF / grade distribution
7. Student My Results + personal report
8. MySQL Workbench showing normalized tables

---

## 11. Conclusion

The Student Result & Grade Management System fulfills the EAD-1 coursework deliverables: input/process/output screens, dashboard, OOP architecture, multithreading, IO streams, exception handling, MySQL data store, and Jasper decision reports. The application addresses the institutional need for accurate, role-controlled result processing and provides actionable analytics for academic decision making.

---

## 12. References

- Oracle Java Documentation — Swing, JDBC, NIO
- JasperReports 7 Documentation
- MySQL 8 Reference Manual
- NetBeans IDE Project Configuration Guide
- NIBM EAD-1 Module Descriptor and Coursework Brief (DSE_25.1P_EAD-1_CW)

---

*Export this document to PDF or Word for LMS submission alongside the proposal and screenshots.*
