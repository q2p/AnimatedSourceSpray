package q2p.animatedsourcespray.engine.dithering;

import java.util.*;

public final class RandomSymmetricDithering extends RandomDithering {
	RandomSymmetricDithering() {
		super("random_symmetric");
	}

	private final long seed = new Random().nextLong();

	protected long getSeed() {
		return seed;
	}
}