import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AdminService.java
 * Handles admin authentication.
 *
 * By default this uses static credentials (username: admin / password: admin123)
 * as required by the core spec. Set USE_DB_AUTH = true to instead validate
 * against an "admins" table in MySQL (bonus feature) - see student.sql.
 */
public class AdminService {

    private static final String STATIC_USERNAME = "admin";
    private static final String STATIC_PASSWORD = "admin123";

    private static final boolean USE_DB_AUTH = false;

    public boolean login(String username, String password) {
        if (username == null || password == null) return false;

        if (USE_DB_AUTH) {
            return loginFromDatabase(username, password);
        }
        return username.equals(STATIC_USERNAME) && password.equals(STATIC_PASSWORD);
    }

    private boolean loginFromDatabase(String username, String password) {
        String sql = "SELECT * FROM admins WHERE username=? AND password=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password); // NOTE: for production, store hashed passwords, not plaintext
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error validating admin login: " + e.getMessage());
            return false;
        }
    }
}
