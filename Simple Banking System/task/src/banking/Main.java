package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static String path;

    public static void main(String[] args) {
        path = "jdbc:sqlite:" + args[1];
        createTable();
        welcome();
        greetingOption(inputOption());
    }

    static void createTable() {

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(path);

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card (" +
                        "id INTEGER PRIMARY KEY," +
                        "number TEXT," +
                        "pin TEXT," +
                        "balance INTEGER DEFAULT 0)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addAccountToDB(Account account) {

        String insertAccount = "INSERT INTO card (number, pin) VALUES (?, ?)";

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(path);

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertAccount)) {
                preparedStatement.setString(1, account.getAccountNum());
                preparedStatement.setString(2, account.getPinNum());
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void welcome() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    static int inputOption() {
        return scanner.nextInt();
    }

    static void enterInfo() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your card number:");
        String accountNum = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pinNum = scanner.nextLine();
        Account login = loginAccount(accountNum, pinNum);
        if (login == null) {
            System.out.println("Wrong card number or PIN!");
            welcome();
            greetingOption(inputOption());
        } else {
            System.out.println("You have successfully logged in!");
            login.accountScreen();
            login.accountOption(inputOption());
        }
    }

    static Account loginAccount(String accountNum, String pinNum) {

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(path);

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery("SELECT * FROM card")) {
                    while (result.next()) {
                        int id = result.getInt("id");
                        String number = result.getString("number");
                        String pin = result.getString("pin");
                        int balance = result.getInt("balance");

                        if (number.equals(accountNum) && pin.equals(pinNum)) {
                            return new Account(number, pin, balance);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static void greetingOption(int option) {
        switch (option) {
            case 1:
                Account account = new Account();
                account.display();
                addAccountToDB(account);
                welcome();
                greetingOption(inputOption());
                break;
            case 2:
                enterInfo();
                break;
            case 0:
                System.out.println("Bye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option");
        }
    }
}