package q2p.animatedsourcespray.engine.dithering;

import q2p.animatedsourcespray.base.*;
import q2p.animatedsourcespray.engine.*;

public final class OrderedDithering extends DitheringAlgorithm {
	private final double[][] thresholdMap;
	OrderedDithering(final int magnitude, final int thresholdMapResolution, final int[] thresholdMap) {
		super("ordered_"+thresholdMapResolution+"x"+thresholdMapResolution);

		this.thresholdMap = new double[thresholdMapResolution][thresholdMapResolution];

		for(int y = 0; y != thresholdMapResolution; y++)
			for(int x = 0; x != thresholdMapResolution; x++)
				this.thresholdMap[y][x] = (double)thresholdMap[y * thresholdMapResolution + x] / (double)magnitude - 0.5d;
	}

	public void dither(final int sizeX, final int sizeY, final byte[] intensity, int offset, final byte step, final byte bitDepth) {
		assert sizeX > 0 && sizeY > 0 && bitDepth > 0;

		final short steps = (short)(1 << bitDepth);

		// TODO: раньше steps позволяли понять, понять девиацию между значениями, но после перехода на более точный алгоритм шаги не показывают правильно девиацию.
		final int r = 2 * ((256 / steps) - 1); // -1 to prevent black #000 -> gray and white #FFF -> gray.

		for(int y = 0; y != sizeY; y++) {
			for(int x = 0; x != sizeX; x++, offset += step) {
				final int oldColor = (0xFF & intensity[offset]) + (int)Math.round(r * thresholdMap[y % thresholdMap.length][x % thresholdMap.length]);
				intensity[offset] = (byte) ColorDepth.eightBitToLowerDepth(Assist.limit(0, oldColor, 255), bitDepth);
			}
		}
	}
}
