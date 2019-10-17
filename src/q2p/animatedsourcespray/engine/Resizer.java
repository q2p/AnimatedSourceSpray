package q2p.animatedsourcespray.engine;

import java.util.*;

final class Resizer implements Runnable {
	private static final Deque<ResizedImage> queue = new LinkedList<>();

	public static void push(final ResizedImage image) {
		queue.addLast(image);
	}

	public static void process() {
		final Thread[] threads = new Thread[Arguments.getDesiredThreadsAmount()];
		for(int i = 0; i != threads.length; i++) {
			threads[i] = new Thread(new Resizer());
			threads[i].start();
		}
		for(int i = 0; i != threads.length; i++) {
			try {
				threads[i].join();
			} catch(final InterruptedException e) {
				assert false;
			}
		}
	}

	public void run() {
		while(true) {
			final ResizedImage resizedImage;
			synchronized(queue) {
				if(queue.isEmpty())
					return;

				resizedImage = queue.removeFirst();
			}
			resizedImage.resize();
		}
	}
}