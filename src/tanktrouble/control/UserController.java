package tanktrouble.control;

import tanktrouble.reflection.Tanque;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class UserController extends TanqueController implements KeyListener {

    /**
     * La dirección del tanque se realizará con las teclas A, S, D y W del teclado.
     * El disparo será con la tecla espacio.
     * Este es el tipo de control por defecto.
     */
    public static final int KEYS_ASDW = 1;

    /**
     * La direción del tanque se realizará con las felchas del teclado.
     * El disparo se realiza con la tecla M.
     */
    public static final int KEYS_ARROWS = 2;

    private int keys;

    public UserController(Tanque tanque) {
        super(tanque);
        keys = KEYS_ASDW;
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //System.out.println("Key typed: "+e.getKeyChar()+" "+e.getKeyCode());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("Key pressed: "+e.getKeyChar()+" "+e.getKeyCode());
        setCode(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("Key released: "+e.getKeyChar()+" "+e.getKeyCode());
        setCode(e.getKeyCode(), false);
    }


    private void setCode(int key, boolean state) {
        if (key == getForwardKey())
            forward = state;
        else if (key == getBackKey()) {
            back = state;
            if (right && state) {
                left = true;
                right = false;
            } else if (left && state) {
                right = true;
                left = false;
            }
        } else if (key == getRightKey())
            if (!forward && back) {
                left = state;
                right = false;
            } else {
                right = state;
                left = false;
            }
        else if (key == getLeftKey())
            if (!forward && back) {
                right = state;
                left = false;
            } else {
                left = state;
                right = false;
            }
        else if (key == getShootKey())
            shoot = state;
    }


    private int getForwardKey() {
        switch (keys) {
            case KEYS_ASDW:
                return 87;
            case KEYS_ARROWS:
                return 38;
        }
        throw new IllegalStateException("Illegal keys value: " + keys);
    }

    private int getBackKey() {
        switch (keys) {
            case KEYS_ASDW:
                return 83;
            case KEYS_ARROWS:
                return 40;
        }
        throw new IllegalStateException("Illegal keys value: " + keys);
    }

    private int getLeftKey() {
        switch (keys) {
            case KEYS_ASDW:
                return 65;
            case KEYS_ARROWS:
                return 37;
        }
        throw new IllegalStateException("Illegal keys value: " + keys);
    }

    private int getRightKey() {
        switch (keys) {
            case KEYS_ASDW:
                return 68;
            case KEYS_ARROWS:
                return 39;
        }
        throw new IllegalStateException("Illegal keys value: " + keys);
    }

    private int getShootKey() {
        switch (keys) {
            case KEYS_ASDW:
                return 32;
            case KEYS_ARROWS:
                return 77;
        }
        throw new IllegalStateException("Illegal keys value: " + keys);
    }


}
