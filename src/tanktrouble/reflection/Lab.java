package tanktrouble.reflection;

import tanktrouble.labcreator.LabManager;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tanktrouble.reflection.Pared.GROSOR;

/**
 * Esta clase define al objeto {@link Lab laberinto} en su conjunto. Hereda de la clase {@link Area}, lo que facilitara
 * luego calcular las intersecciones con otros objetos.
 */

public class Lab extends Area implements Pintable {

    /**
     * Color de fondo del interior del {@link Lab}
     */
    public static Color COLOR_BG;

    private Dibujo dibujo;

    private Dimension size;
    private List<Pared> paredes;
    private List<Point2D> tanques;

    /**
     * Un punto es un vertice si y solo si:
     * 1. Exactamente dos lineas contienen ese punto
     * 2. Cada una de las dos lineas empieza o termina en dicho punto.
     */
    private List<Point2D> vertices;

    private boolean labDrawn;


    /**
     * Inicializa el {@link Lab}
     *
     * @param size    tamano en pixeles
     * @param paredes lista con las {@link Pared paredes}
     * @param tanques lista con la posicion inicial de los {@link Tanque tanques}
     */
    public Lab(Dimension size, List<Pared> paredes, List<Point2D> tanques) {
        super();
        this.paredes = paredes;
        this.size = size;
        for (Pared p : paredes)
            add(new Area(p.getRectangle()));
        this.vertices = new ArrayList<>();
        this.tanques = tanques;
        calcularVertices();
        labDrawn = false;
    }

    /**
     * Lee un {@link Lab} para jugar aleatorio de los archivos internos y externos (si existen). Devuelve null si no se
     * encuentra ningun Lab valido tras 10 intentos.
     *
     * @return {@link Lab} para ser jugado
     */
    public static Lab createRamdomLab() {
        Lab lab;
        int i = 0;
        while (true) {
            lab = LabManager.readRandom(LabManager.CONVERT_TYPE_GAME);
            if (Lab.validLabSize(lab.getSize()) || i == 10)
                break;
            i++;
        }
        return lab;
    }

    /**
     * Devuelve si una dimension concreta de un {@link Lab} se puede jugar en este dispositivo.
     *
     * @param size dimension a comprobar
     * @return si se puede jugar
     */
    public static boolean validLabSize(Dimension size) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return size.getWidth() + 100 < screenSize.getWidth() && size.getHeight() + 150 < screenSize.getHeight();
    }

    /**
     * Devuelve si este dispositivo tiene suficiente tamano de pantalla para ejecutar el juego.
     *
     * @return si este dispositivo puede ejecutar el juego
     */
    public static boolean validDevice() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return screenSize.getWidth() >= 900 && screenSize.getHeight() >= 550;
    }

    /**
     * Configura el {@link Color} de fondo del laberinto
     *
     * @param c {@link Color} a configurar
     */
    public static void setColorBg(Color c) {
        COLOR_BG = c;
    }

    @Override
    public void pintar(Graphics2D g) {
        g.setColor(COLOR_BG);
        Rectangle2D bounds = getBounds2D();
        g.fill(new Rectangle2D.Double(bounds.getX() + 1.0 * GROSOR / 2, bounds.getY() + 1.0 * GROSOR / 2,
                bounds.getWidth() - GROSOR, bounds.getHeight() - GROSOR));
        for (Pared pared : paredes) {
            pared.pintar(g);
        }
        for (Point2D p : vertices) {
            g.fillOval((int) (p.getX() - 1.0 * GROSOR / 2), (int) (p.getY() - 1.0 * GROSOR / 2), GROSOR, GROSOR);
        }
    }

    /**
     * Devuelve el tamano de este laberinto
     *
     * @return tamano del laberinto
     */
    public Dimension getSize() {
        return size;
    }

    /**
     * Lista con todas las {@link Pared paredes} de este {@link Lab}.
     *
     * @return lista con todas las {@link Pared paredes} del {@link Lab}
     */
    public List<Pared> getParedes() {
        return paredes;
    }

    /**
     * Devuelve las posiciones iniciales de los {@link Tanque tanques} en este {@link Lab}
     *
     * @return posiciones iniciales de los {@link Tanque tanques}
     */
    public List<Point2D> getPosicionTanques() {
        return tanques;
    }

    public void setDibujo(Dibujo dibujo) {
        this.dibujo = dibujo;
    }

    /**
     * Genera automaticamente la lista de vertices siguiendo la definicion de vertice.
     */
    private void calcularVertices() {
        Map<Point2D, Integer> ocurrencias = new HashMap<>();
        for (Pared pared : paredes) {
            Point2D start = pared.getStart();
            Point2D end = pared.getEnd();
            if (ocurrencias.containsKey(start))
                ocurrencias.put(start, ocurrencias.get(start) + 1);
            else
                ocurrencias.put(start, 1);
            if (ocurrencias.containsKey(end))
                ocurrencias.put(end, ocurrencias.get(end) + 1);
            else
                ocurrencias.put(end, 1);
        }
        for (Point2D p : ocurrencias.keySet()) {
            if (ocurrencias.get(p) == 2)
                vertices.add(p);
        }
    }

    public Rectangle2D getRepaintBounds() {
        labDrawn = true;
        return this.getBounds2D();
    }

}
