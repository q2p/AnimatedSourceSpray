package q2p.animatedsourcespray.conversion.dataFormat;

import q2p.animatedsourcespray.base.*;
import q2p.animatedsourcespray.engine.*;

public class DataFormatI8 extends VTFDirectFormat {
	DataFormatI8() {
		super(5, true, -1, -1, -1, 0, 8, "GRAY_8", "I8", "");
	}

	private static final double rWeight = 0.2126d;
	private static final double gWeight = 0.7152d;
	private static final double bWeight = 0.0722d;
	private static final double rWeightA = 0.2989d;
	private static final double gWeightA = 0.5870d;
	private static final double bWeightA = 0.1140d;
	public byte[] convert(final int sizeX, final int sizeY, final byte[] bgra8888, final RenderHints renderHints) {
		final byte[] gray8 = new byte[sizeX*sizeY];

		for(int i = 0; i != gray8.length; i++) {
			final int bgraOffset = i * 4;

			final int a = (0xFF & bgra8888[bgraOffset+3]);
			if(a >= renderHints.alphaThreshold) {
				final int b = (0xFF & bgra8888[bgraOffset  ]);
				final int g = (0xFF & bgra8888[bgraOffset+1]);
				final int r = (0xFF & bgra8888[bgraOffset+2]);
				gray8[i] = (byte) Assist.limit(0, (int)Math.round(r * rWeight + g * gWeight + b * bWeight), 255);
			} else {
				gray8[i] = 0;
			}
		}

		return gray8;
	}

	public byte[] extract(final int sizeX, final int sizeY, final byte[] gray8) {
		final byte[] bgra8888 = new byte[sizeX*sizeY*4];

		for(int i = 0; i != gray8.length; i++) {
			final int bgraOffset = i * 4;

			final byte gray = gray8[i];

			bgra8888[bgraOffset  ] = gray;
			bgra8888[bgraOffset+1] = gray;
			bgra8888[bgraOffset+2] = gray;
			bgra8888[bgraOffset+3] = (byte) 0xFF;
		}

		return bgra8888;
	}
}
