import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * StudentDAO.java
 * Data Access Object - all direct database interaction for the students table lives here.
 */
public class StudentDAO {

    // ---------- CREATE ----------
    public boolean addStudent(Student s) {
        String sql = "INSERT INTO students (name, roll_no, department, email, phone, marks) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getName());
            ps.setString(2, s.getRollNo());
            ps.setString(3, s.getDepartment());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setDouble(6, s.getMarks());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
            return false;
        }
    }

    // ---------- READ (ALL) ----------
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching students: " + e.getMessage());
        }
        return list;
    }

    // ---------- UPDATE ----------
    public boolean updateStudent(String rollNo, Student updated) {
        String sql = "UPDATE students SET name=?, department=?, email=?, phone=?, marks=? WHERE roll_no=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, updated.getName());
            ps.setString(2, updated.getDepartment());
            ps.setString(3, updated.getEmail());
            ps.setString(4, updated.getPhone());
            ps.setDouble(5, updated.getMarks());
            ps.setString(6, rollNo);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
            return false;
        }
    }

    // ---------- DELETE ----------
    public boolean deleteStudent(String rollNo) {
        String sql = "DELETE FROM students WHERE roll_no=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rollNo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }

    // ---------- SEARCH ----------
    public Student searchByRollNo(String rollNo) {
        String sql = "SELECT * FROM students WHERE roll_no=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching by roll no: " + e.getMessage());
        }
        return null;
    }

    public List<Student> searchByName(String name) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE name LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching by name: " + e.getMessage());
        }
        return list;
    }

    public List<Student> searchByDepartment(String department) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE department LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + department + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching by department: " + e.getMessage());
        }
        return list;
    }

    public List<Student> searchByMarksRange(double min, double max) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE marks BETWEEN ? AND ? ORDER BY marks DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, min);
            ps.setDouble(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching by marks range: " + e.getMessage());
        }
        return list;
    }

    // ---------- STATISTICS ----------
    public int getTotalStudents() {
        String sql = "SELECT COUNT(*) AS total FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.out.println("Error counting students: " + e.getMessage());
        }
        return 0;
    }

    public Student getHighestScorer() {
        return getExtremeScorer(true);
    }

    public Student getLowestScorer() {
        return getExtremeScorer(false);
    }

    private Student getExtremeScorer(boolean highest) {
        String sql = "SELECT * FROM students ORDER BY marks " + (highest ? "DESC" : "ASC") + " LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.out.println("Error fetching extreme scorer: " + e.getMessage());
        }
        return null;
    }

    public Map<String, Integer> getDepartmentWiseCount() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT department, COUNT(*) AS cnt FROM students GROUP BY department ORDER BY department";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("department"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            System.out.println("Error computing department-wise count: " + e.getMessage());
        }
        return map;
    }

    // ---------- EXPORT (bonus) ----------
    public boolean exportToCSV(String filePath) {
        List<Student> students = getAllStudents();
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write("ID,Name,RollNo,Department,Email,Phone,Marks,Grade\n");
            for (Student s : students) {
                fw.write(String.format("%d,%s,%s,%s,%s,%s,%.2f,%s%n",
                        s.getId(), escape(s.getName()), escape(s.getRollNo()), escape(s.getDepartment()),
                        escape(s.getEmail()), escape(s.getPhone()), s.getMarks(), s.getGrade()));
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error exporting to CSV: " + e.getMessage());
            return false;
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        if (value.contains(",")) {
            return "\"" + value + "\"";
        }
        return value;
    }

    // ---------- helper ----------
    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("roll_no"),
                rs.getString("department"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getDouble("marks")
        );
    }
}
