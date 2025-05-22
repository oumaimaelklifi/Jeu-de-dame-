import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class CheckersGameScreen {
    private static final int BOARD_SIZE = 8;
    private static JPanel[][] boardSquares = new JPanel[BOARD_SIZE][BOARD_SIZE];
    private static int selectedRow = -1;
    private static int selectedCol = -1;
    private static boolean isWhiteTurn = true;

    private static JLabel turnLabel;
    private static JLabel whiteTimerLabel;
    private static JLabel blackTimerLabel;
    private static int whiteSeconds = 0;
    private static int blackSeconds = 0;
    private static Timer timer;

    // Palette inspirée de l'image
    private static final Color LIGHT_SQUARE = new Color(214, 175, 110); // Bois clair
    private static final Color DARK_SQUARE = new Color(123, 74, 18);    // Bois foncé
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 215, 0); // Jaune doré

    public static void showGameScreen(JFrame frame) {
        frame.getContentPane().removeAll();
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Titre
        JLabel titleLabel = new JLabel("Jeu de Dames", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Infos jeu
        JPanel infoPanel = new JPanel(new GridLayout(1, 3));
        infoPanel.setBackground(BACKGROUND_COLOR);

        whiteTimerLabel = new JLabel("Blanc: 00:00", JLabel.CENTER);
        whiteTimerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        whiteTimerLabel.setForeground(Color.WHITE);
        infoPanel.add(whiteTimerLabel);

        turnLabel = new JLabel("Tour: Blanc", JLabel.CENTER);
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        turnLabel.setForeground(HIGHLIGHT_COLOR);
        infoPanel.add(turnLabel);

        blackTimerLabel = new JLabel("Noir: 00:00", JLabel.CENTER);
        blackTimerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        blackTimerLabel.setForeground(Color.WHITE);
        infoPanel.add(blackTimerLabel);

        mainPanel.add(infoPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Plateau
        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                final int currentRow = row;
                final int currentCol = col;

                boardSquares[row][col] = new JPanel(new BorderLayout());
                boardSquares[row][col].setPreferredSize(new Dimension(60, 60));
                boardSquares[row][col].setBackground((row + col) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE);

                if ((row + col) % 2 != 0) {
                    if (row < 3) addPiece(row, col, false);
                    else if (row > 4) addPiece(row, col, true);
                }

                boardSquares[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleSquareClick(currentRow, currentCol);
                    }
                });

                boardPanel.add(boardSquares[row][col]);
            }
        }

        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Bouton retour
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton backButton = new JButton("Retour au menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(120, 80, 210));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            stopTimer();
            MainMenu.showMainMenu(frame); // Assure-toi que MainMenu est défini ailleurs
        });

        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.revalidate();
        frame.repaint();

        startTimer();
    }

    private static void addPiece(int row, int col, boolean isWhite) {
        boardSquares[row][col].removeAll();

        JLabel piece = new JLabel();
        piece.setOpaque(true);
        piece.setBackground(isWhite ? Color.WHITE : Color.BLACK);
        piece.setPreferredSize(new Dimension(40, 40));
        piece.setName(isWhite ? "white" : "black");
        piece.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2)); // Bord doré

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(piece);

        boardSquares[row][col].add(wrapper, BorderLayout.CENTER);
        boardSquares[row][col].revalidate();
        boardSquares[row][col].repaint();
    }

    private static boolean hasPiece(int row, int col) {
        return boardSquares[row][col].getComponentCount() > 0;
    }

    private static boolean isWhitePiece(int row, int col) {
        if (!hasPiece(row, col)) return false;
        Component wrapper = boardSquares[row][col].getComponent(0);
        if (wrapper instanceof JPanel panel && panel.getComponentCount() > 0) {
            Component piece = panel.getComponent(0);
            if (piece instanceof JLabel label) {
                return "white".equals(label.getName());
            }
        }
        return false;
    }

    private static void handleSquareClick(int row, int col) {
        if ((row + col) % 2 == 0) return;

        if (selectedRow != -1 && selectedCol != -1) {
            highlightSquare(selectedRow, selectedCol, false);
        }

        if (selectedRow != -1 && selectedCol != -1) {
            if (isValidMove(selectedRow, selectedCol, row, col)) {
                movePiece(selectedRow, selectedCol, row, col);
                selectedRow = -1;
                selectedCol = -1;
                isWhiteTurn = !isWhiteTurn;
                updateTurnLabel();
            } else if (hasPiece(row, col) && ((isWhiteTurn && isWhitePiece(row, col)) || (!isWhiteTurn && !isWhitePiece(row, col)))) {
                selectedRow = row;
                selectedCol = col;
                highlightSquare(row, col, true);
            } else {
                selectedRow = -1;
                selectedCol = -1;
            }
        } else if (hasPiece(row, col) && ((isWhiteTurn && isWhitePiece(row, col)) || (!isWhiteTurn && !isWhitePiece(row, col)))) {
            selectedRow = row;
            selectedCol = col;
            highlightSquare(row, col, true);
        }
    }

    private static boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (hasPiece(toRow, toCol)) return false;
        int direction = isWhitePiece(fromRow, fromCol) ? -1 : 1;

        if (toRow == fromRow + direction && (toCol == fromCol - 1 || toCol == fromCol + 1)) return true;

        if (toRow == fromRow + 2 * direction) {
            int midRow = fromRow + direction;
            if (toCol == fromCol - 2) {
                int midCol = fromCol - 1;
                return hasPiece(midRow, midCol) && isWhitePiece(midRow, midCol) != isWhitePiece(fromRow, fromCol);
            } else if (toCol == fromCol + 2) {
                int midCol = fromCol + 1;
                return hasPiece(midRow, midCol) && isWhitePiece(midRow, midCol) != isWhitePiece(fromRow, fromCol);
            }
        }

        return false;
    }

    private static void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        boolean isWhite = isWhitePiece(fromRow, fromCol);
        boardSquares[fromRow][fromCol].removeAll();
        boardSquares[fromRow][fromCol].revalidate();
        boardSquares[fromRow][fromCol].repaint();

        if (Math.abs(fromRow - toRow) == 2) {
            int capturedRow = (fromRow + toRow) / 2;
            int capturedCol = (fromCol + toCol) / 2;
            boardSquares[capturedRow][capturedCol].removeAll();
            boardSquares[capturedRow][capturedCol].revalidate();
            boardSquares[capturedRow][capturedCol].repaint();
        }

        addPiece(toRow, toCol, isWhite);
        highlightSquare(fromRow, fromCol, false);
    }

    private static void highlightSquare(int row, int col, boolean highlight) {
        if (highlight) {
            boardSquares[row][col].setBorder(BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 3));
        } else {
            boardSquares[row][col].setBorder(null);
        }
        boardSquares[row][col].revalidate();
        boardSquares[row][col].repaint();
    }

    private static void updateTurnLabel() {
        turnLabel.setText("Tour: " + (isWhiteTurn ? "Blanc" : "Noir"));
    }

    private static void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (isWhiteTurn) whiteSeconds++;
                else blackSeconds++;
                SwingUtilities.invokeLater(() -> updateTimers());
            }
        }, 1000, 1000);
    }

    private static void stopTimer() {
        if (timer != null) timer.cancel();
    }

    private static void updateTimers() {
        whiteTimerLabel.setText("Blanc: " + formatTime(whiteSeconds));
        blackTimerLabel.setText("Noir: " + formatTime(blackSeconds));
    }

    private static String formatTime(int totalSeconds) {
        int min = totalSeconds / 60;
        int sec = totalSeconds % 60;
        return String.format("%02d:%02d", min, sec);
    }
}




