package tanktrouble.misc;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class Sonido {

    public static final int GUNSHOOT = 1;
    public static final int CHINESE_GONG = 2;
    /**
     * Sonido activado
     */
    public static final int STATE_ON = 1;
    /**
     * Sonido no activado
     */
    public static final int STATE_OFF = 2;
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
     * Obtiene el nombre del archivo correspondiente al sonido pasado como parámetro.
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
     * Obtiene la posición inicial, en milisegundos, donde deberá empezar a sonar el audio pasado como parámetro.
     *
     * @param sound sonido a buscar
     * @return posición inicial en segundos
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
     * Obtiene el tiempo, en milisegundos, que deberá sonar un audio, es decir, el tiempo a partir del cual se deberá
     * parar..
     *
     * @param sound sonido a buscar
     * @return duración el milisegundos
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

    public static int getState() {
        return state;
    }

    public static void setState(int _state) {
        state = _state;
    }

    /**
     * Pone un sonido por los altavoces del dispositivo.
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
