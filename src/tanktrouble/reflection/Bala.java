package tanktrouble.reflection;

import tanktrouble.control.BalasController;
import tanktrouble.control.Speeder;
import tanktrouble.ui.Dibujo;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Esta clase define al objeto {@link Bala}, que hereda de la clase abstracta {@link Movible} al estar definida por una
 * posicion y angulo, moverse siempre hacia adelante y tener unas restricciones de posicion concretas.
 */

public class Bala extends Movible {

    /**
     * Radio de la bala
     */
    public static final int RADIO = 3;
    /**
     * Diametro de la bala
     */
    public static final int DIAMETRO = 2 * RADIO;
    /**
     * La bala se autodestruira despues de este numero de choques
     */
    public static final int MAX_CHOQUES = 10;

    /**
     * Color de fondo de la bala
     */
    public static Color COLOR_BG = Color.DARK_GRAY;
    /**
     * Color del borde de la bala. Es muy fino, por lo que en ocasiones puede ser dificil apreciarlo
     */
    public static Color COLOR_EDGE = Color.BLACK;
    private int choques = 0;
    private boolean active = true;

    /**
     * Crea una bala con una posicion, angulo y dibujo concretos.
     *
     * @param posicion {@link Movible#posicion}
     * @param theta    {@link Movible#theta}
     * @param dibujo   {@link Dibujo}
     */
    public Bala(Point2D posicion, double theta, Dibujo dibujo) {
        super(posicion, theta, dibujo);
    }

    /**
     * Devuelve si la bala esta activada.
     *
     * @return si esta activada
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Pinta la {@link Bala} sobre el objeto {@link Graphics2D}.
     *
     * @param g Dónde se pintar la {@link Bala}
     */
    @Override
    public void pintar(Graphics2D g) {
        if (illegalPosition()) {
            active = false;
            return;
        }
        Ellipse2D ellipse = getShape();
        g.setColor(COLOR_BG);
        g.fill(ellipse);
        g.setColor(COLOR_EDGE);
        g.draw(ellipse);
    }

    /**
     * Este metodo es invocado cuando la {@link Bala} se choca contra un {@link Tanque}.
     *
     * @param t {@link Tanque} con el que se choca
     */
    private void choque(Tanque t) {
        active = false;
        dibujo.hitPlayer(t);
    }


    /**
     * Permite el avanza de la {@link Bala}. Debido a la alta velocidad y pequeño tamaño de la misma, este metodo
     * avanzara la {@link Bala} dando pequeños saltos de {@link Pared#GROSOR}, con el objetivo de que esta no atraviese
     * a otros objetos.
     *
     * @param distancia distancia que avanzara la {@link Bala}
     */
    @Override
    public void avanza(int distancia) {
        if (!active) return;
        if (distancia > Pared.GROSOR) {
            avanzaIgnoraCamino(Pared.GROSOR);
            avanza(distancia - Pared.GROSOR);
        } else {
            avanzaIgnoraCamino(distancia);
        }
    }

    /**
     * Esto metodo avanzara la {@link Bala} una distancia concreta si el punto final cumple con las restricciones de
     * posicion. En caso contrario, no se produce movimiento. Sin embargo, este metodo no se preocupa porque todos los puntos
     * entre el incial y el final cumplan las restricciones de posicion. Por tanto, si se introduce un valor de distancia
     * lo suficientemente grande, la {@link Bala} podria llegar a atravesar objetos
     *
     * @param distancia distancia a avanzar
     */
    public void avanzaIgnoraCamino(int distancia) {
        int d = calcAvance(distancia);
        setPosicion(new Point2D.Double(posicion.getX() + Math.cos(theta) * d, posicion.getY() + Math.sin(theta) * d));
        if (distancia != d) {
            //Choque
            choques++;
            if (choques == MAX_CHOQUES) {
                active = false;
                return;
            }
            boolean rebote = false;
            if (hasObstacleOver() || hasObstacleBelow()) {
                reboteHorizontal();
                rebote = true;
            }
            if (hasObstacleRight() || hasObstacleLeft()) {
                reboteVertical();
                rebote = true;
            }
            if (!rebote) {
                Tanque t = choqueConTanque(10);
                if (t != null) {
                    choque(t);
                } else {
                    reboteOpuesto();
                }
            }
        }
    }

