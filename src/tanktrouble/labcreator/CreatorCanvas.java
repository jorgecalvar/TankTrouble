package tanktrouble.labcreator;

import tanktrouble.misc.Styler;
import tanktrouble.reflection.Lab;
import tanktrouble.reflection.Pared;
import tanktrouble.reflection.Tanque;
import tanktrouble.ui.CreateWindow;

import javax.naming.OperationNotSupportedException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase representa al canvas en el que el usuario podrá diseñar objetos Labs, es decir, laberintos en los que
 * posteriormente se podrá jugar.
 */
public class CreatorCanvas extends Canvas {

    /**
     * Espacio extra dejado en todos los lados del canvas para que los Dots tengan un padding.
     * La red de puntos no empieza en el (0,0), sino en el (OFFSET, OFFSET)
     */
    public static final int OFFSET = 10 + Dot.DIAMETER / 2;

    /**
     * Separación entre los Dots.
     */
    public static final int LONGITUD = 100;

    /**
     * Máxima dimensión que se puede dibujar, que determina el tamaño de la red de Dots.
     */
    public static final Dimension MAX_DIMENSION = new Dimension(1200, 600);

    /**
     * Color de los Dots
     */
    public static final Color COLOR_DOT = new Color(0x2d4159);

    /**
     * Color de un Dot cuando ha sido seleccionado
     */
    public static final Color COLOR_DOT_SELECTED = COLOR_DOT.brighter().brighter();

    /**
     * Color de fondo del canvas
     */
    public static final Color COLOR_BG = new Color(0xf7f9fb);

    /**
     * Color del rectángulo que representa al tanque
     */
    public static final Color COLOR_TANQUE = new Color(0x31708e);

    /**
     * Anchura del rectángulo que representa al tanque
     */
    public static final int TANQUE_WIDTH = (int) Math.ceil(Tanque.BASE_WIDTH + (1 - Tanque.CANNON_FACTOR) * Tanque.CANNON_WIDTH);

    /**
     * ALtura del rectángulo que representa al tanque
     */
    public static final int TANQUE_HEIGHT = (int) Math.ceil(Tanque.BASE_HEIGTH);

    /**
     * El canvas no está en ningún estado de pintado o borrado.
     */
    public static final int STATE_NONE = 0;

    /**
     * Estado de pintar paredes
     */
    public static final int STATE_PINTAR = 1;

    /**
     * Estado de borrar paredes
     */
    public static final int STATE_BORRAR = 2;

    /**
     * Estado de pintar los rectángulos que representan a los tanques
     */
    public static final int STATE_CREAR_TANQUE = 3;

    /**
     * Estado de borrar los rectángulos que representan a los tanques
     */
    public static final int STATE_BORRAR_TANQUE = 4;

    /**
     * JFrame en el que está contenido este canvas
     */
    private CreateWindow window;

    /**
     * Estado actual
     */
    private int state;

    /**
     * Dimensión aplicada por el usuario, es decir, el tamaño de lab que se está dibujando.
     */
    private Dimension dimension;

    /**
     * Lista con todos los Dots de la red
     */
    private List<Dot> dots = new ArrayList<>();

    /**
     * Primer dot en el que se ha hecho click, null si no hay ninguno seleccionado.
     */
    private Dot firstDot;

    /**
     * Segundo dot en el que se ha hecho clic, null si no hay ninguno seleccionado.
     */
    private Dot secondDot;

    /**
     * Lista con todas las paredes dibujadas.
     */
    private List<Pared> paredes = new ArrayList<>();

    /**
     * Lista con todos los objetos que representan tanques dibujados.
     */
    private List<Rectangle2D> tanques = new ArrayList<>();

