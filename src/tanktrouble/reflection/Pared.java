package tanktrouble.reflection;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Esta clase define a las paredes del laberinto. Cada pared es necesariamente una línea recta que debe ser o bien
 * horizontal o bien vertical.
 */

public class Pared implements Pintable {

    /**
     * Tipo que tienee las paredes horizontales
     */
    public static final int TIPO_HORIZONTAL = 1;

    /**
     * Tipo que tienen las paredes vertiacales
     */
    public static final int TIPO_VERTICAL = 2;

    /**
     * Grosor de la pred
     */
    public static final int GROSOR = 5;

    /**
     * Color de las paredes
     */
    public static Color COLOR;


    private Point2D start;
    private int longitud;

    private int tipo;

    private Rectangle2D rectangle;

    public Pared(Point2D start, int longitud, int tipo) {
        this.start = start;
        this.longitud = longitud;
        this.tipo = tipo;
        createRectangle();
    }

    /**
     * Configura el color de las paredes
     *
     * @param c color a configurar
     */
    public static void setColor(Color c) {
        COLOR = c;
    }

    /**
     * Devuelve la longitud de la pared
     *
     * @return longitud de la pared
     */
    public int getLongitud() {
        return longitud;
    }

    /**
     * Devuelve el tipo de la pared, que puede ser horizontal o vertical
     *
     * @return tipo de la pared
     */
    public int getTipo() {
        return tipo;
    }

    /**
     * Obtiene el punto donde empieza la pared, es decir, el punto con menores coordendas de la línea imaginaria que
     * delimita la pared
     *
     * @return Punto donde empieza la pared
     */
    public Point2D getStart() {
        return start;
    }

    /**
     * Obtiene el punto donde termina la pared, es decir, el punto con mayores coordendas de la línea imaginaria que
     * delimita la pared
     *
     * @return Punto donde termina la pared
     */
    public Point2D getEnd() {
        if (tipo == TIPO_HORIZONTAL)
            return new Point2D.Double(start.getX() + longitud, start.getY());
        return new Point2D.Double(start.getX(), start.getY() + longitud);
    }

    /**
     * Devuelve el rectángulo que incluye la pared compelta, es decir, teniendo en cuenta el grosor
     *
     * @return Rectángulo correspondiente a la pared
     */
    public Rectangle2D getRectangle() {
        return rectangle;
    }

    @Override
    public void pintar(Graphics2D g) {
        g.setColor(COLOR);
        g.fill(rectangle);
    }

    private void createRectangle() {
        switch (tipo) {
            case TIPO_HORIZONTAL:
                rectangle = new Rectangle2D.Double(start.getX(), start.getY() - 1.0 * GROSOR / 2, longitud, GROSOR);
                break;
            case TIPO_VERTICAL:
                rectangle = new Rectangle2D.Double(start.getX() - 1.0 * GROSOR / 2, start.getY(), GROSOR, longitud);
                break;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pared)) return false;
        if (obj == this) return true;
        Pared p = (Pared) obj;
        return start.equals(p.getStart()) && longitud == p.getLongitud() && tipo == p.getTipo();
    }
}
