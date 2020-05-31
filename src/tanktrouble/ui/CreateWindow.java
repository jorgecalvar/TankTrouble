package tanktrouble.ui;

import tanktrouble.labcreator.CreatorCanvas;
import tanktrouble.labcreator.LabManager;
import tanktrouble.misc.Util;
import tanktrouble.reflection.Lab;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Esta clase representa a la ventana en la que el usuario diseñará objetos {@link Lab}.
 */
public class CreateWindow extends JFrame {

    /**
     * Mapa con todas las dimensiónes posibles
     */
    public static final Map<String, Dimension> TAMANOS = Map.of("1200x600", new Dimension(1200, 600),
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
    private JButton btnImportar;
    private JButton btnExportar;
    private JButton btnAyuda;
    private JButton btnSalir;

    /**
     * Obejto CreatorCanvas, el elemnto principal de la ventana.
     */
    private CreatorCanvas canvas;


    /**
     * Inicia la venta y sus componentes.
     */
    public CreateWindow() {
        setTitle("Tank Trouble | Crear Laberinto");
        setLayout(new BorderLayout());

        //Panel norte

        cbTamano = new JComboBox(TAMANOS.keySet().toArray());
        btnAplicar = new JButton("Aplicar");
        btnPintar = new JToggleButton("Pintar");
        btnBorrar = new JToggleButton("Borrar");
        btnCrearTanque = new JToggleButton("Añadir");
        btnBorrarTanque = new JToggleButton("Borrar");
        btnBorrarTodo = new JButton("Borrar todo");
        btnImportar = new JButton("Importar");
        btnExportar = new JButton("Exportar");
        btnAyuda = new JButton("Ayuda");
        btnSalir = new JButton("Volver al Inicio");

        btnAplicar.addActionListener(e -> aplicarClicked());
        btnPintar.addActionListener(e -> pintarClicked());
        btnBorrar.addActionListener(e -> borrarClicked());
        btnCrearTanque.addActionListener(e -> crearTanqueClicked());
        btnBorrarTanque.addActionListener(e -> borrarTanqueClicked());
        btnBorrarTodo.addActionListener(e -> borrarTodoClicked());
        btnImportar.addActionListener(e -> importarClicked());
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
        pNorte.add(btnImportar);
        pNorte.add(btnExportar);
        pNorte.add(new JLabel(" | "));
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
        setIconImage(Util.getIcono());
        setVisible(true);
    }

    /**
     * Llamado cuando se ha hecho click en el botón Aplicar
     */
    private void aplicarClicked() {
        canvas.aplicarDimension(TAMANOS.get(cbTamano.getSelectedItem()));
    }

    /**
     * Si está seleccionado, cambia el estado del canvas a STATE_PINTAR y desselecciona los demás
     * {@link JToggleButton}. Si no está selecciondao, cambia el estado del canvas a {@link CreatorCanvas#STATE_NONE}.
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
     * Si esta seleccionado, cambia el estado del {@link CreatorCanvas} a {@link CreatorCanvas#STATE_BORRAR} y
     * desselecciona los demas {@link JToggleButton}. Si no esta selecciondao, cambia el estado del
     * {@link CreatorCanvas} a {@link CreatorCanvas#STATE_NONE}.
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
     * Si esta seleccionado, cambia el estado del {@link CreatorCanvas} a {@link CreatorCanvas#STATE_CREAR_TANQUE}
     * y desselecciona los demas {@link JToggleButton}. Si no esta selecciondao, cambia el estado del
     * {@link CreatorCanvas} a {@link CreatorCanvas#STATE_NONE}.
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
     * Si esta seleccionado, cambia el estado del {@link CreatorCanvas} a {@link CreatorCanvas#STATE_BORRAR_TANQUE} y
     * desselecciona los demas {@link JToggleButton}. Si no esta selecciondao, cambia el estado del {@link CreatorCanvas}
     * a {@link CreatorCanvas#STATE_NONE}:
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
     * Borra todos los objetos del {@link Lab}, llamado al método correspondiente de {@link CreatorCanvas}.
     */
    private void borrarTodoClicked() {
        canvas.borrarTodo();
    }

    /**
     * Lee un archivo que contiene un {@link Lab} en formato json.
     */
    private void importarClicked() {
        String name = input("Introduzca el nombre del archivo a leer.\n" +
                "Debe estar en una carpeta llamada 'labs' en el directorio donde está el juego.\n" +
                "Un ejemplo de nombre es: lab5.json");
        if (name == null) return;
        if (name.isEmpty() || !name.substring(name.length() - 5).equals(".json")) {
            warn("No ha introducido un nombre válido.");
            return;
        }
        Lab lab = LabManager.readExternal("labs/" + name, LabManager.CONVERT_TYPE_CREATOR);
        canvas.borrarTodo();
        assert lab != null;
        canvas.setDimension(lab.getSize());
        canvas.setParedes(lab.getParedes());
        canvas.setTanques(lab.getPosicionTanques());
        canvas.repaint();
    }

    /**
     * Guarda el objeto {@link Lab} que esta dibujado. Avisa al usuario tanto si ha habido algún error como si se realiza
     * correctamente.
     */
    private void exportarClicked() {
        try {
            Lab lab = canvas.createLab();
            String name = input("Introduzca el nombre del archivo.\n" +
                    "La extensión debe ser .json.\n" +
                    "Deje el campo en blanco para poner el nombre por defecto");
            if (name == null) return;
            if (name.isEmpty() || !name.substring(name.length() - 5).equals(".json")) name = null;
            if (LabManager.write(lab, name))
                info("Se ha exportado correctamente!");
            else
                warn("Ha ocurrido un error en la exportación.");
        } catch (UnsupportedOperationException e) {
            warn(e.getMessage());
        }
    }

    /**
     * Llama al método {@link #showAyuda()}
     */
    private void ayudaClicked() {
        showAyuda();
    }

    /**
     * Vuelve a la pestaña principal del juego: {@link InicioWindow}.
     */
    private void salirClicked() {
        setVisible(false);
        new InicioWindow();
        dispose();
    }

    /**
     * Muestra un dialogo con ayuda al usuario
     */
    public void showAyuda() {
        String sb = "¿Como usar el creador de laberintos?\n" +
                "1. Elija el tamaño del laberinto que desea crear y presione Aplicar.\n" +
                "2. Presione Pintar o Borrar y haga click en los puntos para formar paredes.\n" +
                "3. Utilice Añadir Tanque para elegir dónde apareceran los dos tanque al inicio de la partida.\n" +
                "4. Cuando haya terminado presione Exportar para guardar el laberinto creado.\n" +
                "El archivo guardado aparecerá dentro de una carpeta llamada 'labs' en la misma localización donde\n" +
                "está almacenado el juego.";
        info(sb);
    }

    /**
     * Muestra un dialogo de información
     *
     * @param txt mensaje a mostrar
     */
    public void info(String txt) {
        Util.info(txt, this);
    }

    /**
     * Muestra un dialogo de aviso
     *
     * @param txt mensaje a mostrar
     */
    public void warn(String txt) {
        Util.warn(txt, this);
    }

    /**
     * Solicita información al usuario.
     *
     * @param txt mensaje a mostrar
     * @return información introducida por el usuario
     */
    public String input(String txt) {
        return Util.input(txt, this);
    }
}
