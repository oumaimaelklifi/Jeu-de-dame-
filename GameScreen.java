// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.MouseAdapter;
// import java.awt.event.MouseEvent;

// public class GameScreen {
//     private static final int BOARD_SIZE = 8;
//     private static JPanel[][] squares = new JPanel[BOARD_SIZE][BOARD_SIZE];
//     private static int selectedRow = -1;
//     private static int selectedCol = -1;
//     private static boolean isWhiteTurn = true; // Blanc commence

//     public static void showGameScreen(JFrame frame) {
//         frame.getContentPane().removeAll();

//         JPanel gamePanel = new JPanel(new BorderLayout());
//         gamePanel.setBackground(new Color(40, 40, 40));

//         // Titre du jeu
//         JLabel titleLabel = new JLabel("Jeu de Dames", JLabel.CENTER);
//         titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
//         titleLabel.setForeground(Color.WHITE);
//         titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
//         gamePanel.add(titleLabel, BorderLayout.NORTH);

//         // Panneau pour le plateau de jeu
//         JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
//         boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

//         // Création du plateau de jeu
//         for (int row = 0; row < BOARD_SIZE; row++) {
//             for (int col = 0; col < BOARD_SIZE; col++) {
//                 final int currentRow = row;
//                 final int currentCol = col;
                
//                 squares[row][col] = new JPanel(new BorderLayout());
//                 squares[row][col].setPreferredSize(new Dimension(60, 60));
                
//                 // Alternance des couleurs des cases
//                 if ((row + col) % 2 == 0) {
//                     squares[row][col].setBackground(new Color(255, 206, 158)); // Case claire
//                 } else {
//                     squares[row][col].setBackground(new Color(209, 139, 71)); // Case foncée
                    
//                     // Placement des pions initiaux
//                     if (row < 3) {
//                         addPiece(row, col, false); // Pions noirs
//                     } else if (row > 4) {
//                         addPiece(row, col, true); // Pions blancs
//                     }
//                 }

//                 // Gestion des clics
//                 squares[row][col].addMouseListener(new MouseAdapter() {
//                     @Override
//                     public void mouseClicked(MouseEvent e) {
//                         handleSquareClick(currentRow, currentCol);
//                     }
//                 });

//                 boardPanel.add(squares[row][col]);
//             }
//         }

//         gamePanel.add(boardPanel, BorderLayout.CENTER);

//         // Panneau des boutons
//         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//         buttonPanel.setBackground(new Color(40, 40, 40));

//         JButton backButton = new JButton("Retour au menu");
//         backButton.setFont(new Font("Arial", Font.BOLD, 16));
//         backButton.setBackground(new Color(201, 161, 76));
//         backButton.setForeground(Color.WHITE);
//         backButton.addActionListener(e -> MainMenu.showMainMenu(frame));

//         buttonPanel.add(backButton);
//         gamePanel.add(buttonPanel, BorderLayout.SOUTH);

//         frame.add(gamePanel);
//         frame.revalidate();
//         frame.repaint();
//     }

//     private static void addPiece(int row, int col, boolean isWhite) {
//         squares[row][col].removeAll();
//         JLabel piece = new JLabel(new ImageIcon(
//             isWhite ? "Media/images/dames.png" : "Media/images/info.png"));
//         squares[row][col].add(piece, BorderLayout.CENTER);
//         squares[row][col].revalidate();
//         squares[row][col].repaint();
//     }

//     private static void handleSquareClick(int row, int col) {
//         // On ne peut jouer que sur les cases foncées
//         if ((row + col) % 2 == 0) return;

//         // Si une pièce est déjà sélectionnée
//         if (selectedRow != -1 && selectedCol != -1) {
//             // Tentative de déplacement
//             if (isValidMove(selectedRow, selectedCol, row, col)) {
//                 movePiece(selectedRow, selectedCol, row, col);
//                 selectedRow = -1;
//                 selectedCol = -1;
//                 isWhiteTurn = !isWhiteTurn; // Changement de tour
//             } else {
//                 // Sélection d'une autre pièce du même joueur
//                 if (hasPiece(row, col) && ((isWhiteTurn && isWhitePiece(row, col)) || 
//                                           (!isWhiteTurn && !isWhitePiece(row, col)))) {
//                     selectedRow = row;
//                     selectedCol = col;
//                     highlightSquare(row, col, true);
//                 }
//             }
//         } 
//         // Sélection d'une pièce
//         else if (hasPiece(row, col)) {
//             // Vérifier que c'est bien le tour du joueur
//             if ((isWhiteTurn && isWhitePiece(row, col)) || 
//                 (!isWhiteTurn && !isWhitePiece(row, col))) {
//                 selectedRow = row;
//                 selectedCol = col;
//                 highlightSquare(row, col, true);
//             }
//         }
//     }

//     private static boolean hasPiece(int row, int col) {
//         return squares[row][col].getComponentCount() > 0;
//     }

//     private static boolean isWhitePiece(int row, int col) {
//         // Cette méthode suppose que l'image du pion blanc contient "blanc" dans son nom
//         JLabel piece = (JLabel)squares[row][col].getComponent(0);
//         return piece.getIcon().toString().contains("blanc");
//     }

//     private static boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
//         // Vérifier que la case de destination est vide
//         if (hasPiece(toRow, toCol)) return false;

//         // Les pions blancs vont vers le haut (lignes décroissantes)
//         // Les pions noirs vont vers le bas (lignes croissantes)
//         int direction = isWhitePiece(fromRow, fromCol) ? -1 : 1;

//         // Déplacement simple (1 case en diagonale)
//         if (toRow == fromRow + direction && 
//             (toCol == fromCol - 1 || toCol == fromCol + 1)) {
//             return true;
//         }

//         // Prise (2 cases en diagonale avec une pièce adverse entre les deux)
//         if (toRow == fromRow + 2*direction) {
//             int middleRow = fromRow + direction;
//             if (toCol == fromCol - 2) {
//                 int middleCol = fromCol - 1;
//                 return hasPiece(middleRow, middleCol) && 
//                        isWhitePiece(middleRow, middleCol) != isWhitePiece(fromRow, fromCol);
//             } else if (toCol == fromCol + 2) {
//                 int middleCol = fromCol + 1;
//                 return hasPiece(middleRow, middleCol) && 
//                        isWhitePiece(middleRow, middleCol) != isWhitePiece(fromRow, fromCol);
//             }
//         }

//         return false;
//     }

//     private static void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
//         // Copier la pièce
//         boolean isWhite = isWhitePiece(fromRow, fromCol);
        
//         // Supprimer la pièce de la case de départ
//         squares[fromRow][fromCol].removeAll();
//         squares[fromRow][fromCol].revalidate();
//         squares[fromRow][fromCol].repaint();
        
//         // Si c'est une prise, supprimer la pièce prise
//         if (Math.abs(fromRow - toRow) == 2) {
//             int middleRow = (fromRow + toRow) / 2;
//             int middleCol = (fromCol + toCol) / 2;
//             squares[middleRow][middleCol].removeAll();
//             squares[middleRow][middleCol].revalidate();
//             squares[middleRow][middleCol].repaint();
//         }
        
//         // Ajouter la pièce à la case d'arrivée
//         addPiece(toRow, toCol, isWhite);
        
//         // Désélectionner la case
//         highlightSquare(fromRow, fromCol, false);
//     }

//     private static void highlightSquare(int row, int col, boolean highlight) {
//         if (highlight) {
//             squares[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
//         } else {
//             squares[row][col].setBorder(null);
//         }
//         squares[row][col].revalidate();
//         squares[row][col].repaint();
//     }
// }



