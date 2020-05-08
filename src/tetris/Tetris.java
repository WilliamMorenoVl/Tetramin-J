package tetris;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Tetris extends JFrame {

    JLabel statusbar;

    public Tetris() {

    // Cremos el tablero de juego
        Board board = new Board(this);
        add(board);
        board.start(); // este metodo arranca el juego
        setSize(200, 400);
        setTitle("UniTETRIS");

   }

   public JLabel getStatusBar() {
       return statusbar;
   }

    public static void main(String[] args) {

        Tetris game = new Tetris();
        game.setLocationRelativeTo(null);
        game.setVisible(true);

    }
}
