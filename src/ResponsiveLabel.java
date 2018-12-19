import java.util.*;
import javax.swing.*;

class ResponsiveLabel extends JLabel implements labelListener {

		private String pre = "<html><div style='text-align: center;'>";
		private String post = "</div></html>";
		@Override
		public void changeturns(int turn) {
				if (turn == 1) {
					this.setText(pre + "X to play" + post);
				} else if (turn == 2) {
					this.setText(pre + "O to play" + post);
				} else if (turn == -1) {
					this.setText(pre + "X wins!" + post);
				} else if (turn == -2) {
					this.setText(pre + "O wins!" + post);
				}
		}
}
