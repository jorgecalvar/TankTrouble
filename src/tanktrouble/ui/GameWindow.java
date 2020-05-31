package tanktrouble.ui;

import tanktrouble.misc.Util;
import tanktrouble.reflection.Dibujo;
import tanktrouble.reflection.Lab;

import javax.swing.*;

/**
 * Ventana de juego, que contiene al {@link Dibujo}.
 */
public class GameWindow extends JFrame {

    /**
     * Formato de juego de dos jugadores en el mismo ordenador, cada uno controlando un tanque con teclas diferntes.
     */
    public static final int PLAYER_VS_PLAYER = 1;

    /**
     * Formato de juego contra el ordenador.
     */
    public static final int PLAYER_VS_COMPUTER = 2;

    /**
     * Formato de juego online, siendo el {@link tanktrouble.net.Servidor}.
     */
    public static final int PLAYER_VS_INTERNET = 3;

    /**
     * Formato de juego online, siendo el {@link tanktrouble.net.Cliente}.
     */
    public static final int PLAYER_VS_INTERNET_CLIENTE = 4;

    private Dibujo dibujo;
    private int gameType;

    public GameWindow() {
        this(PLAYER_VS_PLAYER);
    }

    /**
     * Inicializa la venta de juego.
     *
     * @param type tipo de juego
     */
    public GameWindow(int type) {

        this.gameType = type;

        if (!Lab.validDevice())
            throw new UnsupportedOperationException("El tamaño de pantalla de su disopsitivo es " +
                    "demasiado pequeño para poder ejecutar este juego.");

        setTitle("Tank Trouble");

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        dibujo = new Dibujo(this, type);
        add(dibujo);

        pack();

        setIconImage(Util.getIcono());
        setVisible(true);

    }

    /**
     * Vuelve a la {@link InicioWindow ventana inicial} y cierra la actual.
     */
    public void quit() {
        setVisible(false);
        new InicioWindow();
        dispose();
    }


}