    /**
     * Crea un canvas
     *
     * @param window JFrame en el que está contenido
     */
    public CreatorCanvas(CreateWindow window) {

        this.window = window;

        setPreferredSize(new Dimension(2 * OFFSET + (int) MAX_DIMENSION.getWidth(),
                2 * OFFSET + (int) MAX_DIMENSION.getHeight()));
        setBackground(COLOR_BG);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CreatorCanvas.this.mouseClicked(e.getX(), e.getY());
            }
        });

        createDots();

        Styler.setStyle(Styler.STRIKING_BLUE);

    }

    /**
     * Configura el valor de tanques
     *
     * @param tanques nuevo valor de tanques
     */
    public void setTanques(List<Rectangle2D> tanques) {
        this.tanques = tanques;
    }

    /**
     * Configura el valor de la variable state.
     *
     * @param state nuevo valor de state
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * Crea la red de Dots
     */
    private void createDots() {
        for (int x = OFFSET; x <= MAX_DIMENSION.getWidth() + OFFSET; x += LONGITUD) {
            for (int y = OFFSET; y <= MAX_DIMENSION.getHeight() + OFFSET; y += LONGITUD) {
                dots.add(new Dot(new Point2D.Double(x, y)));
            }
        }
    }

    /**
     * Pinta el canvas
     *
     * @param g objeto sobre el que pintar
     */
    @Override
    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(COLOR_DOT);
        for (Dot dot : dots) {
            dot.pintar(g2);
        }

        if (firstDot != null) {
            g2.setColor(COLOR_DOT_SELECTED);
            firstDot.pintar(g2);
        }

        for (Pared pared : paredes) {
            pared.pintar(g2);
        }

        g2.setColor(COLOR_TANQUE);
        for (Rectangle2D tanque : tanques)
            g2.fill(tanque);

    }

    /**
     * Aplica una nueva dimensión al canvas. Se borrará todas las paredes y tanque que estén fuera.
     *
     * @param dimension nueva dimensión a aplicar
     */
    public void aplicarDimension(Dimension dimension) {
        this.dimension = dimension;
        borrarFueraTamano();
        crearParedExterna();
        repaint();
    }

    /**
     * Método llamada cucando se hace click con el mouse sobre el canvas
     *
     * @param x coordenada x
     * @param y coordenada y
     */
    private void mouseClicked(int x, int y) {
        if (state == STATE_NONE) {
            window.showAyuda();
        } else if (dimension == null) {
            window.warn("Debe aplicar un tamaño antes de pintar!");
        } else if (state == STATE_PINTAR || state == STATE_BORRAR) {
            for (Dot dot : dots)
                if (dot.contains(x, y))
                    dotClicked(dot);
        } else if (state == STATE_CREAR_TANQUE) {
            if (tanques.size() < 2)
                crearTanque(x - TANQUE_WIDTH / 2, y - TANQUE_HEIGHT / 2);
            else
                window.warn("Ya ha creado 2 tanques!");
        } else if (state == STATE_BORRAR_TANQUE) {
            borrarTanque(x, y);
        }
    }

    /**
     * Crea un tanque si se puede en la posición especificada. Si no se puede, da un mensaje de aviso.
     *
     * @param x coordenada x
     * @param y coordenada y
     */
    private void crearTanque(int x, int y) {
        Rectangle2D tanque = new Rectangle2D.Double(x, y, TANQUE_WIDTH, TANQUE_HEIGHT);
        if (legalPosition(tanque)) {
            tanques.add(tanque);
            repaint();
        } else
            window.warn("Ha elegido una posición inválida para un tanque!");
    }

    /**
     * Borra un tanque si existe en la posición especificada. No lanza un aviso si no existe.
     *
     * @param x coordenada x
     * @param y coordenada y
     */
    private void borrarTanque(int x, int y) {
        tanques.removeIf(t -> t.contains(x, y));
        repaint();
    }

    /**
     * Comprueba si un rectángulo está en una posición válida, es decir, dentro del tamaño del laberint (dimension) y
     * no superpuesto con paredes y otros rectángulos que representan a tanques.
     *
     * @param tanque réctangulo a comprobar
     * @return si está en una posición válida
     */
    private boolean legalPosition(Rectangle2D tanque) {
        //Dentro del tamaño
        final double MAX_X = OFFSET + (int) dimension.getWidth();
        final double MAX_Y = OFFSET + (int) dimension.getHeight();
        if (tanque.getX() <= OFFSET || tanque.getY() <= OFFSET ||
                tanque.getX() + tanque.getWidth() >= MAX_X ||
                tanque.getY() + tanque.getHeight() >= MAX_Y)
            return false;
        //No choca con paredes
        for (Pared pared : paredes)
            if (tanque.intersects(pared.getRectangle()))
                return false;
        //No choca con otros tanques
        for (Rectangle2D t : tanques)
            if (tanque != t && t.intersects(tanque))
                return false;
        return true;
    }

    /**
     * Comprueba si una pared está en una posición válida, es decir, no superpone a ningún tanque.
     *
     * @param pared pared a comprobar
     * @return si está en una posición válida
     */
    private boolean legalPosition(Pared pared) {
        //No choca con tanques
        for (Rectangle2D t : tanques)
            if (t.intersects(pared.getRectangle()))
                return false;
        return true;
    }

    /**
     * Método llamdo cuando se ha hecho click en un Dot. Lo almacenará en las variables correspondientes y invocará la
     * creación o borrado de una pared cuando sea necesario. Por último, manda redibujar el canvas.
     *
     * @param dot donde se ha hecho click
     */
    private void dotClicked(Dot dot) {
        if (firstDot == null) {
            firstDot = dot;
        } else {
            if (secondDot == firstDot) {
                window.warn("Los puntos deben de ser diferentes!");
                return;
            }
            secondDot = dot;
            if (!validDots()) {
                window.warn("La pared debe estar dentro dal tamaño especificado!");
            } else {
                if (state == STATE_PINTAR)
                    createPared();
                else
                    deletePared();
            }

            firstDot = null;
            secondDot = null;
        }
        repaint();
    }

    /**
     * Crea todos las paredes en la línea que uno los dos Dots en los que se ha hecho click. No lsa crea alguna de ellas
     * si ya hay una pared creada en ese lugar o si no está en una posición legal.
     */
    private void createPared() {
        List<Pared> paredesToAdd = getParedes();
        if (paredesToAdd == null) return;
        for (Pared pared : paredesToAdd) {
            if (!paredes.contains(pared) && legalPosition(pared))
                paredes.add(pared);
        }
    }

    /**
     * Borra todas las paredes que se encuentran en la línea que une a los dos Dots en los que se ha hecho click. Lanza
     * un aviso si no se ha borrado ninguna pared.
     */
    public void deletePared() {
        List<Pared> paredesToDelete = getParedes();
        if (paredesToDelete == null) return;
        boolean paredFound = false;
        for (Pared paredToDelete : paredesToDelete) {
            for (Pared pared : paredes) {
                if (pared.equals(paredToDelete)) {
                    paredes.remove(pared);
                    paredFound = true;
                    break;
                }
            }
        }
        if (!paredFound)
            window.warn("La pared que intenta borrar no es válida");
    }

    /**
     * Devuelve una lista con todas las paredes que conforman la línea que une los dos puntos en los que se ha hecho
     * click.
     *
     * @return paredes seleccionadas por el usuario
     */
    private List<Pared> getParedes() {
        try {
            List<Pared> paredes = new ArrayList<>();
            int tipo = tipoPared();
            Point2D firstDotLoc = firstDot.getLoc();
            Point2D secondDotLoc = secondDot.getLoc();
            if (tipo == Pared.TIPO_HORIZONTAL) {
                double a;
                double b;
                if (firstDotLoc.getX() < secondDotLoc.getX()) {
                    a = firstDotLoc.getX();
                    b = secondDotLoc.getX();
                } else {
                    a = secondDotLoc.getX();
                    b = firstDotLoc.getX();
                }
                while (a < b) {
                    paredes.add(new Pared(new Point2D.Double(a, firstDotLoc.getY()), LONGITUD, Pared.TIPO_HORIZONTAL));
                    a += LONGITUD;
                }
            } else {
                double a;
                double b;
                if (firstDotLoc.getY() < secondDotLoc.getY()) {
                    a = firstDotLoc.getY();
                    b = secondDotLoc.getY();
                } else {
                    a = secondDotLoc.getY();
                    b = firstDotLoc.getY();
                }
                while (a < b) {
                    paredes.add(new Pared(new Point2D.Double(firstDotLoc.getX(), a), LONGITUD, Pared.TIPO_VERTICAL));
                    a += LONGITUD;
                }
            }
            return paredes;
        } catch (OperationNotSupportedException e) {
            window.warn("Las paredes deben ser verticales o horizontales!");
            return null;
        }
    }

    /**
     * Configura el valor de paredes
     *
     * @param paredes nuevo valor de paredes
     */
    public void setParedes(List<Pared> paredes) {
        this.paredes = paredes;
        repaint();
    }

    /**
     * Devuelve el tipo de pared, siguiendo las constantes definidas en la clase Pared.
     * Lanza una excepción si la pared que se busca crear no es válida.
     *
     * @return el tipo de pared
     * @throws OperationNotSupportedException si la pared no es ni horizontal ni vertical
     */
    private int tipoPared() throws OperationNotSupportedException {
        Point2D firstDotLoc = firstDot.getLoc();
        Point2D secondDotLoc = secondDot.getLoc();
        if (firstDotLoc.getX() == secondDotLoc.getX())
            return Pared.TIPO_VERTICAL;
        else if (firstDotLoc.getY() == secondDotLoc.getY())
            return Pared.TIPO_HORIZONTAL;
        throw new OperationNotSupportedException("Las paredes deben ser verticales o horizontales!");
    }

    /**
     * Borra todas las paredes, todos los tanques y desconfigura la dimension, es decir, será necesario volver a aplicar
     * un tamaño (dimensión).
     */
    public void borrarTodo() {
        setParedes(new ArrayList<>());
        setTanques(new ArrayList<>());
        dimension = null;
    }

    /**
     * Crea toda la pared externa del laberinto, según la dimensión aplicada.
     */
    private void crearParedExterna() {
        for (int x = OFFSET; x < OFFSET + (int) dimension.getWidth(); x += LONGITUD) {
            paredes.add(new Pared(new Point2D.Double(x, OFFSET), LONGITUD, Pared.TIPO_HORIZONTAL));
            paredes.add(new Pared(new Point2D.Double(x, OFFSET + (int) dimension.getHeight()),
                    LONGITUD, Pared.TIPO_HORIZONTAL));
        }
        for (int y = OFFSET; y < OFFSET + (int) dimension.getHeight(); y += LONGITUD) {
            paredes.add(new Pared(new Point2D.Double(OFFSET, y), LONGITUD, Pared.TIPO_VERTICAL));
            paredes.add(new Pared(new Point2D.Double(OFFSET + (int) dimension.getWidth(), y),
                    LONGITUD, Pared.TIPO_VERTICAL));
        }
    }

    /**
     * Comprueba si los dos Dots en los que se ha hecho click son válidos, es decir, al menos uno de ellas está
     * completamente dentro del borde externo (dimensión) y el otro está o dentro o en el borde externo.
     */
    private boolean validDots() {
        Point2D firstDotLoc = firstDot.getLoc();
        Point2D secondDotLoc = secondDot.getLoc();
        final double MAX_X = dimension.getWidth() + OFFSET;
        final double MAX_Y = dimension.getHeight() + OFFSET;
        return !(firstDotLoc.getX() > MAX_X) && !(firstDotLoc.getY() > MAX_Y) &&
                !(secondDotLoc.getX() > MAX_X) && !(secondDotLoc.getY() > MAX_Y) &&
                (firstDotLoc.getX() != MAX_X || secondDotLoc.getX() != MAX_X) &&
                (firstDotLoc.getX() != OFFSET || secondDotLoc.getX() != OFFSET) &&
                (firstDotLoc.getY() != MAX_Y || secondDotLoc.getY() != MAX_Y) &&
                (firstDotLoc.getY() != OFFSET || secondDotLoc.getY() != OFFSET);

    }

    /**
     * Borra todas las paredes y tanques que están fuera o solapanda con el borde externo.
     */
    private void borrarFueraTamano() {
        //Paredes
        for (Pared pared : new ArrayList<>(paredes)) {
            Point2D start = pared.getStart();
            int tipo = pared.getTipo();
            if (start.getX() >= OFFSET + (int) dimension.getWidth() ||
                    start.getY() >= OFFSET + (int) dimension.getHeight() ||
                    (start.getY() == OFFSET && tipo == Pared.TIPO_HORIZONTAL) ||
                    (start.getX() == OFFSET && tipo == Pared.TIPO_VERTICAL))
                paredes.remove(pared);
        }
        //Tanques
        tanques.removeIf(t -> !legalPosition(t));
    }

    /**
     * Crea el objeto laberino que se corresponde que el dibujo actual.
     *
     * @return objeto Lab dibujado
     * @throws UnsupportedOperationException si no se reúnen las características para crearlo.
     */
    public Lab createLab() {
        if (!validLab())
            throw new UnsupportedOperationException("No se ha podido crear el laberinto, ya que no reúne las condiciones necesarias!");
        List<Pared> paredesLab = new ArrayList<>();
        for (Pared pared : paredes) {
            Point2D start = pared.getStart();
            Point2D startLab = new Point2D.Double(start.getX() - OFFSET, start.getY() - OFFSET);
            paredesLab.add(new Pared(startLab, LONGITUD, pared.getTipo()));
        }
        List<Point2D> tanquesLab = new ArrayList<>();
        for (Rectangle2D t : tanques)
            tanquesLab.add(new Point2D.Double(t.getX(), t.getY()));
        return new Lab(dimension, paredesLab, tanquesLab);
    }

    /**
     * Comprueba si el dibujo actual reúne las carácterísticas necesarias para creaa un objeto Lab, es decir, se ha
     * aplicado un tamaño y se han añadido dos tanques.
     *
     * @return si se puede crear un objeto Lab
     */
    private boolean validLab() {
        return tanques.size() == 2 && dimension != null;
    }

}