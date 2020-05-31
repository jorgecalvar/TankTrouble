package tanktrouble.control;

import org.json.JSONObject;
import tanktrouble.net.Servidor;
import tanktrouble.reflection.Environment;
import tanktrouble.reflection.Tanque;
import tanktrouble.ui.Dibujo;

/**
 * Clase que controla el tanque del oponente en caso de que el tipo de juego sea
 * {@link tanktrouble.ui.GameWindow#PLAYER_VS_INTERNET}. Utiliza las clases {@link Environment} y {@link Servidor} para
 * comunicarse, codificar y decodificar las comunicaciones y aplicar lo recibido al tanque del oponente.
 */
public class InternetController extends TanqueController {

    private Dibujo dibujo;
    private Environment environment;

    private Servidor servidor;

    /**
     * Inicializa el controlador
     *
     * @param tanque {@link Tanque} del oponente
     * @param dibujo {@link Dibujo}
     */
    public InternetController(Tanque tanque, Dibujo dibujo) {
        super(tanque);
        this.dibujo = dibujo;

        environment = new Environment(dibujo);
        environment.actualizar();

        servidor = Servidor.getInstance();
        servidor.setController(this);
        servidor.setActivo(true);
    }

    @Override
    public void destroy() {
        super.destroy();
        servidor.setActivo(false);
    }

    /**
     * Texto a enviar con la informacion actual del {@link Environment}, obtenida por el metodo
     * {@link Environment#toString()}.
     *
     * @return texto a enviar
     */
    public String getText() {
        return environment.toString();
    }

    /**
     * Ejectua el movimiento en el tanque controlado, segun lo recibido del {@link tanktrouble.net.Cliente}.
     *
     * @param s informacion recibido en formato {@link JSONObject}
     */
    public void receivedText(String s) {
        JSONObject o = new JSONObject(s);
        forward = ((int) o.get("forward")) != 0;
        back = ((int) o.get("back")) != 0;
        right = ((int) o.get("right")) != 0;
        left = ((int) o.get("left")) != 0;
        shoot = ((int) o.get("shoot")) != 0;
    }

}
