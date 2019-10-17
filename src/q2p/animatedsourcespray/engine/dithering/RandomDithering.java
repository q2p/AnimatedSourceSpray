package q2p.animatedsourcespray.engine.dithering;

import q2p.animatedsourcespray.base.*;
import q2p.animatedsourcespray.engine.*;

import java.util.*;

public abstract class RandomDithering extends DitheringAlgorithm {
	RandomDithering(final String name) {
		super(name);
	}

	public void dither(final int sizeX, final int sizeY, final byte[] intensity, int offset, final byte step, final byte bitDepth) {
		assert sizeX > 0 && sizeY > 0 && bitDepth > 0;

		final Random random = new Random();
		random.setSeed(getSeed());
		final short steps = (short)(1 << bitDepth);

		// TODO: раньше steps позволяли понять, понять девиацию между значениями, но после перехода на более точный алгоритм шаги не показывают правильно девиацию.
		final int bucket = 256 / steps;
		final int bucketDist = bucket * 2 - 1;
		final int bucketStepDown = bucket - 1;

		for(int y = 0; y != sizeY; y++) {
			for(int x = 0; x != sizeX; x++, offset += step) {
				final int oldColor = Assist.limit(0, (0xFF & intensity[offset]) + random.nextInt(bucketDist) - bucketStepDown, 255);
				intensity[offset] = (byte) ColorDepth.eightBitToLowerDepth(oldColor, bitDepth);
			}
		}
	}

	protected abstract long getSeed();
}
