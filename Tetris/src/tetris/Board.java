package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    // Inicializamos algunas variables importantes

    // El tablero contiene un conjunto de formas
    Shape.Tetrominoes[] board;
    // Tamano del tablero
    final int BoardWidth = 10;
    final int BoardHeight = 20;

    // numLinesRemoved mantiene el contador de las lineas que hemos limpiado
    int numLinesRemoved = 0;
    JLabel statusbar;

    // pieza actual
    Shape curPiece;
    // curX y curY determinan la posicion actual de la pieza que esta cayendo
    int curX = 0;
    int curY = 0;

    // isFallingFinished determina si la pieza ha terminado de caer
    // para asi saber si tenemos que generar una nueva
    boolean isFallingFinished = false;
    boolean isStarted = false;
    boolean isPaused = false;
    Timer timer;

    /* Constructor */
    public Board(Tetris parent) {

       // Llamamos explicitamente al metodo setFocusable() con true
       // para que desde ahora tenga el foco y el imput del teclado
       setFocusable(true);

       // Generamos una nueva pieza
       curPiece = new Shape();

       // El timer lanza eventos cada cierto tiempo indicado por el delay.
       // En nuestro caso el timer llama a actionPerformed() cada 400 ms
       timer = new Timer(400, this);
       timer.start();

       // Asignamos la barra de estado
       statusbar =  parent.getStatusBar();

       // Iniciamos el tablero con piezas vacias hasta el ancho y alto indicados
       board = new Shape.Tetrominoes[BoardWidth * BoardHeight];

       addKeyListener(new TAdapter());
       clearBoard();
    }

    /* El metodo actionPerformed() comprueba si la caida de la pieza ha finalizado. En ese caso genera una nueva pieza con newPieze(). En caso contrario mueve una linea abajo con oneLineDown() */
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    // Metodos auxiliares
    int squareWidth() { return (int) getSize().getWidth() / BoardWidth; }
    int squareHeight() { return (int) getSize().getHeight() / BoardHeight; }
    Shape.Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }


    // Inicializa una partida nueva
    public void start()
    {
        if (isPaused)
            return;

        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();

        newPiece();
        timer.start();
    }

    // Pausa o despausa la partida
    

    /* Este metodo dibuja todos los objetos en el tablero.
     * El proceso tiene 2 pasos:
     * 1. Se pintan todas las figuras que ya se habian colocado en el tablero.
     * 2. Pintamos la figura que esta cayendo actualmente. */
    public void paint(Graphics g)
    {
        super.paint(g);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

        /* 1. Se pintan todas las figuras que ya han tocado la parte baja del tablero. Todos los cuadrados estan guardados en el array de tablero y podemos acceder a el usando el metodo shapeAt() */
        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Shape.Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                if (shape != Shape.Tetrominoes.NoShape)
                    drawSquare(g, 0 + j * squareWidth(),
                               boardTop + i * squareHeight(), shape);
            }
        }

        /* 2. Pintamos la figura que esta cayendo actualmente. */
        if (curPiece.getShape() != Shape.Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(),
                           boardTop + (BoardHeight - y - 1) * squareHeight(),
                           curPiece.getShape());
            }
        }
    }

    /* Este metodo mueve la pieza una linea abajo si es posible.
     Para implementar el metodo se utilizan dos metodos auxiliares:
     - tryMove para saber si se puede mover la pieza a ese nuevo lugar
     - y pieceDropped que una vez que la pieza tiene ya su posicion definitiva la guarda en el array del tablero */
    private void oneLineDown()
    {
        if (!tryMove(curPiece, curX, curY - 1))
            pieceDropped();
    }


    /* Este metodo limpia el array del tablero (board). Para ello, asigna a cada una de sus casillas una figura vacia (Tetrominoes NoShape). */
    private void clearBoard()
    {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Shape.Tetrominoes.NoShape;
    }

    /* Este metodo anade la pieza que esta cayendo al array del tablero (board). Se llamara cuando la pieza ya haya terminado de caer, asi que debemos comprobar si ha hecho una linea que hay que borrar o no, llamando para ello al metodo removeFullLines(). Por ultimo, intentamos crear una nueva pieza para seguir jugando. */
    private void pieceDropped()
    {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
        }


    }

    /* Este metodo crea una nueva pieza que cae y la asigna a curPiece. Lo asigna con una forma aleatoria usando el metodo setRandomShape. Entonces inicializamos su posicion actual curX y curY a la parte superior. Posteriormente vemos si la pieza se puede mover a esa posicion inicial que hemos asignado, utilizando el metodo tryMove.
     Si no se puede mover es porque ya esta todo el tablero lleno y hemos perdido y por lo tanto, debemos hacer varias cosas:
     - asignar a la p ieza actual curPiece la figura NoShape
     - parar el timer
     - cambiar el booleano de comienzo isStarted a falso
     - asignar a la barra de estado statusbar el texto "game over" */
    private void newPiece()
    {
        curPiece.setRandomShape();
        curX = BoardWidth / 2 + 1;
        curY = BoardHeight - 1 + curPiece.minY();

    }

    /* Este metodo intenta mover una pieza a una posicion x y que pasamos como argumentos. El metodo devuelve false si no ha sido posible moverla a esa posicion. Esto puede pasar por dos motivos:
     1. que queramos salir de los limites del tablero.
     2. que haya tocado otra pieza
     Si no ocurre ninguno de estos casos, la pieza se puede mover, por lo que actualizamos su posicion, repintamos y devolvemos verdadero. */
    private boolean tryMove(Shape newPiece, int newX, int newY)
    {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != Shape.Tetrominoes.NoShape)
                return false;
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }


     /* Este metodo dibuja cada uno de los 4 cuadrados que componen una pieza.
      Asigna para cada tipo de pieza un color distinto.
      Y anade a los bordes izquierdo y superior de cada cuadrado un poco de brillo, y al derecho e inferior un poco de sombra, para dar un efecto 3d. */
    private void drawSquare(Graphics g, int x, int y, Shape.Tetrominoes shape)
    {
        Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102)};

        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                         x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                         x + squareWidth() - 1, y + 1);
    }

    /* Implementacion de los controles por teclado.*/
    class TAdapter extends KeyAdapter {
         public void keyPressed(KeyEvent e) {

             if (!isStarted || curPiece.getShape() == Shape.Tetrominoes.NoShape) {
                 return;
             }

             int keycode = e.getKeyCode();


             switch (keycode) {
             case KeyEvent.VK_LEFT:
                 tryMove(curPiece, curX - 1, curY);
                 break;
             case KeyEvent.VK_RIGHT:
                 tryMove(curPiece, curX + 1, curY);
                 break;
             case KeyEvent.VK_DOWN:
                 tryMove(curPiece.rotateRight(), curX, curY);
                 break;
             case KeyEvent.VK_UP:
                 tryMove(curPiece.rotateLeft(), curX, curY);
                 break;

             }

         }
     }
}
 