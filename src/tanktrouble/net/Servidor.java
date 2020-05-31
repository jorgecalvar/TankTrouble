package tanktrouble.net;

import tanktrouble.control.InternetController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Se encarga de la comunicacion con el {@link Cliente} para el modo de juego
 * {@link tanktrouble.ui.GameWindow#PLAYER_VS_INTERNET}.
 */
public class Servidor {

    private static Servidor servidor;

    private ServerSocket ss;
    private Socket s;
    private DataInputStream dis;
    private DataOutputStream dos;

    private InternetController controller;

    private volatile boolean activo = false;

    /**
     * Inicializa el servidor
     */
    private Servidor() {

        try {
            System.out.println("Server started");
            ss = new ServerSocket(10);
            s = ss.accept();
            System.out.println(s);
            System.out.println("CLIENTE CONECTADO");
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
            System.out.println("Iniciando chat...");
            new Thread(() -> {
                try {
                    ServerChat();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Devuelve el objeto {@link Servidor}, ya que solo puede haber uno
     *
     * @return {@link Servidor}
     */
    public static Servidor getInstance() {
        if (servidor == null) {
            servidor = new Servidor();
        }
        return servidor;
    }

    /**
     * Establece el controlador del {@link Cliente}, de donde se obtendra el texto a enviar y al que se entregara la
     * informacion recibida.
     *
     * @param controller controlador del {@link Cliente}
     */
    public void setController(InternetController controller) {
        this.controller = controller;
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
     * Inicia el proceso de comunicacion
     *
     * @throws IOException si hay un error
     */
    public void ServerChat() throws IOException {

        do {
            // if (!activo) continue;
            String send = textToSend();
            System.out.println(send);
            dos.writeUTF(send);
            dos.flush();
            System.out.println("Esperando respusta...");
            String res = dis.readUTF();
            System.out.println(res);
            textReceived(res);
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
