package tanktrouble.control;

import tanktrouble.ui.Dibujo;

import java.util.ArrayList;
import java.util.List;

public class Speeder extends Thread {

    public static int FRAMES_PER_SECOND = 25;

    private Dibujo dibujo;
    private List<Controller> controllers = new ArrayList<>();
    private volatile boolean continuar = true;

    public Speeder(Dibujo dibujo) {
        this.dibujo = dibujo;
    }

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
            dibujo.repaint();
        }
    }

    public void parar() {
        continuar = false;
        for (Controller c : controllers) {
            c.destroy();
            c = null;
        }

    }


}
