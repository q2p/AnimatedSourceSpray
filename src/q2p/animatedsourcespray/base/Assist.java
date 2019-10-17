package q2p.animatedsourcespray.base;

import java.lang.Thread.*;
import java.security.*;
import java.util.*;

public final class Assist {
	public static int perfectPositiveCeil(final int number, final int divisor) {
		assert number >= 0 && divisor > 0;
		return number / divisor + (number % divisor == 0 ? 0 : 1);
	}

	public static int limit(final int min, final int value, final int max) {
		if(value < min)
			return min;

		if(value > max)
			return max;

		return value;
	}
}