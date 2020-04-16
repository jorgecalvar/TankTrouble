package tanktrouble.reflection;

import tanktrouble.control.Speeder;
import tanktrouble.control.TanqueController;
import tanktrouble.misc.Util;
import tanktrouble.ui.Dibujo;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase define al objeto Tanque, que representa una forma que es capaz de dibujarse en un Canvas. La funcionalidad
 * incluye además ser capaz de moverse y de girar, abstrayendo el movimiento de los vehículos en el mundo real. Es decir,
 * el tanque es capaz de cambiar su posición automáticamente siguiente las definiciones de los métodos avanzar y rotar. *
 */

public class Tanque extends Movible {

    /**
     * Longitud del lado de la base del tanque que es paralelo al eje horizontal cuando theta es 0
     */
    public static final int BASE_WIDTH = 50;

    /**
     * Longitud del lado de la base del tanque que es vertical al eje horizontal cunando theta es 0
     */
    public static final int BASE_HEIGTH = 40;

    /**
     * Longitud del lado largo del cañón del tanque
     */
    public static final int CANNON_WIDTH = 30;

    /**
     * Longitud del lado corto de la longitud del tanque
     */
    public static final int CANNON_HEIGHT = 10;

    /**
     * Parte del cañón que se encuentra sobre la base del tanque
     */
    public static final double CANNON_FACTOR = 0.6;

    /**
     * Radio del círculo que se encuentra en el centro de la base del tanque
     */
    public static final int CIRCLE_RADIO = 10;


    /**
     * Color de la base del tanque
     */
    private static Color COLOR_BASE;

    /**
     * Color del canon del tanque
     */
    private static Color COLOR_CANNON;

    /**
     * Color de los bordes de las partes del tanque
     */
    private static Color COLOR_BORDE = Color.BLACK;


    /**
     * Construye un objeto tanque
     *
     * @param x      coordenada x de la esquina superior izquierda cuando theta es 0
     * @param y      coordenada y de la esquina superior izquiereda cunado theta es 0
     * @param theta  angulo (en radianes) respecto al eje x positivo en sentido del eje y positivo
     * @param dibujo dibujo en el que se encuentra este tanque
     */
    public Tanque(int x, int y, double theta, Dibujo dibujo) {
        this(new Point2D.Double(x, y), theta, dibujo);
    }

    /**
     * Construye un objeto tanque
     *
     * @param posicion posicion de la esquina superior izquierda cuando theta es 0
     * @param theta    ángulo (en radianes) respecto al eje x positivo en sentido del eje y positivo
     * @param dibujo   dibujo en el que se encuentra este tanque
     */
    public Tanque(Point2D posicion, double theta, Dibujo dibujo) {
        super(posicion, theta, dibujo);
    }

    /**
     * Configura el color de la base
     *
     * @param c color
     */
    public static void setColorBase(Color c) {
        COLOR_BASE = c;
        COLOR_CANNON = c.brighter();
    }

    @Override
    public void pintar(Graphics2D g) {
        if (illegalPosition())
            throw new UnsupportedOperationException("Es ilegal pintar un tanque que no cumpla " +
                    "las restricciones de posición");
        Polygon base = getBase();
        Polygon cannon = getCannon();
        Ellipse2D circle = getCircle();
        g.setColor(COLOR_BASE);
        g.fill(base);
        g.setColor(COLOR_BORDE);
        g.draw(base);
        g.setColor(COLOR_CANNON);
        g.fill(cannon);
        g.setColor(COLOR_BORDE);
        g.draw(cannon);
        g.setColor(COLOR_CANNON);
        g.fill(circle);
        g.setColor(COLOR_BORDE);
        g.draw(circle);
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
        if (Math.abs(d) < Math.abs(distancia))
            rotaContraPared(distancia);
    }

    private void rotaContraPared(int distancia) {
        final double angle = 1.0 / Speeder.FRAMES_PER_SECOND * TanqueController.VELOCIDAD_ROTACION;
        double thethaMov = distancia > 0 ? theta : Util.formatAngle(theta + Math.PI);
        if (thethaMov < Math.PI / 2) {
            if (labDown() && !labRight())
                rota(-angle);
            else if (labRight() && !labDown())
                rota(angle);
        } else if (thethaMov < Math.PI) {
            if (labDown() && !labLeft())
                rota(angle);
            else if (labLeft() && !labDown())
                rota(-angle);
        } else if (thethaMov < 3 * Math.PI / 2) {
            if (labLeft() && !labUp())
                rota(angle);
            else if (labUp() && !labLeft())
                rota(-angle);
        } else if (thethaMov < 2 * Math.PI) {
            if (labRight() && !labUp())
                rota(-angle);
            else if (labUp() && !labRight()) {
                rota(angle);
            }
        }
    }

    /**
     * Dispara una bala
     */
    public void shoot() {
        Point2D centroBala = getCentroBala();
        Point2D posicionBala = new Point2D.Double(centroBala.getX() - Bala.RADIO, centroBala.getY() - Bala.RADIO);
        Bala bala = new Bala(posicionBala, theta, dibujo);
        dibujo.getBalasController().add(bala);
    }

    /**
     * Se sobrescribe este método con el objetivo de que se ignore a un tanque cuando se esté calculando si está en
     * una posición ilegal
     *
     * @return si la posición es ilegal
     */
    @Override
    public boolean illegalPosition() {
        return illegalPosition(this);
    }

    /**
     * Comprueba si la rotación del tanque un ángulo theta cumple con las restricciones de posición
     *
     * @param theta ángulo a rotar
     * @return si dicho ángulo cumple las restricciones de posición
     */
    @Override
    protected boolean validRotation(double theta) {
        Tanque t = (Tanque) clone();
        t.rotaIgnoraRestricciones(theta);
        return !t.illegalPosition(this);
    }

