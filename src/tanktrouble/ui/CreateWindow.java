package tanktrouble.ui;

import tanktrouble.labcreator.CreatorCanvas;
import tanktrouble.labcreator.LabEditor;
import tanktrouble.reflection.Lab;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Esta clase representa a la ventana en la que el usuario diseñará objetos Lab.
 */
public class CreateWindow extends JFrame {

    /**
     * Mapa con todas las dimensiónes posibles
     */
    public static final Map<String, Dimension> tamanos = Map.of("1200x600", new Dimension(1200, 600),
            "1000x600", new Dimension(1000, 600),
            "800x600", new Dimension(800, 600),
            "800x400", new Dimension(800, 400));


    private JComboBox<String> cbTamano;
    private JButton btnAplicar;
    private JToggleButton btnPintar;
    private JToggleButton btnBorrar;
    private JToggleButton btnCrearTanque;
    private JToggleButton btnBorrarTanque;
    private JButton btnBorrarTodo;
    private JButton btnExportar;
    private JButton btnAyuda;
    private JButton btnSalir;

    /**
     * Obejto CreatorCanvas, el elemnto principal de la ventana.
     */
    private CreatorCanvas canvas;


    public CreateWindow() {
        setTitle("Tank Trouble | Crear Laberinto");
        setLayout(new BorderLayout());

        //Panel norte

        cbTamano = new JComboBox(tamanos.keySet().toArray());
        btnAplicar = new JButton("Aplicar");
        btnPintar = new JToggleButton("Pintar");
        btnBorrar = new JToggleButton("Borrar");
        btnCrearTanque = new JToggleButton("Añadir");
        btnBorrarTanque = new JToggleButton("Borrar");
        btnBorrarTodo = new JButton("Borrar todo");
        btnExportar = new JButton("Exportar");
        btnAyuda = new JButton("Ayuda");
        btnSalir = new JButton("Volver al Inicio");

        btnAplicar.addActionListener(e -> aplicarClicked());
        btnPintar.addActionListener(e -> pintarClicked());
        btnBorrar.addActionListener(e -> borrarClicked());
        btnCrearTanque.addActionListener(e -> crearTanqueClicked());
        btnBorrarTanque.addActionListener(e -> borrarTanqueClicked());
        btnBorrarTodo.addActionListener(e -> borrarTodoClicked());
        btnExportar.addActionListener(e -> exportarClicked());
        btnAyuda.addActionListener(e -> ayudaClicked());
        btnSalir.addActionListener(e -> salirClicked());


        JPanel pNorte = new JPanel(new FlowLayout());

        pNorte.add(new JLabel("Tamaño:"));
        pNorte.add(cbTamano);
        pNorte.add(btnAplicar);
        pNorte.add(new JLabel(" | "));
        pNorte.add(new JLabel("Pared: "));
        pNorte.add(btnPintar);
        pNorte.add(btnBorrar);
        pNorte.add(new JLabel(" | "));
        pNorte.add(new JLabel("Tanque: "));
        pNorte.add(btnCrearTanque);
        pNorte.add(btnBorrarTanque);
        pNorte.add(new JLabel(" | "));
        pNorte.add(btnBorrarTodo);
        pNorte.add(new JLabel(" | "));
        pNorte.add(btnExportar);
        pNorte.add(btnAyuda);
        pNorte.add(btnSalir);

        //Panel centro
        JPanel pCentro = new JPanel(new FlowLayout());
        canvas = new CreatorCanvas(this);
        pCentro.add(canvas);

        //We add panels
        add(pNorte, BorderLayout.NORTH);
        add(pCentro, BorderLayout.CENTER);

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Llamado cuando se ha hecho click en el botón Aplicar
     */
    private void aplicarClicked() {
        canvas.aplicarDimension(tamanos.get(cbTamano.getSelectedItem()));
    }

    /**
     * Si está seleccionado, cambia el estado del canvas a STATE_PINTAR y desselecciona los demás
     * JToggleButton. Si no está selecciondao, cambia el estado del canvas a STATE_NONE:
     */
    private void pintarClicked() {
        if (btnPintar.isSelected()) {
            canvas.setState(CreatorCanvas.STATE_PINTAR);
            btnBorrar.setSelected(false);
            btnCrearTanque.setSelected(false);
            btnBorrarTanque.setSelected(false);
        } else
            canvas.setState(CreatorCanvas.STATE_NONE);
    }

    /**
     * Si está seleccionado, cambia el estado del canvas a STATE_BORRAR y desselecciona los demás
     * JToggleButton. Si no está selecciondao, cambia el estado del canvas a STATE_NONE:
     */
    private void borrarClicked() {
        if (btnBorrar.isSelected()) {
            canvas.setState(CreatorCanvas.STATE_BORRAR);
            btnPintar.setSelected(false);
            btnCrearTanque.setSelected(false);
            btnBorrarTanque.setSelected(false);
        } else
            canvas.setState(CreatorCanvas.STATE_NONE);

    }

    /**
     * Si está seleccionado, cambia el estado del canvas a STATE_CREAR_TANQUE y desselecciona los demás
     * JToggleButton. Si no está selecciondao, cambia el estado del canvas a STATE_NONE:
     */
    private void crearTanqueClicked() {
        if (btnCrearTanque.isSelected()) {
            canvas.setState(CreatorCanvas.STATE_CREAR_TANQUE);
            btnPintar.setSelected(false);
            btnBorrar.setSelected(false);
            btnBorrarTanque.setSelected(false);
        } else
            canvas.setState(CreatorCanvas.STATE_NONE);
    }

    /**
     * Si está seleccionado, cambia el estado del canvas a STATE_BORRAR_TANQUE y desselecciona los demás
     * JToggleButton. Si no está selecciondao, cambia el estado del canvas a STATE_NONE:
     */
    private void borrarTanqueClicked() {
        if (btnBorrarTanque.isSelected()) {
            canvas.setState(CreatorCanvas.STATE_BORRAR_TANQUE);
            btnPintar.setSelected(false);
            btnBorrar.setSelected(false);
            btnCrearTanque.setSelected(false);
        } else
            canvas.setState(CreatorCanvas.STATE_NONE);

    }

    /**
     * Borra todos los objetos del tanque, llamado al método correspondiente de CreatorCanvas.
     */
    private void borrarTodoClicked() {
        canvas.borrarTodo();
    }

    /**
     * Guarda el objeto Lab que está dibujado. Avisa al usuario tanto si ha habido algún error como si se realiza
     * correctamente.
     */
    private void exportarClicked() {
        try {
            Lab lab = canvas.createLab();
            if (LabEditor.write(lab))
                info("Se ha exportado correctamente!");
            else
                warn("Ha ocurrido un error en la exportación.");
        } catch (UnsupportedOperationException e) {
            warn(e.getMessage());
        }
    }

    /**
     * Llama al método showAyuda()
     */
    private void ayudaClicked() {
        showAyuda();
    }

    /**
     * Vuelve a la pestaña principal del juego.
     */
    private void salirClicked() {
        setVisible(false);
        new InicioWindow();
        dispose();
    }

    /**
     * Muestra un diálogo con ayuda al usuario
     */
    public void showAyuda() {
        String sb = "¿Como usar el creador de laberintos?\n" +
                "1. Elija el tamaño del laberinto que desea crear y presione Aplicar.\n" +
                "2. Presione Pintar o Borrar y haga click en los puntos para formar paredes.\n" +
                "3. Utiliza Añadir Tanque para elgir dónde apareceran los dos tanque al inicio de la partida.\n" +
                "4. Cuando haya terminado presione Exportar para guardar el laberinto creado.\n" +
                "El archivo guardado aparecerá dentro de una carpeta llamada Labs en la misma localización donde" +
                "está almacenado el juego.";
        info(sb);
    }

    /**
     * Muestra un diálogo de información
     *
     * @param txt mensaje a mostrar
     */
    public void info(String txt) {
        JOptionPane.showMessageDialog(this, txt, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un diálogo de aviso
     *
     * @param txt mensaje a mostrar
     */
    public void warn(String txt) {
        JOptionPane.showMessageDialog(this, txt, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

}
