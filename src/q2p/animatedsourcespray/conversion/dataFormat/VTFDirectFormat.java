package q2p.animatedsourcespray.conversion.dataFormat;

import q2p.animatedsourcespray.engine.*;

abstract class VTFDirectFormat extends VTFDataFormat {
	VTFDirectFormat(
		final int code,
		final boolean grayscale,
		final int rBits,
		final int gBits,
		final int bBits,
		final int aBits,
		final int totalBits,
		final String localName,
		final String externalName,
		final String comment
	) {
		super(code, grayscale, rBits, gBits, bBits, aBits, totalBits, false, localName, externalName, comment, 1);
	}

	public final byte[] convert(final int inputSizeX, final int inputSizeY, final byte[] bgra8888, final int outputSizeX, final int outputSizeY, final RenderHints renderHints) {
		final byte[] canvasBGRA8888 = new byte[outputSizeX * outputSizeY * 4];

		final int offsetX = (outputSizeX - inputSizeX) / 2;
		final int offsetY = (outputSizeY - inputSizeY) / 2;

		for(int y = 0; y != inputSizeY; y++) {
			final int coy = (offsetY+y)*outputSizeX;
			final int ioy = y*inputSizeX;
			for(int x = 0; x != inputSizeX; x++) {
				final int cox = 4*(coy+offsetX+x); // hee hee hee...
				final int iox = 4*(ioy+x);
				canvasBGRA8888[cox  ] = bgra8888[iox  ];
				canvasBGRA8888[cox+1] = bgra8888[iox+1];
				canvasBGRA8888[cox+2] = bgra8888[iox+2];
				canvasBGRA8888[cox+3] = bgra8888[iox+3];
			}
		}

		return convert(outputSizeX, outputSizeY, canvasBGRA8888, renderHints);
	}

	protected abstract byte[] convert(int outputSizeX, int outputSizeY, byte[] canvasBGRA8888, RenderHints renderHints);
}