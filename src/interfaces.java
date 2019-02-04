import java.util.*;

interface labelListener {
	void changelabel(String message);
}

interface ThreadCompleteListener {
	void threadComplete(final Thread thread);
}
