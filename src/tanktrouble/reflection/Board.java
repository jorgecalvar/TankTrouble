package tanktrouble.reflection;

import java.awt.*;

/**
 * Esta clase se encarga de manjar la estética y pintado de los mensajes que se muestran en el canvas en tiempo de juego.
 */

public class Board implements Pintable {

    public static Color COLOR;

    public static Font FONT = new Font("Courier New", Font.BOLD, 18);

    /**
     * Configura el color de los elementos dibujados por este objet
     *
     * @param c color a configurar
     */
    public static void setColor(Color c) {
        COLOR = c;
    }

    @Override
    public void pintar(Graphics2D g) {
        g.setColor(COLOR);
        g.setFont(FONT);
        g.drawString("PLAYER 1: 2pt", 20, 40);
    }
}
