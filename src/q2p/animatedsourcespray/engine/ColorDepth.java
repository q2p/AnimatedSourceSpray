package q2p.animatedsourcespray.engine;

import q2p.animatedsourcespray.base.*;

public final class ColorDepth {
	/*
		x8 = 255/31 * x5
		x8 = 255/63 * x6

		x6 = 63/255 * x8

		cY = cX * max(cY)/max(cX)

		// TODO: test
		Pixel8 red   = (5bitRedChannel   * 255 + 15) / 31
		Pixel8 green = (6bitGreenChannel * 255 + 31) / 63
	*/
	private static final int[][] tableFrom8 = new int[8][256];
	private static final int[][] tableTo8 = new int[8][];
	static {
		for(int bitDepth = 0; bitDepth != 8; bitDepth++) {
			final int maxValueReduced = (1 << bitDepth) - 1;
			for(int color = 0; color != 256; color++) {
				tableFrom8[bitDepth][color] = Assist.limit(0,
					(int)Math.round((color * maxValueReduced) / 255.0d)
				, maxValueReduced);
			}
		}


		for(int bitDepth = 0; bitDepth != 8; bitDepth++) {
			final int values = 1 << bitDepth;
			final double maxValuesReduced = values - 1;
			tableTo8[bitDepth] = new int[values];
			for(int j = 0; j != values; j++)
				tableTo8[bitDepth][j] = Assist.limit(0, (int)Math.round((j * 255) / maxValuesReduced), 255);
		}
	}
	public static int lowDepthTo8Bit(final int lowDepthValue, final int bitDepth) {
		return tableTo8[bitDepth][lowDepthValue];

		// return Math.min(lowDepthValue * 256 / ((1 << bitDepth)-1), 255);
	}

	public static int eightBitToLowerDepth(final int eightBitValue, final int bitDepth) {
		return tableFrom8[bitDepth][eightBitValue];

		// eightBitValue = Assist.limit(0, eightBitValue, 255); // TODO:
		// final int bucket = 256 / ((1 << bitDepth)-1);
		// return eightBitValue / bucket;
	}
}