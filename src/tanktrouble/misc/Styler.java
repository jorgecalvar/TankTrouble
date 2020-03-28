package tanktrouble.misc;

import tanktrouble.reflection.Board;
import tanktrouble.reflection.Lab;
import tanktrouble.reflection.Pared;
import tanktrouble.reflection.Tanque;
import tanktrouble.ui.Dibujo;

import java.awt.*;

/**
 * Esta clase define y configura las diferntes combinaciones de estilos que se pueden aplicar al canvas.
 */

public class Styler {

    public static final int LIVELY_RED = 1;

    public static final int FUTURE = 2;

    public static final int STRIKING_BLUE = 3;

    public static void setRandomStyle() {
        setFuture();
    }

    public static void setStyle(int style) {
        switch (style) {
            case LIVELY_RED:
                setLivelyRed();
                break;
            case FUTURE:
                setFuture();
                break;
            case STRIKING_BLUE:
                setStrikingBlue();
                break;

        }
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

}
