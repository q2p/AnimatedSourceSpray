package q2p.animatedsourcespray.conversion.dataFormat;

import q2p.animatedsourcespray.engine.*;

import java.nio.*;

public class DataFormatBGRA5551 extends VTFDirectFormat {
	DataFormatBGRA5551() {
		super(4, false, 5, 5, 5, 1, 16, "RGBA_5551", "BGRA5551", "");
	}

	public byte[] convert(final int sizeX, final int sizeY, final byte[] bgra8888, final RenderHints renderHints) {
		for(byte channel = 0; channel != 3; channel++)
			renderHints.ditheringAlgorithm.dither(sizeX, sizeY, bgra8888, channel, (byte) 4, bgraDepth[channel]);

		final byte[] bgra5551 = new byte[sizeX*sizeY*2];
		final ByteBuffer bb = ByteBuffer.wrap(bgra5551);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		for(int bgraOffset = 0; bgraOffset != bgra8888.length; bgraOffset += 4) {
			final byte b, g, r;

			int a = (0xFF & bgra8888[bgraOffset+3]);
			if(a >= renderHints.alphaThreshold) {
				b = bgra8888[bgraOffset  ];
				g = bgra8888[bgraOffset+1];
				r = bgra8888[bgraOffset+2];
				a = 1;
			} else {
				b = 0;
				g = 0;
				r = 0;
				a = 0;
			}

			bb.putShort((short) ((b << 11) | (g << 6) | (r << 1) | a));
		}

		return bgra5551;
	}

	public byte[] extract(final int sizeX, final int sizeY, final byte[] bgra5551) {
		final byte[] bgra8888 = new byte[sizeX*sizeY*4];

		final ByteBuffer bb = ByteBuffer.wrap(bgra5551);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		for(int bgraOffset = 0; bgraOffset != bgra8888.length; bgraOffset += 4) {
			final short pixel = bb.getShort();

			bgra8888[bgraOffset  ] = (byte) ColorDepth.lowDepthTo8Bit((0b1111100000000000 & pixel) >>> 11, 5);
			bgra8888[bgraOffset+1] = (byte) ColorDepth.lowDepthTo8Bit((0b0000011111000000 & pixel) >>>  6, 5);
			bgra8888[bgraOffset+2] = (byte) ColorDepth.lowDepthTo8Bit((0b0000000000111110 & pixel) >>>  1, 5);
			bgra8888[bgraOffset+3] = (byte) ((0b1 & pixel) *255);
		}

		return bgra8888;
	}
}
