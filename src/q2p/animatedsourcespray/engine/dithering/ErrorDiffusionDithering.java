package q2p.animatedsourcespray.engine.dithering;

import q2p.animatedsourcespray.base.*;
import q2p.animatedsourcespray.engine.*;

final class ErrorDiffusionDithering extends DitheringAlgorithm {
	private final double[][] errorMap;
	private final boolean[][] errorMapUsed;
	private final int mapLeftOffset;
	private final int mapBottomOffset;

	ErrorDiffusionDithering(final String name, final int magnitude, final int mapSizeX, final int[] errorMap) {
		super(name);

		this.errorMap = new double[errorMap.length/mapSizeX][mapSizeX];
		this.errorMapUsed = new boolean[this.errorMap.length][mapSizeX];
		this.mapLeftOffset = mapSizeX / 2;
		this.mapBottomOffset = this.errorMap.length - 1;

		for(int y = 0; y != this.errorMap.length; y++) {
			for(int x = 0; x != mapSizeX; x++) {
				final int value = errorMap[y * mapSizeX + x];
				if(value != 0) {
					errorMapUsed[y][x] = true;
					this.errorMap[y][x] = (double) value / (double) magnitude;
				}
			}
		}
	}

	public void dither(final int sizeX, final int sizeY, final byte[] intensity, int startOffset, final byte step, final byte bitDepth) {
		assert sizeX > 0 && sizeY > 0 && bitDepth > 0;

		final double[] pixels = new double[sizeX * sizeY];
		for(int i = 0; i != sizeX*sizeY; i++)
			pixels[i] = 0xFF & intensity[startOffset + i*step];

		for(int y = 0; y != sizeY; y++) {
			final int eyMax = Assist.limit(0, sizeY - y - 1, mapBottomOffset) + 1;
			for(int x = 0; x != sizeX; x++) {
				final int exMin = mapLeftOffset - Math.min(mapLeftOffset, x);
				final int exMax = mapLeftOffset + Assist.limit(0, sizeX - x - 1, mapLeftOffset) + 1;

				final int pixOffset = y * sizeX + x;
				final double oldColor = pixels[pixOffset];
				final int newColor = ColorDepth.eightBitToLowerDepth(Assist.limit(0, (int)(Math.round(oldColor)), 255), bitDepth);
				intensity[startOffset] = (byte)newColor;
				startOffset += step;

				final double error = oldColor - ColorDepth.lowDepthTo8Bit(newColor, bitDepth);

				for(int ey = 0; ey != eyMax; ey++) {
					final int dy = y + ey;
					for(int ex = exMin; ex != exMax; ex++) {
						if(errorMapUsed[ey][ex]) {
							// y * sizeX + x
							final int off = dy * sizeX + x + ex - mapLeftOffset;
							pixels[off] = pixels[off] + error * errorMap[ey][ex];
						}
					}
				}
			}
		}
	}
}
