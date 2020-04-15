package tanktrouble.ui;

import tanktrouble.misc.Styler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;

public class OptionsWindow extends JFrame {

    private JComboBox<String> estilo;

    public OptionsWindow() {

        setLayout(new GridLayout(3, 1, 10, 5));

        // (1,1) Titulo
        JPanel panel1 = new JPanel(new FlowLayout());
        JLabel lblTitle = new JLabel("OPCIONES");
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 22));
        panel1.add(lblTitle);
        add(panel1);

        // (2,1) Estilo
        JPanel panel2 = new JPanel(new FlowLayout());
        panel2.add(new JLabel("Estilo:"));
        Map<String, Integer> estilos = Styler.getStyleList();
        String[] estiloString = new String[estilos.size()];
        estilos.keySet().toArray(estiloString);
        estilo = new JComboBox<>(estiloString);
        panel2.add(estilo);
        add(panel2);


        // (?,1) Aplicar
        JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        panel3.add(btnAceptar);
        panel3.add(btnCancelar);
        add(panel3);

        //Listeners
        btnAceptar.addActionListener((ActionListener) e -> {
            Styler.setDefaultStyle(Styler.getStyleList().get(estilo.getSelectedItem()));
            dispose();
        });
        btnCancelar.addActionListener(e -> dispose());


        pack();

        //setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public static void main(String[] args) {
        new OptionsWindow();
    }

}
