import java.awt.Color;

public interface GameConstants {
    int BOARD_SIZE = 8;
    int TILE_SIZE = 70;
    
    // Couleurs modernes
    Color DARK_BACKGROUND = new Color(30, 33, 36);
    Color ACCENT_COLOR = new Color(72, 133, 237);
    Color LIGHT_TILE = new Color(232, 235, 239);
    Color DARK_TILE = new Color(100, 115, 140);
    Color HIGHLIGHT_COLOR = new Color(255, 213, 79);
    Color WHITE_PIECE_COLOR = new Color(241, 243, 244);
    Color BLACK_PIECE_COLOR = new Color(60, 64, 67);
    
    // Directions
    int WHITE_DIRECTION = -1;
    int BLACK_DIRECTION = 1;
}