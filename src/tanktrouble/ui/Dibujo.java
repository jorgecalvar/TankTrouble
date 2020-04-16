package tanktrouble.ui;

import tanktrouble.control.BalasController;
import tanktrouble.control.Speeder;
import tanktrouble.control.TankController;
import tanktrouble.control.UserController;
import tanktrouble.misc.Sonido;
import tanktrouble.misc.Styler;
import tanktrouble.reflection.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

public class Dibujo extends Canvas implements Pintable {

    /**
     * No se establecerán Hints para el render.
     */
    public static final int RENDERING_LIGHT = 1;

    /**
     * Se establecen Hints que mejorar un poco la imagen.
     */
    public static final int RENDERING_MODERATE = 2;

    /**
     * Se establecen HInts que provocan que la imagen mejora muy notablemente pero se ralentiza mucho el dibujo.
     */
    public static final int REDERING_INTENSE = 3;

    private static int rendering = RENDERING_MODERATE;


    /**
     * Color de fondo del dibujo
     */
    private static Color COLOR_BG;


    private int gameType;


    private Speeder speeder;
    private Lab lab;
    private Map<Tanque, TankController> tanques;
    private BalasController balasController;
    private Board board;
    private Sonido sonido;

    private volatile BufferedImage bf;

    private volatile boolean active = false;

    public Dibujo(GameWindow window, int gameType) {

        this.gameType = gameType;
        board = new Board(this);

        sonido = new Sonido();

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        bf = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

        setFocusable(true);

        init();
        board.showHelp();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    window.quit();
                }
            }
        });
    }

    /**
     * Configura el color de fondo del dibujo
     *
     * @param c color a configurar
     */
    public static void setColorBg(Color c) {
        COLOR_BG = c;
    }

    /**
     * Devuelve el valor de rendering
     *
     * @return valor de rendering
     */
    public static int getRendering() {
        return rendering;
    }

    /**
     * Configura el valor del rendering
     *
     * @param _rendering nuevo valor de rendering
     */
    public static void setRendering(int _rendering) {
        rendering = _rendering;
    }

    /**
     * Devuelve el objeto sonido que pondrá los audios en este juego
     *
     * @return sonido
     */
    public Sonido getSonido() {
        return sonido;
    }

    /**
     * Devuelve el objeto Lab asociado a este dibujo
     *
     * @return objeto Lab
     */
    public Lab getLab() {
        return lab;
    }

    /**
     * Devuelve el objeto Speeder asociado a este dibujo
     *
     * @return objeto Speeder
     */
    public Speeder getSpeeder() {
        return speeder;
    }

    /**
     * Devuelve el tipo de juego
     *
     * @return tipo de juego
     */
    public int getGameType() {
        return gameType;
    }

    /**
     * Devuelve el objeto Board asociado a este dibujo
     *
     * @return objeto Board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Devuelve el objeto BalasController asociado a este dibujo
     *
     * @return objeto BalasControllers
     */
    public BalasController getBalasController() {
        return balasController;
    }

    /**
     * Devuelve el set con todos los tanque presentes en el dibujo
     *
     * @return tanque dibujados
     */
    public Set<Tanque> getTanques() {
        return tanques.keySet();
    }

    /**
     * Devuelve si el dibujo está activado
     *
     * @return si está activado
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Añade un tanque al dibujo
     *
     * @param t tanque
     * @param c controlador del tanque
     */
    public void addTanque(Tanque t, TankController c) {
        tanques.put(t, c);
        speeder.add(c);
    }

    /**
     * Método invocado continuamente para representar el dibujo en el canvas
     *
     * @param g objeto sobre el que pintar
     */
    @Override
    public void paint(Graphics g) {
        setHints((Graphics2D) g);
        pintar((Graphics2D) bf.getGraphics());
        g.drawImage(bf, 0, 0, null);
    }

    /**
     * Método que recibe un objeto Graphics2D sobre el que dibujará el dibujo en su estado presente
     *
     * @param g objeto sobre el que pintar
     */
    @Override
    public void pintar(Graphics2D g) {
        setHints(g);
        g.setColor(COLOR_BG);
        g.fillRect(0, 0, getWidth(), getHeight());
        board.pintar(g);
        if (active) {
            lab.pintar(g);
            pintarTanques(g);
            pintarBalas(g);
        }
    }

    /**
     * Configura el tipo de rendering que se realizará en el objeto gráfico pasado
     *
     * @param g objeto que configurar
     */
    private void setHints(Graphics2D g) {
        switch (rendering) {
            case REDERING_INTENSE:
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            case RENDERING_MODERATE:
                g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
    }

    /**
     * Pinta todos los tanque sobre el objeto g
     *
     * @param g objeto sobre el que pintar
     */
    private void pintarTanques(Graphics2D g) {
        for (Tanque t : getTanques())
            t.pintar(g);

    }

    /**
     * Pinta todas las balas
     *
     * @param g objeto sobre el que pintar
     */
    private void pintarBalas(Graphics2D g) {
        List<Bala> balas = new ArrayList<>(balasController.getBalas()); //To avoid ConcurrentNotificationException
        for (Bala b : balas)
            b.pintar(g);
    }


    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public void init() {
        destroy();
        setUpLab();
        speeder = new Speeder(this);
        balasController = new BalasController();
        speeder.add(balasController);
        setUpTanques();
        Styler.setStyle(Styler.DEFAULT);
        setBackground(COLOR_BG);
        active = true;
        speeder.start();
        sonido.playSound(Sonido.CHINESE_GONG);
    }

    public void deactivate() {
        active = false;
    }

    public void destroy() {
        if (speeder != null) {
            speeder.parar();
        }
    }

    private void setUpLab() {
        lab = Lab.createRamdomLab();
        lab.setDibujo(this);
    }

    /**
     * Configura los tanque dependiendo del modo de juego
     */
    private void setUpTanques() {
        tanques = new HashMap<>();
        switch (gameType) {
            case GameWindow.PLAYER_VS_PLAYER:
                setUpPvsP();
                break;
            case GameWindow.PLAYER_VS_COMPUTER:
                setUpPvsC();
                break;
            case GameWindow.PLAYER_VS_INTERNET:
                break;
        }
    }

    /**
     * Configura los tanque suponeindo que el tipo de juego es PLAYER_VS_PLAYER
     */
    private void setUpPvsP() {
        List<Point2D> posiciones = lab.getPosicionTanques();

        Point2D p1 = posiciones.get(0);
        Tanque t1 = new Tanque((int) p1.getX(), (int) p1.getY(), 0, this);
        UserController c = new UserController(t1);
        addKeyListener(c);
        addTanque(t1, c);

        Point2D p2 = posiciones.get(1);
        Tanque t2 = new Tanque((int) p2.getX(), (int) p2.getY(), 0, this);
        UserController c2 = new UserController(t2);
        c2.setKeys(UserController.KEYS_ARROWS);
        addKeyListener(c2);
        addTanque(t2, c2);

        board.init(t1, t2);
    }

    /**
     * Configura los tanque suponiendo que el tipo de juego es PLAYER_VS_COMPUTER
     */
    private void setUpPvsC() {

    }


}
