package q2p.animatedsourcespray.engine.dithering;

import java.util.*;

public final class PurelyRandomDithering extends RandomDithering {
	PurelyRandomDithering() {
		super("random");
	}

	protected long getSeed() {
		return new Random().nextLong();
	}
}