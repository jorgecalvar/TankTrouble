package tanktrouble.ui;

import tanktrouble.control.BalasController;
import tanktrouble.control.Speeder;
import tanktrouble.control.UserController;
import tanktrouble.misc.Styler;
import tanktrouble.reflection.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Dibujo extends Canvas implements Pintable {

    public static Color COLOR_BG = new Color(0x9a1750);

    private Speeder speeder;

    private Lab lab;
    private List<Tanque> tanques = new ArrayList<>();
    private BalasController balasController;
    private Board board;


    private volatile BufferedImage bf;

    private boolean hintsSet = false;

    public Dibujo() {

        super();

        setBackground(COLOR_BG);
        setUpLab();

        speeder = new Speeder(this);

        balasController = new BalasController();
        speeder.add(balasController);

        setUpTanques();
        board = new Board();
        Styler.setRandomStyle();

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

        bf = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);


        speeder.start();
    }

    /**
     * Configura el color de fondo del dibujo
     *
     * @param c color a configurar
     */
    public static void setColorBg(Color c) {
        COLOR_BG = c;
    }

    public Lab getLab() {
        return lab;
    }

    public Speeder getSpeeder() {
        return speeder;
    }

    public BalasController getBalasController() {
        return balasController;
    }

    public List<Tanque> getTanques() {
        return tanques;
    }

    public void addTanque(Tanque t) {
        tanques.add(t);
    }

    @Override
    public void paint(Graphics g) {
        if (!hintsSet) {
            setHints((Graphics2D) g);
        }
        pintar((Graphics2D) bf.getGraphics());
        g.drawImage(bf, 0, 0, null);
    }

    @Override
    public void pintar(Graphics2D g) {
        setHints(g);
        g.setColor(COLOR_BG);
        g.fillRect(0, 0, getWidth(), getHeight());
        lab.pintar(g);
        pintarTanques(g);
        pintarBalas(g);
        board.pintar(g);
    }

    private void setHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        //g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private void pintarTanques(Graphics2D g) {
        for (Tanque t : tanques)
            t.pintar(g);

    }

    private void pintarBalas(Graphics2D g) {
        for (Bala b : balasController.getBalas())
            b.pintar(g);
    }

    public void update(Graphics g) {
        paint(g);
    }

    private void setUpLab() {
        lab = Lab.createRamdomLab();
        lab.setDibujo(this);
    }

    private void setUpTanques() {
        Tanque t = new Tanque(478, 347, Math.PI / 3, this);
        addTanque(t);
        UserController c = new UserController(t);
        addKeyListener(c);
        speeder.add(c);
        Tanque t2 = new Tanque(668, 282, 0, this);
        addTanque(t2);
        UserController c2 = new UserController(t2);
        c2.setKeys(UserController.KEYS_ARROWS);
        addKeyListener(c2);
        speeder.add(c2);
    }

}
