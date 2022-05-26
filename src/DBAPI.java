import org.postgresql.PGProperty;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Properties;

import static java.nio.file.StandardOpenOption.*;

public class DBAPI {

    public static Connection connect(String username, String password, String database) throws SQLException, IOException {
        String url = "jdbc:postgresql://localhost/";
        Properties props = new Properties();
        props.setProperty(PGProperty.USER.getName(), username);
        props.setProperty(PGProperty.PASSWORD.getName(), password);
        props.setProperty(PGProperty.PG_DBNAME.getName(), database);

        Connection connection = null;
        connection = DriverManager.getConnection(url, props);

        Files.writeString(
            Path.of("src/dbconnection.txt"),
            String.format("%s,%s,%s", username, password, database),
            CREATE, WRITE, TRUNCATE_EXISTING
        );

        return connection;
    }
    public static String[] readConnectInfo() throws IOException {
        try {
            String data = Files.readString(Path.of("src/dbconnection.txt"));
            return data.split(",");
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(null, "Failed to to read src/dbconnection.txt file");
            throw  ex;
        }
    }

    public static void removeByYear(Connection connection, int year) throws SQLException {
        assert connection != null;
        String query = "CALL delete_book_by_year(?);";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, year);
        pstmt.execute();
        pstmt.close();
    }

    public static void getAllBooks(Connection connection, DefaultTableModel model) throws RuntimeException, SQLException {
        assert connection != null;

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String query = "SELECT * FROM books;";
        PreparedStatement pstmt = connection.prepareStatement(query);
        execGetResult(model, pstmt);
    }


    public static void clearTable(Connection connection) throws SQLException {
        assert connection != null;
        String query = "TRUNCATE TABLE books CASCADE;";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.execute();
        pstmt.close();
    }


    public static void createDatabase(Connection connection, String database) throws SQLException {
        assert connection != null;
        String query = String.format("CREATE DATABASE %s;", database, database);
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.execute();
        pstmt.close();
    }

    public static void dropDatabase(Connection connection, String database) throws SQLException {
        assert connection != null;
        String query = String.format("DROP DATABASE IF EXISTS %s;", database);
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.execute();
        pstmt.close();
    }

    public static void execSQLScript(Connection connection, String fname) throws SQLException, IOException {
        assert connection != null;
        String query = Files.readString(Path.of(fname));
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.execute();
        pstmt.close();
    }

    public static void createUser(Connection connection, String username, String password, boolean isAdmin) throws SQLException, IOException {
        String createUserQuery = String.format("CREATE USER %s WITH PASSWORD '%s';", username, password);
        String grantQuery = isAdmin ? "GRANT ALL PRIVILEGES ON books TO %s; GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public to %s;" : "GRANT SELECT ON books TO %s;";
        String formattedGrantQuery = String.format(grantQuery, username, username, username);
        String formattedCreateUserQuery = String.format(createUserQuery, username, password);
        String query = formattedCreateUserQuery + formattedGrantQuery;
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.execute();
        pstmt.close();
    }

    public static void addBook(Connection connection, String name, String surname, int age) throws SQLException {
        assert connection != null;

        String query = "CALL create_book(?,?,?);";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, name);
        pstmt.setString(2, surname);
        pstmt.setInt(3, age);
        pstmt.execute();
        pstmt.close();
    }


    public static void updateBook(Connection connection, String title, String author, int year, int id) throws SQLException {
        assert connection != null;
        String query = "CALL update_book(?,?,?,?);";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, title);
        pstmt.setString(2, author);
        pstmt.setInt(3, year);
        pstmt.setInt(4, id);
        pstmt.execute();
        pstmt.close();
    }

    public static void findBookByYear(Connection connection, DefaultTableModel model, int value) throws RuntimeException, SQLException {
        assert connection != null;
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String query = "SELECT * FROM find_by_year(?);";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, value);
        ResultSet rs = execGetResult(model, pstmt);
    }

    private static ResultSet execGetResult(DefaultTableModel model, PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();
        model.setRowCount(0);
        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String author = rs.getString("author");
            int year = rs.getInt("year");
            model.addRow(new Object[]{id, title, author, year});
        }
        rs.close();
        pstmt.close();
        return rs;
    }
}