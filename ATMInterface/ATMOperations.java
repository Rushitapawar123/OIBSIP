package ATMInterface;

import java.util.List;

public class ATMOperations {

    private BankAccount account;
    private TransactionManager transactionManager;
    private boolean loggedIn = false;

    // Constructor
    public ATMOperations(BankAccount account,
                         TransactionManager transactionManager) {

        this.account = account;
        this.transactionManager = transactionManager;
    }

    // Login
    public boolean login(String accountNumber, String pin) {

        if (account.verifyAccount(accountNumber)
                && account.verifyPin(pin)) {

            loggedIn = true;
            return true;
        }

        return false;
    }

    // Logout
    public void logout() {
        loggedIn = false;
    }

    // Check Login Status
    public boolean isLoggedIn() {
        return loggedIn;
    }

    // Get Account Holder
    public String getAccountHolder() {
        return account.getAccountHolder();
    }

    // Get Account Number
    public String getAccountNumber() {
        return account.getAccountNumber();
    }

    // Check Balance
    public double checkBalance() {

        if (!loggedIn) {
            throw new IllegalStateException("Please login first.");
        }

        return account.getBalance();
    }

    // Deposit Money
    public boolean deposit(double amount) {

        if (!loggedIn || !account.deposit(amount)) {
            return false;
        }

        transactionManager.recordTransaction(
                "Deposit",
                amount,
                account.getBalance()
        );

        return true;
    }

    // Withdraw Money
    public boolean withdraw(double amount) {

        if (!loggedIn || !account.withdraw(amount)) {
            return false;
        }

        transactionManager.recordTransaction(
                "Withdraw",
                amount,
                account.getBalance()
        );

        return true;
    }

    // Change PIN
    public boolean changePin(String oldPin, String newPin) {

        if (!loggedIn) {
            return false;
        }

        boolean changed = account.changePin(oldPin, newPin);

        if (changed) {
            transactionManager.recordTransaction(
                    "PIN Changed",
                    0,
                    account.getBalance()
            );
        }

        return changed;
    }

    // Mini Statement
    public List<String> getMiniStatement() {

        if (!loggedIn) {
            throw new IllegalStateException("Please login first.");
        }

        return transactionManager.getTransactionHistory();
    }
}
