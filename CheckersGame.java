import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class CheckersGame {
    // Couleurs modernes
    private static final Color DARK_BACKGROUND = new Color(30, 30, 36);
    private static final Color LIGHT_TILE = new Color(240, 240, 240);
    private static final Color DARK_TILE = new Color(80, 80, 90);
    private static final Color WHITE_PIECE_COLOR = new Color(245, 245, 245);
    private static final Color BLACK_PIECE_COLOR = new Color(50, 50, 55);
    private static final Color HIGHLIGHT_COLOR = new Color(100, 180, 255);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    
    private static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 70;
    private static final int PIECE_PADDING = 8;
    
    private final Piece[][] board;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private Move lastMove;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private JPanel[][] boardSquares;
    private JLabel turnLabel;
    private JLabel whiteScoreLabel;
    private JLabel blackScoreLabel;

    public CheckersGame() {
        this.board = new Piece[BOARD_SIZE][BOARD_SIZE];
        this.whitePlayer = new Player(true, "Joueur Blanc");
        this.blackPlayer = new Player(false, "Joueur Noir");
        whitePlayer.setCurrentTurn(true);
        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 != 0) {
                    if (row < 3) {
                        board[row][col] = new Pawn(false);
                    } else if (row > 4) {
                        board[row][col] = new Pawn(true);
                    }
                }
            }
        }
    }

    public void showGameScreen(JFrame frame) {
        frame.getContentPane().removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header avec titre et infos
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DARK_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Titre avec effet de dégradé
        JLabel titleLabel = new JLabel("JEU DE DAMES", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Font font = new Font("Segoe UI", Font.BOLD, 36);
                g2.setFont(font);
                
                // Dégradé pour le texte
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(180, 220, 255), 
                    getWidth(), 0, Color.WHITE);
                g2.setPaint(gradient);
                
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        titleLabel.setPreferredSize(new Dimension(0, 60));

        // Panel info avec ombre
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        infoPanel.setBackground(new Color(40, 40, 46));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Effet d'ombre pour le panel info
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        whiteScoreLabel = createPlayerLabel(whitePlayer);
        turnLabel = createTurnLabel();
        blackScoreLabel = createPlayerLabel(blackPlayer);
        
        infoPanel.add(whiteScoreLabel);
        infoPanel.add(turnLabel);
        infoPanel.add(blackScoreLabel);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(infoPanel, BorderLayout.CENTER);

        // Plateau avec ombre portée
        JPanel boardContainer = new JPanel(new GridBagLayout());
        boardContainer.setBackground(DARK_BACKGROUND);
        
        // Effet d'ombre pour le plateau
        JPanel boardShadow = new JPanel(new BorderLayout());
        boardShadow.setBackground(DARK_BACKGROUND);
        boardShadow.setBorder(BorderFactory.createEmptyBorder(5, 5, 15, 15));
        boardShadow.setOpaque(false);
        
        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        boardPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 2));
        
        boardSquares = new JPanel[BOARD_SIZE][BOARD_SIZE];
        initializeBoardSquares(boardPanel);
        
        boardShadow.add(boardPanel);
        boardContainer.add(boardShadow);

        // Boutons avec style moderne
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DARK_BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton backButton = createModernButton("RETOUR AU MENU");
        backButton.addActionListener(e -> MainMenu.showMainMenu(frame));
        
        buttonPanel.add(backButton);

        // Assemblage
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(boardContainer, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.revalidate();
        frame.repaint();
    }

    private JLabel createPlayerLabel(Player player) {
        JLabel label = new JLabel(
            "<html><div style='text-align:center'>" + 
            "<b>" + player.getName().toUpperCase() + "</b><br>" + 
            "<span style='font-size:18px'>" + player.getScore() + "</span></div></html>", 
            JLabel.CENTER
        );
        
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(player.isWhite() ? WHITE_PIECE_COLOR : new Color(200, 200, 200));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Fond arrondi pour le joueur dont c'est le tour
        if (player.isCurrentTurn()) {
            label.setOpaque(true);
            label.setBackground(new Color(60, 60, 70));
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        }
        
        return label;
    }

    private JLabel createTurnLabel() {
        JLabel label = new JLabel(
            "<html><div style='text-align:center'>" + 
            "<span style='font-size:12px'>TOUR ACTUEL</span><br>" + 
            "<b style='font-size:16px'>" + (whitePlayer.isCurrentTurn() ? whitePlayer.getName() : blackPlayer.getName()) + "</b></div></html>", 
            JLabel.CENTER
        );
        
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(ACCENT_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
    }

    private void initializeBoardSquares(JPanel boardPanel) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                final int currentRow = row;
                final int currentCol = col;

                boardSquares[row][col] = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Dessin de la case avec bordure arrondie
                        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                            1, 1, getWidth()-2, getHeight()-2, 0, 0);
                        
                        if ((currentRow + currentCol) % 2 == 0) {
                            g2.setColor(LIGHT_TILE);
                        } else {
                            g2.setColor(DARK_TILE);
                            
                            // Dessin de la pièce si présente
                            if (board[currentRow][currentCol] != null) {
                                int diameter = Math.min(getWidth(), getHeight()) - PIECE_PADDING*2;
                                int x = (getWidth() - diameter) / 2;
                                int y = (getHeight() - diameter) / 2;
                                
                                // Effet de profondeur pour la pièce
                                Ellipse2D pieceShadow = new Ellipse2D.Float(
                                    x+1, y+2, diameter, diameter);
                                g2.setColor(new Color(0, 0, 0, 50));
                                g2.fill(pieceShadow);
                                
                                board[currentRow][currentCol].draw(g2, x, y, diameter);
                                
                                // Effet de sélection
                                if (selectedRow == currentRow && selectedCol == currentCol) {
                                    g2.setStroke(new BasicStroke(3));
                                    g2.setColor(HIGHLIGHT_COLOR);
                                    g2.drawOval(x-2, y-2, diameter+4, diameter+4);
                                }
                            }
                        }
                        
                        g2.fill(roundedRectangle);
                    }
                };
                
                boardSquares[row][col].setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                boardSquares[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleSquareClick(currentRow, currentCol);
                    }
                    
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if ((currentRow + currentCol) % 2 != 0 && 
                            (board[currentRow][currentCol] == null || 
                             board[currentRow][currentCol].isWhite() == (whitePlayer.isCurrentTurn()))) {
                            boardSquares[currentRow][currentCol].setBorder(
                                BorderFactory.createLineBorder(new Color(120, 120, 130), 2));
                        }
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (selectedRow != currentRow || selectedCol != currentCol) {
                            boardSquares[currentRow][currentCol].setBorder(null);
                        }
                    }
                });
                
                boardPanel.add(boardSquares[row][col]);
            }
        }
    }

    private void handleSquareClick(int row, int col) {
        if ((row + col) % 2 == 0) return; // Cases claires non jouables

        Piece piece = board[row][col];
        Player currentPlayer = whitePlayer.isCurrentTurn() ? whitePlayer : blackPlayer;

        // Désélection si case déjà sélectionnée
        if (selectedRow == row && selectedCol == col) {
            selectedRow = -1;
            selectedCol = -1;
            highlightSquare(row, col, false);
            return;
        }

        // Sélection d'une pièce
        if (piece != null && piece.isWhite() == currentPlayer.isWhite()) {
            if (selectedRow != -1) {
                highlightSquare(selectedRow, selectedCol, false);
            }
            selectedRow = row;
            selectedCol = col;
            highlightSquare(row, col, true);
            return;
        }

        // Tentative de déplacement
        if (selectedRow != -1 && board[selectedRow][selectedCol] != null) {
            Move move = new Move(selectedRow, selectedCol, row, col);
            if (board[selectedRow][selectedCol].isValidMove(selectedRow, selectedCol, row, col, board)) {
                executeMove(move);
                switchTurn();
            }
        }
    }

    private void executeMove(Move move) {
        Piece piece = board[move.getFromRow()][move.getFromCol()];
        board[move.getToRow()][move.getToCol()] = piece;
        board[move.getFromRow()][move.getFromCol()] = null;
        
        if (move.isCapture()) {
            board[move.getCapturedRow()][move.getCapturedCol()] = null;
            if (piece.isWhite()) {
                whitePlayer.incrementScore();
            } else {
                blackPlayer.incrementScore();
            }
            updateScoreLabels();
        }
        
        // Promotion en dame
        if ((piece.isWhite() && move.getToRow() == 0) || (!piece.isWhite() && move.getToRow() == BOARD_SIZE - 1)) {
            piece.promote();
        }
        
        lastMove = move;
        selectedRow = -1;
        selectedCol = -1;
        repaintBoard();
    }

    private void switchTurn() {
        whitePlayer.setCurrentTurn(!whitePlayer.isCurrentTurn());
        blackPlayer.setCurrentTurn(!blackPlayer.isCurrentTurn());
        
        // Mise à jour des styles pour indiquer le tour
        whiteScoreLabel = createPlayerLabel(whitePlayer);
        blackScoreLabel = createPlayerLabel(blackPlayer);
        turnLabel.setText(
            "<html><div style='text-align:center'>" + 
            "<span style='font-size:12px'>TOUR ACTUEL</span><br>" + 
            "<b style='font-size:16px'>" + (whitePlayer.isCurrentTurn() ? whitePlayer.getName() : blackPlayer.getName()) + "</b></div></html>"
        );
    }

    private void highlightSquare(int row, int col, boolean highlight) {
        if (highlight) {
            boardSquares[row][col].setBorder(
                BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 3)
            );
        } else {
            boardSquares[row][col].setBorder(null);
        }
        boardSquares[row][col].repaint();
    }

    private void repaintBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                boardSquares[row][col].repaint();
            }
        }
    }

    private void updateScoreLabels() {
        whiteScoreLabel.setText(
            "<html><div style='text-align:center'>" + 
            "<b>" + whitePlayer.getName().toUpperCase() + "</b><br>" + 
            "<span style='font-size:18px'>" + whitePlayer.getScore() + "</span></div></html>"
        );
        
        blackScoreLabel.setText(
            "<html><div style='text-align:center'>" + 
            "<b>" + blackPlayer.getName().toUpperCase() + "</b><br>" + 
            "<span style='font-size:18px'>" + blackPlayer.getScore() + "</span></div></html>"
        );
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec bordure arrondie
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 20, 20);
                
                if (getModel().isPressed()) {
                    g2.setColor(ACCENT_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(ACCENT_COLOR.brighter());
                } else {
                    g2.setColor(ACCENT_COLOR);
                }
                
                g2.fill(roundedRectangle);
                
                // Texte
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Pas de bordure par défaut
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        
        return button;
    }
}

