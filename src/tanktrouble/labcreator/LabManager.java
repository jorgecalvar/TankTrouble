package tanktrouble.labcreator;

import org.json.JSONArray;
import org.json.JSONObject;
import tanktrouble.misc.Util;
import tanktrouble.reflection.Lab;
import tanktrouble.reflection.Pared;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class LabManager {

    /**
     * Número de archivos con laboratorios en la memoria interna del programa.
     */
    public static final int NUM_LABS_INTERNAL = 11;

    /**
     * Se convertirá el Lab para que aparezca en el centro de la pantalla.
     */
    public static final int CONVERT_TYPE_GAME = 1;

    /**
     * Se convertirá el lab para ser editado con el labcreator, es decir, teniendo en cuenta el OFFSET.
     */
    public static final int CONVERT_TYPE_CREATOR = 2;

    /**
     * Los Labs será obtenidos tanto de los archivos interna como externa.
     */
    public static final int SOURCE_BOTH = 1;

    /**
     * Los Labs serán obtenidos de los archivos internos
     */
    public static final int SOURCE_INTERNAL = 2;

    /**
     * Los Labs serán obtenidos de los archivos externos
     */
    public static final int SOURCE_EXTERNAL = 3;


    public static int source = SOURCE_BOTH;

    /**
     * Devuelve el valor del attributo source
     *
     * @return de donde provienen los archivos
     */
    public static int getSource() {
        return source;
    }

    /**
     * Configura el valor de source, es decir, de donde provendrán los Labs
     *
     * @param _source fuente de los archivos
     */
    public static void setSource(int _source) {
        source = _source;
    }

    /**
     * Devuelve el objeto Lab número n almacenado en los achivos internos del programa
     *
     * @param n el número de elemento a devolver (mayor 1 y 10)
     * @return objeto Lab decodificado
     */
    public static Lab readInternal(int n, int convertType) {
        if (n > NUM_LABS_INTERNAL || n < 1)
            throw new IllegalArgumentException("Not valid n: " + n);
        try {
            String name = "labs/lab" + n + ".json";
            InputStream stream = LabManager.class.getClassLoader().getResourceAsStream(name);
            if (stream == null) throw new NullPointerException("Problem reading internal files");
            String s = Util.readInputStream(stream);
            return convertToLab(new JSONObject(s), convertType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Genera un archivo Json a partir de un objeto lab y lo escribe en un archivo, cuyo nombre se especifica como
     * parámetro. El archivo debe estar dentro de la carpeta labs, localizada en la ruta donde está el ejecutable.
     * Si el nombre es nulo, pondrá un por defecto.
     *
     * @param lab  objeto lab a guardar
     * @param name nombre del archivo
     */
    public static boolean write(Lab lab, String name) {

        //Generate Json
        JSONObject o = new JSONObject();

        Dimension size = lab.getSize();
        JSONObject size_o = new JSONObject();
        size_o.put("width", size.getWidth());
        size_o.put("height", size.getHeight());

        o.put("size", size_o);

        JSONArray paredes_o = new JSONArray();

        for (Pared pared : lab.getParedes()) {
            JSONObject p_o = new JSONObject();
            Point2D start = pared.getStart();
            p_o.put("x", start.getX());
            p_o.put("y", start.getY());
            p_o.put("longitud", pared.getLongitud());
            p_o.put("tipo", pared.getTipo() == Pared.TIPO_HORIZONTAL ? "horizontal" : "vertical");
            paredes_o.put(p_o);
        }

        o.put("paredes", paredes_o);

        JSONArray tanques_o = new JSONArray();

        for (Point2D tanque : lab.getPosicionTanques()) {
            JSONObject t_o = new JSONObject();
            t_o.put("x", tanque.getX());
            t_o.put("y", tanque.getY());
            tanques_o.put(t_o);
        }

        o.put("tanques", tanques_o);


        try {
            final String DIR = "labs";
            File dir = new File(DIR);
            if (!dir.exists())
                dir.mkdir();
            if (!dir.exists())
                return false;
            int i = 1;
            String file_name;
            if (name == null) {
                while (true) {
                    file_name = DIR + "/lab" + i + ".json";
                    File tmpFile = new File(file_name);
                    if (!tmpFile.exists())
                        break;
                    i++;
                }
            } else
                file_name = "labs/" + name;
            FileWriter f = new FileWriter(file_name);
            f.write(o.toString(2));
            f.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Lee un archivo externo codificado en JSON y lo convierte a un objeto Lab
     *
     * @param fileName archivo a leer
     * @return objeto Lab
     */
    public static Lab readExternal(String fileName, int convertType) {
        String s;
        try {
            s = Files.readString(Path.of(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        JSONObject o = new JSONObject(s);
        return convertToLab(o, convertType);
    }

    public static Lab readRandom(int convertType) {
        switch (source) {
            case SOURCE_BOTH:
                if (getNumLabsExternal() > 0 && Math.random() < 0.5)
                    return readRandomExternal(convertType);
            case SOURCE_INTERNAL:
                return readRandomInternal(convertType);
            case SOURCE_EXTERNAL:
                if (getNumLabsExternal() > 0)
                    return readRandomExternal(convertType);
                else
                    throw new UnsupportedOperationException("No hay ningún archivo externo!");
        }
        throw new IllegalStateException("Illegal source value: " + source);
    }

    public static Lab readRandomExternal(int convertType) {
        int n = Util.randomInteger(getNumLabsExternal());
        String name = "labs/lab" + n + ".json";
        return readExternal(name, convertType);
    }

    public static Lab readRandomInternal(int convertType) {
        return readInternal(Util.randomInteger(NUM_LABS_INTERNAL), convertType);
    }

    public static int getNumLabsExternal() {
        File tmpDir = new File("labs");
        if (!tmpDir.exists())
            return 0;
        int i = 1;
        while (true) {
            File tmp = new File(tmpDir + "/lab" + i + ".json");
            if (!tmp.exists())
                break;
            i++;
        }
        return i - 1;
    }

    /**
     * Convierte un objeto JSON codificado siguiente las instrucciones de esta clase a un objeto Lab
     *
     * @param o objeto JSON con la información
     * @return objeto Lab
     */
    private static Lab convertToLab(JSONObject o, int convertType) {
        JSONObject size_o = (JSONObject) o.get("size");
        Dimension size = new Dimension((Integer) size_o.get("width"), (Integer) size_o.get("height"));

        double x_offset;
        double y_offset;
        if (convertType == CONVERT_TYPE_GAME) {
            Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
            x_offset = (screen_size.getWidth() - size.getWidth()) / 2;
            y_offset = (screen_size.getHeight() - size.getHeight()) / 2;
        } else if (convertType == CONVERT_TYPE_CREATOR) {
            x_offset = CreatorCanvas.OFFSET;
            y_offset = CreatorCanvas.OFFSET;
        } else {
            throw new IllegalArgumentException("Illgal convert type: " + convertType);
        }


        JSONArray paredes_o = (JSONArray) o.get("paredes");
        List<Pared> paredes = new ArrayList<>();
        for (Object p_o : paredes_o) {
            JSONObject pared_o = (JSONObject) p_o;
            paredes.add(new Pared(new Point2D.Double((Integer) pared_o.get("x") + x_offset,
                    (Integer) pared_o.get("y") + y_offset),
                    (Integer) pared_o.get("longitud"),
                    pared_o.get("tipo").equals("horizontal") ? Pared.TIPO_HORIZONTAL : Pared.TIPO_VERTICAL));
        }

        JSONArray tanques_o = (JSONArray) o.get("tanques");
        List<Point2D> tanques = new ArrayList<>();
        for (Object t_o : tanques_o) {
            JSONObject tanque_o = (JSONObject) t_o;
            tanques.add(new Point2D.Double((Integer) tanque_o.get("x") + x_offset,
                    (Integer) tanque_o.get("y") + y_offset));
        }
        return new Lab(size, paredes, tanques);
    }


}
