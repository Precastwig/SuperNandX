import java.awt.*;

class MoveValue {
	public float returnvalue;
	public Point returnmove;

	public MoveValue() {
		returnvalue = 0;
	}

	public MoveValue(float rv) {
		returnvalue = rv;
	}

	public MoveValue(float rv, Point rm) {
		returnvalue = rv;
		returnmove = rm;
	}
}
