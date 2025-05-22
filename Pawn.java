import java.awt.*;

public class Pawn extends Piece {
    public Pawn(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        int direction = isWhite ? GameConstants.WHITE_DIRECTION : GameConstants.BLACK_DIRECTION;
        
        // Déplacement simple
        if (toRow == fromRow + direction && Math.abs(toCol - fromCol) == 1) {
            return board[toRow][toCol] == null;
        }
        
        // Prise
        if (toRow == fromRow + 2 * direction && Math.abs(toCol - fromCol) == 2) {
            int middleRow = fromRow + direction;
            int middleCol = (fromCol + toCol) / 2;
            return board[toRow][toCol] == null && 
                   board[middleRow][middleCol] != null && 
                   board[middleRow][middleCol].isWhite() != isWhite;
        }
        
        return false;
    }

    @Override
    public void draw(Graphics2D g, int x, int y, int diameter) {
        // Ombre
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(x + 2, y + 2, diameter, diameter);
        
        // Dégradé de couleur
        GradientPaint gradient = new GradientPaint(
            x, y, 
            isWhite ? GameConstants.WHITE_PIECE_COLOR : GameConstants.BLACK_PIECE_COLOR, 
            x + diameter/2, y + diameter/2, 
            isWhite ? new Color(200, 200, 200) : new Color(40, 40, 40)
        );
        g.setPaint(gradient);
        g.fillOval(x, y, diameter, diameter);
        
        // Bordure
        g.setColor(isWhite ? new Color(180, 180, 180) : new Color(30, 30, 30));
        g.setStroke(new BasicStroke(2));
        g.drawOval(x, y, diameter, diameter);
    }
}