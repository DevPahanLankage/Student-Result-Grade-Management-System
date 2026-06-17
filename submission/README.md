# Submission Pack

## GitHub Repository

Push the project to GitHub and submit the repository URL on LMS.

Suggested repository structure:
```
courseworkv1/
├── README.md
├── CODSE251P-015 - COURSEWORK-EAD-StudentResultSystem.pdf  (proposal)
├── Student Result & Grade Management System/
│   ├── src/
│   ├── database/
│   ├── reports/
│   ├── docs/COURSEWORK_REPORT.md
│   └── dist/  (after build)
└── submission/
    ├── screenshots/
    └── COURSEWORK_REPORT.pdf  (export from docs/)
```

## LMS Submission Checklist

- [ ] GitHub repository link
- [ ] Proposal PDF (`CODSE251P-015 - COURSEWORK-EAD-StudentResultSystem.pdf`)
- [ ] Coursework report PDF (export from `docs/COURSEWORK_REPORT.md`)
- [ ] Screenshot folder (see `SCREENSHOTS.md`)
- [ ] Confirm built JAR runs on submission machine

## Before Submitting

1. Run `database/schema.sql` and `database/seed.sql` on a clean MySQL instance
2. Copy `config/db.properties.example` to `config/db.properties`
3. NetBeans **Clean and Build**
4. Run `DatabaseCheck` and `LogicCheck`
5. Capture all screenshots listed in `SCREENSHOTS.md`
6. Push latest code to GitHub

## Demo Accounts

| Role | Username | Password |
| --- | --- | --- |
| Admin | admin | admin123 |
| Lecturer | lecturer | lect123 |
| Student | student | stud123 |

Student account is linked to `CODSE251P-015`.
