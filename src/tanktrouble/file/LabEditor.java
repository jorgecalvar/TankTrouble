package tanktrouble.file;

import org.json.JSONArray;
import org.json.JSONObject;
import tanktrouble.misc.Util;
import tanktrouble.reflection.Lab;
import tanktrouble.reflection.Pared;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class LabEditor {


    /**
     * Devuelve el objeto Lab número n almacenado en los achivos internos del programa
     *
     * @param n el número de elemento a devolver (mayor 1 y 10)
     * @return objeto Lab decodificado
     */
    public static Lab readInternal(int n) {
        n = 1; //Temporal, while we only have 1 lab
        try {
            String name = "labs/lab" + n + ".json";
            InputStream stream = LabEditor.class.getClassLoader().getResourceAsStream(name);
            if (stream == null) throw new NullPointerException("Problem reading internal files");
            String s = Util.readInputStream(stream);
            return convertToLab(new JSONObject(s));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Genera un archivo Json a partir de un objeto lab y lo escribe en un archivo
     *
     * @param lab      objeto lab a guardar
     * @param fileName archivo donde se guardará
     */
    public static void write(Lab lab, String fileName) {

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

        System.out.println(o);


        try {
            FileWriter f = new FileWriter(fileName);
            f.write(o.toString());
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Lee un archivo externo codificado en JSON y lo convierte a un objeto Lab
     *
     * @param fileName archivo a leer
     * @return objeto Lab
     */
    public static Lab readExternal(String fileName) {
        String s;
        try {
            s = Files.readString(Path.of(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        JSONObject o = new JSONObject(s);
        return convertToLab(o);
    }

    /**
     * Convierte un objeto JSON codificado siguiente las instrucciones de esta clase a un objeto Lab
     *
     * @param o objeto JSON con la información
     * @return objeto Lab
     */
    private static Lab convertToLab(JSONObject o) {
        JSONObject size_o = (JSONObject) o.get("size");
        Dimension size = new Dimension((Integer) size_o.get("width"), (Integer) size_o.get("height"));

        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        double x_offset = (screen_size.getWidth() - size.getWidth()) / 2;
        double y_offset = (screen_size.getHeight() - size.getHeight()) / 2;

        JSONArray paredes_o = (JSONArray) o.get("paredes");
        List<Pared> paredes = new ArrayList<>();
        for (Object p_o : paredes_o) {
            JSONObject pared_o = (JSONObject) p_o;
            paredes.add(new Pared(new Point2D.Double((Integer) pared_o.get("x") + x_offset,
                    (Integer) pared_o.get("y") + y_offset),
                    (Integer) pared_o.get("longitud"),
                    pared_o.get("tipo").equals("horizontal") ? Pared.TIPO_HORIZONTAL : Pared.TIPO_VERTICAL));
        }

        return new Lab(size, paredes);
    }


}
