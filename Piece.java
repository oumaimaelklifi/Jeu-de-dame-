import java.awt.*;

public abstract class Piece {
    protected boolean isWhite;
    protected boolean isKing = false;

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public boolean isWhite() { return isWhite; }
    public boolean isKing() { return isKing; }
    public void promote() { this.isKing = true; }

    public abstract boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board);
    public abstract void draw(Graphics2D g, int x, int y, int diameter);
}