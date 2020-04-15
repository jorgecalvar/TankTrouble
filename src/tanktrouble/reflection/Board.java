package tanktrouble.reflection;

import tanktrouble.ui.Dibujo;
import tanktrouble.ui.GameWindow;

import java.awt.*;
import java.util.LinkedList;

/**
 * Esta clase se encarga de manjar la estética y pintado de los mensajes que se muestran en el canvas en tiempo de juego.
 */

public class Board implements Pintable {

    /**
     * Duración, en milisegundos, de los mensajes mostrados por pantalla
     */
    public static final int DURATION_MESSAGE = 3000;
    /**
     * Mínima duración, en milisegundos, entre dos mensajes.
     */
    public static final int MINIMUM_WAIT = 500;
    /**
     * Vidas iniciales de cada jugador
     */
    public static final int START_LIVES = 10;
    /**
     * Color de los mensajes que se muestran
     */
    public static Color COLOR;
    public static Font FONT = new Font("Courier New", Font.BOLD, 25);
    public static Font FONT_BIG = new Font("Courier New", Font.BOLD, 40);
    private Dibujo dibujo;
    private Tanque player1;
    private Tanque player2;
    private int livesPlayer1;
    private int livesPlayer2;
    private LinkedList<String> messages = new LinkedList<>();
    private boolean emptyingMessages = false;
    private boolean showMessage = false;
    private boolean gameOver;

    public Board(Dibujo dibujo) {
        this.dibujo = dibujo;
    }

    /**
     * Configura el color de los elementos dibujados por este objet
     *
     * @param c color a configurar
     */
    public static void setColor(Color c) {
        COLOR = c;
    }

    @Override
    public void pintar(Graphics2D g) {
        g.setColor(COLOR);
        g.setFont(FONT);
        FontMetrics metrics = g.getFontMetrics();
        g.drawString("JUGADOR 1: " + livesPlayer1, 20, 40);
        g.drawString("JUGADOR 2: " + livesPlayer2, dibujo.getWidth() - metrics.stringWidth("JUGADOR 2: 10") - 20, 40);
        if (showMessage) {
            String msg = messages.get(0);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            g.drawString(msg, (int) (screenSize.getWidth() - metrics.stringWidth(msg)) / 2,
                    (int) dibujo.getLab().getBounds().getMinY() - metrics.getHeight() - 10);
        }
        if (gameOver) {
            String msg = "¡¡GANA EL JUGADOR  " + (livesPlayer1 > 0 ? "1" : "2") + "!!";
            g.setFont(FONT_BIG);
            metrics = g.getFontMetrics();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            g.drawString(msg, (int) (screenSize.getWidth() - metrics.stringWidth(msg)) / 2,
                    (int) (screenSize.getHeight() - metrics.getHeight()) / 2);
        }
    }

    /**
     * Es llamada cuando un tanque es chocado por una bala
     *
     * @param player tanque que ha sido chocado
     */
    public void hitPlayer(Tanque player) {
        if (player == player1)
            livesPlayer1--;
        else if (player == player2)
            livesPlayer2--;
        if (livesPlayer1 == 0 || livesPlayer2 == 0)
            gameOver();
    }


    /**
     * Muestra un mensaje al usuario durante el juego
     *
     * @param msg mensaje a mostrar
     */
    public void showMessage(String msg) {
        messages.add(msg);
        emptyMessages();
    }

    /**
     * Muestra por pantalla todos los mensajes que se encuentran en la cola
     */
    private void emptyMessages() {
        if (emptyingMessages || messages.size() == 0)
            return;
        emptyingMessages = true;
        showMessage = true;
        new Thread(() -> {
            try {
                Thread.sleep(DURATION_MESSAGE);
                showMessage = false;
                messages.remove(0);
                Thread.sleep(MINIMUM_WAIT);
                emptyingMessages = false;
                if (messages.size() != 0)
                    emptyMessages();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Inicializa el board
     *
     * @param t1 primer jugador
     * @param t2 segundo jugador
     */
    public void init(Tanque t1, Tanque t2) {
        player1 = t1;
        player2 = t2;
        livesPlayer1 = START_LIVES;
        livesPlayer2 = START_LIVES;
        gameOver = false;
    }

    /**
     * Llamado cuando el juego es finalizado
     */
    private void gameOver() {
        gameOver = true;
        new Thread(() -> {
            dibujo.deactivate();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dibujo.init();
        }).start();
    }

    /**
     * Muestra una ayuda al usuario acerca cómo controlar su tanque
     */
    public void showHelp() {
        int gameType = dibujo.getGameType();
        switch (gameType) {
            case GameWindow.PLAYER_VS_PLAYER:
                showMessage("JUGADOR 1: Usa las teclas ASWD para moverte y ESPACIO para disparar!");
                showMessage("JUGADOR 2: Usa las teclas FLECHAS para moverte y M para disparar!");
                break;
            case GameWindow.PLAYER_VS_INTERNET:
                showMessage("Usa las teclas ASWD para moverte y ESPACIO para disparar!");

        }
        showMessage("Presiona ESC para salir");

    }

}
