package tanktrouble.control;

import tanktrouble.reflection.Bala;
import tanktrouble.reflection.Dibujo;

import java.util.ArrayList;
import java.util.List;

/**
 * Se encarga de controlar las {@link Bala balas} representadas en el {@link Dibujo} y eliminarlas
 * cuando proceda.
 */
public class BalasController implements Controller {

    /**
     * Velocidad de la bala en pixeles por segundo
     */
    public static int VELOCIDAD_BALA = 500;

    private volatile List<Bala> balas = new ArrayList<>();

    /**
     * Anade una {@link Bala}
     *
     * @param b {@link Bala}
     */
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

    /**
     * Elimina todas las {@link Bala balas} no activas.
     */
    public void removeBalas() {
        balas.removeIf(b -> !b.isActive());
    }

    /**
     * Devuelve el listado con las {@link Bala balas} en este instante.
     *
     * @return lista con las {@link Bala balas} actuales
     */
    public List<Bala> getBalas() {
        return balas;
    }

    /**
     * Configura la lista con las {@link Bala balas} actuales
     *
     * @param balas lista con las {@link Bala balas}
     */
    public void setBalas(List<Bala> balas) {
        this.balas = balas;
    }

    /**
     * Destruye las balas.
     */
    public void destroy() {
        balas = null;
    }

}
