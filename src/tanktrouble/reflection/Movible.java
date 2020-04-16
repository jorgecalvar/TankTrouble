package tanktrouble.reflection;

import tanktrouble.misc.Util;
import tanktrouble.ui.Dibujo;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Este clase define a todas las formas que se dibujan sobre el Dibujo y que tienen un movimiento. Se los define mediante
 * una posición y un ángulo.
 * <p>
 * Los métodos diseñados para permitir el movimiento de los objetos son avanzar(int distancia) y rotar(double theta), ya
 * que antes de permitir el movimiento comprueban que el nuevo estado del objeto cumplirá las restricciones de posición.
 * Sin embargo, también es posible forzar un cambio en el movimiento mediante métodos como setPoisicón(...), setTheta(...)
 * o rotaIgnoraRestricciones(...).
 * <p>
 * IMPORTANTE: Los métodos encargados de cambiar la posición del tanque vigilarán que la nueva posición cumpla estas
 * restricciones.
 * <p>
 * Las restricciones de posición que debe cumplir cualquier objeto movible en el Dibujo son las siguientes:
 * 1. Debe encontrarse en el interior de los límites exteriores del Laberinto (objeto Lab)
 * 2. No puedue solapar ninguna de las paredes interiores del laberinto.
 * 3. No puede solapar a ninguno de los objetos tanque que se encuentran en el laberinto.
 * <p>
 * Nótese: que un otros objetos Movible distinto de tanque sí pueden solaparse pero ninguno podrá solapar un tanque o
 * un tanque solapar a otro tanque.
 */
public abstract class Movible extends Area implements Pintable {

    /**
     * Offset utilizado cuando se comprueba si hay laberinto en alguno de los lados del objeto movible.
     */
    public static final int OFFSET = 2;

    protected Point2D posicion;
    protected double theta;
    protected AffineTransform at;

    protected Dibujo dibujo;

    public Movible(Point2D posicion, double theta, Dibujo dibujo) {
        this.dibujo = dibujo;
        setTheta(theta);
        setPosicion(posicion);
    }


    public Point2D getPosicion() {
        return posicion;
    }

    /**
     * Da un nuevo valor al parámetro posición
     *
     * @param posicion Nuevo valor de la posición
     */
    public void setPosicion(Point2D posicion) {
        this.posicion = posicion;
        at = AffineTransform.getRotateInstance(theta, posicion.getX(), posicion.getY());
        createArea();
    }

    public double getTheta() {
        return theta;
    }

    /**
     * Da un nuevo al parámetro theta entre 0 y 2PI
     *
     * @param theta Nuevo valor de theta
     */
    public void setTheta(double theta) {
        this.theta = Util.formatAngle(theta);
    }

    public Dibujo getDibujo() {
        return dibujo;
    }

    /**
     * Calcula si la base de este tanque intersecta con el laberinto en el que se encuentra
     *
     * @return Si el tanque intersecta el laberinto
     */
    public boolean overlapsLab() {
        Area a = (Area) dibujo.getLab().clone();
        a.intersect(this);
        return !a.isEmpty();
    }

    /**
     * Calcula si el objeto cumple las restricciones de posición.
     *
     * @return Si e tanque cumple sus restricciones de posición
     */
    public boolean illegalPosition() {
        return illegalPosition(null);
    }

    /**
     * Comprueba si el tanque cumple las restricciones de posición de su dibujo sin tener en cuenta el tanque ignore
     *
     * @param ignore tanque que se ignorá en la comprobación
     * @return si el tanque cumple con las restricciones de posición
     */
    public boolean illegalPosition(Tanque ignore) {
        return overlapsLab() || overlapsTanques(ignore);
    }


    /**
     * Comprueba si este tanque se solapa con cualquier de los tanque que hay en el dibujo
     *
     * @return si hay solapamiento
     */
    public boolean overlapsTanques(Tanque ignore) {
        for (Tanque t : dibujo.getTanques())
            if (!t.equals((Object) ignore) && overlapsTanque(t))
                return true;
        return false;
    }


    /**
     * Calcula si este tanque se solapa con otro tanque t
     *
     * @param t tanque con el comprobar el solaparmiento
     * @return si hay solapamiento
     */
    public boolean overlapsTanque(Tanque t) {
        Area a = (Area) clone();
        a.intersect(t);
        return !a.isEmpty();
    }

