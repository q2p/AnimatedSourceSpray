package q2p.animatedsourcespray.conversion.dataFormat;

import q2p.animatedsourcespray.engine.*;

import java.nio.*;

public class DataFormatBGR565 extends VTFDirectFormat {
	DataFormatBGR565() {
		super(1, false, 5, 6, 5, 0, 16, "RGB_565", "BGR565", "");
	}

	public byte[] convert(final int sizeX, final int sizeY, final byte[] bgra8888, final RenderHints renderHints) {
		for(byte channel = 0; channel != 3; channel++)
			renderHints.ditheringAlgorithm.dither(sizeX, sizeY, bgra8888, channel, (byte) 4, bgraDepth[channel]);

		final byte[] bgr565 = new byte[sizeX*sizeY*2];
		final ByteBuffer bb = ByteBuffer.wrap(bgr565);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		for(int bgraOffset = 0; bgraOffset != bgra8888.length; bgraOffset += 4) {
			final byte b, g, r;

			final int a = (0xFF & bgra8888[bgraOffset+3]);
			if(a >= renderHints.alphaThreshold) {
				b = bgra8888[bgraOffset  ];
				g = bgra8888[bgraOffset+1];
				r = bgra8888[bgraOffset+2];
			} else {
				b = 0;
				g = 0;
				r = 0;
			}

			bb.putShort((short) ((b << 11) | (g << 5) | r));
		}

		return bgr565;
	}

	public byte[] extract(final int sizeX, final int sizeY, final byte[] bgr565) {
		final byte[] bgra8888 = new byte[sizeX*sizeY*4];

		final ByteBuffer bb = ByteBuffer.wrap(bgr565);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		for(int bgraOffset = 0; bgraOffset != bgra8888.length; bgraOffset += 4) {
			final short pixel = bb.getShort();

			bgra8888[bgraOffset  ] = (byte) ColorDepth.lowDepthTo8Bit((0b1111100000000000 & pixel) >>> 11, 5);
			bgra8888[bgraOffset+1] = (byte) ColorDepth.lowDepthTo8Bit((0b0000011111100000 & pixel) >>>  5, 6);
			bgra8888[bgraOffset+2] = (byte) ColorDepth.lowDepthTo8Bit((0b0000000000011111 & pixel)       , 5);
			bgra8888[bgraOffset+3] = (byte) 255;
		}

		return bgra8888;
	}
}
