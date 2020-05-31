package tanktrouble.labcreator;

import tanktrouble.reflection.Pintable;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * {@link Area} (con forma de circulo) que representa al cada uno de los puntos que conforman la red de puntos del
 * {@link CreatorCanvas} sobre la que el usuario dibujara sus laberintos personalizados.
 */
public class Dot extends Area implements Pintable {

    /**
     * Diametro de cada punto
     */
    public static final int DIAMETER = 24;

    /**
     * Posicion de cada punto (centro del circulo)
     */
    private Point2D loc;

    /**
     * Crea un objeto {@link Dot}.
     *
     * @param loc posicion del centro
     */
    public Dot(Point2D loc) {
        this.loc = loc;
        add(new Area(new Ellipse2D.Double(loc.getX() - DIAMETER / 2., loc.getY() - DIAMETER / 2., DIAMETER, DIAMETER)));
    }

    /**
     * Devuelve la posicion del objeto
     *
     * @return posicion del obeto
     */
    public Point2D getLoc() {
        return loc;
    }

    /**
     * Pinta el objeto
     *
     * @param g Donde se pinta el objeto
     */
    @Override
    public void pintar(Graphics2D g) {
        g.fill(this);
    }
}
