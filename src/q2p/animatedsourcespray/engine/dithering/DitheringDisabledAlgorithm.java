package q2p.animatedsourcespray.engine.dithering;

import q2p.animatedsourcespray.engine.*;

final class DitheringDisabledAlgorithm extends DitheringAlgorithm {
	DitheringDisabledAlgorithm() {
		super("none");
	}

	public void dither(final int sizeX, final int sizeY, final byte[] intensity, final int offset, final byte step, final byte bitDepth) {
		assert sizeX > 0 && sizeY > 0 && bitDepth > 0;

		for(int i = (sizeX*sizeY-1)*step+offset; i > 0; i -= step)
			intensity[i] = (byte) ColorDepth.eightBitToLowerDepth(0xFF & intensity[i], bitDepth);
	}
}