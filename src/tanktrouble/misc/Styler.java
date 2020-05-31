package tanktrouble.misc;

import tanktrouble.reflection.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase define y configura las diferntes combinaciones de estilos que se pueden aplicar al {@link Dibujo}.
 */

public class Styler {

    /**
     * Estilo aleatorio
     */
    public static final int RANDOM = 0;

    /**
     * Estilo rojo vivo
     */
    public static final int LIVELY_RED = 1;

    /**
     * Estilo azul futuro
     */
    public static final int FUTURE = 2;

    /**
     * Estilo azul chocante
     */
    public static final int STRIKING_BLUE = 3;

    /**
     * Estilo por defecto
     */
    public static final int DEFAULT = RANDOM;

    private static int style = -1;

    /**
     * Devuelve el estilo actual
     *
     * @return estilo actual
     */
    public static int getStyle() {
        return style;
    }

    private static void setLivelyRed() {
        setColors(0x9a1750, 0xe3e2df, 0x5d001e, 0x9a1750, 0xc5c6c7);
    }

    private static void setFuture() {
        setColors(0x116466, 0xd1e8e2, 0x2c3531, 0xcf9211, 0x66fcf1);
    }

    private static void setStrikingBlue() {
        setColors(0x1f2833, 0xc5c6c7, 0x0b0c10, 0x45a29e, 0x66fcf1);
    }

    private static void setColors(int dibujo, int lab, int pared, int tanque, int board) {
        Dibujo.setColorBg(new Color(dibujo));
        Lab.setColorBg(new Color(lab));
        Pared.setColor(new Color(pared));
        Tanque.setColorBase(new Color(tanque));
        Board.setColor(new Color(board));
    }

    private static void setRandomStyle() {
        setStyle((int) (Math.random() * 3) + 1);
    }

    /**
     * Configura el estilo
     *
     * @param _style nuevo estilo
     */
    public static void setStyle(int _style) {
        switch (_style) {
            case RANDOM:
                setRandomStyle();
                break;
            case LIVELY_RED:
                setLivelyRed();
                break;
            case FUTURE:
                setFuture();
                break;
            case STRIKING_BLUE:
                setStrikingBlue();
                break;
            default:
                throw new IllegalArgumentException("Illegal value for style: " + _style);
        }
        style = _style;
    }

    /**
     * Si todavia no se ha configurado ningun estilo, configura el estilo por defecto.
     */
    public static void initStyle() {
        if (style == -1) setStyle(DEFAULT);
    }


    /**
     * Devuelve un map con el nombre humano de los estilos (claves) y su entero correspondiente (valores).
     *
     * @return mapa con todos los estilos
     */
    public static Map<String, Integer> getStyleList() {
        Map<String, Integer> estilos = new HashMap<>();
        estilos.put("Aleatorio", RANDOM);
        estilos.put("Rosa vivo", LIVELY_RED);
        estilos.put("Azul chocante", STRIKING_BLUE);
        estilos.put("Azul futuro", FUTURE);
        return estilos;
    }

}
