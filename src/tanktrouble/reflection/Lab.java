package tanktrouble.reflection;

import tanktrouble.file.LabEditor;
import tanktrouble.misc.Util;
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

    /**
     * Un punto es un vértice si y solo si:
     * 1. Exactamente dos líneas contienen ese punto
     * 2. Cada una de las dos líneas empieza o termina en dicho punto.
     */
    private List<Point2D> vertices;


    public Lab(Dimension size, List<Pared> paredes) {
        super();
        this.paredes = paredes;
        this.size = size;
        for (Pared p : paredes)
            add(new Area(p.getRectangle()));
        this.vertices = new ArrayList<>();
        calcularVertices();
    }

    public static Lab createRamdomLab() {

        /*List<Pared> paredes = new ArrayList<>();

        paredes.add(new Pared(new Point2D.Double(100, 100), 800, Pared.TIPO_HORIZONTAL));
        paredes.add(new Pared(new Point2D.Double(100, 100), 400, Pared.TIPO_VERTICAL));
        paredes.add(new Pared(new Point2D.Double(100,500), 800, Pared.TIPO_HORIZONTAL));
        paredes.add(new Pared(new Point2D.Double(900, 100), 400, Pared.TIPO_VERTICAL));

        paredes.add(new Pared(new Point2D.Double(200, 100), 100, Pared.TIPO_VERTICAL));
        paredes.add(new Pared(new Point2D.Double(300, 100), 100, Pared.TIPO_VERTICAL));
        paredes.add(new Pared(new Point2D.Double(500, 100), 100, Pared.TIPO_VERTICAL));
        paredes.add(new Pared(new Point2D.Double(800, 100), 100, Pared.TIPO_VERTICAL));

        paredes.add(new Pared(new Point2D.Double(400, 200), 100, Pared.TIPO_VERTICAL));
        paredes.add(new Pared(new Point2D.Double(600, 200), 100, Pared.TIPO_HORIZONTAL));
        paredes.add(new Pared(new Point2D.Double(700, 200), 100, Pared.TIPO_VERTICAL));

        paredes.add(new Pared(new Point2D.Double(100, 300), 200, Pared.TIPO_HORIZONTAL));
        paredes.add(new Pared(new Point2D.Double(400, 300), 100, Pared.TIPO_HORIZONTAL));
        paredes.add(new Pared(new Point2D.Double(500, 300), 200, Pared.TIPO_VERTICAL));
        paredes.add(new Pared(new Point2D.Double(600, 300), 100, Pared.TIPO_VERTICAL));
        paredes.add(new Pared(new Point2D.Double(700, 300), 100, Pared.TIPO_VERTICAL));
        paredes.add(new Pared(new Point2D.Double(700, 300), 200, Pared.TIPO_HORIZONTAL));

        paredes.add(new Pared(new Point2D.Double(200, 400), 100, Pared.TIPO_VERTICAL));
        paredes.add(new Pared(new Point2D.Double(200, 400), 200, Pared.TIPO_HORIZONTAL));
        paredes.add(new Pared(new Point2D.Double(600, 400), 100, Pared.TIPO_HORIZONTAL));
        paredes.add(new Pared(new Point2D.Double(800, 400), 100, Pared.TIPO_VERTICAL));


        List<Point2D.Double> vertices = new ArrayList<>();
        vertices.add(new Point2D.Double(100, 100));
        vertices.add(new Point2D.Double(900, 100));
        vertices.add(new Point2D.Double(100, 500));
        vertices.add(new Point2D.Double(900, 500));

        Dimension size = new Dimension(1000, 800);

        return new Lab(size, paredes);*/


        return LabEditor.readInternal(Util.randomOneDigitInteger() + 1);

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

    public Dimension getSize() {
        return size;
    }

    public List<Pared> getParedes() {
        return paredes;
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
