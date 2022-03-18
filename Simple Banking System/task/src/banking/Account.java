package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

import static banking.Main.*;

class Account {
    private final String accountNum;
    private final String pinNum;
    private int balance;

    public Account() {
        accountNum = createAccountNum();
        pinNum = createPinNum();
        balance = 0;
    }

    public Account(String accountNum, String pinNum, int balance) {
        this.accountNum = accountNum;
        this.pinNum = pinNum;
        this.balance = balance;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public String getPinNum() {
        return pinNum;
    }

    private int inputOption() {
        return scanner.nextInt();
    }

    private String createAccountNum() {
        Random random = new Random();
        int accountInterval = 999999999 - 100000000 + 1;
        String BIN = "400000";
        String accountIdentifier = String.valueOf(random.nextInt(accountInterval) + 100000000);
        String checkSum = luhn(BIN + accountIdentifier);
        return BIN + accountIdentifier + checkSum;
    }

    private String createPinNum() {
        Random random = new Random();
        int pinInterval = 9999 - 1000 + 1;
        return String.valueOf(random.nextInt(pinInterval) + 1000);
    }

    void display() {
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(accountNum);
        System.out.println("Your card PIN:");
        System.out.println(pinNum);
    }

    private String luhn(String nums) {
        String[] numArray = nums.split("");
        int total = 0;
        int digitCount = 1;
        for(String num : numArray) {
            int n = Integer.parseInt(num);
            if (digitCount % 2 != 0) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            total += n;
            digitCount++;
        }
        return total % 10 == 0 ? "0" : String.valueOf(10 - (total % 10));
    }

    void accountScreen() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }

    void accountOption(int option) {
        switch (option) {
            case 1:
                System.out.println("Balance: " + balance);
                accountScreen();
                accountOption(inputOption());
                break;
            case 2:
                System.out.println("Enter income:");
                int income = inputOption();
                balance += income;
                transfer(accountNum, income);
                System.out.println("Income was added!");
                accountScreen();
                accountOption(inputOption());
                break;
            case 3:
                Scanner scanner = new Scanner(System.in);
                System.out.println("Transfer");
                System.out.println("Enter card number:");
                String transferNumber = scanner.nextLine();
                if (isTransferNumberValid(transferNumber)) {
                    System.out.println("Enter how much money you want to transfer:");
                    int transferAmount = inputOption();
                    if (transferAmount > balance) {
                        System.out.println("Not enough money!");
                    } else {
                        System.out.println("Success!");
                        transfer(transferNumber, transferAmount);
                        transfer(accountNum, -transferAmount );

                    }

                }
                accountScreen();
                accountOption(inputOption());
                break;
            case 4:
                closeAccount();
                System.out.println("The account has been closed!");
                welcome();
                greetingOption(inputOption());
                break;
            case 5:
                System.out.println("You have successfully logged out!");
                welcome();
                greetingOption(inputOption());
                break;
            case 0:
                System.out.println("Bye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option");
        }
    }

    private void closeAccount() {
        String deleteInfo = "DELETE FROM card WHERE number = ?";

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(path);

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteInfo)) {
                preparedStatement.setString(1, accountNum);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void transfer(String number, int amount) {
        String updateInfo = "UPDATE card SET balance = balance + ? WHERE number = ?";

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(path);

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateInfo)) {
                preparedStatement.setInt(1,amount);
                preparedStatement.setString(2, number);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isTransferNumberValid(String transferNumber) {
        String num = transferNumber.substring(0, 15);
        String last = transferNumber.substring(transferNumber.length() - 1);
        if (!luhn(num).equals(last)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return false;
        }

        if (transferNumber.equals(accountNum)) {
            System.out.println("You can't transfer money to the same account!");
            return false;
        }

        if (!isAccountValid(transferNumber)) {
            System.out.println("Such a card does not exist.");
            return false;
        }
        return true;
    }

    boolean isAccountValid(String accountNumber) {

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(path);

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery("SELECT number FROM card")) {
                    while (result.next()) {

                        String number = result.getString("number");

                        if (number.equals(accountNumber)) {
                            return true;
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
        return false;
    }
}