    /**
     * Mueve este movible la distancia especificada siempre que sea posible siguiendo sus restricciones de posición.
     *
     * @param distancia cistancia que avanzará el tanque
     */
    public void avanza(int distancia) {
        int d = calcAvance(distancia);
        if (d != 0)
            setPosicion(new Point2D.Double(posicion.getX() + Math.cos(theta) * d, posicion.getY() + Math.sin(theta) * d));
    }


    /**
     * Calcula la mayor distancia menor que el parámetro distancia que puede avanzar el tanque siguiendo sus
     * restricciones de posición
     *
     * @param distancia máxima distancia que se desea avanzar
     * @return máxima distancia posible para avanzar
     */
    protected int calcAvance(int distancia) {
        int i = distancia;
        while (i != 0) {
            if (validAvance(i)) return i;
            if (distancia > 0)
                i--;
            else
                i++;
        }
        return 0;
    }

    /**
     * Calcula si el avanza de distancia es posible según las restricciones de posición
     *
     * @param distancia distancia que se quiere avanzar
     * @return si el avanza es legal
     */
    protected abstract boolean validAvance(int distancia);

    /**
     * Cambia el ángulo del tanque a una nueva posición respecto al centro de masas de la base siguiendo las restriciones
     * de posición. En caso de no ser posible, rotará el mayor ángulo posible (en valor absoluto) menor que theta que
     * cumpla con las restricciones.
     *
     * @param theta ángulo a rotar
     */
    public void rota(double theta) {
        double t = calcRotacion(theta);
        if (t != 0)
            rotaIgnoraRestricciones(t);
    }

    /**
     * Calcula la máxima rotación posible menor que el parámetro theta que cumpla con las restricciones de posición
     *
     * @param theta máximmo ángulo a rotar
     * @return ángulo a rotar
     */
    public double calcRotacion(double theta) {
        final double ANGULO_MINIMO = Math.toRadians(1);
        if (theta > 0) {
            while (theta > 0) {
                if (validRotation(theta)) return theta;
                theta = theta - ANGULO_MINIMO;
            }
        } else {
            while (theta < 0) {
                if (validRotation(theta)) return theta;
                theta = theta + ANGULO_MINIMO;
            }
        }
        return 0;
    }

    /**
     * Calcula si la rotación de un ángulo determinado es legal según las restricciones de posición
     *
     * @param theta ángulo que se quiere rotar
     * @return si la rotación es posible
     */
    protected abstract boolean validRotation(double theta);

    /**
     * Cambia el ángulo del tanque a una posición respecto al centro de masa de masas sin tener en cuenta las restricciones
     * de posición
     *
     * @param theta ángulo a rotar
     */
    public void rotaIgnoraRestricciones(double theta) {
        AffineTransform at2 = AffineTransform.getRotateInstance(theta, posicion.getX(), posicion.getY());
        Point2D oldCentro = getMassCenter();
        Point2D newCentro = new Point2D.Double();
        at2.transform(oldCentro, newCentro);
        setTheta(this.theta + theta);
        setPosicion(new Point2D.Double(posicion.getX() - (newCentro.getX() - oldCentro.getX()),
                posicion.getY() - (newCentro.getY() - oldCentro.getY())));
    }

    public boolean labUp() {
        Lab lab = dibujo.getLab();
        Rectangle2D bounds = getBounds();
        return lab.contains(new Point2D.Double(bounds.getCenterX(), bounds.getMinY() - OFFSET));
    }

    public boolean labRight() {
        Lab lab = dibujo.getLab();
        Rectangle2D bounds = getBounds();
        return lab.contains(new Point2D.Double(bounds.getMaxX() + OFFSET, bounds.getCenterY()));
    }

    public boolean labLeft() {
        Lab lab = dibujo.getLab();
        Rectangle2D bounds = getBounds();
        return lab.contains(new Point2D.Double(bounds.getMinX() - OFFSET, bounds.getCenterY()));
    }

    public boolean labDown() {
        Lab lab = dibujo.getLab();
        Rectangle2D bounds = getBounds();
        return lab.contains(new Point2D.Double(bounds.getCenterX(), bounds.getMaxY() + OFFSET));
    }

    /**
     * Genera el área de dicho objeto, que se corresponde con la parte de dicho objeto que "chocará" con otros en el
     * Dibujo.
     */
    protected abstract void createArea();

    /**
     * Obtiene el centro de masas del objeto, que se utilizará para rotar sobre él
     *
     * @return centro de masas
     */
    abstract Point2D getMassCenter();

}
