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
        frame.setLayout(new BorderLayout());
        // Create menu
        JMenuBar menu = new JMenuBar();
        JMenu col = new JMenu("Online");
        menu.add(col);
        JMenuItem item = new JMenuItem("Connect");
        col.add(item);
        // col.addOnClickListener()

        // Create the turn label and set it up
        ResponsiveLabel turnlabel = new ResponsiveLabel();
        turnlabel.setFont(turnlabel.getFont().deriveFont(25.0f));
        turnlabel.setText("X to start");
        turnlabel.setHorizontalAlignment(SwingConstants.CENTER);
        turnlabel.setOpaque(true);
        turnlabel.setBackground(Color.WHITE);

        //Create the game frame and link to the turn label by passing into constructor
        GameFrame main = new GameFrame(turnlabel);

        //Add objects to frame
        frame.setJMenuBar(menu);
        frame.add(main,BorderLayout.CENTER);
        frame.add(turnlabel, BorderLayout.PAGE_END);
        //Make see
        frame.setVisible(true);
    }
}
