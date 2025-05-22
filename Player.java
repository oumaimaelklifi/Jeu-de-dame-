public class Player {
    private final boolean isWhite;
    private int score = 0;
    private String name;
    private boolean isCurrentTurn = false;

    public Player(boolean isWhite, String name) {
        this.isWhite = isWhite;
        this.name = name;
    }

    public boolean isWhite() { return isWhite; }
    public int getScore() { return score; }
    public String getName() { return name; }
    public boolean isCurrentTurn() { return isCurrentTurn; }
    
    public void setCurrentTurn(boolean currentTurn) { 
        this.isCurrentTurn = currentTurn; 
    }
    
    public void incrementScore() { 
        score++; 
    }
    
    public void resetScore() { 
        score = 0; 
    }
}