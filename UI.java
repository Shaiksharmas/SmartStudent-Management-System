import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * UI.java
 * Console-based user interface: login screen + main menu that drives all
 * CRUD, search, and statistics operations through StudentDAO.
 */
public class UI {

    private final Scanner scanner = new Scanner(System.in);
    private final AdminService adminService = new AdminService();
    private final StudentDAO studentDAO = new StudentDAO();

    public void start() {
        System.out.println("=========================================");
        System.out.println("   SmartStudent - Student Management System");
        System.out.println("=========================================");

        if (!login()) {
            System.out.println("Too many failed attempts. Exiting.");
            return;
        }

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1": addStudent(); break;
                    case "2": viewAllStudents(); break;
                    case "3": updateStudent(); break;
                    case "4": deleteStudent(); break;
                    case "5": searchMenu(); break;
                    case "6": statisticsMenu(); break;
                    case "7": exportCSV(); break;
                    case "0":
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (RuntimeException e) {
                System.out.println("\n[ERROR] " + e.getMessage());
            }
        }
        DatabaseConnection.closeConnection();
    }

    // ---------------- LOGIN ----------------
    private boolean login() {
        int attempts = 3;
        while (attempts > 0) {
            System.out.print("Username: ");
            String user = scanner.nextLine().trim();
            System.out.print("Password: ");
            String pass = scanner.nextLine().trim();

            if (adminService.login(user, pass)) {
                System.out.println("Login successful. Welcome, " + user + "!\n");
                return true;
            } else {
                attempts--;
                System.out.println("Invalid credentials. Attempts remaining: " + attempts);
            }
        }
        return false;
    }

    // ---------------- MENU ----------------
    private void printMenu() {
        System.out.println("\n----------------- MAIN MENU -----------------");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Update Student");
        System.out.println("4. Delete Student");
        System.out.println("5. Search Students");
        System.out.println("6. Statistics");
        System.out.println("7. Export to CSV");
        System.out.println("0. Logout & Exit");
        System.out.print("Choose an option: ");
    }

    // ---------------- CRUD ----------------
    private void addStudent() {
        System.out.println("\n-- Add New Student --");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Roll No: ");
        String rollNo = scanner.nextLine().trim();
        System.out.print("Department: ");
        String dept = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        double marks = readDouble("Marks: ");

        Student s = new Student(name, rollNo, dept, email, phone, marks);
        if (studentDAO.addStudent(s)) {
            System.out.println("Student added successfully.");
        } else {
            System.out.println("Failed to add student. (Roll No may already exist.)");
        }
    }

    private void viewAllStudents() {
        System.out.println("\n-- All Students --");
        List<Student> students = studentDAO.getAllStudents();
        printTable(students);
    }

    private void updateStudent() {
        System.out.println("\n-- Update Student --");
        System.out.print("Enter Roll No of student to update: ");
        String rollNo = scanner.nextLine().trim();

        Student existing = studentDAO.searchByRollNo(rollNo);
        if (existing == null) {
            System.out.println("No student found with roll no " + rollNo);
            return;
        }

        System.out.println("Leave a field blank to keep its current value.");

        System.out.print("Name [" + existing.getName() + "]: ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) existing.setName(name);

        System.out.print("Department [" + existing.getDepartment() + "]: ");
        String dept = scanner.nextLine().trim();
        if (!dept.isEmpty()) existing.setDepartment(dept);

        System.out.print("Email [" + existing.getEmail() + "]: ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) existing.setEmail(email);

        System.out.print("Phone [" + existing.getPhone() + "]: ");
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty()) existing.setPhone(phone);

        System.out.print("Marks [" + existing.getMarks() + "]: ");
        String marksInput = scanner.nextLine().trim();
        if (!marksInput.isEmpty()) {
            try {
                existing.setMarks(Double.parseDouble(marksInput));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, keeping previous marks.");
            }
        }

        if (studentDAO.updateStudent(rollNo, existing)) {
            System.out.println("Student updated successfully.");
        } else {
            System.out.println("Failed to update student.");
        }
    }

    private void deleteStudent() {
        System.out.println("\n-- Delete Student --");
        System.out.print("Enter Roll No of student to delete: ");
        String rollNo = scanner.nextLine().trim();

        System.out.print("Are you sure you want to delete " + rollNo + "? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            return;
        }

        if (studentDAO.deleteStudent(rollNo)) {
            System.out.println("Student deleted successfully.");
        } else {
            System.out.println("No student found with that roll no, or delete failed.");
        }
    }

    // ---------------- SEARCH ----------------
    private void searchMenu() {
        System.out.println("\n-- Search Students --");
        System.out.println("1. By Roll No");
        System.out.println("2. By Name");
        System.out.println("3. By Department");
        System.out.println("4. By Marks Range");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1": {
                System.out.print("Roll No: ");
                String rollNo = scanner.nextLine().trim();
                Student s = studentDAO.searchByRollNo(rollNo);
                if (s == null) {
                    System.out.println("No student found.");
                } else {
                    printTable(java.util.Collections.singletonList(s));
                }
                break;
            }
            case "2": {
                System.out.print("Name (or part of name): ");
                String name = scanner.nextLine().trim();
                printTable(studentDAO.searchByName(name));
                break;
            }
            case "3": {
                System.out.print("Department: ");
                String dept = scanner.nextLine().trim();
                printTable(studentDAO.searchByDepartment(dept));
                break;
            }
            case "4": {
                double min = readDouble("Minimum marks: ");
                double max = readDouble("Maximum marks: ");
                printTable(studentDAO.searchByMarksRange(min, max));
                break;
            }
            default:
                System.out.println("Invalid option.");
        }
    }

    // ---------------- STATISTICS ----------------
    private void statisticsMenu() {
        System.out.println("\n-- Statistics --");
        int total = studentDAO.getTotalStudents();
        System.out.println("Total students: " + total);

        if (total == 0) return;

        Student highest = studentDAO.getHighestScorer();
        Student lowest = studentDAO.getLowestScorer();
        if (highest != null) {
            System.out.printf("Highest marks: %s (%s) - %.2f%n", highest.getName(), highest.getRollNo(), highest.getMarks());
        }
        if (lowest != null) {
            System.out.printf("Lowest marks: %s (%s) - %.2f%n", lowest.getName(), lowest.getRollNo(), lowest.getMarks());
        }

        System.out.println("\nDepartment-wise student count:");
        Map<String, Integer> deptCounts = studentDAO.getDepartmentWiseCount();
        for (Map.Entry<String, Integer> entry : deptCounts.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    // ---------------- EXPORT ----------------
    private void exportCSV() {
        System.out.print("\nEnter output file name (e.g. students.csv): ");
        String fileName = scanner.nextLine().trim();
        if (fileName.isEmpty()) fileName = "students.csv";

        if (studentDAO.exportToCSV(fileName)) {
            System.out.println("Exported successfully to " + fileName);
        } else {
            System.out.println("Export failed.");
        }
    }

    // ---------------- helpers ----------------
    private void printTable(List<Student> students) {
        if (students == null || students.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.printf("%-4s %-20s %-10s %-15s %-25s %-12s %-6s %-3s%n",
                "ID", "Name", "RollNo", "Department", "Email", "Phone", "Marks", "Grd");
        StringBuilder divider = new StringBuilder();
        for (int i = 0; i < 105; i++) divider.append('-');
        System.out.println(divider);
        for (Student s : students) {
            System.out.println(s);
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
