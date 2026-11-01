public enum Difficulty {

    EASY("Easy (1-50)", 50, 10),
    MEDIUM("Medium (1-100)", 100, 7),
    HARD("Hard (1-500)", 500, 6);

    private final String displayName;
    private final int maxNumber;
    private final int attempts;

    Difficulty(String displayName, int maxNumber, int attempts) {
        this.displayName = displayName;
        this.maxNumber = maxNumber;
        this.attempts = attempts;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxNumber() {
        return maxNumber;
    }

    public int getAttempts() {
        return attempts;
    }

    public static Difficulty fromText(String text) {
        if (text != null && text.startsWith("Medium")) {
            return MEDIUM;
        }

        if (text != null && text.startsWith("Hard")) {
            return HARD;
        }

        return EASY;
    }
}