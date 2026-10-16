package internetbilling.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Centralized Database Connection Manager.
 * Handles JDBC connectivity to MySQL Server and MySQL Workbench schema (InternetBillingDB).
 */
public class DBConnection {

    private static final String CONFIG_FILE = "db.properties";
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Engine: "MYSQL"
    private static String dbEngine = "MYSQL";

    // Configuration defaults
    private static String host = "localhost";
    private static int port = 3307;
    private static String database = "InternetBillingDB";
    private static String username = "root";
    private static String password = "";
    private static boolean trustServerCertificate = true;
    private static boolean encrypt = false;
    private static boolean integratedSecurity = false;

    static {
        loadConfiguration();
        try {
            Class.forName(MYSQL_DRIVER);
        } catch (ClassNotFoundException ignored) {
        }
    }

    private static File getConfigFile() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            File sub = new File("InternetBillingManagementSystem", CONFIG_FILE);
            if (sub.exists()) {
                return sub;
            }
        }
        return file;
    }

    /**
     * Loads database configuration from db.properties if it exists.
     */
    public static void loadConfiguration() {
        File file = getConfigFile();
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Properties props = new Properties();
                props.load(fis);
                dbEngine = props.getProperty("db.engine", "MYSQL");
                host = props.getProperty("db.host", host);
                port = Integer.parseInt(props.getProperty("db.port", String.valueOf(port)));
                database = props.getProperty("db.name", database);
                username = props.getProperty("db.user", username);
                password = props.getProperty("db.password", password);
                trustServerCertificate = Boolean.parseBoolean(props.getProperty("db.trustServerCertificate", "true"));
                encrypt = Boolean.parseBoolean(props.getProperty("db.encrypt", "false"));
                integratedSecurity = Boolean.parseBoolean(props.getProperty("db.integratedSecurity", "false"));
            } catch (Exception e) {
                System.err.println("Warning: Could not read db.properties, using defaults: " + e.getMessage());
            }
        } else {
            saveConfiguration();
        }
    }

    /**
     * Saves database configuration to db.properties across workspace locations.
     */
    public static void saveConfiguration() {
        Properties props = new Properties();
        props.setProperty("db.engine", "MYSQL");
        props.setProperty("db.host", host);
        props.setProperty("db.port", String.valueOf(port));
        props.setProperty("db.name", database);
        props.setProperty("db.user", username);
        props.setProperty("db.password", password);
        props.setProperty("db.trustServerCertificate", String.valueOf(trustServerCertificate));
        props.setProperty("db.encrypt", String.valueOf(encrypt));
        props.setProperty("db.integratedSecurity", String.valueOf(integratedSecurity));

        // Save to current directory
        File target = new File(CONFIG_FILE);
        try (FileOutputStream fos = new FileOutputStream(target)) {
            props.store(fos, "Database Connection Configuration for Internet Billing Management System");
        } catch (IOException e) {
            System.err.println("Warning: Could not save db.properties: " + e.getMessage());
        }

        // Also save to subfolder if running from workspace root
        File sub = new File("InternetBillingManagementSystem", CONFIG_FILE);
        if (sub.getParentFile() != null && sub.getParentFile().exists()) {
            try (FileOutputStream fos = new FileOutputStream(sub)) {
                props.store(fos, "Database Connection Configuration for Internet Billing Management System");
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Returns the appropriate table identifier for 'Usage' to prevent MySQL keyword conflict.
     */
    public static String getUsageTable() {
        return "`Usage`";
    }

    /**
     * Constructs JDBC connection URL for MySQL.
     */
    public static String getMySQLConnectionString() {
        return "jdbc:mysql://" + host + ":" + port + "/" + database +
               "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=3000";
    }

    /**
     * Gets a database connection according to the configured engine.
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(MYSQL_DRIVER);
        } catch (ClassNotFoundException ex) {
            String msg = "MySQL JDBC Driver (" + MYSQL_DRIVER + ") not found.\n" +
                         "Please ensure mysql-connector-j-8.3.0.jar is in the lib/ folder\n" +
                         "and added to project Libraries in NetBeans.";
            if (!java.awt.GraphicsEnvironment.isHeadless()) {
                JOptionPane.showMessageDialog(null, msg, "MySQL Driver Missing", JOptionPane.ERROR_MESSAGE);
            }
            throw new SQLException(msg, ex);
        }

        try {
            return DriverManager.getConnection(getMySQLConnectionString(), username, password);
        } catch (SQLException ex) {
            String helpMsg = "Could not connect to MySQL Server at " + host + ":" + port + " (Database: " + database + ").\n\n";
            int errorCode = ex.getErrorCode();
            if (errorCode == 1045) {
                helpMsg += "Error 1045: Access denied for user '" + username + "'.\n" +
                           "Please update the password in db.properties to match your MySQL root password.";
            } else if (errorCode == 1049) {
                helpMsg += "Error 1049: Database '" + database + "' does not exist.\n" +
                           "Please open database.sql in MySQL Workbench and execute it to create the schema.";
            } else if (ex.getMessage() != null && (ex.getMessage().contains("Communications link failure") || ex.getMessage().contains("Connection refused"))) {
                helpMsg += "Connection Refused: MySQL Server service is not running.\n" +
                           "Please ensure MySQL Server 8.0 (MySQL80) service is running on Windows.";
            } else {
                helpMsg += "Error: " + ex.getMessage();
            }

            if (!java.awt.GraphicsEnvironment.isHeadless()) {
                JOptionPane.showMessageDialog(null, helpMsg, "MySQL Connection Error", JOptionPane.ERROR_MESSAGE);
            }
            throw new SQLException(helpMsg, ex);
        }
    }

    /**
     * Tests the database connection.
     * 
     * @return true if successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Getters and Setters for configuration
    public static String getDbEngine() {
        return dbEngine;
    }

    public static void setDbEngine(String engine) {
        dbEngine = engine;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String h) {
        host = h;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int p) {
        port = p;
    }

    public static String getDatabase() {
        return database;
    }

    public static void setDatabase(String db) {
        database = db;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String u) {
        username = u;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String p) {
        password = p;
    }

    public static boolean isTrustServerCertificate() {
        return trustServerCertificate;
    }

    public static void setTrustServerCertificate(boolean t) {
        trustServerCertificate = t;
    }

    public static boolean isEncrypt() {
        return encrypt;
    }

    public static void setEncrypt(boolean e) {
        encrypt = e;
    }

    public static boolean isIntegratedSecurity() {
        return integratedSecurity;
    }

    public static void setIntegratedSecurity(boolean i) {
        integratedSecurity = i;
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Internet Billing Management System - DB Test Utility");
        System.out.println("==================================================");
        loadConfiguration();
        System.out.println("Engine: " + dbEngine);
        System.out.println("Target: " + host + ":" + port + "/" + database);
        System.out.println("User: " + username);
        System.out.println("Attempting connection...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println(">>> Connection SUCCESSFUL! <<<");
                System.out.println("Database Product: " + conn.getMetaData().getDatabaseProductName() + " " + conn.getMetaData().getDatabaseProductVersion());
                try (Statement stmt = conn.createStatement()) {
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Customers")) {
                        if (rs.next()) {
                            System.out.println("Customer records found: " + rs.getInt(1));
                        }
                    }
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Plans")) {
                        if (rs.next()) {
                            System.out.println("Plan records found: " + rs.getInt(1));
                        }
                    }
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + getUsageTable())) {
                        if (rs.next()) {
                            System.out.println("Usage records found: " + rs.getInt(1));
                        }
                    }
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Bills")) {
                        if (rs.next()) {
                            System.out.println("Bill records found: " + rs.getInt(1));
                        }
                    }
                }
            } else {
                System.err.println("Connection was null or closed.");
            }
        } catch (Exception e) {
            System.err.println("Connection FAILED: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("==================================================");
    }
}
