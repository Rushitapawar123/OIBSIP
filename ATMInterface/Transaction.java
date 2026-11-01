package ATMInterface;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    private String type;
    private double amount;
    private double balanceAfterTransaction;
    private String dateTime;

    // Constructor
    public Transaction(String type, double amount,
                      double balanceAfterTransaction) {

        this.type = type;
        this.amount = amount;
        this.balanceAfterTransaction = balanceAfterTransaction;

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        this.dateTime = LocalDateTime.now().format(formatter);
    }

    // Get Transaction Type
    public String getType() {
        return type;
    }

    // Get Transaction Amount
    public double getAmount() {
        return amount;
    }

    // Get Balance After Transaction
    public double getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    // Get Transaction Date and Time
    public String getDateTime() {
        return dateTime;
    }

    // Display Transaction Details
    @Override
    public String toString() {

        return "Type: " + type
                + " | Amount: ₹" + amount
                + " | Balance: ₹" + balanceAfterTransaction
                + " | Date: " + dateTime;
    }
}