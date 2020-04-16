package tanktrouble.ui;

import tanktrouble.misc.Util;

import javax.swing.*;
import java.awt.*;

public class InicioWindow extends JFrame {

    public static final Color COLOR_BG = new Color(0x264653);
    public static final Font FONT_TITLE = new Font("Courier New", Font.BOLD, 35);
    public static final Font FONT_BUTTONS = new Font("Helvetica", Font.BOLD, 25);

    public InicioWindow() {

        setTitle("Tank Trouble");

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 30));


        JLabel lblTitle = new JLabel("TANK TROUBLE");
        JButton btnPlayers = new JButton("2 Jugadores");
        JButton btnComputer = new JButton("Jugador vs Ordenador");
        JButton btnCreate = new JButton("Crear Laberinto");
        JButton btnOptions = new JButton("Opciones");

        styleTitle(lblTitle);
        styleButton(btnPlayers);
        styleButton(btnComputer);
        styleButton(btnCreate);
        styleButton(btnOptions);

        btnPlayers.addActionListener(e -> playerVsPlayer());
        btnComputer.addActionListener(e -> playerVsComputer());
        btnCreate.addActionListener(e -> createLab());
        btnOptions.addActionListener(e -> options());

        panel.add(lblTitle);
        panel.add(btnPlayers);
        panel.add(btnComputer);
        panel.add(btnCreate);
        panel.add(btnOptions);

        panel.setBackground(COLOR_BG);
        panel.setPreferredSize(new Dimension(390, 438));

        add(panel);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
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

    private static void styleButton(JButton btn) {
        btn.setFont(FONT_BUTTONS);
        btn.setMargin(new Insets(10, 10, 10, 10));
        btn.setPreferredSize(new Dimension(300, 50));
        btn.setBackground(new Color(0x2a9d8f));
        btn.setForeground(new Color(0x0b032d));

    }

    private void playerVsPlayer() {
        try {
            new GameWindow();
            exit();
        } catch (Exception e) {
            Util.error(e.getMessage(), this);
        }
    }

    private void playerVsComputer() {
        Util.warn("Not supported yet!", this);
    }

    private void createLab() {
        setVisible(false);
        new CreateWindow();
        dispose();
    }

    private void options() {
        new OptionsWindow();
    }

    private void exit() {
        setVisible(false);
        dispose();
    }


}
