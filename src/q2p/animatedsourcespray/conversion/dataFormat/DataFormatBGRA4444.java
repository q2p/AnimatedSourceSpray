package q2p.animatedsourcespray.conversion.dataFormat;

import q2p.animatedsourcespray.engine.*;

import java.nio.*;

public class DataFormatBGRA4444 extends VTFDirectFormat {
	DataFormatBGRA4444() {
		super(3, false, 4, 4, 4, 4, 16, "RGBA_4444", "BGRA4444", "");
	}

	public byte[] convert(final int sizeX, final int sizeY, final byte[] bgra8888, final RenderHints renderHints) {
		for(byte channel = 0; channel != 4; channel++)
			renderHints.ditheringAlgorithm.dither(sizeX, sizeY, bgra8888, channel, (byte) 4, bgraDepth[channel]);

		final byte[] bgr4444 = new byte[sizeX*sizeY*2];
		final ByteBuffer bb = ByteBuffer.wrap(bgr4444);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		for(int bgraOffset = 0; bgraOffset != bgra8888.length; bgraOffset += 4) {
			final byte b = bgra8888[bgraOffset  ];
			final byte g = bgra8888[bgraOffset+1];
			final byte r = bgra8888[bgraOffset+2];
			final byte a = bgra8888[bgraOffset+3];

			bb.putShort((short)((b << 12) | (g << 8) | (r << 4) | a));
		}

		return bgr4444;
	}

	public byte[] extract(final int sizeX, final int sizeY, final byte[] bgr4444) {
		final byte[] bgra8888 = new byte[sizeX*sizeY*4];

		final ByteBuffer bb = ByteBuffer.wrap(bgr4444);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		for(int bgraOffset = 0; bgraOffset != bgra8888.length; bgraOffset += 4) {
			final short pixel = bb.getShort();

			bgra8888[bgraOffset  ] = (byte) ColorDepth.lowDepthTo8Bit((0xF000 & pixel) >>> 12, 4);
			bgra8888[bgraOffset+1] = (byte) ColorDepth.lowDepthTo8Bit((0x0F00 & pixel) >>>  8, 4);
			bgra8888[bgraOffset+2] = (byte) ColorDepth.lowDepthTo8Bit((0x00F0 & pixel) >>>  4, 4);
			bgra8888[bgraOffset+3] = (byte) ColorDepth.lowDepthTo8Bit((0x000F & pixel)       , 4);
		}

		return bgra8888;
	}
}
