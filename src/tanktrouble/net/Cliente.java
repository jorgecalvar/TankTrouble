package tanktrouble.net;

import tanktrouble.control.ClienteController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Se encarga de establecer la conexion con el {@link Servidor}, para el modo de juego
 * {@link tanktrouble.ui.GameWindow#PLAYER_VS_INTERNET_CLIENTE}.
 */
public class Cliente {

    /**
     * Direccion IP del {@link Servidor}
     */
    private static String ipAddress;

    private static Cliente cliente;

    private Socket s;
    private DataInputStream dis;
    private DataOutputStream dos;

    private ClienteController controller;

    private volatile boolean activo = false;

    /**
     * Inicializa el objeto cliente
     */
    private Cliente() {

        try {
            System.out.println("Conectando...");
            s = new Socket(ipAddress, 10);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
            System.out.println("CONECTADO");
            new Thread(() -> {
                try {
                    ClienteChat();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Devuelve el objeto {@link Cliente}, ya que solo puede haber uno
     *
     * @return {@link Cliente}
     */
    public static Cliente getInstance() {
        if (cliente == null)
            cliente = new Cliente();
        return cliente;
    }

    /**
     * Configura el valor de la direccion IP del {@link Servidor}. Es necesario configurarla antes de crear la instancia.
     *
     * @param ip direccion IP del {@link Servidor}
     */
    public static void setIpAddress(String ip) {
        ipAddress = ip;
    }

    /**
     * Establece si esta activo el {@link Cliente}
     *
     * @param activo si esta activo
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Establece el controlador del {@link Cliente}, de donde se obtendra el texto a enviar y al que se entregara la
     * informacion recibida.
     *
     * @param controller controlador del {@link Cliente}
     */
    public void setController(ClienteController controller) {
        this.controller = controller;
    }

    /**
     * Inicia el proceso de comunicacion
     *
     * @throws IOException si hay un error
     */
    public void ClienteChat() throws IOException {

        do {
            // if (!activo) continue;
            // TODO ELiminar comentarios
            // System.out.println("Esperando recepción...");
            String str = dis.readUTF();
            textReceived(str);
            str = textToSend();
            // System.out.println("Enviando...");
            // System.out.println(str);
            dos.writeUTF(str);
            dos.flush();
        } while (true);

    }

    private String textToSend() {
        try {
            return controller.getText();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void textReceived(String s) {
        try {
            controller.receivedText(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