    /**
     * Calcula si el tanque resultante de avanzar una distancia cumple las restricciones de posición
     *
     * @param distancia distancia a avanzar
     * @return si la posición resultante es válida
     */
    protected boolean validAvance(int distancia) {
        Movible m = (Movible) clone();
        m.setPosicion(new Point2D.Double(posicion.getX() + Math.cos(theta) * distancia,
                posicion.getY() + Math.sin(theta) * distancia));
        return !m.illegalPosition(this);
    }

    /**
     * Devuelve el centro de masa del tanque, considerando este como el centro de masa de la base
     *
     * @return centro de masa
     */
    @Override
    public Point2D getMassCenter() {
        return getBaseCenter();
    }

    /**
     * Calcula el centro de masa de la base del tanque
     *
     * @return centro de masa de la base del tanque
     */
    public Point2D getBaseCenter() {
        List<Point2D> corners = getBaseCorners();
        return new Point2D.Double((corners.get(0).getX() + corners.get(2).getX()) / 2,
                (corners.get(0).getY() + corners.get(2).getY()) / 2);
    }

    /**
     * Devuelve el polígono con la base del tanque
     *
     * @return Polígono de la base del tanque
     */
    public Polygon getBase() {
        Polygon p = new Polygon();
        for (Point2D point : getBasePoints())
            p.addPoint((int) point.getX(), (int) point.getY());
        return p;
    }

    /**
     * Devuelve el polígono con el cañón del tanque
     *
     * @return Polígono del cañón del tanque
     */
    public Polygon getCannon() {
        Polygon p = new Polygon();
        for (Point2D point : getCannonPoints())
            p.addPoint((int) point.getX(), (int) point.getY());
        return p;
    }

    public Ellipse2D getCircle() {
        Point2D c = getMassCenter();
        return new Ellipse2D.Double(c.getX() - CIRCLE_RADIO, c.getY() - CIRCLE_RADIO,
                2 * CIRCLE_RADIO, 2 * CIRCLE_RADIO);
    }

    @Override
    public Object clone() {
        return new Tanque(posicion, theta, dibujo);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tanque)) return false;
        if (this == obj) return true;
        Tanque t = (Tanque) obj;
        return posicion.equals(t.getPosicion()) && theta == t.getTheta() && dibujo.equals(t.getDibujo());
    }

    /**
     * Calcula la posición de la bala que genera el tanque teniendo en cuenta su posición actual
     *
     * @return posicion de la bala
     */
    private Point2D getCentroBala() {
        final int offset = 1;
        Point2D p = new Point2D.Double(posicion.getX() + BASE_WIDTH + (1 - CANNON_FACTOR) * CANNON_WIDTH + Bala.RADIO + offset,
                posicion.getY() + 1.0 * BASE_HEIGTH / 2);
        Point2D q = new Point2D.Double();
        at.transform(p, q);
        return q;
    }

    private List<Point2D> getOriginalBasePoints() {
        final int OFFSET = 4;
        List<Point2D> corners = Util.getCornersFromRectangle(posicion, BASE_WIDTH, BASE_HEIGTH);
        List<Point2D> points = new ArrayList<>();
        Point2D p = corners.get(0); //Upper left
        points.add(new Point2D.Double(p.getX(), p.getY() + OFFSET));
        points.add(new Point2D.Double(p.getX() + OFFSET, p.getY()));
        p = corners.get(1); //Upper right
        points.add(new Point2D.Double(p.getX() - OFFSET, p.getY()));
        points.add(new Point2D.Double(p.getX(), p.getY() + OFFSET));
        p = corners.get(2);
        points.add(new Point2D.Double(p.getX(), p.getY() - OFFSET));
        points.add(new Point2D.Double(p.getX() - OFFSET, p.getY()));
        p = corners.get(3);
        points.add(new Point2D.Double(p.getX() + OFFSET, p.getY()));
        points.add(new Point2D.Double(p.getX(), p.getY() - OFFSET));
        return points;
    }

    private List<Point2D> getBasePoints() {
        return rotate(getOriginalBasePoints());
    }

    private List<Point2D> getOriginalBaseCorners() {
        return Util.getCornersFromRectangle(posicion, BASE_WIDTH, BASE_HEIGTH);
    }

    private List<Point2D> getBaseCorners() {
        return rotate(getOriginalBaseCorners());
    }

    private List<Point2D> getOriginalCannonPoints() {
        Point2D firstPoint = new Point2D.Double(posicion.getX() + CANNON_FACTOR * BASE_WIDTH,
                posicion.getY() + 1.0 * (BASE_HEIGTH - CANNON_HEIGHT) / 2);
        return Util.getCornersFromRectangle(firstPoint, CANNON_WIDTH, CANNON_HEIGHT);
    }

    private List<Point2D> getCannonPoints() {
        return rotate(getOriginalCannonPoints());
    }

    /**
     * Rota respectro al centro de masa del tanque
     *
     * @param p punto a rotar
     * @return punto rotado
     */
    private Point2D rotate(Point2D p) {
        Point2D q = new Point2D.Double();
        at.transform(p, q);
        return q;
    }

    /**
     * Método de conveniencia para rotate(Point2D) pero recibe una lista como parámetro
     *
     * @param points puntos a rotar
     * @return puntos rotados
     */
    private List<Point2D> rotate(List<Point2D> points) {
        List<Point2D> points2 = new ArrayList<>();
        for (Point2D p : points)
            points2.add(rotate(p));
        return points2;
    }

    /**
     * Elimina y vuelva a crear el área de este rectángulo
     */
    protected void createArea() {
        reset();
        add(new Area(getBase()));
        add(new Area(getCannon()));
    }

}
