package tanktrouble.control;

import tanktrouble.reflection.Tanque;

public abstract class TankController implements Controller {

    /**
     * Velocidad del tanque en píxeles por segundo cuando se avanza hacia delante
     */
    public static final int VELOCIDAD_FORWARD = 300;

    /**
     * Velocidad del tanque en píxeles por segundo cuando se avanza hacia atrás
     */
    public static final int VELOCIDAD_BACK = 150;

    /**
     * Velocidad de rotación del tanque en radianes por segundo
     */
    public static final double VELOCIDAD_ROTACION = Math.toRadians(270);
    protected boolean forward = false;
    protected boolean back = false;
    protected boolean right = false;
    protected boolean left = false;
    protected boolean shoot = false;
    private Tanque tanque;
    private int framesToAllowShooting = 0;


    public TankController(Tanque tanque) {
        this.tanque = tanque;
    }


    public void forward() {
        tanque.avanza((int) (1.0 / Speeder.FRAMES_PER_SECOND * VELOCIDAD_FORWARD));
    }

    public void back() {
        tanque.avanza((int) (-1.0 / Speeder.FRAMES_PER_SECOND * VELOCIDAD_BACK));
    }

    public void right() {
        tanque.rota(1.0 / Speeder.FRAMES_PER_SECOND * VELOCIDAD_ROTACION);
    }

    public void left() {
        tanque.rota(-1.0 / Speeder.FRAMES_PER_SECOND * VELOCIDAD_ROTACION);
    }

    public void shoot() {
        tanque.shoot();
    }

    public boolean execute() {
        if (forward) forward();
        if (back) back();
        if (right) right();
        if (left) left();
        if (shoot)
            if (framesToAllowShooting == 0) {
                shoot();
                framesToAllowShooting = 3;
            }
        if (framesToAllowShooting > 0) framesToAllowShooting--;
        return forward || back || right || left || shoot;
    }

    public void destroy() {
        tanque = null;
    }

}
