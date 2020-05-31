package tanktrouble.ui;

import tanktrouble.misc.Util;
import tanktrouble.net.Cliente;

import javax.swing.*;
import java.awt.*;

/**
 * Esta es la clase inicial desde la que se accede al programa. Permiti iniciar los disintos
 * {@link GameWindow tipos de juego}, asi como iniciar el {@link CreateWindow creador de laberintos} o abrir
 * la pestaña de {@link OptionsWindow opciones}.
 */

public class InicioWindow extends JFrame {

    public static final Color COLOR_BG = new Color(0x264653);
    public static final Font FONT_TITLE = new Font("Courier New", Font.BOLD, 35);
    public static final Font FONT_BUTTONS = new Font("Helvetica", Font.BOLD, 25);

    /**
     * Crea la ventana de inicio.
     */
    public InicioWindow() {

        setTitle("Tank Trouble");

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 30));


        JLabel lblTitle = new JLabel("TANK TROUBLE");
        JButton btnPlayers = new JButton("2 Jugadores");
        JButton btnComputer = new JButton("Jugador vs Ordenador");
        JButton btnInternet = new JButton("Partida Online");
        JButton btnCreate = new JButton("Crear Laberinto");
        JButton btnOptions = new JButton("Opciones");

        styleTitle(lblTitle);
        styleButton(btnPlayers);
        styleButton(btnComputer);
        styleButton(btnInternet);
        styleButton(btnCreate);
        styleButton(btnOptions);

        btnPlayers.addActionListener(e -> playerVsPlayer());
        btnComputer.addActionListener(e -> playerVsComputer());
        btnInternet.addActionListener(e -> playerVsInternet());
        btnCreate.addActionListener(e -> createLab());
        btnOptions.addActionListener(e -> options());

        panel.add(lblTitle);
        panel.add(btnPlayers);
        // panel.add(btnComputer); // Modo todavia no funcional
        panel.add(btnInternet);
        panel.add(btnCreate);
        panel.add(btnOptions);

        panel.setBackground(COLOR_BG);
        panel.setPreferredSize(new Dimension(390, 438)); // Height 498 cuando se añade Computer

        add(panel);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setIconImage(Util.getIcono());
        setVisible(true);

    }

    public static void main(String[] args) {
        new InicioWindow();
    }

    private static void styleTitle(JLabel title) {
        title.setFont(FONT_TITLE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(new Color(0xffffff));

    }

    /**
     * Da estilo a los botones que conforman esta ventana.
     *
     * @param btn boton a dar estilo
     */
    private static void styleButton(JButton btn) {
        btn.setFont(FONT_BUTTONS);
        btn.setMargin(new Insets(10, 10, 10, 10));
        btn.setPreferredSize(new Dimension(300, 50));
        btn.setBackground(new Color(0x2a9d8f));
        btn.setForeground(new Color(0x0b032d));

    }

    /**
     * Inicia el modo de juego de dos jugadores en el mismo ordenador.
     */
    private void playerVsPlayer() {
        try {
            new GameWindow();
            exit();
        } catch (Exception e) {
            Util.error(e.getMessage(), this);
        }
    }

    private void playerVsComputer() {
        //Util.warn("Not supported yet!", this);
        try {
            new GameWindow(GameWindow.PLAYER_VS_COMPUTER);
            exit();
        } catch (Exception e) {
            Util.error(e.getMessage(), this);
        }
    }

    /**
     * Inicializa el modo de juego online. Por tanto, informa al usuairio de la direccion ip (en caso de ser creador) o
     * solicita una ip para conectare (en caso de unirse). Muestra un barra de progreso mientras se espera a que se
     * produzca la conexion. Finalmente, abre la ventana de juego y cierra la actual.
     */
    private void playerVsInternet() {

        String titulo = "Partida Online (Misma Wifi)";
        String msg = "¿Deseas crear una partida online o unirte a una ya creada?\n" +
                "IMPORTANTE: Solo funciona para personas conectadas a la misma wifi.";

        String[] options = new String[2];
        options[0] = "Crear";
        options[1] = "Unirme";

        int a = JOptionPane.showOptionDialog(this, msg, titulo,
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, 0);

        if (a == -1)
            return;

        try {

            int gameType;
            if (a == 0) { // Crear partida online
                gameType = GameWindow.PLAYER_VS_INTERNET;

                msg = "Para que alguien se una a tu partida debes darle el siguiente código: " +
                        Util.getSimplifiedWifiIP();
                JOptionPane.showMessageDialog(this, msg, titulo, JOptionPane.INFORMATION_MESSAGE);

            } else { // Unirme a partida online
                gameType = GameWindow.PLAYER_VS_INTERNET_CLIENTE;
                msg = "Introduce el código proporcionado por el creador de la partida:";
                String c = JOptionPane.showInputDialog(this, msg, titulo, JOptionPane.QUESTION_MESSAGE);
                if (c == null || c.isEmpty()) return;
                String ip = Util.unsimplifyIP(c);
                Cliente.setIpAddress(ip);
            }
            ProgressMonitor pm = new ProgressMonitor(this, "Esperando conexión...", null, 0, 20);
            final boolean[] connected = {false};
            Thread t1 = new Thread(() -> {
                new GameWindow(gameType);
                connected[0] = true;
                pm.close();
                exit();
            });
            Thread t2 = new Thread(() -> {
                try {
                    int i = 0;
                    while (i <= 20) {
                        Thread.sleep(1000);
                        pm.setProgress(i);
                        i++;
                        if (connected[0]) break;
                    }
                    pm.close();
                    if (!connected[0]) {
                        t1.interrupt();
                        Util.warn("No se ha recibido la conexión!", InicioWindow.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            t1.start();
            t2.start();
        } catch (Exception e) {
            e.printStackTrace();
            Util.error(e.toString(), this);
        }
    }

    /**
     * Abre el {@link CreateWindow creador de laberintos} y cierra la ventana actual.
     */
    private void createLab() {
        setVisible(false);
        new CreateWindow();
        dispose();
    }

    /**
     * Abre la ventana de {@link OptionsWindow opciones}.
     */
    private void options() {
        new OptionsWindow();
    }

    /**
     * Destruye la ventana actual.
     */
    private void exit() {
        setVisible(false);
        dispose();
    }


}
