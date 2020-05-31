package tanktrouble.reflection;

import tanktrouble.control.Speeder;
import tanktrouble.control.TanqueController;
import tanktrouble.misc.Util;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase define al objeto {@link Tanque}, que representa una forma que es capaz de dibujarse en un {@link Canvas}.
 * La funcionalidad incluye ademas ser capaz de moverse y de girar, abstrayendo el movimiento de los vehiculos en el
 * mundo real. Es decir, el {@link Tanque} es capaz de cambiar su posicion automaticamente siguiente las definiciones
 * de los metodos avanzar y rotar.
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
     * Longitud del lado largo del cañon del tanque
     */
    public static final int CANNON_WIDTH = 30;

    /**
     * Longitud del lado corto de la longitud del tanque
     */
    public static final int CANNON_HEIGHT = 10;

    /**
     * Parte del cañon que se encuentra sobre la base del tanque
     */
    public static final double CANNON_FACTOR = 0.6;

    /**
     * Radio del circulo que se encuentra en el centro de la base del tanque
     */
    public static final int CIRCLE_RADIO = 10;


    /**
     * {@link Color} de la base del tanque
     */
    private static Color COLOR_BASE;

    /**
     * {@link Color} del canon del tanque
     */
    private static Color COLOR_CANNON;

    /**
     * {@link Color} de los bordes de las partes del tanque
     */
    private static Color COLOR_BORDE = Color.BLACK;


    /**
     * Construye un objeto {@link Tanque}
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
     * Construye un objeto {@link Tanque}
     *
     * @param posicion posicion de la esquina superior izquierda cuando theta es 0
     * @param theta    angulo (en radianes) respecto al eje x positivo en sentido del eje y positivo
     * @param dibujo   dibujo en el que se encuentra este tanque
     */
    public Tanque(Point2D posicion, double theta, Dibujo dibujo) {
        super(posicion, theta, dibujo);
    }

    /**
     * Configura el {@link Color} de la base
     *
     * @param c {@link Color}
     */
    public static void setColorBase(Color c) {
        COLOR_BASE = c;
        COLOR_CANNON = c.brighter();
    }

    @Override
    public void pintar(Graphics2D g) {
        if (illegalPosition())
            throw new UnsupportedOperationException("Es ilegal pintar un tanque que no cumpla " +
                    "las restricciones de posicion");
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
     * Mueve este {@link Tanque} la distancia especificada siempre que sea posible siguiendo sus restricciones de posicion.
     *
     * @param distancia cistancia que avanzara el tanque
     */
    @Override
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
            if (hasObstacleBelow() && !hasObstacleRight())
                rota(-angle);
            else if (hasObstacleRight() && !hasObstacleBelow())
                rota(angle);
        } else if (thethaMov < Math.PI) {
            if (hasObstacleBelow() && !hasObstacleLeft())
                rota(angle);
            else if (hasObstacleLeft() && !hasObstacleBelow())
                rota(-angle);
        } else if (thethaMov < 3 * Math.PI / 2) {
            if (hasObstacleLeft() && !hasObstacleOver())
                rota(angle);
            else if (hasObstacleOver() && !hasObstacleLeft())
                rota(-angle);
        } else if (thethaMov < 2 * Math.PI) {
            if (hasObstacleRight() && !hasObstacleOver())
                rota(-angle);
            else if (hasObstacleOver() && !hasObstacleRight()) {
                rota(angle);
            }
        }
    }

    @Override
    public boolean hasObstacleOver() {
        Lab lab = dibujo.getLab();
        Rectangle2D bounds = getBounds();
        return lab.contains(new Point2D.Double(bounds.getMinX(), bounds.getMinY() - OFFSET)) ||
                lab.contains(new Point2D.Double(bounds.getMaxX(), bounds.getMinY() - OFFSET));
    }

    @Override
    public boolean hasObstacleRight() {
        Lab lab = dibujo.getLab();
        Rectangle2D bounds = getBounds();
        return lab.contains(new Point2D.Double(bounds.getMaxX() + OFFSET, bounds.getMinY())) ||
                lab.contains(new Point2D.Double(bounds.getMaxX() + OFFSET, bounds.getMaxY()));
    }

    @Override
    public boolean hasObstacleLeft() {
        Lab lab = dibujo.getLab();
        Rectangle2D bounds = getBounds();
        return lab.contains(new Point2D.Double(bounds.getMinX() - OFFSET, bounds.getMinY())) ||
                lab.contains(new Point2D.Double(bounds.getMinX() - OFFSET, bounds.getMaxY()));
    }

    @Override
    public boolean hasObstacleBelow() {
        Lab lab = dibujo.getLab();
        Rectangle2D bounds = getBounds();
        return lab.contains(new Point2D.Double(bounds.getMinX(), bounds.getMaxY() + OFFSET)) ||
                lab.contains(new Point2D.Double(bounds.getMaxX(), bounds.getMaxY() + OFFSET));
    }

    /**
     * Dispara una {@link Bala}.
     */
    public void shoot() {
        Point2D centroBala = getCentroBala();
        Point2D posicionBala = new Point2D.Double(centroBala.getX() - Bala.RADIO, centroBala.getY() - Bala.RADIO);
        Bala bala = new Bala(posicionBala, theta, dibujo);
        dibujo.getBalasController().add(bala);
    }

    /**
     * Se sobrescribe este metodo con el objetivo de que se ignore a un {@link Tanque} cuando se este calculando si esta en
     * una posicion ilegal
     *
     * @return si la posicion es ilegal
     */
    @Override
    public boolean illegalPosition() {
        return illegalPosition(this);
    }

    /**
     * Comprueba si la rotacion del {@link Tanque} un angulo theta cumple con las restricciones de posicion
     *
     * @param theta angulo a rotar
     * @return si dicho angulo cumple las restricciones de posicion
     */
    @Override
    protected boolean validRotation(double theta) {
        Tanque t = (Tanque) clone();
        t.rotaIgnoraRestricciones(theta);
        return !t.illegalPosition(this);
    }

    /**
     * Calcula si el {@link Tanque} resultante de avanzar una distancia cumple las restricciones de posicion
     *
     * @param distancia distancia a avanzar
     * @return si la posicion resultante es valida
     */
    protected boolean validAvance(int distancia) {
        Movible m = (Movible) clone();
        m.setPosicion(new Point2D.Double(posicion.getX() + Math.cos(theta) * distancia,
                posicion.getY() + Math.sin(theta) * distancia));
        return !m.illegalPosition(this);
    }

    /**
     * Devuelve el centro de masa del {@link Tanque}, considerando este como el centro de masa de la base
     *
     * @return centro de masa
     */
    @Override
    public Point2D getMassCenter() {
        return getBaseCenter();
    }

    /**
     * Calcula el centro de masa de la base del {@link Tanque}
     *
     * @return centro de masa de la base del {@link Tanque}
     */
    public Point2D getBaseCenter() {
        List<Point2D> corners = getBaseCorners();
        return new Point2D.Double((corners.get(0).getX() + corners.get(2).getX()) / 2,
                (corners.get(0).getY() + corners.get(2).getY()) / 2);
    }

    /**
     * Devuelve el {@link Polygon} con la base del {@link Tanque}
     *
     * @return {@link Polygon} de la base del {@link Tanque}
     */
    public Polygon getBase() {
        Polygon p = new Polygon();
        for (Point2D point : getBasePoints())
            p.addPoint((int) point.getX(), (int) point.getY());
        return p;
    }

    /**
     * Devuelve el {@link Polygon} con el cañon del {@link Tanque}
     *
     * @return {@link Polygon} del cañon del {@link Tanque}
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
     * Calcula la posicion de la {@link Bala} que genera el tanque teniendo en cuenta su posicion actual
     *
     * @return posicion de la {@link Bala}
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
     * Rota respectro al centro de masa del {@link Tanque}
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
     * Metodo de conveniencia para {@link #rotate(Point2D)} pero recibe una lista como parametro
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
     * Elimina y vuelva a crear el {@link Area} de este rectangulo
     */
    protected void createArea() {
        reset();
        add(new Area(getBase()));
        add(new Area(getCannon()));
    }

    public Rectangle2D getRepaintBounds() {
        Rectangle2D bounds = getBounds2D();
        final int offset = (int) (1.0 * TanqueController.VELOCIDAD_FORWARD / Speeder.FRAMES_PER_SECOND + 1);
        return new Rectangle2D.Double(bounds.getX() - offset, bounds.getY() - offset,
                bounds.getWidth() + 2 * offset, bounds.getHeight() + 2 * offset);
    }

}
