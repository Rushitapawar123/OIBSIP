package ATMInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ATMFrame extends JFrame {

    // Soft pastel color theme
    private final Color BACKGROUND = new Color(245, 243, 250);
    private final Color CARD_COLOR = new Color(233, 230, 242);
    private final Color PURPLE = new Color(124, 111, 232);
    private final Color LIGHT_PURPLE = new Color(224, 220, 250);
    private final Color BLUE = new Color(168, 199, 250);
    private final Color GREEN = new Color(168, 213, 186);
    private final Color TEXT_COLOR = new Color(48, 48, 71);
    private final Color MUTED = new Color(120, 118, 140);

    private ATMOperations operations;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JLabel balanceLabel;
    private JLabel welcomeLabel;

    private JTextField accountField;
    private JPasswordField pinField;

    public ATMFrame() {

        // Demo account
        BankAccount account = new BankAccount(
                "1234567890",
                "Rahul Sharma",
                "1234",
                25000
        );

        TransactionManager transactionManager = new TransactionManager();

        operations = new ATMOperations(account, transactionManager);

        setTitle("Nova ATM | Smart Banking");
        setSize(1050, 700);
        setMinimumSize(new Dimension(850, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createDashboardPanel(), "DASHBOARD");

        add(mainPanel);

        cardLayout.show(mainPanel, "LOGIN");
    }

    // --LOGIN SCREEN
    private JPanel createLoginPanel() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(new EmptyBorder(40, 45, 40, 45));

        card.setPreferredSize(new Dimension(420, 490));

        JLabel logo = new JLabel("NOVA BANK");
        logo.setFont(new Font("Arial", Font.BOLD, 27));
        logo.setForeground(PURPLE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Welcome Back!");
        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setForeground(TEXT_COLOR);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Login to access your account");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel accountLabel = createLabel("Account Number");

        accountField = new JTextField();
        styleInput(accountField);

        JLabel pinLabel = createLabel("4-Digit PIN");

        pinField = new JPasswordField();
        styleInput(pinField);

        JButton loginButton = createButton("LOGIN", PURPLE);

        loginButton.addActionListener(e -> loginUser());

        JLabel demo = new JLabel(
                "<html><center>Demo Account: 1234567890<br>Demo PIN: 1234</center></html>"
        );

        demo.setFont(new Font("Arial", Font.PLAIN, 12));
        demo.setForeground(MUTED);
        demo.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(logo);
        card.add(Box.createVerticalStrut(20));
        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(35));

        card.add(accountLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(accountField);
        card.add(Box.createVerticalStrut(20));

        card.add(pinLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(pinField);
        card.add(Box.createVerticalStrut(30));

        card.add(loginButton);
        card.add(Box.createVerticalStrut(25));
        card.add(demo);

        panel.add(card);

        return panel;
    }

    //  DASHBOARD 

    private JPanel createDashboardPanel() {

        JPanel panel = new JPanel(new BorderLayout(25, 25));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel logo = new JLabel("NOVA BANK");
        logo.setFont(new Font("Arial", Font.BOLD, 25));
        logo.setForeground(PURPLE);

        JButton logoutButton = createButton("Logout", LIGHT_PURPLE);
        logoutButton.setForeground(TEXT_COLOR);

        logoutButton.addActionListener(e -> logoutUser());

        header.add(logo, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);

        // Main content
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(TEXT_COLOR);

        JLabel message = new JLabel("What would you like to do today?");
        message.setFont(new Font("Arial", Font.PLAIN, 15));
        message.setForeground(MUTED);

        // Balance card
        JPanel balanceCard = new JPanel();
        balanceCard.setLayout(new BoxLayout(balanceCard, BoxLayout.Y_AXIS));
        balanceCard.setBackground(LIGHT_PURPLE);
        balanceCard.setBorder(new EmptyBorder(25, 30, 25, 30));
        balanceCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel balanceTitle = new JLabel("AVAILABLE BALANCE");
        balanceTitle.setFont(new Font("Arial", Font.BOLD, 14));
        balanceTitle.setForeground(TEXT_COLOR);

        balanceLabel = new JLabel("₹ 0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 35));
        balanceLabel.setForeground(TEXT_COLOR);

        balanceCard.add(balanceTitle);
        balanceCard.add(Box.createVerticalStrut(12));
        balanceCard.add(balanceLabel);

        // Action buttons
        JPanel actions = new JPanel(new GridLayout(2, 2, 20, 20));
        actions.setOpaque(false);

        JButton depositButton = createButton("＋  Deposit Money", GREEN);
        JButton withdrawButton = createButton("－  Withdraw Money", BLUE);
        JButton statementButton = createButton("▤  Mini Statement", CARD_COLOR);
        JButton pinButton = createButton("⚙  Change PIN", CARD_COLOR);

        depositButton.setForeground(TEXT_COLOR);
        withdrawButton.setForeground(TEXT_COLOR);
        statementButton.setForeground(TEXT_COLOR);
        pinButton.setForeground(TEXT_COLOR);

        depositButton.addActionListener(e -> depositMoney());
        withdrawButton.addActionListener(e -> withdrawMoney());
        statementButton.addActionListener(e -> showStatement());
        pinButton.addActionListener(e -> changePin());

        actions.add(depositButton);
        actions.add(withdrawButton);
        actions.add(statementButton);
        actions.add(pinButton);

        content.add(welcomeLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(message);
        content.add(Box.createVerticalStrut(30));
        content.add(balanceCard);
        content.add(Box.createVerticalStrut(30));
        content.add(actions);

        // Footer
        JLabel footer = new JLabel(
                "Secure Banking Experience  |  Nova ATM"
        );

        footer.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.setForeground(MUTED);
        footer.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(header, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    //  LOGIN FUNCTION 

    private void loginUser() {

        String accountNumber = accountField.getText().trim();
        String pin = new String(pinField.getPassword());

        if (accountNumber.isEmpty() || pin.isEmpty()) {
            showError("Please enter account number and PIN.");
            return;
        }

        if (operations.login(accountNumber, pin)) {

            welcomeLabel.setText(
                    "Welcome, " + operations.getAccountHolder() + "!"
            );

            updateBalance();

            pinField.setText("");

            cardLayout.show(mainPanel, "DASHBOARD");

        } else {
            showError("Invalid Account Number or PIN.");
        }
    }

    //  BALANCE 

    private void updateBalance() {

        double balance = operations.checkBalance();

        balanceLabel.setText(
                String.format("₹ %.2f", balance)
        );
    }

    // DEPOSIT 

    private void depositMoney() {

        String input = JOptionPane.showInputDialog(
                this,
                "Enter amount to deposit:",
                "Deposit Money",
                JOptionPane.PLAIN_MESSAGE
        );

        if (input == null) return;

        try {

            double amount = Double.parseDouble(input.trim());

            if (operations.deposit(amount)) {

                updateBalance();

                showSuccess("Money deposited successfully!");

            } else {
                showError("Please enter a valid amount.");
            }

        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
        }
    }

    //  WITHDRAW 

    private void withdrawMoney() {

        String input = JOptionPane.showInputDialog(
                this,
                "Enter amount to withdraw:",
                "Withdraw Money",
                JOptionPane.PLAIN_MESSAGE
        );

        if (input == null) return;

        try {

            double amount = Double.parseDouble(input.trim());

            if (operations.withdraw(amount)) {

                updateBalance();

                showSuccess("Please collect your cash!");

            } else {
                showError(
                        "Invalid amount or insufficient balance."
                );
            }

        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
        }
    }

    // MINI STATEMENT 

    private void showStatement() {

        List<String> history = operations.getMiniStatement();

        StringBuilder statement = new StringBuilder();

        statement.append("       NOVA BANK\n");
        statement.append("-----------------------------\n\n");

        if (history.isEmpty()) {

            statement.append("No transactions available.");

        } else {

            for (String transaction : history) {

                statement.append(transaction);
                statement.append("\n\n");
            }
        }

        JTextArea area = new JTextArea(statement.toString());

        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setBackground(CARD_COLOR);
        area.setForeground(TEXT_COLOR);
        area.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(500, 350));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Mini Statement",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // CHANGE PIN 

    private void changePin() {

        JPasswordField oldPinField = new JPasswordField();
        JPasswordField newPinField = new JPasswordField();
        JPasswordField confirmPinField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));

        panel.add(new JLabel("Current PIN:"));
        panel.add(oldPinField);

        panel.add(new JLabel("New PIN:"));
        panel.add(newPinField);

        panel.add(new JLabel("Confirm PIN:"));
        panel.add(confirmPinField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Change ATM PIN",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        String oldPin = new String(oldPinField.getPassword());
        String newPin = new String(newPinField.getPassword());
        String confirmPin = new String(confirmPinField.getPassword());

        if (!newPin.equals(confirmPin)) {
            showError("New PIN and Confirm PIN do not match.");
            return;
        }

        if (operations.changePin(oldPin, newPin)) {

            showSuccess("Your PIN has been changed successfully!");

        } else {
            showError("Invalid current PIN or new PIN.");
        }
    }

    //  LOGOUT 

    private void logoutUser() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            operations.logout();

            accountField.setText("");
            pinField.setText("");

            cardLayout.show(mainPanel, "LOGIN");
        }
    }

    //  UI HELPERS 

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);

        return label;
    }

    private void styleInput(JTextField field) {

        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setForeground(TEXT_COLOR);
        field.setBackground(new Color(240, 239, 247));

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(215, 211, 230)
                        ),
                        new EmptyBorder(12, 12, 12, 12)
                )
        );

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setPreferredSize(new Dimension(300, 45));
    }

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(color);

        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(14, 18, 14, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setOpaque(true);
        button.setBorderPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "ATM Alert",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showSuccess(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}