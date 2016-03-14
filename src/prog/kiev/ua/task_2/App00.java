package prog.kiev.ua.task_2;

import java.sql.*;
import java.util.Scanner;

public class App00 {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/ordersbase";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "password";
    private static Connection connection;

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in);
             Connection con =  DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD)) {
            connection = con;
            createTables();

            while (true) {
                System.out.println("1: create client");
                System.out.println("2: create product");
                System.out.println("3: create order");
                System.out.println("4: view table");
                System.out.print("-> ");

                String s = sc.nextLine();
                switch (s) {
                    case "1":
                        createClient(sc);
                        break;
                    case "2":
                        createProduct(sc);
                        break;
                    case "3":
                        createOrder(sc);
                        break;
                    case "4":
                        viewTable(sc);
                        break;
                    default:
                        return;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void createTables() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS Products, Clients, Orders");

            st.execute("CREATE TABLE Products (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, price INT NOT NULL )");

            st.execute("CREATE TABLE Clients (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, phone VARCHAR(15) NOT NULL)");

            st.execute("CREATE TABLE Orders (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "prod_id INT NOT NULL REFERENCES Products (id)," +
                    "client_id INT NOT NULL REFERENCES Clients (id)," +
                    "info VARCHAR (100))");
        }
    }

    private static void createClient(Scanner sc) throws SQLException {
        System.out.println("Enter name:");
        String name = sc.nextLine();
        System.out.println("Enter phone:");
        String phone = sc.nextLine();

        String query = "INSERT INTO Clients (name, phone) VALUES(?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.executeUpdate();
        }
    }

    private static void createProduct(Scanner sc) throws SQLException {
        System.out.println("Enter name:");
        String name = sc.nextLine();
        System.out.println("Enter price:");
        String price = sc.nextLine();

        String query = "INSERT INTO Products (name, price) VALUES(?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, price);
            statement.executeUpdate();
        }
    }

    private static void createOrder(Scanner sc) throws SQLException {
        System.out.println("Enter product name:");
        String productName = sc.nextLine();
        System.out.println("Enter client phone:");
        String clientPhone = sc.nextLine();
        System.out.println("Enter info:");
        String info = sc.nextLine();

        String query = "INSERT INTO orders(prod_id, client_id, info) " +
                "VALUES ((SELECT id FROM products WHERE name = ?), (SELECT id FROM clients WHERE phone = ?), ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, productName);
            statement.setString(2, clientPhone);
            statement.setString(3, info);
            statement.executeUpdate();
        }
    }

    private static void viewTable(Scanner sc) throws SQLException {
        System.out.println("Enter table name:");
        String tableName = sc.nextLine();

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + tableName);
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
