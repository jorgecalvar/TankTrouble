package tanktrouble.control;

import tanktrouble.reflection.Tanque;
import tanktrouble.ui.Dibujo;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class ComputerController extends TanqueController implements Runnable {

    private Dibujo dibujo;
    private boolean hitMyself = false;
    private boolean hitOponent = false;
    private boolean done;

    public ComputerController(Tanque tanque, Dibujo dibujo) {
        super(tanque);
        this.dibujo = dibujo;
        setDone(false);
        new Thread(this).start();
    }

    public void hitMyself() {
        hitMyself = true;
    }

    public void hitOponent() {
        hitOponent = true;
    }

    @Override
    public void destroy() {
        super.destroy();
        setDone(true);
    }

    private void setDone(boolean done) {
        this.done = done;
        try {
            FileWriter fw = new FileWriter("rl/done.txt");
            String s = done ? "1" : "0";
            fw.write(s);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (!done) {

            try {

                String line = Files.readString(Path.of("rl/action.txt"));

                int i = Integer.parseInt(line);

                System.out.println(i);

                forward = false;
                back = false;
                right = false;
                left = false;
                shoot = false;

                if (i == 0)
                    forward = true;
                else if (i == 1)
                    back = true;
                else if (i == 2)
                    right = true;
                else if (i == 3)
                    left = true;
                else if (i == 4)
                    shoot = true;

                Thread.sleep(50);

                List<Tanque> tanques = dibujo.getTanques();

                StringBuilder sb = new StringBuilder();
                sb.append("[");

                double offset_x = dibujo.getLab().getBounds2D().getX();
                double offset_y = dibujo.getLab().getBounds2D().getY();
                for (Tanque t : tanques) {

                    double x = t.getPosicion().getX() - offset_x;
                    if (x < 0) x = 0;
                    if (x >= 1200) x = 1199;

                    double y = t.getPosicion().getY() - offset_y;
                    if (y < 0) y = 0;
                    if (y >= 600) y = 599;

                    double theta = t.getTheta();
                    if (theta < 0 || theta >= 2 * Math.PI)
                        throw new IllegalStateException("Theta cannot be smaller than 0 " +
                                "or higher or equal than 2PI: " + theta);

                    sb.append("[").append(x).append(", ").append(y).append(", ").append(theta).append("],");
                }
                sb.replace(sb.length() - 1, sb.length(), "");
                sb.append("]");

                line = sb.toString();

                FileWriter fw = new FileWriter("rl/observation.txt");
                fw.write(line);
                fw.close();

                fw = new FileWriter("rl/reward.txt");
                if (hitOponent)
                    fw.write("10");
                else if (hitMyself)
                    fw.write("-10");
                else
                    fw.write("0");
                fw.close();


                Thread.sleep(50);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}
