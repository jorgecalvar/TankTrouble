package tanktrouble.reflection;

import tanktrouble.control.*;
import tanktrouble.labcreator.LabManager;
import tanktrouble.misc.Sonido;
import tanktrouble.misc.Styler;
import tanktrouble.misc.Util;
import tanktrouble.ui.GameWindow;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal del juego, que hereda de {@link Canvas}. Se envarga de inicializar todos los componentes que conforman
 * el juego, incluyendo {@link Tanque tanques}, el {@link Lab laberinto}, las {@link Bala balas} y el {@link Board marcador},
 * entre otros. Tambien inicializa los diferentes controladores, ya sean por teclado o a traves de Internet.
 * <p>
 * Tambien actua como una clase que comunica los distintos componentes del juego.
 */
public class Dibujo extends Canvas implements Pintable {

    /**
     * No se estableceran Hints para el render.
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

    /**
     * Contiene el tipo de rendering actual. Es inicializado a {@link #RENDERING_MODERATE}.
     */
    private static int rendering = RENDERING_MODERATE;


    /**
     * Color de fondo del dibujo
     */
    private static Color COLOR_BG;

    /**
     * Contiene el tipo de juego actual.
     */
    private int gameType;


    private Speeder speeder;
    private volatile Lab lab;

    private volatile Tanque t1;
    private volatile Tanque t2;
    private TanqueController c1;
    private TanqueController c2;

    private volatile BalasController balasController;
    private Board board;
    private Sonido sonido;

    private int internalLab;

    private volatile BufferedImage bf;

    private volatile boolean active = false;


    /**
     * Inicializa el dibujo.
     *
     * @param window   ventana en la que se encuentra
     * @param gameType tipo de juego
     */
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
                    Dibujo.this.destroy();
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
     * Devuelve el objeto sonido que pondra los audios en este juego
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
     * Devuelve el objeto {@link Board} asociado a este dibujo
     *
     * @return objeto {@link Board}
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Devuelve el objeto {@link BalasController} asociado a este dibujo
     *
     * @return objeto {@link BalasController}
     */
    public BalasController getBalasController() {
        return balasController;
    }

    /**
     * Devuelve el set con todos los {@link Tanque tanques} presentes en el dibujo
     *
     * @return tanque dibujados
     */
    public List<Tanque> getTanques() {
        return List.of(t1, t2);
    }

    /**
     * Devuelve si el dibujo esta activado
     *
     * @return si esta activado
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Metodo invocado continuamente para representar el dibujo en el canvas
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
     * Metodo que recibe un objeto {@link Graphics2D} sobre el que dibujara el dibujo en su estado presente
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
     * Configura el tipo de rendering que se realizara en el objeto grafico pasado
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
     * Pinta todos los {@link Tanque tanques} sobre el objeto g
     *
     * @param g objeto sobre el que pintar
     */
    private void pintarTanques(Graphics2D g) {
        for (Tanque t : getTanques())
            t.pintar(g);

    }

    /**
     * Pinta todas las {@link Bala balas}
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


    /**
     * Inicializa los diferentes parametros del dibujo al comienzo de un juego.
     */
    public void init() {
        destroy();

        Styler.setStyle(Styler.DEFAULT);
        setBackground(COLOR_BG);

        if (gameType == GameWindow.PLAYER_VS_INTERNET_CLIENTE) {
            setUpInternetClient();
        } else {
            setUpLab();
            speeder = new Speeder(this);
            balasController = new BalasController();
            speeder.add(balasController);
            setUpTanques();

            active = true;
            speeder.start();
        }

        sonido.playSound(Sonido.CHINESE_GONG);
    }

    /**
     * Desactive el juego. Esto es utilizado para mostrar el mensaje de ganador entre cada juego.
     */
    public void deactivate() {
        active = false;
    }

    /**
     * Destruye los controladores, y para el speeder encargado de este juego
     */
    public void destroy() {
        if (speeder != null) {
            speeder.parar();
            speeder = null;
        }
    }

    /**
     * Inicializ el laberinto.
     */
    private void setUpLab() {
        if (gameType == GameWindow.PLAYER_VS_COMPUTER)
            lab = LabManager.readInternal(8, LabManager.CONVERT_TYPE_GAME);
        else if (gameType == GameWindow.PLAYER_VS_INTERNET) {
            internalLab = Util.randomInteger(LabManager.NUM_LABS_INTERNAL);
            lab = LabManager.readInternal(internalLab, LabManager.CONVERT_TYPE_GAME);
        } else
            lab = Lab.createRamdomLab();
        assert lab != null;
        lab.setDibujo(this);
    }

    /**
     * En caso de juego {@link GameWindow#PLAYER_VS_INTERNET_CLIENTE} devuelve el laberinto en el que se esta
     * jugando, necesariamnete de la memoria interna.
     *
     * @return numero de laberino que se esta jugando.
     */
    public int getInternalLabN() {
        return internalLab;
    }

    /**
     * Configura los {@link Tanque tanques} dependiendo del {@link #gameType modo de juego}.
     */
    private void setUpTanques() {
        List<Tanque> tanques = generateTanques();
        t1 = tanques.get(0);
        t2 = tanques.get(1);
        switch (gameType) {
            case GameWindow.PLAYER_VS_PLAYER:
                setUpPvsP();
                break;
            case GameWindow.PLAYER_VS_COMPUTER:
                setUpPvsC();
                break;
            case GameWindow.PLAYER_VS_INTERNET:
                setUpPvsI();
                break;
        }
        speeder.add(c1);
        speeder.add(c2);
        board.init(t1, t2);
    }

