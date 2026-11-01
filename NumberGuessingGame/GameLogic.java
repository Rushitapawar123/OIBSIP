import java.util.Random;

public class GameLogic {

    private final Random random = new Random();

    private Difficulty currentDifficulty;
    private int secretNumber;
    private int attemptsLeft;
    private int hintsUsed;

    public GameLogic() {
        startNewGame(Difficulty.EASY);
    }

    public void startNewGame(String difficultyText) {
        startNewGame(Difficulty.fromText(difficultyText));
    }

    public void startNewGame(Difficulty difficulty) {
        currentDifficulty = difficulty;
        secretNumber = random.nextInt(currentDifficulty.getMaxNumber()) + 1;
        attemptsLeft = currentDifficulty.getAttempts();
        hintsUsed = 0;
    }

    public String makeGuess(int guess) {
        if (guess < 1 || guess > getMaxNumber()) {
            return "INVALID";
        }

        attemptsLeft--;

        if (guess == secretNumber) {
            return "CORRECT";
        }

        if (attemptsLeft <= 0) {
            return "GAME_OVER";
        }

        if (guess < secretNumber) {
            return "LOW";
        }

        return "HIGH";
    }

    public boolean isVeryClose(int guess) {
        return Math.abs(guess - secretNumber) <= 5;
    }

    public String getHint() {
        hintsUsed++;

        String type = secretNumber % 2 == 0 ? "even" : "odd";
        int range = Math.max(5, getMaxNumber() / 8);
        int low = Math.max(1, secretNumber - range);
        int high = Math.min(getMaxNumber(), secretNumber + range);

        return "Hint: Number is " + type
                + " and between " + low + " and " + high;
    }

    public int calculateScore(int seconds) {
        return Math.max(0,
                (attemptsLeft * 100) + 500 - (seconds * 3) - (hintsUsed * 50));
    }

    public int getMaxNumber() {
        return currentDifficulty.getMaxNumber();
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public int getSecretNumber() {
        return secretNumber;
    }

    public int getHintsUsed() {
        return hintsUsed;
    }

    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }
}