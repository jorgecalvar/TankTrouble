package tanktrouble.reflection;

import tanktrouble.misc.Sonido;
import tanktrouble.ui.Dibujo;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * Esta clase define al objeto Bala, que hereda de la clase abstracta movible al estar definida por una posición y
 * ángulo, moverse siempre hacia adelante y tener unas restricciones de posición concretas.
 */

public class Bala extends Movible {

    /**
     * Radio de la bala
     */
    public static final int RADIO = 3;
    /**
     * Diámetro de la bala
     */
    public static final int DIAMETRO = 2 * RADIO;
    /**
     * La bala se autodestruirá después de este número de choques
     */
    public static final int MAX_CHOQUES = 10;

    /**
     * Color de fondo de la bala
     */
    public static Color COLOR_BG = Color.DARK_GRAY;
    /**
     * Color del borde de la bala. Es muy fino, por lo que en ocasiones puede ser difícil apreciarlo
     */
    public static Color COLOR_EDGE = Color.BLACK;
    private int choques = 0;
    private boolean active = true;

    public Bala(Point2D posicion, double theta, Dibujo dibujo) {
        super(posicion, theta, dibujo);
    }

    public boolean isActive() {
        return active;
    }


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
     * Este método es invocado cuando la bala se choca contra un tanque
     *
     * @param t tanque con el que se choca
     */
    private void choque(Tanque t) {
        active = false;
        dibujo.getSonido().playSound(Sonido.GUNSHOOT);
        dibujo.getBoard().hitPlayer(t);
    }


    /**
     * Permite el avanza de la bala. Debido a la alta velocidad y pequeño tamaño de la misma, este método avanzará la
     * bala dando pequeños salto de Pared.GROSOR, con el objetivo de que esta no atraviese a otros objetos.
     *
     * @param distancia distancia que avanzará la bala
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
     * Esto método avanzará la bala una distancia concreta si el punto final cumple con las restricciones de posición.
     * En caso contrario, no se produce movimiento. Sin embargo, este método no se preocupa porque todos los puntos
     * entre el incial y el final cumplan las restricciones de posición. Por tanto, si se introduce un valor de distancia
     * lo suficientemente grande, la bala podría llegar a atravesar objetos
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
            if (labUp() || labDown()) {
                reboteHorizontal();
                rebote = true;
            }
            if (labRight() || labLeft()) {
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
     * Cambio de ángulo provocado por el choque contra una pared horizontal
     */
    private void reboteHorizontal() {
        setTheta(-theta);
    }

    /**
     * Cambio de ángulo provocado por el choque contra un pared verical
     */
    private void reboteVertical() {
        setTheta(Math.PI - theta);
    }

    /**
     * Cambio de ángulo con el objetivo de realizar un cambio de sentido pero no dirección. Este metodo es llamado cuando
     * no se conoce el obstáculo que impide el movimiento de la bala.
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


    /*private boolean labDir(int offset) {
        Lab lab = dibujo.getLab();
        return lab.contains(new Point2D.Double(posicion.getX()+(RADIO+offset)*Math.cos(theta),
                posicion.getY()+(RADIO+offset)*Math.sin(theta)));
    }*/


    @Override
    protected boolean validAvance(int distancia) {
        Movible m = (Movible) clone();
        m.setPosicion(new Point2D.Double(posicion.getX() + Math.cos(theta) * distancia,
                posicion.getY() + Math.sin(theta) * distancia));
        return !m.illegalPosition();
    }

    /**
     * Este método siempre devuelve verdadero, ya que una bala, al tener forma circular, no solapará a ningún objeto tras
     * la rotación
     *
     * @param theta ángulo a rotar
     * @return si se permite la rotación siguiendo las restricciones de posicón
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
}
