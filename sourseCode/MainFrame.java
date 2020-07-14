import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static int WIDTH = 500;
    private static int HEIGHT = 600;
    static JButton startGameButton = new JButton("Начать игру");
    private static JButton exitButton = new JButton("Выйти из игры");
    static JPanel panel = new JPanel();
    static PlayingField playingField = new PlayingField();
    static JLabel whoseTurnPanel = new JLabel("Игра пока не начата");


    public MainFrame(){
        super();
        setSize(WIDTH, HEIGHT);
        setTitle("OnlineGame");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        exitButton.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        exitButton.addActionListener((event) -> {
            System.exit(0);
        });
        startGameButton.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        startGameButton.addActionListener(new StartButtonLogic(this));
        panel.add(startGameButton);
        panel.add(exitButton);
        add(panel, BorderLayout.NORTH);

        playingField.setSize(WIDTH, 45);
        add(playingField, BorderLayout.CENTER);

        whoseTurnPanel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        add(whoseTurnPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new MainFrame();
        });
    }

    public static int getWIDTH(){
        return WIDTH;
    }
    public static int getHEIGHT(){
        return HEIGHT;
    }
}
