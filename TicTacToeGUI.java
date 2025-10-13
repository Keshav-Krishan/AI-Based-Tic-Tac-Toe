import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class TicTacToeGUI extends JFrame implements ActionListener {
    private JButton[][] buttons = new JButton[3][3];
    private boolean playerTurn = true;
    private char[][] board = new char[3][3];
    private String difficulty;
    private Random random = new Random();

    // Panels
    private JPanel gamePanel;
    private JPanel menuPanel;

    public TicTacToeGUI() {
        setTitle("AI-Based Tic Tac Toe");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new CardLayout());

        createMenuPanel();
        createGamePanel();

        add(menuPanel, "Menu");
        add(gamePanel, "Game");

        showMenu();
        setVisible(true);
    }

    /** ---------------- MENU PANEL ---------------- */
    private void createMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new BorderLayout());
        menuPanel.setBackground(new Color(230, 240, 255));

        JLabel title = new JLabel("🎮 Welcome to Tic Tac Toe 🎮", SwingConstants.CENTER);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        title.setForeground(Color.DARK_GRAY);
        menuPanel.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBackground(new Color(230, 240, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        JButton easyBtn = new JButton("🟢 Easy Mode");
        JButton hardBtn = new JButton("🔴 Hard Mode");
        JButton exitBtn = new JButton("🚪 Exit");

        easyBtn.addActionListener(e -> startGame("Easy"));
        hardBtn.addActionListener(e -> startGame("Hard"));
        exitBtn.addActionListener(e -> System.exit(0));

        for (JButton btn : new JButton[]{easyBtn, hardBtn, exitBtn}) {
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.setFocusPainted(false);
            btn.setBackground(Color.WHITE);
            buttonPanel.add(btn);
        }

        menuPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    /** ---------------- GAME PANEL ---------------- */
    private void createGamePanel() {
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(3, 3));
        gamePanel.setBackground(Color.WHITE);

        Font buttonFont = new Font("Arial", Font.BOLD, 60);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(buttonFont);
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].addActionListener(this);
                gamePanel.add(buttons[i][j]);
                board[i][j] = ' ';
            }
        }
    }

    /** ---------------- MENU NAVIGATION ---------------- */
    private void showMenu() {
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "Menu");
    }

    private void startGame(String mode) {
        this.difficulty = mode;
        resetBoard();
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "Game");
        setTitle("Tic Tac Toe - " + mode + " Mode");
    }

    /** ---------------- GAME LOGIC ---------------- */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!playerTurn) return;

        JButton btn = (JButton) e.getSource();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (btn == buttons[i][j] && board[i][j] == ' ') {
                    board[i][j] = 'X';
                    buttons[i][j].setText("X");
                    playerTurn = false;

                    if (checkWin('X')) {
                        celebrate("🎉 You Win! 🥳", new Color(144, 238, 144));
                        return;
                    } else if (isFull()) {
                        celebrate("🤝 It's a Draw!", new Color(255, 255, 153));
                        return;
                    }

                    aiMove();
                }
            }
        }
    }

    private void aiMove() {
        int row, col;
        if (difficulty.equals("Easy")) {
            do {
                row = random.nextInt(3);
                col = random.nextInt(3);
            } while (board[row][col] != ' ');
        } else {
            int[] move = findBestMove();
            row = move[0];
            col = move[1];
        }

        board[row][col] = 'O';
        buttons[row][col].setText("O");

        if (checkWin('O')) {
            celebrate("😞 You Lost! Try Again!", new Color(255, 182, 193));
        } else if (isFull()) {
            celebrate("🤝 It's a Draw!", new Color(255, 255, 153));
        } else {
            playerTurn = true;
        }
    }

    /** ---------------- AI LOGIC ---------------- */
    private int[] findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] move = {-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = 'O';
                    int score = minimax(false);
                    board[i][j] = ' ';
                    if (score > bestScore) {
                        bestScore = score;
                        move[0] = i;
                        move[1] = j;
                    }
                }
            }
        }
        return move;
    }

    private int minimax(boolean isMaximizing) {
        if (checkWin('O')) return 1;
        if (checkWin('X')) return -1;
        if (isFull()) return 0;

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = isMaximizing ? 'O' : 'X';
                    int score = minimax(!isMaximizing);
                    board[i][j] = ' ';
                    bestScore = isMaximizing
                            ? Math.max(score, bestScore)
                            : Math.min(score, bestScore);
                }
            }
        }
        return bestScore;
    }

    /** ---------------- UTILITIES ---------------- */
    private boolean checkWin(char symbol) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol) return true;
            if (board[0][i] == symbol && board[1][i] == symbol && board[2][i] == symbol) return true;
        }
        return (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol)
                || (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol);
    }

    private boolean isFull() {
        for (char[] row : board)
            for (char cell : row)
                if (cell == ' ')
                    return false;
        return true;
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
                buttons[i][j].setText("");
                buttons[i][j].setBackground(null);
            }
        playerTurn = true;
    }

    /** ---------------- CELEBRATION / END MESSAGE ---------------- */
    private void celebrate(String message, Color color) {
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                btn.setBackground(color);
            }
        }

        Timer timer = new Timer(800, e -> {
            int choice = JOptionPane.showOptionDialog(
                    this,
                    message + "\nPlay Again or Go to Menu?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Play Again", "Main Menu"},
                    "Play Again");

            if (choice == JOptionPane.YES_OPTION) {
                resetBoard();
            } else {
                showMenu();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /** ---------------- MAIN ---------------- */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeGUI::new);
    }
}
