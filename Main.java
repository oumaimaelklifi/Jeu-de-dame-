import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Jeu de Dames");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);

        MusicPlayer.playBackgroundMusic("Media/audio/city-bgm-336601.wav");

        MainMenu.showMainMenu(frame);
        
    }
}
