package tanktrouble.misc;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase con diferentes metodos estaticos de utilidad.
 */
public class Util {

    /**
     * Recibe un rectangulo caracterizado mediante su posicion (esquina superior izquierda) y su diension, y calcula las
     * posiciones de cada una de sus esquinas. Estas esquinas seran devueltos en una lista siguiendo el siguiente orden:
     * primero se encuentra la esquina superior izquierda (que se pasa como paramtro) y a continuacion se sigue el
     * sentido horario.
     *
     * @param point  posicion del rectangulo
     * @param width  anchura del rectangulo
     * @param height altura del rectangulo
     * @return esquinas del rectangulo
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
     * Este metodo leera un stream hasta el final y devolvera el resultado en un string
     * IMPORTANTE: usar con cuidado, este metodo no estan pensado para leer archivos muy grandes in streams infinitos.
     *
     * @param stream InputStream que se desea leer
     * @return string leida
     * @throws IOException si hay algun problema en la lectura
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
     * Devuelve un numero aleatorio del 0 al 9
     *
     * @return numero aleatorio del 1 al 10
     */
    public static int randomOneDigitInteger() {
        return (int) (10 * Math.random());
    }

    /**
     * Devuelve un numero aleatorio del [1, max]
     *
     * @param max maximo numero posible
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
     * Muestra un dialogo de informacion
     *
     * @param txt    mensaje a mostrar
     * @param parent componente padre
     */
    public static void info(String txt, Component parent) {
        JOptionPane.showMessageDialog(parent, txt, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un dialogo de error
     *
     * @param txt    mensaje a mostrar
     * @param parent componente padre
     */
    public static void error(String txt, Component parent) {
        JOptionPane.showMessageDialog(parent, txt, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Solicita informacion al usuario.
     *
     * @param txt    mensaje a mostar
     * @param parent componente padre
     * @return texto introducido por el usuario, null si presiona canelar
     */
    public static String input(String txt, Component parent) {
        String a = JOptionPane.showInputDialog(parent, txt, "Input", JOptionPane.QUESTION_MESSAGE);
        System.out.println(a);
        return a;
    }

    /**
     * Formatea un angulo cualquiera (-inf, +inf), en radianes, a un angulo en el intervalo [0, 2PI)
     *
     * @param theta angulo a formatear
     * @return angulo formateado
     */
    public static double formatAngle(double theta) {
        while (theta >= 2 * Math.PI)
            theta = theta - 2 * Math.PI;
        while (theta < 0)
            theta = theta + 2 * Math.PI;
        return theta;
    }

    /**
     * Recibe una lista con rectangulos y devolvera una misma lista con rectangulos simplificada, es decir
     * si hay un rectangulo que ya esta incluido, ya que hay otros rectangulos que cubren ese area, este sera eliminado.
     *
     * @param lista lista con rectangulos a simplificar
     * @return lista simplificada
     */
    public static List<Rectangle2D> simplifyRectangles(List<Rectangle2D> lista) {
        if (lista.size() < 2) return lista;
        for (Rectangle2D r : lista) {
            List<Rectangle2D> lista2 = new ArrayList<>(lista);
            lista2.removeIf(e -> e.equals(r));
            Area a = new Area();
            for (Shape s : lista2)
                a.add(new Area(s));
            if (a.contains(r))
                return simplifyRectangles(lista2);
        }
        return lista;
    }

    /**
     * Devuelve un double obtenido de un objeto que forma parte de un fichero JSON.
     *
     * @param o objeto a convertir a double
     * @return double
     */
    public static double decodeDouble(Object o) {
        if (o instanceof Integer)
            return (double) ((int) ((Integer) o));
        return (Double) o;
    }

    /**
     * Obtiene la direccion IP Wifi para jugar online. Si la IP comienza por 192.168.1. devolvera solo los digitos
     * despues del ultimo punto, para simplificar el proceso.
     *
     * @return direccion IP wifi simplificada
     * @throws UnknownHostException si se lanza intenado obtener la IP
     */
    public static String getSimplifiedWifiIP() throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        if (ip.substring(0, 10).equals("192.168.1."))
            return ip.substring(10);
        return ip;
    }

    /**
     * Realiza el proceso inverso al metodo getSimplifiedWifiIP para devulver la IP Wifi completa. EL metodo asume que
     * se entrega una IP simplificada valida.
     *
     * @param simplifiedIP ip que se desea dessimplificar.
     * @return direccion IP Wifi completa
     */
    public static String unsimplifyIP(String simplifiedIP) {
        if (simplifiedIP.length() > 3)
            return simplifiedIP;
        return "192.168.1." + simplifiedIP;
    }

}