class Pawn extends Piece {
    private boolean isKing = false;
    
    public Pawn(boolean isWhite) {
        super(isWhite);
    }
    
    @Override
    public void draw(Graphics2D g2, int x, int y, int diameter) {
        // Dégradé pour la pièce
        GradientPaint gradient;
        if (isWhite()) {
            gradient = new GradientPaint(
                x, y, new Color(245, 245, 255),
                x, y+diameter, new Color(220, 220, 230));
        } else {
            gradient = new GradientPaint(
                x, y, new Color(80, 80, 90),
                x, y+diameter, new Color(50, 50, 60));
        }
        
        g2.setPaint(gradient);
        g2.fillOval(x, y, diameter, diameter);
        
        // Bordure
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(isWhite() ? new Color(200, 200, 210) : new Color(40, 40, 50));
        g2.drawOval(x, y, diameter, diameter);
        
        // Couronne pour les dames
        if (isKing) {
            int crownSize = diameter / 2;
            int crownX = x + (diameter - crownSize) / 2;
            int crownY = y + (diameter - crownSize) / 2;
            
            g2.setColor(isWhite() ? new Color(255, 215, 0) : new Color(200, 160, 0));
            
            // Dessin simplifié d'une couronne
            int[] xPoints = {crownX, crownX + crownSize/4, crownX + crownSize/2, crownX + 3*crownSize/4, crownX + crownSize};
            int[] yPoints = {crownY + crownSize/2, crownY, crownY + crownSize/2, crownY, crownY + crownSize/2};
            
            g2.fillPolygon(xPoints, yPoints, 5);
            
            // Détails de la couronne
            g2.setColor(isWhite() ? new Color(255, 235, 150) : new Color(220, 180, 50));
            g2.drawPolygon(xPoints, yPoints, 5);
            g2.drawLine(crownX + crownSize/4, crownY, crownX + crownSize/4, crownY + crownSize/3);
            g2.drawLine(crownX + 3*crownSize/4, crownY, crownX + 3*crownSize/4, crownY + crownSize/3);
        }
    }
    
    @Override
    public void promote() {
        this.isKing = true;
    }
    
    @Override
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // Logique de mouvement (à implémenter selon les règles du jeu de dames)
        return true;
    }
}