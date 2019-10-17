package q2p.animatedsourcespray.base.time;

import java.util.function.*;

public final class ProfilingTimer {
	private final String name;
	public String getName() {
		return name;
	}

	private long nanosCounted;
	private long timerStartingTime = -1;

	public ProfilingTimer(final String name) {
		this.name = name;
		this.nanosCounted = 0;
	}
	public ProfilingTimer(final String name, final long nanoSecoundsCounted) {
		this.name = name;
		this.nanosCounted = nanoSecoundsCounted;
	}
	public void start() {
		if(timerStartingTime == -1)
			timerStartingTime = System.nanoTime();
	}
	public void pause() {
		if(timerStartingTime != -1) {
			nanosCounted += System.nanoTime() - timerStartingTime;
			timerStartingTime = -1;
		}
	}
	public long nanoSecondsCounted() {
		assert timerStartingTime == -1;

		return nanosCounted;
	}
	public enum TimeUnit {
		nanoSeconds ("ns", 1L),
		milliSeconds("ms", 1000000L),
		seconds     ("sec", Time.second * 1000000L),
		minutes     ("min", Time.minute * 1000000L),
		hours       ("hr", Time.hour * 1000000L),
		days        ("d", Time.day * 1000000L);

		private final String suffix;
		private final long scale;
		TimeUnit(final String suffix, final long scale) {
			this.suffix = suffix;
			this.scale = scale;
		}
	}
	public String toString() {
		return toString(TimeUnit.milliSeconds, 1000, 1);
	}
	public String toString(final TimeUnit timeUnit, final int precision) {
		return toString(timeUnit, precision, 1);
	}
	private String toString(final TimeUnit timeUnit, final int precision, final int divide) {
		assert timerStartingTime == -1;
		assert divide > 0;

		return name + ": " +
			((double) ((int) ((double) nanosCounted / divide / timeUnit.scale * precision)) / precision)
			+ timeUnit.suffix;
	}

	public static void measure(final String name, final TimeUnit timeUnit, final int precision, final Runnable runnable) {
		final ProfilingTimer timer = new ProfilingTimer(name);
		timer.start();

		try {
			runnable.run();
		} finally {
			timer.pause();
			System.out.println(timer.toString());
		}
	}

	public static <T> T measure(final String name, final TimeUnit timeUnit, final int precision, final Supplier<T> supplier) {
		final ProfilingTimer timer = new ProfilingTimer(name);
		timer.start();

		try {
			return supplier.get();
		} finally {
			timer.pause();
			System.out.println(timer.toString());
		}
	}
}