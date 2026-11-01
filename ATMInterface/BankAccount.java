package ATMInterface;


public class BankAccount {

    private String accountNumber;
    private String accountHolder;
    private String pin;
    private double balance;

    public BankAccount(String accountNumber, String accountHolder,
                       String pin, double balance) {

        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.pin = pin;
        this.balance = balance;
    }

    // Verify Account Number
    public boolean verifyAccount(String enteredAccountNumber) {
        return accountNumber.equals(enteredAccountNumber);
    }

    // Verify ATM PIN
    public boolean verifyPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    // Get Account Number
    public String getAccountNumber() {
        return accountNumber;
    }

    // Get Account Holder Name
    public String getAccountHolder() {
        return accountHolder;
    }

    // Get Balance
    public double getBalance() {
        return balance;
    }

    // Deposit Money
    public boolean deposit(double amount) {

        if (amount <= 0) {
            return false;
        }

        balance += amount;
        return true;
    }

    // Withdraw Money
    public boolean withdraw(double amount) {

        if (amount <= 0 || amount > balance) {
            return false;
        }

        balance -= amount;
        return true;
    }

    // Change ATM PIN
    public boolean changePin(String oldPin, String newPin) {

        if (!pin.equals(oldPin)) {
            return false;
        }

        if (!newPin.matches("\\d{4}")) {
            return false;
        }

        pin = newPin;
        return true;
    }
}