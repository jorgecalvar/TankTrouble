package tanktrouble.control;

import tanktrouble.reflection.Bala;

import java.util.ArrayList;
import java.util.List;

public class BalasController implements Controller {

    /**
     * Velocidad de la bala en píxeles por segundo
     */
    public static int VELOCIDAD_BALA = 500;

    private volatile List<Bala> balas = new ArrayList<>();

    public void add(Bala b) {
        balas.add(b);
    }

    @Override
    public boolean execute() {
        for (Bala bala : balas) {
            bala.avanza((int) (1.0 / Speeder.FRAMES_PER_SECOND * VELOCIDAD_BALA));
        }
        removeBalas();
        return balas.size() > 0;
    }

    public void removeBalas() {
        balas.removeIf(b -> !b.isActive());
    }

    public List<Bala> getBalas() {
        return balas;
    }
}
