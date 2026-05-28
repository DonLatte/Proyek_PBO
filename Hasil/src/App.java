import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame();

            window.setTitle("Horror Escape");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);

            GamePanel gamePanel = new GamePanel();
            window.add(gamePanel);

            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}