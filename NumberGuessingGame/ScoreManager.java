import java.util.prefs.Preferences;

public class ScoreManager {

    private static final String HIGH_SCORE_KEY = "highScore";

    private final Preferences preferences =
            Preferences.userRoot().node("NumberGuessingGame");

    public int getHighScore() {
        return preferences.getInt(HIGH_SCORE_KEY, 0);
    }

    public boolean saveHighScoreIfHigher(int score) {
        if (score > getHighScore()) {
            preferences.putInt(HIGH_SCORE_KEY, score);
            return true;
        }

        return false;
    }
}