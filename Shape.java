/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

/**
 *
 * @author jhonlinux2
 */
import java.util.Random;
import java.lang.Math;

public class Shape {

/* Este enumerado mantiene los Tetrominoes, que son las 7 piezas posibles y una pieza en blanco llamada NoShape */
enum Tetrominoes { NoShape,  MirroredLShape
                };


/**
 * enum Tetrominoes { NoShape, ZShape, SShape, LineShape,
               TShape, SquareShape, LShape, MirroredLShape };
 */

// Cada pieza tiene un tipo de este enumerado
private Tetrominoes pieceShape;
// y unas coordenadas para la forma de la pieza (de 4x2)
private int coords[][];
// CoordsTable almacena todas las coordenadas de los diferentes tipos de piezas
private int[][][] coordsTable;


/* El constructuor inicializa:
- el array de coordenadas de la pieza a un array de 4x2
- el tipo de pieza a NoShape */
public Shape() {
        coords = new int[4][2];
        setShape(Tetrominoes.NoShape);
    }

/* Este método recibe un tipo de pieza (shape) y asigna los valores de las coordenadas de la pieza (coords) segun el tipo de pieza que es. Para ello busca en (coordsTable), el tipo de pieza y la copia a coords. */
    public void setShape(Tetrominoes shape) {

/* Cada fila de coordsTable representa un tipo de pieza y estan en el orden en el que aparecen en el enumerado. Por ejemplo, los numeros { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 }, representan la pieza S-shape rotada.*/
        coordsTable = new int[][][] {
        { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
        { { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } }
        
     /*
        { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
        { { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } },
        { { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } },
        { { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } },
        { { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } },
        { { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },
        { { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } },
        { { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } }
                
                
                */           
                
                
};

/* Aqui es donde se copia la fila de la pieza que nos interesa de la coordsTable a las coordenadas de la pieza (coords). Para saber que fila de la tabla coger debemos saber que posicion ocupa esa pieza (shape) dentro del enumerado. Esto lo hacemos utilizando el metodo ordinal() que nos devuelve un entero con la posicion que ocupa. */
for (int i = 0; i < 4 ; i++) {
        for (int j = 0; j < 2; ++j) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
        }
}

// Por ultimo asignamos el tipo de pieza actual
pieceShape = shape;

}

/* Metodos Get y Set */
private void setX(int index, int x) { coords[index][0] = x; }
private void setY(int index, int y) { coords[index][1] = y; }
public int x(int index) { return coords[index][0]; }
public int y(int index) { return coords[index][1]; }
public Tetrominoes getShape()  { return pieceShape; }


/* Metodo que genera una figura aleatoriamente */
public void setRandomShape()
{
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 1 + 1;
        Tetrominoes[] values = Tetrominoes.values();
        setShape(values[x]);
}


/* Mira las coordenadas de la pieza y buscan la posicion minima en x e y */
public int minX()
{
        int m = coords[0][0];
        for (int i=0; i < 4; i++) {
                m = Math.min(m, coords[i][0]);
        }
        return m;
}

public int minY()
{
        int m = coords[0][1];
        for (int i=0; i < 4; i++) {
                m = Math.min(m, coords[i][1]);
        }
        return m;
}

/* Rota la pieza a la izquierda o derecha y devuelve la nueva forma.
        - Primero comprueba que la forma no sea el cuadrado, ya que si lo es, como no hay que rotarlo lo devuelve directamente.
        - En caso contrario crea una nueva forma (Shape) donde va guardando la forma rotada.
        - Para ello, recorre los 4 elementos de la forma, del tipo {int, int} y va asignandole los nuevos valores.
        - Utiliza los metodos get y(int index) x(int index) y los metodos set setX(int index, int x) y setY(int index, int y) */


public Shape rotateLeft()
{
        if (pieceShape == Tetrominoes.NoShape)  //. SquareShape
                return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
                result.setX(i, y(i));
                result.setY(i, -x(i));
        }
        return result;
}

public Shape rotateRight()
{
        if (pieceShape == Tetrominoes.NoShape)    //. SquareShape
                return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
                result.setX(i, -y(i));
                result.setY(i, x(i));
        }
        return result;
}
}
