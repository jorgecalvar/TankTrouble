package tanktrouble.misc;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Util {

    /**
     * Recibe un rectángulo caracterizado mediante su posición (esquina superior izquierda) y su diensión, y calcula las
     * posiciones de cada una de sus esquinas. Estas esquinas serán devueltos en una lista siguiendo el siguiente orden:
     * primero se encuentra la esquina superior izquierda (que se pasa como páramtro) y a continuación se sigue el
     * sentido horario.
     *
     * @param point  posición del rectángulo
     * @param width  anchura del rectángulo
     * @param height altura del rectángulo
     * @return esquinas del rectángulo
     */
    public static List<Point2D> getCornersFromRectangle(Point2D point, int width, int height) {
        List<Point2D> points = new ArrayList<>();
        points.add(point);
        points.add(new Point2D.Double(point.getX() + width, point.getY()));
        points.add(new Point2D.Double(point.getX() + width, point.getY() + height));
        points.add(new Point2D.Double(point.getX(), point.getY() + height));
        return points;
    }

    /**
     * Este método leerá un stream hasta el final y devolverá el resultado en un string
     * IMPORTANTE: usar con cuidado, este método no están pensado para leer archivos muy grandes in streams infinitos.
     *
     * @param stream InputStream que se desea leer
     * @return string leida
     * @throws IOException si hay algún problema en la lectura
     */
    public static String readInputStream(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int a = stream.read();
            if (a != -1)
                sb.append((char) a);
            else
                break;
        }
        return sb.toString();
    }

    /**
     * Devuelve un número aleatorio del 0 al 9
     *
     * @return número aleatorio del 1 al 10
     */
    public static int randomOneDigitInteger() {
        return (int) (10 * Math.random());
    }

    /**
     * Devuelve un número aleatorio del [1, max]
     *
     * @param max máximo número posible
     * @return entero aleatorio
     */
    public static int randomInteger(int max) {
        return (int) Math.round(Math.random() * (max - 1) + 1);
    }

    /**
     * Muestra un mensaje de aviso
     *
     * @param txt    mensaje a mostrar
     * @param parent componente padre
     */
    public static void warn(String txt, Component parent) {
        JOptionPane.showMessageDialog(parent, txt, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Muestra un diálogo de información
     *
     * @param txt    mensaje a mostrar
     * @param parent componente padre
     */
    public static void info(String txt, Component parent) {
        JOptionPane.showMessageDialog(parent, txt, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un diálogo de error
     *
     * @param txt    mensaje a mostrar
     * @param parent componente padre
     */
    public static void error(String txt, Component parent) {
        JOptionPane.showMessageDialog(parent, txt, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Solicita información al usuario.
     *
     * @param txt    mensaje a mostar
     * @param parent componente padre
     * @return texto introducido por el usuario
     */
    public static String input(String txt, Component parent) {
        return JOptionPane.showInputDialog(parent, txt, "Input", JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Formatea un ángulo cualquiera (-inf, +inf), en radianes, a un ángulo en el intervalo [0, 2PI]
     *
     * @param theta angulo a formatear
     * @return angulo formateado
     */
    public static double formatAngle(double theta) {
        while (theta > 2 * Math.PI)
            theta = theta - 2 * Math.PI;
        while (theta < 0)
            theta = theta + 2 * Math.PI;
        return theta;
    }


}
