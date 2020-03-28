package tanktrouble.reflection;

import java.awt.*;

/**
 * Esta interfaz es implementada por los objetos que pueden ser pintados
 */

public interface Pintable {

    /**
     * Pinta el objeto utilizando el parámetro
     *
     * @param g Dónde se pintar el objeto
     */
    void pintar(Graphics2D g);

}
