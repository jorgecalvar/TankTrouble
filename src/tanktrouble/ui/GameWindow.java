package tanktrouble.ui;

import javax.swing.*;

public class GameWindow extends JFrame {

    private Dibujo dibujo;

    public GameWindow() {

        setTitle("Tank Trouble");

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        dibujo = new Dibujo();
        add(dibujo);

        pack();


        setVisible(true);

    }

    public static void main(String[] args) {
        new GameWindow();
    }

}
