package ATMInterface;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            ATMFrame atm = new ATMFrame();

            atm.setVisible(true);

        });
    }
}