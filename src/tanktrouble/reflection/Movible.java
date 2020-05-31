package tanktrouble.reflection;

import tanktrouble.misc.Util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Este clase define a todas las formas que se dibujan sobre el {@link Dibujo} y que tienen un movimiento. Se los define
 * mediante una posicion y un angulo.
 * <p>
 * Los metodos diseñados para permitir el movimiento de los objetos son {@link #avanza(int)} y {@link #rota(double)} , ya
 * que antes de permitir el movimiento comprueban que el nuevo estado del objeto cumplira las restricciones de posicion.
 * Sin embargo, tambien es posible forzar un cambio en el movimiento mediante metodos como {@link #setPosicion(Point2D)},
 * {@link #setTheta(double)} o {@link #rotaIgnoraRestricciones(double)}.
 * <p>
 * Las restricciones de posicion que debe cumplir cualquier objeto {@link Movible} en el {@link Dibujo} son las siguientes:
 * 1. Debe encontrarse en el interior de los limites exteriores del {@link Lab}.
 * 2. No puedue solapar ninguna de las {@link Pared paredes} interiores del {@link Lab}.
 * 3. No puede solapar a ninguno de los objetos {@link Tanque} que se encuentran en el {@link Lab}.
 * <p>
 * Notese: que otros objetos {@link Movible} distintos de {@link Tanque} si pueden solaparse pero ninguno podra solapar
 * un {@link Tanque} o un {@link Tanque} solapar a otro {@link Tanque}.
 */
public abstract class Movible extends Area implements Pintable {

    /**
     * Offset utilizado cuando se comprueba si hay {@link Lab} en alguno de los lados del objeto {@link Movible}.
     */
    public static final int OFFSET = 2;

    /**
     * Posicion en la que se encuentra el objeto, generalmetne representada por el punto superior izquierdo.
     */
    protected Point2D posicion;

    /**
     * Angulo del objeto, respecto al eje x positivo, en sentido positivo hacia el eje y positivo.
     */
    protected double theta;

    /**
     * Transformacion afin creada por la posicion y angulo concretos.
     */
    protected AffineTransform at;

    /**
     * {@link Dibujo} en el que se enmarca este objeto.
     */
    protected Dibujo dibujo;

    /**
     * Crea un objeto {@link Movible} con una {@link #posicion}, {@link #theta} y {@link #dibujo} concretos.
     *
     * @param posicion posicion inicial
     * @param theta    angulo inicial
     * @param dibujo   {@link Dibujo} en el que se encuentra.
     */
    public Movible(Point2D posicion, double theta, Dibujo dibujo) {
        this.dibujo = dibujo;
        setTheta(theta);
        setPosicion(posicion);
    }

    /**
     * Devuelve la {@link #posicion} del objeto.
     *
     * @return {@link #posicion}
     */
    public Point2D getPosicion() {
        return posicion;
    }

    /**
     * Da un nuevo valor al parametro {@link #posicion}
     *
     * @param posicion Nuevo valor de la {@link #posicion}
     */
    public void setPosicion(Point2D posicion) {
        this.posicion = posicion;
        if (posicion != null) {
            at = AffineTransform.getRotateInstance(theta, posicion.getX(), posicion.getY());
            createArea();
        }
    }

    /**
     * Devuelve el {@link #theta angulo} del objeto.
     *
     * @return {@link #theta}
     */
    public double getTheta() {
        return theta;
    }

    /**
     * Da un nuevo valor a {@link #theta} entre 0 y 2PI
     *
     * @param theta Nuevo valor de {@link #theta}
     */
    public void setTheta(double theta) {
        this.theta = Util.formatAngle(theta);
    }

    /**
     * Devuelve el {@link #dibujo} actual.
     *
     * @return {@link #dibujo}
     */
    public Dibujo getDibujo() {
        return dibujo;
    }

    /**
     * Calcula si la base de este {@link Movible} intersecta con el {@link Lab} en el que se encuentra
     *
     * @return Si el {@link Movible} intersecta el laberinto
     */
    public boolean overlapsLab() {
        Area a = (Area) dibujo.getLab().clone();
        a.intersect(this);
        return !a.isEmpty();
    }

    /**
     * Calcula si el objeto cumple las restricciones de posicion.
     *
     * @return Si el {@link Movible} cumple sus restricciones de posicion
     */
    public boolean illegalPosition() {
        return illegalPosition(null);
    }

    /**
     * Comprueba si el {@link Movible} cumple las restricciones de posicion de su dibujo sin tener en cuenta el
     * {@link Tanque} ignore
     *
     * @param ignore {@link Tanque} que se ignora en la comprobacion
     * @return si el {@link Movible} cumple con las restricciones de posicion
     */
    public boolean illegalPosition(Tanque ignore) {
        return overlapsLab() || overlapsTanques(ignore);
    }


    /**
     * Comprueba si este {@link Movible} se solapa con cualquier de los {@link Tanque tanques} que hay en el dibujo,
     * excepto al que se ignora.
     *
     * @param ignore {@link Tanque} que se ignora en la comprobacion
     * @return si hay solapamiento
     */
    public boolean overlapsTanques(Tanque ignore) {
        for (Tanque t : dibujo.getTanques())
            if (!t.equals((Object) ignore) && overlapsTanque(t))
                return true;
        return false;
    }


    /**
     * Calcula si este {@link Movible} se solapa con otro {@link Tanque}
     *
     * @param t {@link Tanque} con el comprobar el solaparmiento
     * @return si hay solapamiento
     */
    public boolean overlapsTanque(Tanque t) {
        Area a = (Area) clone();
        a.intersect(t);
        return !a.isEmpty();
    }

    /**
     * Mueve este {@link Movible} la distancia especificada siempre que sea posible siguiendo sus restricciones de
     * posicion.
     *
     * @param distancia distancia que avanzara el {@link Tanque}
     */
    public void avanza(int distancia) {
        int d = calcAvance(distancia);
        if (d != 0)
            setPosicion(new Point2D.Double(posicion.getX() + Math.cos(theta) * d, posicion.getY() + Math.sin(theta) * d));
    }


    /**
     * Calcula la mayor distancia menor que el parametro distancia que puede avanzar el {@link Tanque} siguiendo sus
     * restricciones de posicion
     *
     * @param distancia maxima distancia que se desea avanzar
     * @return maxima distancia posible para avanzar
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
     * Calcula si el avanza de distancia es posible segun las restricciones de posicion
     *
     * @param distancia distancia que se quiere avanzar
     * @return si el avanza es legal
     */
    protected abstract boolean validAvance(int distancia);

    /**
     * Cambia el {@link #theta angulo} del {@link Tanque} a una nueva {@link #posicion} respecto al centro de masa de
     * la base siguiendo las restriciones de posicion. En caso de no ser posible, rotara el mayor angulo posible (en
     * valor absoluto) menor que parametro theta que cumpla con las restricciones.
     *
     * @param theta angulo a rotar
     */
    public void rota(double theta) {
        double t = calcRotacion(theta);
        if (t != 0)
            rotaIgnoraRestricciones(t);
    }

    /**
     * Calcula la maxima rotacion posible menor que el parametro theta que cumpla con las restricciones de posicion
     *
     * @param theta maximmo angulo a rotar
     * @return angulo a rotar
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
     * Calcula si la rotacion de un angulo determinado es legal segun las restricciones de posicion
     *
     * @param theta angulo que se quiere rotar
     * @return si la rotacion es posible
     */
    protected abstract boolean validRotation(double theta);

    /**
     * Cambia el {@link #theta angulo} del {@link Movible} a una posicion respecto al centro de masa de masas sin tener
     * en cuenta las restricciones de posicion
     *
     * @param theta angulo a rotar
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

    /**
     * Genera el {@link Area} de dicho objeto, que se corresponde con la parte de dicho objeto que "chocara" con otros
     * en el {@link Dibujo}.
     */
    protected abstract void createArea();

    /**
     * Obtiene el centro de masas del objeto, que se utilizara para rotar sobre el.
     *
     * @return centro de masas
     */
    public abstract Point2D getMassCenter();

    /**
     * Si hay un obstaculo sobre el {@link Movible}
     *
     * @return si hay un obstaculo sobre el {@link Movible}
     */
    public abstract boolean hasObstacleOver();

    /**
     * Si hay un obstaculo a la derecha del {@link Movible}
     *
     * @return si hay un obstaculo a la derecha del {@link Movible}
     */
    public abstract boolean hasObstacleRight();

    /**
     * Si hay un obstaculo a la izquierda del {@link Movible}
     *
     * @return si hay un obstaculo a la izquierda del {@link Movible}
     */
    public abstract boolean hasObstacleLeft();

    /**
     * Si hay un obstaculo bajo el {@link Movible}
     *
     * @return si hay un obstaculo bajo el {@link Movible}
     */
    public abstract boolean hasObstacleBelow();

    /**
     * Devuelve el rectangulo que debe repintarse para garantizar que el dibujo se actualiza respecto a la posicion
     * pasado.
     *
     * @return rectangulo que debe repintarse.
     */
    public abstract Rectangle2D getRepaintBounds();

}
