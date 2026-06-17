# Submission Checklist

- Start MySQL and confirm `student_result_system` exists.
- Run `database/schema.sql` then `database/seed.sql` (includes demo results A–F).
- Run `database/upgrade_existing_database.sql` once if upgrading an older database.
- Copy `config/db.properties.example` to `config/db.properties`.
- Confirm `DatabaseCheck` and `LogicCheck` pass.
- NetBeans **Clean and Build** — verify `dist/Student_Result___Grade_Management_System.jar` runs via `run.ps1`.
- Preview/export both JRXML reports (batch shows grade distribution; individual shows GPA/standing).
- Capture screenshots per `submission/SCREENSHOTS.md`.
- Export `docs/COURSEWORK_REPORT.md` to PDF for LMS.
- Push to GitHub and submit link on LMS.
- Include proposal PDF, report, screenshots, schema, and seed data.
