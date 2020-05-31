package tanktrouble.reflection;

import org.json.JSONArray;
import org.json.JSONObject;
import tanktrouble.misc.Util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Este objeto se utiliza cuando el tipo de juego es {@link tanktrouble.ui.GameWindow#PLAYER_VS_INTERNET} o
 * {@link tanktrouble.ui.GameWindow#PLAYER_VS_INTERNET_CLIENTE}. En caso de ser {@link tanktrouble.net.Servidor} esta
 * clase codifica el estado del juego (posicion de los {@link Tanque tanques}, {@link Bala balas} y
 * {@link Board marcador}) y se lo envia al {@link tanktrouble.net.Cliente}. En caso de ser {@link tanktrouble.net.Cliente}
 * decodifica esta informacion y la aplica al {@link Dibujo}.
 */
public class Environment {

    private volatile Dibujo dibujo;
    private JSONObject estado;

    /**
     * Inicializa el {@link Environment}
     *
     * @param dibujo {@link Dibujo} correspondiente
     */
    public Environment(Dibujo dibujo) {
        setDibujo(dibujo);
    }

    /**
     * Configura el valor de {@link #dibujo}.
     *
     * @param dibujo nuevo valor de {@link #dibujo}.
     */
    public void setDibujo(Dibujo dibujo) {
        this.dibujo = dibujo;
    }

    /**
     * Actualiza el valor de la variable {@link JSONObject} que contiene el estado del {@link Dibujo}.
     */
    public void actualizar() {
        double x_offset = dibujo.getLab().getBounds2D().getMinX();
        double y_offset = dibujo.getLab().getBounds2D().getMinY();
        JSONObject o = new JSONObject();
        // Estado
        o.put("active", dibujo.isActive());
        // Número de lab
        o.put("lab", dibujo.getInternalLabN());
        // Tanques
        JSONArray tanques = new JSONArray();
        for (Tanque t : dibujo.getTanques()) {
            Point2D posicion = t.getPosicion();
            double theta = t.getTheta();
            JSONObject tanque = new JSONObject();
            tanque.put("x", posicion.getX() - x_offset);
            tanque.put("y", posicion.getY() - y_offset);
            tanque.put("theta", theta);
            tanques.put(tanque);
        }
        o.put("tanques", tanques);
        // Balas
        JSONArray balas = new JSONArray();
        for (Bala b : dibujo.getBalasController().getBalas()) {
            Point2D posicion = b.getPosicion();
            double theta = b.getTheta();
            JSONObject bala = new JSONObject();
            bala.put("x", posicion.getX() - x_offset);
            bala.put("y", posicion.getY() - y_offset);
            bala.put("theta", theta);
            balas.put(bala);
        }
        o.put("balas", balas);
        // Board
        o.put("p1", dibujo.getBoard().getLivesPlayer1());
        o.put("p2", dibujo.getBoard().getLivesPlayer2());

        // TODO sonido

        // Acutlizamos
        estado = o;
    }

    /**
     * Configura el valor de la variable en formato {@link JSONObject} segun lo recibido por el parametro.
     *
     * @param s texto en formato JSON.
     */
    public void actualizar(String s) {
        if (s.substring(0, 1).equals("{"))
            estado = new JSONObject(s);
    }

    /**
     * Configura el {@link Dibujo} para representar el estado de este {@link Environment}.
     */
    public void actualizarUI() {
        // System.out.println("Actualizando UI...");
        // System.out.println("Estado:");
        // System.out.println(estado.toString());

        // Active
        boolean active = (Boolean) estado.get("active");
        if (!active) {
            System.out.println("COMENZANDO GAMEOVER");
            dibujo.getBoard().gameOver();
        } else {

            // Lab
            int l = (int) estado.get("lab");
            // System.out.println("Lab recibido: "+l);
            dibujo.setLabN((int) estado.get("lab"));

            // Offset
            if (dibujo == null) System.out.println("DIBUJO ES NULL!");
            if (dibujo.getLab() == null) System.out.println("LAB ES NUL!!");
            if (dibujo.getLab().getBounds2D() == null) System.out.println("BOUNDS ES NULL");
            double x_offset = dibujo.getLab().getBounds2D().getMinX();
            double y_offset = dibujo.getLab().getBounds2D().getMinY();

            // Tanques
            JSONArray tanques = (JSONArray) estado.get("tanques");

            Tanque t1 = dibujo.getTanque1();
            JSONObject t1o = (JSONObject) tanques.get(0);
            t1.setPosicion(new Point2D.Double((Double) t1o.get("x") + x_offset, (Double) t1o.get("y") + y_offset));
            t1.setTheta(Util.decodeDouble(t1o.get("theta")));

            Tanque t2 = dibujo.getTanque2();
            JSONObject t2o = (JSONObject) tanques.get(1);
            t2.setPosicion(new Point2D.Double((Double) t2o.get("x") + x_offset, (Double) t2o.get("y") + y_offset));
            t2.setTheta(Util.decodeDouble(t2o.get("theta")));

            // Balas
            List<Bala> balas = new ArrayList<>();
            JSONArray balas_o = (JSONArray) estado.get("balas");
            for (Object o : balas_o) {
                JSONObject bo = (JSONObject) o;
                Bala b = new Bala(new Point2D.Double((Double) bo.get("x") + x_offset, (Double) bo.get("y") + y_offset),
                        Util.decodeDouble(bo.get("theta")), dibujo);
                balas.add(b);
            }
            dibujo.getBalasController().setBalas(balas);

        }

        //Board
        dibujo.getBoard().setLivesPlayer1((Integer) estado.get("p1"));
        dibujo.getBoard().setLivesPlayer2((Integer) estado.get("p2"));

    }

    /**
     * Convierte el {@link Environment} a texto en formato JSON.
     *
     * @return {@link Environment} en formato JSON.
     */
    @Override
    public String toString() {
        actualizar();
        return estado.toString();
    }


}
