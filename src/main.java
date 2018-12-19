import javax.swing.*;
import java.awt.*;

/**
 * Created by Precastwig on 10/03/2017.
 */
public class main {

    static JFrame frame;

    private static void gamescreen() {

    }

    public static void main(String[] args) {
        frame = new JFrame("Super Noughts and Crosses");
        frame.setSize(600,700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GameFrame main = new GameFrame();
        frame.add(main);
        //frame.pack();
        frame.setVisible(true);
    }
}
