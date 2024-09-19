package pl.galusgaming.models;

import java.sql.*;
import java.util.*;

public class Biblioteka {
    public static final String DRIVER = "org.mariadb.jdbc.Driver";
    public static final String DB_URL = "jdbc:mariadb://localhost:3306/biblioteka";

    private Connection conn;
    private Statement stat;
    public Biblioteka() {
        try {
            Class.forName(Biblioteka.DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Brak sterownika JDBC");
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(DB_URL, "root", "");
            stat = conn.createStatement();
        } catch (SQLException e) {
            System.err.println("Problem z otwarciem polaczenia");
            e.printStackTrace();
        }
        createTables();
    }
    public boolean createTables() {
        String createBooks = "CREATE TABLE IF NOT EXISTS books (id_book INTEGER PRIMARY KEY AUTO_INCREMENT, title VARCHAR(255), author VARCHAR(255), year INT)";
        String createCategories = "CREATE TABLE IF NOT EXISTS categories (id_category INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255))";
        String createUsers = "CREATE TABLE IF NOT EXISTS users (id_user INTEGER PRIMARY KEY AUTO_INCREMENT, FirstName VARCHAR(255), LastName VARCHAR(255))";
        String createLoans = "CREATE TABLE IF NOT EXISTS loans (id_loan INTEGER PRIMARY KEY AUTO_INCREMENT, id_book INT, id_user INT, date_from date, date_to date)";
        try {
            stat.execute(createBooks);
            stat.execute(createCategories);
            stat.execute(createUsers);
            stat.execute(createLoans);
        } catch (SQLException e) {
            System.err.println("Blad przy tworzeniu tabeli");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean insertBook(String title, String author, int year) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "INSERT INTO books VALUES (NULL, ?, ?, ?);");
            prepStmt.setString(1, title);
            prepStmt.setString(2, author);
            prepStmt.setInt(3, year);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy wstawianiu ksiazki");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean insertCategory(String name) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "INSERT INTO categories VALUES (NULL, ?);");
            prepStmt.setString(1, name);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy wstawianiu kategorii");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean insertUser(String FirstName, String LastName) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "INSERT INTO users VALUES (NULL, ?, ?);");
            prepStmt.setString(1, FirstName);
            prepStmt.setString(2, LastName);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy wstawianiu uzytkownika");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean insertLoan(int id_book, int id_user, String date_from, String date_to) {
        try {
            // Convert date format from 'DD.MM.YYYY' to 'YYYY-MM-DD'
            String formattedDateFrom = date_from.substring(6) + "-" + date_from.substring(3, 5) + "-" + date_from.substring(0, 2);
            String formattedDateTo = date_to.substring(6) + "-" + date_to.substring(3, 5) + "-" + date_to.substring(0, 2);

            PreparedStatement prepStmt = conn.prepareStatement(
                    "INSERT INTO loans VALUES (NULL, ?, ?, ?, ?);");
            prepStmt.setInt(1, id_book -1);
            prepStmt.setInt(2, id_user-1);
            prepStmt.setString(3, formattedDateFrom);
            prepStmt.setString(4, formattedDateTo);
            prepStmt.execute();
        } catch (SQLException e) {
            System.err.println("Blad przy wstawianiu wypozyczenia");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Books> selectBooks() {
        List<Books> books = new LinkedList<>();
        try {
            ResultSet result = stat.executeQuery("SELECT * FROM books");
            int id;
            String title, author;
            int year;
            while (result.next()) {
                id = result.getInt("id_book");
                title = result.getString("title");
                author = result.getString("author");
                year = result.getInt("year");
                books.add(new Books(id, title, author, year));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return books;
    }

    public List<Categories> selectCategories() {
        List<Categories> categories = new LinkedList<>();
        try {
            ResultSet result = stat.executeQuery("SELECT * FROM categories");
            int id;
            String name;
            while (result.next()) {
                id = result.getInt("id_category");
                name = result.getString("name");
                categories.add(new Categories(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return categories;
    }

    public List<Users> selectUsers() {
        List<Users> users = new LinkedList<>();
        try {
            ResultSet result = stat.executeQuery("SELECT * FROM users");
            int id;
            String FirstName, LastName;
            while (result.next()) {
                id = result.getInt("id_user");
                FirstName = result.getString("FirstName");
                LastName = result.getString("LastName");
                users.add(new Users(id, FirstName, LastName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return users;
    }

    public List<Loans> selectLoans() {
        List<Loans> loans = new LinkedList<>();
        try {
            ResultSet result = stat.executeQuery("SELECT * FROM loans");
            int id, id_book, id_user;
            String date_from, date_to;

            while (result.next()) {
                id = result.getInt("id_loan");
                id_book = result.getInt("id_book");
                id_user = result.getInt("id_user");
                date_from = result.getString("date_from");//YYYY-MM-DD --> DD.MM.YYYY
                date_to = result.getString("date_to");
                String formattedDateFrom = date_from.substring(8,10) + "-" + date_from.substring(5,7) + "-" + date_from.substring(0, 4);
                String formattedDateTo = date_from.substring(8,10) + "-" + date_from.substring(5,7) + "-" + date_from.substring(0, 4);
                loans.add(new Loans(id, id_book, id_user, formattedDateFrom, formattedDateTo));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return loans;
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("Problem z zamknieciem polaczenia");
            e.printStackTrace();
        }
    }
}
