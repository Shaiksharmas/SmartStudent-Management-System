# SmartStudent: Java-Based Student Management System

A console-based Student Management System built with **Java + JDBC + MySQL**.
An admin logs in and can add, view, update, delete, and search student
records, view basic statistics, and export the student list to CSV.

## Features

- **Admin login** — static credentials (`admin` / `admin123`) by default;
  can be switched to database-backed authentication (see `AdminService.java`).
- **CRUD** — add, view (table format), update, delete student records.
- **Search** — by Roll No, Name (partial match), Department, or Marks range.
- **Statistics** — total students, highest/lowest marks, department-wise
  student count.
- **Export** — dump the current student list to a CSV file.
- **MySQL + JDBC** — all data is persisted in a `students` table.

## Project Structure

```
SmartStudent/
├── Main.java                 # Entry point
├── DatabaseConnection.java   # JDBC connection handling
├── Student.java              # Model class
├── StudentDAO.java           # Data access logic (CRUD, search, stats, export)
├── AdminService.java         # Login/authentication logic
├── UI.java                   # Console UI / menu system
├── student.sql               # Database schema + sample data
└── README.md
```

## Requirements

- JDK 8 or later
- MySQL Server (via XAMPP, MySQL Workbench, or standalone install)
- MySQL Connector/J (JDBC driver) — download the `mysql-connector-j-<version>.jar`
  from https://dev.mysql.com/downloads/connector/j/

## Setup

### 1. Create the database

Start MySQL, then run the schema file:

```bash
mysql -u root -p < student.sql
```

This creates the `smartstudent` database, the `students` table (with sample
rows), and an optional `admins` table.

### 2. Configure the connection

Set environment variables for the account MySQL is configured to accept. This
keeps the password out of the source code. The app defaults to the URL below
and user `root` when the matching variable is not set.

```bash
export DB_URL='jdbc:mysql://localhost:3306/smartstudent?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC' # optional
export DB_USER='root' # optional
export DB_PASSWORD='your-mysql-password'
```

On Windows Command Prompt, use `set DB_PASSWORD=your-mysql-password` before
starting the program. If your local MySQL root account has no password (a
common XAMPP default), leave `DB_PASSWORD` unset.

### 3. Compile

Place the MySQL Connector/J jar in the `lib/` folder. For example:

```text
lib/mysql-connector-j-9.6.0.jar
```

Then compile (the `lib/*` wildcard includes any JDBC jar in that folder):

```bash
javac -cp ".:lib/*" *.java
```

(On Windows, use `;` instead of `:` as the classpath separator.)

### 4. Run

```bash
java -cp ".:lib/*" Main
```

Log in with:

```
Username: admin
Password: admin123
```

## Using the App

After login you'll see a menu:

```
1. Add Student
2. View All Students
3. Update Student
4. Delete Student
5. Search Students
6. Statistics
7. Export to CSV
0. Logout & Exit
```

Follow the on-screen prompts for each option. Roll numbers must be unique;
updates and deletes are identified by roll number.

## Bonus Features Included

- **Grade calculation** — `Student.getGrade()` derives a letter grade from marks.
- **CSV export** — option 7 in the menu.
- **DB-backed login option** — set `AdminService.USE_DB_AUTH = true` to validate
  against the `admins` table instead of the static credentials.

## Bonus Features Not Included (left as extensions)

- Swing GUI (the app is console-based; the DAO/service layers are already
  separated from the UI, so a `SwingUI.java` could reuse `StudentDAO` and
  `AdminService` directly without changes).
- Subject-wise marks breakdown (currently a single overall `marks` field).

## Notes

- Passwords in this project are stored/compared in plain text for simplicity,
  per the assignment's static-credential requirement. Do not use this
  approach in a production system.
- All SQL queries use `PreparedStatement` to avoid SQL injection.
