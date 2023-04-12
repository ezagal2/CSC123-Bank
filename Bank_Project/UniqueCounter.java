import java.io.Serializable;

public class UniqueCounter implements Serializable {
	private static int counterState = 1000;

	public static int nextValue() {
		return counterState++;
	}

	public static int currentValue() {
		return counterState;
	}

	public static void setValue(Integer currentAccountNum) {
		counterState = currentAccountNum;
	}
}
