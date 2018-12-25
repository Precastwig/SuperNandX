import javax.swing.*;
import java.awt.*;

/**
 * Created by Precastwig on 10/03/2017.
 */
public class main {

    static JFrame frame;
    static GameFrame game;

    public static void main(String[] args) {
        frame = new JFrame("Super Noughts and Crosses");
        frame.setSize(600,700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        // Create the turn label and set it up
        ResponsiveLabel turnlabel = new ResponsiveLabel();
        turnlabel.setFont(turnlabel.getFont().deriveFont(25.0f));
        turnlabel.setText("X to start");
        turnlabel.setHorizontalAlignment(SwingConstants.CENTER);
        turnlabel.setOpaque(true);
        turnlabel.setBackground(Color.WHITE);

        //Create the game frame and link to the turn label by passing into constructor
        game = new GameFrame(frame, turnlabel);

        // Create menu
        JMenuBar menu = new JMenuBar();
        //Columns
        JMenu col = new JMenu("Game");
        menu.add(col);
        JMenuItem item = new JMenuItem("New game");
        item.addActionListener(game);
        col.add(item);

        col = new JMenu("Online");
        menu.add(col);
        item = new JMenuItem("Connect");
        col.add(item);
        // col.addOnClickListener()


        //Add objects to frame
        frame.setJMenuBar(menu);
        frame.add(game,BorderLayout.CENTER);
        frame.add(turnlabel, BorderLayout.PAGE_END);
        //Make see
        frame.setVisible(true);
    }
}
