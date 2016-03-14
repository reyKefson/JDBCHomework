package prog.kiev.ua.task_1;

import java.sql.*;
import java.util.*;

public class App00 {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/flatbase";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "password";

    static Connection connection;

    public static void main(String[] args) {

        try (Scanner sc = new Scanner(System.in);
             Connection conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD)) {
            connection = conn;
            createTable();

            while (true) {
                System.out.println("1: add flat");
                System.out.println("2: add random flats");
                System.out.println("3: delete flat");
                System.out.println("4: change flat");
                System.out.println("5: view flats");
                System.out.println("6: selectByQtyRooms");
                System.out.println("7: selectByAddress");
                System.out.println("8: selectByPrice");

                System.out.print("-> ");

                String s = sc.nextLine();
                switch (s) {
                    case "1":
                        addFlat(sc);
                        break;
                    case "2":
                        insertRandomFlats(sc);
                        break;
                    case "3":
                        deleteFlat(sc);
                        break;
                    case "4":
                        changeFlat(sc);
                        break;
                    case "5":
                        viewFlats();
                        break;
                    case "6":
                        selectByQtyRooms(sc);
                        break;
                    case "7":
                        selectByAddress(sc);
                        break;
                    case "8":
                        selectByPrice(sc);
                        break;
                    default:
                        return;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void createTable() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS Flats");
            st.execute("CREATE TABLE Flats (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "district VARCHAR(20), address VARCHAR(20) NOT NULL, area FLOAT," +
                    "qty_rooms INT NOT NULL , price INT NOT NULL )");
        }
    }

    private static void selectByQtyRooms(Scanner sc) throws SQLException {
        System.out.println("Enter quantity rooms:");
        int qtyRooms = Integer.parseInt(sc.nextLine());

        String sql = "SELECT * FROM Flats WHERE qty_rooms = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, qtyRooms);
            ResultSet rs = ps.executeQuery();
            printResultSet(rs);
        }
    }

    private static void selectByAddress(Scanner sc) throws SQLException {
        System.out.println("Enter address:");
        String address = sc.nextLine();

        String sql = "SELECT * FROM Flats WHERE address = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, address);
            ResultSet rs = ps.executeQuery();
            printResultSet(rs);
        }
    }

    private static void selectByPrice(Scanner sc) throws SQLException {
        System.out.println("Enter price:");
        int price = Integer.parseInt(sc.nextLine());

        String sql = "SELECT * FROM Flats WHERE price = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, price);
            ResultSet rs = ps.executeQuery();
            printResultSet(rs);
        }
    }

    private static void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        while (rs.next()) {
            for (int i = 1; i <= md.getColumnCount(); i++)
                System.out.print(md.getColumnName(i) + ": " + rs.getString(i) + " | ");
            System.out.println();
        }
    }

    private static void addFlat(Scanner sc) throws SQLException {
        System.out.print("Enter address: ");
        String address = sc.nextLine();
        System.out.print("Enter qty_rooms: ");
        int qtyRooms = Integer.parseInt(sc.nextLine());
        System.out.print("Enter price: ");
        int price = Integer.parseInt(sc.nextLine());

        String sql = "INSERT INTO Flats (address, qty_rooms, price) VALUES(?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, address);
            ps.setInt(2, qtyRooms);
            ps.setInt(3, price);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        }
    }

    private static void deleteFlat(Scanner sc) throws SQLException {
        System.out.print("Enter price: ");
        int price = Integer.parseInt(sc.nextLine());

        String sql = "DELETE FROM Flats WHERE price = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, price);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        }
    }

    private static void changeFlat(Scanner sc) throws SQLException {
        System.out.print("Enter address: ");
        String address = sc.nextLine();
        System.out.print("Enter area: ");
        float area = Float.parseFloat(sc.nextLine());
        System.out.print("Enter district: ");
        String district = sc.nextLine();

        String sql = "UPDATE Flats SET area = ?, district = ? WHERE address = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setFloat(1, area);
            ps.setString(2, district);
            ps.setString(3, address);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        }
    }

    private static void insertRandomFlats(Scanner sc) throws SQLException {
        System.out.print("Enter flats count: ");
        int count = Integer.parseInt(sc.nextLine());
        Random rnd = new Random();
        String sql = "INSERT INTO Flats (address, qty_rooms, price) VALUES(?, ?, ?)";

        connection.setAutoCommit(false); // enable transactions
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                ps.setString(1, "address" + i);
                ps.setInt(2, rnd.nextInt(100));
                ps.setInt(3, rnd.nextInt(100));
                ps.executeUpdate();
            }
            connection.commit();
        } catch (Exception ex) {
            connection.rollback();
        } finally {
            connection.setAutoCommit(true); // return to default mode
        }
    }

    private static void viewFlats() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM Flats");
             ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();

            for (int i = 1; i <= md.getColumnCount(); i++)
                System.out.print(md.getColumnName(i) + "\t\t");
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + "\t\t");
                }
                System.out.println();
            }
        }
    }
}
