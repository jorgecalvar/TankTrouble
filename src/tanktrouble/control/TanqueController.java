package tanktrouble.control;

import tanktrouble.reflection.Tanque;

/**
 * Clase abstracta que representa a todos los {@link Controller controladores} de {@link Tanque tanques}.
 */
public abstract class TanqueController implements Controller {

    /**
     * Velocidad del {@link Tanque} en pixeles por segundo cuando se avanza hacia delante
     */
    public static final int VELOCIDAD_FORWARD = 300;

    /**
     * Velocidad del {@link Tanque} en pixeles por segundo cuando se avanza hacia atras
     */
    public static final int VELOCIDAD_BACK = 150;

    /**
     * Velocidad de rotacion del {@link Tanque} en radianes por segundo
     */
    public static final double VELOCIDAD_ROTACION = Math.toRadians(270);


    /**
     * Frames para esperar entre cada disparo
     */
    public static final int FRAMES_TO_WAIT_TO_SHOOT = 3;

    /**
     * Activado cuando se avanza hacia adelante
     */
    protected boolean forward = false;
    /**
     * Activado cuando se avanza hacia atras
     */
    protected boolean back = false;
    /**
     * Activado cuando se rota hacia la derecha
     */
    protected boolean right = false;
    /**
     * Activado cuando se rota hacia la izquierda
     */
    protected boolean left = false;
    /**
     * Activado cuando se dispara
     */
    protected boolean shoot = false;
    private Tanque tanque;
    private int framesToAllowShooting = 0;

    /**
     * Inicializa el {@link TanqueController}
     *
     * @param tanque {@link Tanque} a controlar
     */
    public TanqueController(Tanque tanque) {
        this.tanque = tanque;
    }

    /**
     * Ejecuta el avance hacia adelante
     */
    public void forward() {
        tanque.avanza((int) (1.0 / Speeder.FRAMES_PER_SECOND * VELOCIDAD_FORWARD));
    }

    /**
     * Ejecuta el avance hacia atras
     */
    public void back() {
        tanque.avanza((int) (-1.0 / Speeder.FRAMES_PER_SECOND * VELOCIDAD_BACK));
    }

    /**
     * Ejecuta la rotacion hacia la derecha
     */
    public void right() {
        tanque.rota(1.0 / Speeder.FRAMES_PER_SECOND * VELOCIDAD_ROTACION);
    }

    /**
     * Ejecuta la rotacion hacia la izquierda
     */
    public void left() {
        tanque.rota(-1.0 / Speeder.FRAMES_PER_SECOND * VELOCIDAD_ROTACION);
    }

    /**
     * Dispara
     */
    public void shoot() {
        tanque.shoot();
    }

    /**
     * Llama a los metodos de movimiento y disparo necesario, segun los valores actuales de {@link #forward},
     * {@link #back}, {@link #right}, {@link #left} y {@link #shoot}.
     *
     * @return si ha habido movimiento
     */
    public boolean execute() {
        if (forward) forward();
        if (back) back();
        if (right) right();
        if (left) left();
        if (shoot)
            if (framesToAllowShooting == 0) {
                shoot();
                framesToAllowShooting = FRAMES_TO_WAIT_TO_SHOOT;
            }
        if (framesToAllowShooting > 0) framesToAllowShooting--;
        return forward || back || right || left || shoot;
    }

    /**
     * Elimina el tanque
     */
    public void destroy() {
        tanque = null;
    }

    /**
     * Devuelve {@link #forward}
     *
     * @return {@link #forward}
     */
    public boolean getForward() {
        return forward;
    }

    /**
     * Devuelve {@link #back}
     *
     * @return {@link #back}
     */
    public boolean getBack() {
        return back;
    }

    /**
     * Devuelve {@link #right}
     *
     * @return {@link #right}
     */
    public boolean getRight() {
        return right;
    }

    /**
     * Devuelve {@link #left}
     *
     * @return {@link #left}
     */
    public boolean getLeft() {
        return left;
    }

    /**
     * Devuelve {@link #shoot}
     *
     * @return {@link #shoot}
     */
    public boolean getShoot() {
        return shoot;
    }

}
