package ATMInterface;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionManager {

    private final List<Transaction> transactions;

    // Constructor
    public TransactionManager() {
        transactions = new ArrayList<>();
    }

    // Add New Transaction
    public void recordTransaction(String type, double amount,
                                  double balanceAfterTransaction) {

        Transaction transaction = new Transaction(
                type,
                amount,
                balanceAfterTransaction
        );

        transactions.add(transaction);
    }

    // Get All Transaction History
    public List<String> getTransactionHistory() {

        List<String> history = new ArrayList<>();

        for (Transaction transaction : transactions) {
            history.add(transaction.toString());
        }

        return Collections.unmodifiableList(history);
    }

    // Get Total Transactions
    public int getTotalTransactions() {
        return transactions.size();
    }

    // Clear Transaction History
    public void clearHistory() {
        transactions.clear();
    }
}