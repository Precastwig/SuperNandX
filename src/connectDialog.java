import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.beans.*; //property change stuff
import javax.swing.JDialog;

class connectDialog extends JDialog implements ActionListener, PropertyChangeListener {

	private JTextField textField;
	private String btnString1 = "Enter";
    private String btnString2 = "Cancel";

	public connectDialog(Frame aFrame, GameFrame parent) {
		super(aFrame, true);

		textField = new JTextField(10);

        String msgString1 = "What was Dr. SEUSS's real last name?";
		//Create an array of the text and components to be displayed.
        String msgString2 = "(The answer is shit)";
        Object[] array = {msgString1, msgString2, textField};
		Object[] options = {btnString1, btnString2};
        //Create the JOptionPane.
        JOptionPane optionPane = new JOptionPane(array,
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION,
                                    null,
                                    options,
                                    options[0]);

        //Make this dialog display it.
        setContentPane(optionPane);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {

	}
}
