import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection.java
 * Handles the JDBC connection to the MySQL "smartstudent" database.
 *
 * Configure the connection with DB_URL, DB_USER, and DB_PASSWORD environment
 * variables. Defaults are provided for a typical local MySQL installation.
 * Requires the MySQL Connector/J jar on the classpath (see README.md).
 */
public class DatabaseConnection {

    private static final String DEFAULT_DB_URL =
        "jdbc:mysql://localhost:3306/smartstudent?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_URL = getConfig("DB_URL", DEFAULT_DB_URL);
    private static final String DB_USER = getConfig("DB_USER", "root");
    private static final String DB_PASSWORD = getConfig("DB_PASSWORD", "Student2026Pass");

    private static Connection connection = null;

    private DatabaseConnection() {
        // utility class, prevent instantiation
    }

    private static String getConfig(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.trim().isEmpty() ? defaultValue : value;
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "MySQL JDBC Driver not found on the classpath.\n" +
                "  -> Make sure mysql-connector-j-x.x.x.jar is in the lib/ folder,\n" +
                "     and that you are running the app in a way that includes it\n" +
                "     (VS Code Run/Debug button, or java -cp .:lib/<jar-name>.jar Main).", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Could not connect to the database.\n" +
                "  -> Check that MySQL is running, that student.sql has been imported,\n" +
                "     and that DB_URL / DB_USER / DB_PASSWORD are correct.\n" +
                "     (Set them as environment variables; do not put passwords in source code.)\n" +
                "  -> Details: " + e.getMessage(), e);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
