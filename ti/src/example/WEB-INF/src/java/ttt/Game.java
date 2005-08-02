package ttt;

public class Game {
    public static final char X = 'X';
    public static final char O = 'O';
    public static final char NONE = ' ';

    private char[] board;
    private char winner;
    private static final char[][] winCombos = new char[][] {
        {0, 1, 2},
        {3, 4, 5},
        {6, 7, 8},
        {0, 3, 6},
        {1, 4, 7},
        {2, 5, 8},
        {0, 4, 8},
        {2, 4, 6}
    };    
    
    public Game() {
        board = new char[9];
        for (int x=0; x<board.length; x++) {
            board[x] = NONE;
        }
        winner = NONE;
    }

    public char getWinner() {
        return winner;
    }

    public char[] getBoard() {
        char[] ret = new char[9];
        System.arraycopy(board, 0, ret, 0, board.length);
        return ret;
    }

    public boolean move(char player, int pos) {
        if (board[pos] != NONE) {
            return false;
        } else {
            board[pos] = player;
            checkWin();
            return true;
        }  
    }

    private void checkWin() {
        char[] players = new char[] {X, O};
        for (int p=0; p<players.length; p++) {
            for (int x=0; x<winCombos.length; x++) {
                if (winCombos[x][0] == players[p] &&
                    winCombos[x][1] == players[p] &&
                    winCombos[x][2] == players[p]) {
                    winner = players[p];
                    break;
                }
            }
            if (winner != NONE) break;
        }
    } 
}
