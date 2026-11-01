import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private final GameLogic gameLogic = new GameLogic();
    private final ScoreManager scoreManager = new ScoreManager();

    private JComboBox<String> difficultyBox;
    private JTextField guessField;
    private JLabel rangeLabel, attemptsLabel, timerLabel;
    private JLabel statusLabel, scoreLabel, highScoreLabel, hintLabel;
    private JButton guessButton, hintButton, newGameButton;

    private int seconds;
    private Timer timer;

    public Main() {
        createGUI();
        startNewGame();
    }

    private void createGUI() {
        setTitle("Number Guessing Game");
        setSize(560, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Color background = new Color(25, 28, 40);
        Color panelColor = new Color(39, 44, 60);
        Color textColor = new Color(245, 245, 245);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(background);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        JLabel title = new JLabel("Number Guessing Game", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(100, 210, 255));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(panelColor);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel difficultyPanel = new JPanel(new FlowLayout());
        difficultyPanel.setBackground(panelColor);

        JLabel difficultyLabel = new JLabel("Difficulty: ");
        difficultyLabel.setForeground(textColor);

        difficultyBox = new JComboBox<>(
                new String[]{"Easy (1-50)", "Medium (1-100)", "Hard (1-500)"}
        );

        difficultyPanel.add(difficultyLabel);
        difficultyPanel.add(difficultyBox);
        centerPanel.add(difficultyPanel);

        rangeLabel = createLabel("", textColor, 17);
        attemptsLabel = createLabel("", textColor, 17);
        timerLabel = createLabel("", textColor, 17);
        scoreLabel = createLabel("", textColor, 17);
        highScoreLabel = createLabel("", new Color(255, 215, 80), 17);

        centerPanel.add(rangeLabel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(attemptsLabel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(timerLabel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(scoreLabel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(highScoreLabel);
        centerPanel.add(Box.createVerticalStrut(18));

        guessField = new JTextField();
        guessField.setMaximumSize(new Dimension(250, 35));
        guessField.setHorizontalAlignment(JTextField.CENTER);
        guessField.setFont(new Font("SansSerif", Font.BOLD, 16));
        centerPanel.add(guessField);

        centerPanel.add(Box.createVerticalStrut(12));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(panelColor);

        guessButton = new JButton("Guess");
        hintButton = new JButton("Hint");
        newGameButton = new JButton("New Game");

        buttonPanel.add(guessButton);
        buttonPanel.add(hintButton);
        buttonPanel.add(newGameButton);
        centerPanel.add(buttonPanel);

        statusLabel = createLabel("Choose a difficulty and start guessing!", textColor, 16);
        statusLabel.setForeground(new Color(100, 210, 255));
        centerPanel.add(statusLabel);

        hintLabel = createLabel("", new Color(255, 215, 80), 15);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(hintLabel);

        timer = new Timer(1000, e -> {
            seconds++;
            timerLabel.setText("Time: " + seconds + " seconds");
        });

        guessButton.addActionListener(e -> checkGuess());
        guessField.addActionListener(e -> checkGuess());
        hintButton.addActionListener(e -> showHint());
        newGameButton.addActionListener(e -> startNewGame());
        difficultyBox.addActionListener(e -> startNewGame());
    }

    private JLabel createLabel(String text, Color color, int size) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("SansSerif", Font.PLAIN, size));
        label.setForeground(color);
        return label;
    }

    private void startNewGame() {
        String difficulty = (String) difficultyBox.getSelectedItem();
        gameLogic.startNewGame(difficulty);

        seconds = 0;

        rangeLabel.setText("Guess a number between 1 and " + gameLogic.getMaxNumber());
        attemptsLabel.setText("Attempts left: " + gameLogic.getAttemptsLeft());
        timerLabel.setText("Time: 0 seconds");
        scoreLabel.setText("Current Score: 0");
        highScoreLabel.setText("Best Score: " + scoreManager.getHighScore());
        statusLabel.setText("Game started! Enter your guess.");
        hintLabel.setText("");

        guessField.setText("");
        guessField.setEnabled(true);
        guessButton.setEnabled(true);
        hintButton.setEnabled(true);
        guessField.requestFocus();

        timer.restart();
    }

    private void checkGuess() {
        String input = guessField.getText().trim();

        try {
            int guess = Integer.parseInt(input);
            String result = gameLogic.makeGuess(guess);

            if (result.equals("INVALID")) {
                statusLabel.setText("Please enter a number from 1 to "
                        + gameLogic.getMaxNumber());
                return;
            }

            if (result.equals("CORRECT")) {
                int score = gameLogic.calculateScore(seconds);

                scoreLabel.setText("Current Score: " + score);
                statusLabel.setText("Correct! You guessed the number!");

                if (scoreManager.saveHighScoreIfHigher(score)) {
                    highScoreLabel.setText("New Best Score: " + score);
                }

                endGame();

                JOptionPane.showMessageDialog(this,
                        "Congratulations!\nThe number was "
                                + gameLogic.getSecretNumber()
                                + "\nYour score: " + score);
                return;
            }

            if (result.equals("GAME_OVER")) {
                statusLabel.setText("Game Over! The number was "
                        + gameLogic.getSecretNumber());
                attemptsLabel.setText("Attempts left: 0");
                endGame();
                return;
            }

            String closeMessage = gameLogic.isVeryClose(guess)
                    ? " Very close!"
                    : "";

            if (result.equals("LOW")) {
                statusLabel.setText("Too Low!" + closeMessage);
            } else {
                statusLabel.setText("Too High!" + closeMessage);
            }

            attemptsLabel.setText("Attempts left: " + gameLogic.getAttemptsLeft());
            guessField.setText("");

        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter a valid number.");
        }
    }

    private void showHint() {
        hintLabel.setText(gameLogic.getHint());
        statusLabel.setText("Hint used! 50 points will be deducted.");
    }

    private void endGame() {
        timer.stop();
        guessField.setEnabled(false);
        guessButton.setEnabled(false);
        hintButton.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main game = new Main();
            game.setVisible(true);
        });
    }
}