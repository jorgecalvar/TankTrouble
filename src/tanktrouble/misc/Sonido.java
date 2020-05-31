package tanktrouble.misc;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

/**
 * Reproduce sonido durante el juego.
 */
public class Sonido {

    /**
     * Sonido de disparo
     */
    public static final int GUNSHOOT = 1;

    /**
     * Sonido de gong chino
     */
    public static final int CHINESE_GONG = 2;
    //TODO Add new sounds

    /**
     * Sonido activado
     */
    public static final int STATE_ON = 1;
    /**
     * Sonido no activado
     */
    public static final int STATE_OFF = 2;
    /**
     * Carpeta donde se encuentran los sonidos
     */
    public static final String DIR = "audio/";
    private static int state = STATE_ON;

    public static void main(String[] args) {
        Sonido s = new Sonido();
        System.out.println("NOW");
        s.playSound(CHINESE_GONG);
        try {
            Thread.sleep(2000);
        } catch (Exception ignore) {

        }

    }

    private static void destroyIn(long milliseconds, Clip clip) {
        new Thread(() -> {
            try {
                Thread.sleep(milliseconds);
                clip.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Obtiene el nombre del archivo correspondiente al sonido pasado como parametro.
     *
     * @param sound sonido a buscar
     * @return nombre del archivo
     */
    public static String getFileName(int sound) {
        switch (sound) {
            case GUNSHOOT:
                return "gunshoot.wav";
            case CHINESE_GONG:
                return "chinese-gong.wav";
        }
        throw new IllegalArgumentException("Illegal sound: " + sound);
    }

    /**
     * Obtiene la posicion inicial, en milisegundos, donde debera empezar a sonar el audio pasado como parametro.
     *
     * @param sound sonido a buscar
     * @return posicion inicial en segundos
     */
    public static int getStartPosition(int sound) {
        switch (sound) {
            case GUNSHOOT:
                return 50;
            case CHINESE_GONG:
                return 0;

        }
        throw new IllegalArgumentException("Illegal sound: " + sound);
    }

    /**
     * Obtiene el tiempo, en milisegundos, que debera sonar un audio, es decir, el tiempo a partir del cual se debera
     * parar..
     *
     * @param sound sonido a buscar
     * @return duracion el milisegundos
     */
    public static int getDuration(int sound) {
        switch (sound) {
            case GUNSHOOT:
                return 1000;
            case CHINESE_GONG:
                return 3000;
        }
        throw new IllegalArgumentException("Illegal sound: " + sound);
    }

    /**
     * Devuelve el estado actual
     *
     * @return estado actual
     */
    public static int getState() {
        return state;
    }

    /**
     * Configura el estado
     *
     * @param _state nuevo valor de estado
     */
    public static void setState(int _state) {
        state = _state;
    }

    /**
     * Reproduce un sonido por los altavoces del dispositivo.
     *
     * @param sound sonido para poner
     */
    public void playSound(int sound) {
        if (state == STATE_OFF) return;
        try {
            URL url = Sonido.class.getClassLoader().getResource(DIR + getFileName(sound));
            assert url != null;
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.setMicrosecondPosition(getStartPosition(sound) * 1000);
            clip.start();
            destroyIn(getDuration(sound), clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
