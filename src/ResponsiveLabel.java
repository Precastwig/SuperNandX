import java.util.*;
import javax.swing.*;

class ResponsiveLabel extends JLabel implements labelListener {

		private String pre = "<html><div style='text-align: center;'>";
		private String post = "</div></html>";
		@Override
		public void changelabel(String message) {
			this.setText(message);
		}
}
