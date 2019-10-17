package q2p.animatedsourcespray.conversion.dataFormat;

import q2p.animatedsourcespray.base.*;
import q2p.animatedsourcespray.engine.*;

import java.nio.*;
import java.util.*;

public class DataFormatDXT1 extends VTFDataFormat {
	DataFormatDXT1() {
		super(13, false, -1, -1, -1, 0, 4, true, "DXT1", "DXT1", "", 4);
	}

	private final LinkedList<DXT1Texel> texels = new LinkedList<>();

	public byte[] convert(final int inputSizeX, final int inputSizeY, byte[] bgra8888, final int canvasSizeX, final int canvasSizeY, final RenderHints renderHints) {
		bgra8888 = fitImage(inputSizeX, inputSizeY, bgra8888, canvasSizeX, canvasSizeY);

		final int internalSizeX = Assist.perfectPositiveCeil(canvasSizeX, 4) * 4;
		final int internalSizeY = Assist.perfectPositiveCeil(canvasSizeY, 4) * 4;

		System.out.println(inputSizeX+"x"+inputSizeY);
		System.out.println(canvasSizeX+"x"+canvasSizeY);
		System.out.println(internalSizeX+"x"+internalSizeY);

		final byte[] dxt1 = new byte[internalSizeX*internalSizeY/2];

		int dxt1Offset = 0;
		for(int ty = 0; ty != inputSizeY / 4; ty++) {
			final int textelOffsetY = ty*4;
			for(int tx = 0; tx != inputSizeX / 4; tx++) {
				final int textelOffsetX = tx*4;

				final int texelSizeX = Math.min(4, canvasSizeX - textelOffsetX);
				final int texelSizeY = Math.min(4, canvasSizeY - textelOffsetY);

				texels.addLast(new DXT1Texel(bgra8888, canvasSizeX, textelOffsetX, textelOffsetY, texelSizeX, texelSizeY, dxt1, dxt1Offset, renderHints.alphaThreshold));

				dxt1Offset += 8;
			}
		}

		final Thread[] threads = new Thread[Arguments.getDesiredThreadsAmount()];

		for(int i = 0; i != threads.length; i++) {
			threads[i] = new Thread(() -> {
				DXT1Texel texel;
				while(true) {
					synchronized(texels) {
						texel = texels.pollFirst();
					}
					if(texel == null)
						return;
					texel.run();
				}
			});
			threads[i].start();
		}

		try {
			for(final Thread thread : threads)
				thread.join();
		} catch(final InterruptedException ignore) {}

		return dxt1;
	}

	public static void parseTexel(final ByteBuffer bb) {
		bb.order(ByteOrder.LITTLE_ENDIAN);

		final short c1 = bb.getShort();
		final short c2 = bb.getShort();

		System.out.println(DXT1Texel.hex565Color(c1, c2));

		for(int y = 0; y != 4; y++) {
			byte row = bb.get();
			for(int x = 0; x != 4; x++) {
				System.out.print(row&0b11);
				row >>>= 2;
			}
			System.out.println();
		}
	}

	private static byte[] fitImage(final int inputSizeX, final int inputSizeY, final byte[] bgra8888, final int outputSizeX, final int outputSizeY) {
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

		return canvasBGRA8888;
	}

	public byte[] extract(final int sizeX, final int sizeY, final byte[] bgra5551) {
		// TODO:
		return new byte[sizeX*sizeY*4];
	}
}
