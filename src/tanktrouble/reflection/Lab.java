package tanktrouble.reflection;

import tanktrouble.labcreator.LabManager;
import tanktrouble.ui.Dibujo;

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
 * Esta clase define al objeto laberinto en su conjunto. Hereda de la clase área, lo que facilitará luego calcular las
 * intersecciones con otros objetos.
 */

public class Lab extends Area implements Pintable {

    /**
     * Color de fondo del interior del laberinto
     */
    public static Color COLOR_BG;

    private Dibujo dibujo;

    private Dimension size;
    private List<Pared> paredes;
    private List<Point2D> tanques;

    /**
     * Un punto es un vértice si y solo si:
     * 1. Exactamente dos líneas contienen ese punto
     * 2. Cada una de las dos líneas empieza o termina en dicho punto.
     */
    private List<Point2D> vertices;


    public Lab(Dimension size, List<Pared> paredes, List<Point2D> tanques) {
        super();
        this.paredes = paredes;
        this.size = size;
        for (Pared p : paredes)
            add(new Area(p.getRectangle()));
        this.vertices = new ArrayList<>();
        this.tanques = tanques;
        calcularVertices();
    }

    /**
     * Lee un Lab para jugar aleatorio de los archivos internos y externos (si existen). Devuelve null si no se
     * encuentra ningún Lab válido tras 10 intentos.
     *
     * @return Lab para ser jugado
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

    public static boolean validLabSize(Dimension size) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return size.getWidth() + 100 < screenSize.getWidth() && size.getHeight() + 150 < screenSize.getHeight();
    }

    /**
     * For this game, a minimum screen size of 850x600 is required.
     *
     * @return is this device can run the game
     */
    public static boolean validDevice() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return screenSize.getWidth() >= 900 && screenSize.getHeight() >= 550;
    }

    /**
     * Configura el color de fondo del laberinto
     *
     * @param c color a configurar
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
     * Devuelve el tamaño de este laberinto
     *
     * @return tamaño del laberinto
     */
    public Dimension getSize() {
        return size;
    }

    /**
     * List con todas las paredes de este laberinto
     *
     * @return lista con todas las paredes del laberinto
     */
    public List<Pared> getParedes() {
        return paredes;
    }

    /**
     * Devuelve las posiciones iniciales de los tanques en este laberinto
     *
     * @return posiciones iniciales de los tanques
     */
    public List<Point2D> getPosicionTanques() {
        return tanques;
    }

    public void setDibujo(Dibujo dibujo) {
        this.dibujo = dibujo;
    }

    /**
     * Genera automáticamente la lista de vértices siguiendo la definición de vértice.
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

}
