package tanktrouble.ui;

import tanktrouble.labcreator.LabManager;
import tanktrouble.misc.Sonido;
import tanktrouble.misc.Styler;
import tanktrouble.misc.Util;
import tanktrouble.reflection.Dibujo;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Ventana que permite configurar distintas opciones del juego.
 */
public class OptionsWindow extends JFrame {

    /**
     * Mapa con los diferntes estilos permitidos.
     */
    public static final Map<String, Integer> ESTILOS = Styler.getStyleList();

    /**
     * Mapa con los diferntes rendering permitidos.
     */
    public static final Map<String, Integer> RENDERING = Map.of("Suave", Dibujo.RENDERING_LIGHT,
            "Moderado", Dibujo.RENDERING_MODERATE,
            "Intenso", Dibujo.REDERING_INTENSE);

    /**
     * Mapa con las diferentes fuentes de {@link tanktrouble.reflection.Lab} permitidos.
     */
    public static final Map<String, Integer> SOURCES = Map.of("Aleatorio", LabManager.SOURCE_BOTH,
            "Interna", LabManager.SOURCE_INTERNAL,
            "Externa", LabManager.SOURCE_EXTERNAL);


    private JComboBox<String> cbEstilos;
    private JComboBox<String> cbRendering;
    private JComboBox<String> cbSource;
    private JCheckBox cbSonido;

    //TODO Add Option for sensibility when rotating
    //TODO Add Option to select default key

    /**
     * Inicializ la ventana de opciones.
     */
    public OptionsWindow() {

        //setLayout(new GridLayout(3, 1, 10, 5));
        setLayout(new BorderLayout());

        // NORTE: Titulo
        JPanel panelN = new JPanel(new FlowLayout());
        JLabel lblTitle = new JLabel("OPCIONES");
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 22));
        panelN.add(lblTitle);
        add(panelN, BorderLayout.NORTH);

        //CENTRO

        JPanel panelC = new JPanel();
        panelC.setLayout(new BoxLayout(panelC, BoxLayout.Y_AXIS));

        //Estilos
        JPanel pEstilo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pEstilo.add(new JLabel("Estilo:"));
        cbEstilos = new JComboBox(ESTILOS.keySet().toArray());
        selectPresentStyle();
        pEstilo.add(cbEstilos);
        panelC.add(pEstilo);

        //Rendering
        JPanel pRendering = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pRendering.add(new JLabel("Redering:"));
        cbRendering = new JComboBox(RENDERING.keySet().toArray());
        selectPresentRendering();
        pRendering.add(cbRendering);
        panelC.add(pRendering);

        //Fuente de Labs
        JPanel pSource = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pSource.add(new JLabel("Fuente de laberintos:"));
        cbSource = new JComboBox(SOURCES.keySet().toArray());
        selectPresentSource();
        pSource.add(cbSource);
        panelC.add(pSource);


        //Sonido
        JPanel pSonido = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pSonido.add(new JLabel("Sonido:"));
        cbSonido = new JCheckBox();
        selectPresentSonido();
        pSonido.add(cbSonido);
        panelC.add(pSonido);


        add(panelC, BorderLayout.CENTER);


        // SUR: Aplicar
        JPanel panelS = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        panelS.add(btnAceptar);
        panelS.add(btnCancelar);
        add(panelS, BorderLayout.SOUTH);

        //Listeners
        btnAceptar.addActionListener(e -> {
            applyChanges();
            dispose();
        });
        btnCancelar.addActionListener(e -> dispose());


        pack();

        //setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void selectPresentStyle() {
        int estiloActual = Styler.getStyle();
        for (String name : ESTILOS.keySet())
            if (ESTILOS.get(name) == estiloActual)
                cbEstilos.setSelectedItem(name);
    }

    private void selectPresentRendering() {
        int renderingActual = Dibujo.getRendering();
        for (String name : RENDERING.keySet())
            if (RENDERING.get(name) == renderingActual)
                cbRendering.setSelectedItem(name);
    }

    private void selectPresentSource() {
        int sourceActual = LabManager.getSource();
        for (String name : SOURCES.keySet())
            if (SOURCES.get(name) == sourceActual)
                cbSource.setSelectedItem(name);
    }

    private void selectPresentSonido() {
        if (Sonido.getState() == Sonido.STATE_ON)
            cbSonido.setSelected(true);
        else
            cbSonido.setSelected(false);
    }

    private void applyChanges() {
        //Estilo
        Styler.setStyle(ESTILOS.get(cbEstilos.getSelectedItem()));
        //Rendering
        int rendering = RENDERING.get(cbRendering.getSelectedItem());
        Dibujo.setRendering(rendering);
        if (rendering == Dibujo.REDERING_INTENSE)
            Util.warn("Es muy probable que el Rendering intenso cause lag!", this);
        //Fuente de Labs
        LabManager.setSource(SOURCES.get(cbSource.getSelectedItem()));
        //Sonido
        Sonido.setState(cbSonido.isSelected() ? Sonido.STATE_ON : Sonido.STATE_OFF);
    }


}
