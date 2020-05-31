package tanktrouble.reflection;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Esta clase define a las {@link Pared paredes} del {@link Lab}. Cada {@link Pared} es necesariamente una linea recta
 * que debe ser o bien horizontal o bien vertical.
 */

public class Pared implements Pintable {

    /**
     * Tipo que tienen las {@link Pared paredes} horizontales
     */
    public static final int TIPO_HORIZONTAL = 1;

    /**
     * Tipo que tienen las {@link Pared paredes} vertiacales
     */
    public static final int TIPO_VERTICAL = 2;

    /**
     * Grosor de la {@link Pared}.
     */
    public static final int GROSOR = 5;

    /**
     * Color de las {@link Pared paredes}.
     */
    public static Color COLOR;


    private Point2D start;
    private int longitud;

    private int tipo;

    private Rectangle2D rectangle;

    /**
     * Inicializa una {@link Pared}.
     *
     * @param start    posicion inicial
     * @param longitud longitud en pixeles
     * @param tipo     tipo: vertical y horizontal
     */
    public Pared(Point2D start, int longitud, int tipo) {
        this.start = start;
        this.longitud = longitud;
        this.tipo = tipo;
        createRectangle();
    }

    /**
     * Configura el {@link Color} de las paredes
     *
     * @param c {@link Color} a configurar
     */
    public static void setColor(Color c) {
        COLOR = c;
    }

    /**
     * Devuelve la longitud de la {@link Pared}
     *
     * @return longitud de la {@link Pared}
     */
    public int getLongitud() {
        return longitud;
    }

    /**
     * Devuelve el tipo de la {@link Pared}, que puede ser {@link #TIPO_HORIZONTAL} o {@link #TIPO_VERTICAL}.
     *
     * @return tipo de la {@link Pared}
     */
    public int getTipo() {
        return tipo;
    }

    /**
     * Obtiene el punto donde empieza la {@link Pared}, es decir, el punto con menores coordendas de la linea imaginaria que
     * delimita la {@link Pared}
     *
     * @return Punto donde empieza la {@link Pared}.
     */
    public Point2D getStart() {
        return start;
    }

    /**
     * Obtiene el punto donde termina la {@link Pared}, es decir, el punto con mayores coordendas de la linea imaginaria que
     * delimita la {@link Pared}.
     *
     * @return Punto donde termina la {@link Pared}
     */
    public Point2D getEnd() {
        if (tipo == TIPO_HORIZONTAL)
            return new Point2D.Double(start.getX() + longitud, start.getY());
        return new Point2D.Double(start.getX(), start.getY() + longitud);
    }

    /**
     * Devuelve el rectangulo que incluye la {@link Pared} compelta, es decir, teniendo en cuenta el grosor
     *
     * @return Rectangulo correspondiente a la {@link Pared}
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
