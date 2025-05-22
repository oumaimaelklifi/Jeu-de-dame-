public class Move {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;
    private boolean isCapture;
    private int capturedRow = -1;
    private int capturedCol = -1;

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.isCapture = Math.abs(fromRow - toRow) == 2;
        
        if (isCapture) {
            this.capturedRow = (fromRow + toRow) / 2;
            this.capturedCol = (fromCol + toCol) / 2;
        }
    }

    // Getters
    public int getFromRow() { return fromRow; }
    public int getFromCol() { return fromCol; }
    public int getToRow() { return toRow; }
    public int getToCol() { return toCol; }
    public boolean isCapture() { return isCapture; }
    public int getCapturedRow() { return capturedRow; }
    public int getCapturedCol() { return capturedCol; }
}