    /**
     * Cambio de angulo provocado por el choque contra una pared horizontal
     */
    private void reboteHorizontal() {
        setTheta(-theta);
    }

    /**
     * Cambio de angulo provocado por el choque contra un pared verical
     */
    private void reboteVertical() {
        setTheta(Math.PI - theta);
    }

    /**
     * Cambio de angulo con el objetivo de realizar un cambio de sentido pero no direccion. Este metodo es llamado cuando
     * no se conoce el obstaculo que impide el movimiento de la bala.
     */
    private void reboteOpuesto() {
        setTheta(Math.PI + theta);
    }


    private Tanque choqueConTanque(int offset) {
        Bala b = (Bala) clone();
        b.setPosicion(new Point2D.Double(posicion.getX() + Math.cos(theta) * 10,
                posicion.getY() + Math.sin(theta) * offset));
        for (Tanque t : dibujo.getTanques())
            if (b.overlapsTanque(t))
                return t;
        return null;
    }


    @Override
    public boolean hasObstacleOver() {
        Lab lab = dibujo.getLab();
        Rectangle2D bounds = getBounds();
        return lab.contains(new Point2D.Double(bounds.getCenterX(), bounds.getMinY() - OFFSET));
    }

    @Override
    public boolean hasObstacleRight() {
        Lab lab = dibujo.getLab();
        Rectangle2D bounds = getBounds();
        return lab.contains(new Point2D.Double(bounds.getMaxX() + OFFSET, bounds.getCenterY()));
    }

    @Override
    public boolean hasObstacleLeft() {
        Lab lab = dibujo.getLab();
        Rectangle2D bounds = getBounds();
        return lab.contains(new Point2D.Double(bounds.getMinX() - OFFSET, bounds.getCenterY()));
    }

    @Override
    public boolean hasObstacleBelow() {
        Lab lab = dibujo.getLab();
        Rectangle2D bounds = getBounds();
        return lab.contains(new Point2D.Double(bounds.getCenterX(), bounds.getMaxY() + OFFSET));
    }


    @Override
    protected boolean validAvance(int distancia) {
        Movible m = (Movible) clone();
        m.setPosicion(new Point2D.Double(posicion.getX() + Math.cos(theta) * distancia,
                posicion.getY() + Math.sin(theta) * distancia));
        return !m.illegalPosition();
    }

    /**
     * Este metodo siempre devuelve verdadero, ya que una bala, al tener forma circular, no solapara a ningun objeto tras
     * la rotacion
     * º
     *
     * @param theta angulo a rotar
     * @return si se permite la rotacion siguiendo las restricciones de posicon
     */
    @Override
    protected boolean validRotation(double theta) {
        return true;
    }

    @Override
    public Object clone() {
        return new Bala(posicion, theta, dibujo);
    }

    @Override
    protected void createArea() {
        reset();
        add(new Area(getShape()));
    }

    @Override
    public Point2D getMassCenter() {
        return new Point2D.Double(posicion.getX() + RADIO, posicion.getY() + RADIO);
    }

    private Ellipse2D getShape() {
        return new Ellipse2D.Double(posicion.getX(), posicion.getY(), DIAMETRO, DIAMETRO);
    }

    @Override
    public Rectangle2D getRepaintBounds() {
        Rectangle2D bounds = getBounds2D();
        final int offset = (int) (1.0 * BalasController.VELOCIDAD_BALA / Speeder.FRAMES_PER_SECOND + 1);
        return new Rectangle2D.Double(bounds.getX() - offset, bounds.getY() - offset,
                bounds.getWidth() + 2 * offset, bounds.getHeight() + 2 * offset);
    }

}
