package tanktrouble.control;

import org.json.JSONObject;
import tanktrouble.net.Cliente;
import tanktrouble.reflection.Environment;
import tanktrouble.ui.Dibujo;

/**
 * Controla el {@link Dibujo} para el tipo de juego {@link tanktrouble.ui.GameWindow#PLAYER_VS_INTERNET_CLIENTE},
 * utilizando la clase {@link Environment} y {@link Cliente}.
 */
public class ClienteController implements Controller {

    private Dibujo dibujo;
    private Cliente cliente;
    private Environment environment;

    private UserController userController;

    /**
     * Inicializa el {@link ClienteController}
     *
     * @param dibujo {@link Dibujo}
     */
    public ClienteController(Dibujo dibujo) {
        this.dibujo = dibujo;
        environment = new Environment(dibujo);

        userController = new UserController(null);
        dibujo.addKeyListener(userController);

        cliente = Cliente.getInstance();
        cliente.setController(this);
        cliente.setActivo(true);
    }

    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public void destroy() {
        cliente.setActivo(false);
    }

    /**
     * Devuelve el texto a enviar en formato JSON, que contiene las acciones que esta ejecutando el usuario cliente.
     *
     * @return texto a enviar.
     */
    public String getText() {
        JSONObject o = new JSONObject();
        o.put("forward", userController.getForward() ? 1 : 0);
        o.put("back", userController.getBack() ? 1 : 0);
        o.put("right", userController.getRight() ? 1 : 0);
        o.put("left", userController.getLeft() ? 1 : 0);
        o.put("shoot", userController.getShoot() ? 1 : 0);
        return o.toString();
    }

    /**
     * Metodo invocado cuando se recibe informacion. Esta informacion se pasa al objeto {@link Environment} para que
     * actualice la UI.
     *
     * @param s texto en formato JSON.
     */
    public void receivedText(String s) {
        environment.actualizar(s);
        environment.actualizarUI();
    }

}
