# Deployment Guide

## Prerequisites

- Java JDK 25 (or the JDK version configured in NetBeans)
- MySQL Server running locally
- NetBeans with Ant, or Apache Ant on PATH

## Build

1. Open `Student Result & Grade Management System` in NetBeans.
2. Ensure `lib/` contains the JARs listed in `nbproject/project.properties` (MySQL Connector/J and JasperReports stack).
3. Choose **Clean and Build**.
4. Output: `dist/Student_Result___Grade_Management_System.jar` (libraries bundled when `jar.compress=true`).

From the project folder:

```powershell
ant clean jar
```

## Database

```powershell
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```

Copy `config/db.properties.example` to `config/db.properties` and set your MySQL password.

## Run

From the project root (where `config/` and `reports/` live):

```powershell
.\run.ps1
```

Or:

```powershell
java -jar dist/Student_Result___Grade_Management_System.jar
```

The working directory must contain:

- `config/db.properties`
- `reports/*.jrxml`

## Verify

```powershell
java -cp "dist/Student_Result___Grade_Management_System.jar" student.result.grade.management.system.DatabaseCheck
java -cp "dist/Student_Result___Grade_Management_System.jar" student.result.grade.management.system.LogicCheck
```

## Demo Logins

| Role | Username | Password |
| --- | --- | --- |
| Admin | admin | admin123 |
| Lecturer | lecturer | lect123 |
| Student | student | stud123 |

## Submission Checklist

See `SUBMISSION_CHECKLIST.md` and `submission/README.md`.
