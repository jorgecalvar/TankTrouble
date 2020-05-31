package tanktrouble.reflection;

import java.awt.*;

/**
 * Esta interfaz es implementada por los objetos que pueden ser pintados
 */

public interface Pintable {

    /**
     * Pinta el objeto sobre el objeto {@link Graphics}.
     *
     * @param g Donde se pintar el objeto
     */
    void pintar(Graphics2D g);

}
