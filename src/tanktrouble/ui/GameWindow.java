package tanktrouble.ui;

import tanktrouble.reflection.Lab;

import javax.swing.*;

public class GameWindow extends JFrame {

    /**
     * Formato de juego 1vs1
     */
    public static final int PLAYER_VS_PLAYER = 1;

    /**
     * Formato de juego contra el ordenador
     */
    public static final int PLAYER_VS_COMPUTER = 2;

    /**
     * Formato de juego online
     */
    public static final int PLAYER_VS_INTERNET = 3;

    private Dibujo dibujo;
    private int gameType;

    public GameWindow() {
        this(PLAYER_VS_PLAYER);
    }

    public GameWindow(int type) {

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

        setVisible(true);

    }

    public void quit() {
        setVisible(false);
        new InicioWindow();
        dispose();
    }

    public static void main(String[] args) {
        new GameWindow();
    }


}
