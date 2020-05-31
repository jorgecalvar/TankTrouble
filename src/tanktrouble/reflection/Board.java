package tanktrouble.reflection;

import tanktrouble.ui.GameWindow;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Esta clase se encarga de manjar la estetica y pintado de los mensajes que se muestran en el canvas en tiempo de juego.
 * Tambien controla la puntuacion de cada {@link Tanque} y avisa al dibujo cuando se ha terminado el juego.
 */

public class Board implements Pintable {

    /**
     * Duracion, en milisegundos, de los mensajes mostrados por pantalla
     */
    public static final int DURATION_MESSAGE = 3000;
    /**
     * Minima duracion, en milisegundos, entre dos mensajes.
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
    /**
     * Fuente de los mensajes mostrados
     */
    public static Font FONT = new Font("Courier New", Font.BOLD, 25);
    /**
     * Fuente de mayor tamaño para mensajes importantes.
     */
    public static Font FONT_BIG = new Font("Courier New", Font.BOLD, 40);

    private Dibujo dibujo;

    private Tanque player1;
    private Tanque player2;
    private int livesPlayer1;
    private int livesPlayer2;
    private boolean hasChanged1;
    private boolean hasChanged2;

    private LinkedList<String> messages = new LinkedList<>();
    private boolean emptyingMessages = false;
    private boolean showMessage = false;
    private boolean gameOver;
    private boolean messageHidden = true;

    /**
     * Inicializa el marcador
     *
     * @param dibujo {@link Dibujo} en el que se encuentra
     */
    public Board(Dibujo dibujo) {
        this.dibujo = dibujo;
    }

    /**
     * Configura el {@link Color} de los elementos dibujados por este objeto
     *
     * @param c color a configurar
     */
    public static void setColor(Color c) {
        COLOR = c;
    }

    /**
     * Devuelve las vidas actuales del jugador 1
     *
     * @return vidas del jugador 1
     */
    public int getLivesPlayer1() {
        return livesPlayer1;
    }

    /**
     * Configura las vidas actuales del jugador 1
     *
     * @param lives nuevo valor de {@link #livesPlayer1}
     */
    public void setLivesPlayer1(int lives) {
        livesPlayer1 = lives;
        hasChanged1 = true;
    }

    /**
     * Devuelve las vidas actuales del jugador 2
     *
     * @return vidas del jugador 2
     */
    public int getLivesPlayer2() {
        return livesPlayer2;
    }

    /**
     * Configura las vidas actuales del jugador 2
     *
     * @param lives nuevo valor de {@link #livesPlayer2}
     */
    public void setLivesPlayer2(int lives) {
        livesPlayer2 = lives;
        hasChanged2 = true;
    }

    /**
     * Pinta el {@link Board marcador}.
     *
     * @param g Donde se pintar el objeto
     */
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
     * Es llamada cuando un {@link Tanque} es chocado por una {@link Bala}.
     *
     * @param player {@link Tanque} que ha sido chocado
     */
    public void hitPlayer(Tanque player) {
        if (player == player1) {
            setLivesPlayer1(livesPlayer1 - 1);
        } else if (player == player2) {
            setLivesPlayer2(livesPlayer2 - 1);
        }
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
     * Inicializa el {@link Board}.
     *
     * @param t1 {@link Tanque} correspondiente al primer jugador
     * @param t2 {@link Tanque} correspondiente al segundo jugador
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
    public void gameOver() {
        if (!gameOver) {
            gameOver = true;
            new Thread(() -> {
                System.out.println("DESACTIVANDO DIBUJO...");
                dibujo.deactivate();
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dibujo.init();
            }).start();
        }
    }

    /**
     * Muestra una ayuda al usuario acerca como controlar su {@link Tanque}.
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

    public List<Rectangle2D> getRepaintBounds() {
        List<Rectangle2D> bounds = new ArrayList<>();

        if (gameOver) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            bounds.add(new Rectangle2D.Double(0, 0, screenSize.getWidth(), screenSize.getHeight()));
            return bounds;
        }

        if (hasChanged1) {
            bounds.add(new Rectangle2D.Double(20, 20, 250, 40));
            hasChanged1 = false;
        }
        if (hasChanged2) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            bounds.add(new Rectangle2D.Double(screenSize.getWidth() - 250, 20, 250, 40));
            hasChanged2 = false;
        }
        if ((showMessage && messageHidden) || (!showMessage && !messageHidden)) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            double labMinY = dibujo.getLab().getBounds2D().getMinY();
            bounds.add(new Rectangle2D.Double(0, labMinY - 100, screenSize.getWidth(), 100));
            messageHidden = !messageHidden;
        }

        return bounds;
    }

}
