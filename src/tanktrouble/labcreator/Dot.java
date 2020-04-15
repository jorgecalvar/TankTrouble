package tanktrouble.labcreator;

import tanktrouble.reflection.Pintable;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * Area (con forma de circulo) que representa al cada uno de los puntos que conforman la red de puntos del CreatorCanvas
 * sobre la que el usuario dibujará sus laberintos personalizados.
 */
public class Dot extends Area implements Pintable {

    /**
     * Diámetro de cada punto
     */
    public static final int DIAMETER = 24;

    /**
     * Posición de cada punto (centro del círculo)
     */
    private Point2D loc;

    /**
     * Crea un objeto Dot.
     *
     * @param loc posición del centro
     */
    public Dot(Point2D loc) {
        this.loc = loc;
        add(new Area(new Ellipse2D.Double(loc.getX() - DIAMETER / 2., loc.getY() - DIAMETER / 2., DIAMETER, DIAMETER)));
    }

    /**
     * Devuelve la posición del objeto
     *
     * @return posición del obeto
     */
    public Point2D getLoc() {
        return loc;
    }

    /**
     * Pinta el objeto
     *
     * @param g Dónde se pinta el objeto
     */
    @Override
    public void pintar(Graphics2D g) {
        g.fill(this);
    }
}
