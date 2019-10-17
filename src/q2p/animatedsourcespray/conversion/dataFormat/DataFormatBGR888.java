package q2p.animatedsourcespray.conversion.dataFormat;

import q2p.animatedsourcespray.engine.*;

public final class DataFormatBGR888 extends VTFDirectFormat {
	DataFormatBGR888() {
		super(0, false, 8, 8, 8, 0, 24, "RGB_888", "BGR888", "");
	}

	public byte[] convert(final int sizeX, final int sizeY, final byte[] bgra8888, final RenderHints renderHints) {
		final byte[] bgr888 = new byte[sizeX*sizeY*3];

		for(int i = 0; i != sizeX*sizeY; i++) {
			final int bgraOffset = i * 4;
			final int bgrOffset = i * 3;

			final int a = (0xFF & bgra8888[bgraOffset+3]);
			if(a >= renderHints.alphaThreshold) {
				bgr888[bgrOffset  ] = bgra8888[bgraOffset  ];
				bgr888[bgrOffset+1] = bgra8888[bgraOffset+1];
				bgr888[bgrOffset+2] = bgra8888[bgraOffset+2];
			} else {
				bgr888[bgrOffset  ] = 0;
				bgr888[bgrOffset+1] = 0;
				bgr888[bgrOffset+2] = 0;
			}
		}

		return bgr888;
	}

	public byte[] extract(final int sizeX, final int sizeY, final byte[] bgr888) {
		final byte[] bgra8888 = new byte[sizeX*sizeY*4];

		for(int i = 0; i != sizeX*sizeY; i++) {
			final int bgraOffset = i * 4;
			final int bgrOffset = i * 3;

			bgra8888[bgraOffset  ] = bgr888[bgrOffset  ];
			bgra8888[bgraOffset+1] = bgr888[bgrOffset+1];
			bgra8888[bgraOffset+2] = bgr888[bgrOffset+2];
			bgra8888[bgraOffset+3] = (byte) 255;
		}

		return bgra8888;
	}
}