    /**
     * Configura los {@link Tanque tanques} suponeindo que el {@link #gameType tipo de juego} es
     * {@link GameWindow#PLAYER_VS_PLAYER}.
     */
    private void setUpPvsP() {

        // Tanque 1: Usuario 1
        UserController c1 = new UserController(t1);
        addKeyListener(c1);
        this.c1 = c1;

        // Tanque 2: Usuario 2
        UserController c2 = new UserController(t2);
        c2.setKeys(UserController.KEYS_ARROWS);
        addKeyListener(c2);
        this.c2 = c2;

    }

    /**
     * Obtiene dos objetos {@link Tanque} con la posicion y angulo inicial al comienzo del juego, segun lo establecido
     * en cada {@link Lab}.
     *
     * @return lista con los tanques en su posicion inicial
     */
    private List<Tanque> generateTanques() {
        List<Tanque> tanques = new ArrayList<>();
        List<Point2D> posiciones = lab.getPosicionTanques();

        Point2D p1 = posiciones.get(0);
        Tanque t1 = new Tanque((int) p1.getX(), (int) p1.getY(), 0, this);
        tanques.add(t1);

        Point2D p2 = posiciones.get(1);
        Tanque t2 = new Tanque((int) p2.getX(), (int) p2.getY(), 0, this);
        tanques.add(t2);

        return tanques;
    }

    /**
     * Devuelve una lista con los rectangulos que hay que pintar, es decir, las partes del dibujo que han cambiado desde
     * el ultimo pintado.
     *
     * @return rectangulos que han cambiado
     */
    public List<Rectangle2D> getRepaintBounds() {
        List<Rectangle2D> bounds = new ArrayList<>();
        //Tanques
        for (Tanque t : getTanques())
            bounds.add(t.getRepaintBounds());
        //Balas
        for (Bala b : balasController.getBalas())
            bounds.add(b.getRepaintBounds());
        //Board
        bounds.addAll(board.getRepaintBounds());
        //Lab
        bounds.add(lab.getRepaintBounds());
        return Util.simplifyRectangles(bounds);
    }

    /**
     * Configura los {@link Tanque tanques} suponiendo que el {@link #gameType tipo de juego} es
     * {@link GameWindow#PLAYER_VS_COMPUTER}.
     */
    private void setUpPvsC() {

        // Tanque 1: Usuario
        UserController c = new UserController(t1);
        addKeyListener(c);
        c1 = c;

        // Tanque 2: Ordenador
        c2 = new ComputerController(t2, this);

    }

    /**
     * Informa al marcador de que se un {@link Tanque} ha sido disparado y pone el {@link Sonido} correspondiente.
     * En caso de que el {@link #gameType tipo de juego} sea {@link GameWindow#PLAYER_VS_COMPUTER} se informa tambien
     * al controlador ordenador.
     *
     * @param t {@link Tanque} disparado
     */
    public void hitPlayer(Tanque t) {
        sonido.playSound(Sonido.GUNSHOOT);
        board.hitPlayer(t);
        if (gameType == GameWindow.PLAYER_VS_COMPUTER) {
            ComputerController c = (ComputerController) c2;
            if (t.equals(t1))
                c.hitOponent();
            else if (t.equals(t2))
                c.hitMyself();
            throw new IllegalArgumentException("Tanque recibido no pertenece al dibujo");
        }
    }

    /**
     * Configura los {@link Tanque tanques} suponiendo que el {@link #gameType tipo de juego} es
     * {@link GameWindow#PLAYER_VS_INTERNET}.
     */
    private void setUpPvsI() {

        //Tanque usuario
        UserController c = new UserController(t1);
        addKeyListener(c);
        c1 = c;

        //Tanque Internet
        c2 = new InternetController(t2, this);

    }

    /**
     * Configura el {@link Dibujo} suponiendo que el tipo {@link #gameType tipo de juego} es
     * {@link GameWindow#PLAYER_VS_INTERNET_CLIENTE}.
     */
    private void setUpInternetClient() {

        t1 = new Tanque(null, 0, this);
        t2 = new Tanque(null, 0, this);

        balasController = new BalasController();

        lab = null;

        speeder = new Speeder(this);
        ClienteController c = new ClienteController(this);

        // Wait until lab is not null
        System.out.println("Esperando lab no null...");
        while (lab == null) {
        }
        System.out.println("lab configurado");

        board = new Board(this);

        active = true;

        System.out.println("Creado speeder");
        speeder.add(c);
        speeder.start();

    }

    /**
     * Configura el valor de {@link #internalLab} y cambia el {@link Lab} en caso de que sea necesairio.
     *
     * @param n nuevo valor de {@link #internalLab}
     */
    public void setLabN(int n) {
        if (n == internalLab && lab != null) return;
        internalLab = n;
        lab = LabManager.readInternal(n, LabManager.CONVERT_TYPE_GAME);
    }

    /**
     * Devuelve el {@link #t1 tanque 1}.
     *
     * @return {@link #t1}
     */
    public Tanque getTanque1() {
        return t1;
    }

    /**
     * Devuelve el {@link #t2 tanque 2}.
     *
     * @return {@link #t2}.
     */
    public Tanque getTanque2() {
        return t2;
    }

}

