package q2p.animatedsourcespray.conversion.dataFormat;

import q2p.animatedsourcespray.base.*;
import q2p.animatedsourcespray.engine.*;

public class DataFormatIA88 extends VTFDirectFormat {
	DataFormatIA88() {
		super(6, true, -1, -1, -1, 0, 8, "GRAY_A_88", "IA88", "");
	}

	private static final double rWeight = 0.2126d;
	private static final double gWeight = 0.7152d;
	private static final double bWeight = 0.0722d;
	private static final double rWeightA = 0.2989d;
	private static final double gWeightA = 0.5870d;
	private static final double bWeightA = 0.1140d;
	public byte[] convert(final int sizeX, final int sizeY, final byte[] bgra8888, final RenderHints renderHints) {
		final byte[] graya88 = new byte[2*sizeX*sizeY];

		for(int i = 0; i != graya88.length; i += 2) {
			final int bgraOffset = i * 2;

			final int b = (0xFF & bgra8888[bgraOffset  ]);
			final int g = (0xFF & bgra8888[bgraOffset+1]);
			final int r = (0xFF & bgra8888[bgraOffset+2]);
			graya88[i] = (byte) Assist.limit(0, (int)Math.round(r * rWeight + g * gWeight + b * bWeight), 255);
			graya88[i+1] = bgra8888[bgraOffset+3];
		}

		return graya88;
	}

	public byte[] extract(final int sizeX, final int sizeY, final byte[] graya88) {
		final byte[] bgra8888 = new byte[sizeX*sizeY*4];

		for(int i = 0; i != graya88.length; i += 2) {
			final int bgraOffset = i * 2;

			final byte gray  = graya88[i  ];
			final byte alpha = graya88[i+1];

			bgra8888[bgraOffset  ] = gray;
			bgra8888[bgraOffset+1] = gray;
			bgra8888[bgraOffset+2] = gray;
			bgra8888[bgraOffset+3] = alpha;
		}

		return bgra8888;
	}
}
