package tanktrouble.control;

import tanktrouble.reflection.Dibujo;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Se encarga de actualizar el {@link Dibujo} periodicamente, y de llamar al metodo {@link Controller#execute()} de los
 * controladores.
 * Para realizar el repintado utilizara por defecto el metodo {@link java.awt.Canvas#repaint(int, int, int, int)}, con
 * el objetivo de solo repintar parte del {@link Dibujo}.
 */
public class Speeder extends Thread {

    /**
     * Se repintara solo las partes que hayan cambiado.
     */
    public final static int REPAINT_PARTIAL = 1;

    /**
     * Se repintara el dibujo completo.
     */
    public final static int REPAINT_COMPLETE = 2;
    /**
     * Fotogramas por segundo
     */
    public static int FRAMES_PER_SECOND = 20;
    private int repaintType = 1;

    private Dibujo dibujo;
    private List<Controller> controllers = new ArrayList<>();
    private volatile boolean continuar = true;

    /**
     * Inicializa el {@link Speeder}
     *
     * @param dibujo {@link Dibujo}
     */
    public Speeder(Dibujo dibujo) {
        this.dibujo = dibujo;
    }

    /**
     * Anade un {@link Controller}
     *
     * @param c {@link Controller}
     */
    public void add(Controller c) {
        controllers.add(c);
    }

    @Override
    public void run() {
        while (continuar) {
            if (dibujo.isActive())
                for (Controller c : controllers)
                    c.execute();
            try {
                Thread.sleep(1000 / FRAMES_PER_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
                continuar = false;
            }
            try {
                if (repaintType == REPAINT_PARTIAL)
                    redibujar();
                else
                    dibujo.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void redibujar() {
        for (Rectangle2D bounds : dibujo.getRepaintBounds()) {
            dibujo.repaint((int) bounds.getX(), (int) bounds.getY(),
                    (int) bounds.getWidth(), (int) bounds.getHeight());
        }

    }

    /**
     * Para el {@link Speeder}, cuando se haya terminado el juego. Destruye los {@link Controller controladores}.
     */
    public void parar() {
        continuar = false;
        for (Controller c : controllers) {
            c.destroy();
            c = null;
        }
        dibujo = null;
    }

}
