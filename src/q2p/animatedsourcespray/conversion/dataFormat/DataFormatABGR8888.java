package q2p.animatedsourcespray.conversion.dataFormat;

import q2p.animatedsourcespray.engine.*;

public final class DataFormatABGR8888 extends VTFDirectFormat {
	DataFormatABGR8888() {
		super(2, false, 8, 8, 8, 8, 32, "RGBA_8888", "ABGR8888", "");
	}

	public byte[] convert(final int sizeX, final int sizeY, final byte[] bgra8888, final RenderHints renderHints) {
		for(int i = 0; i != bgra8888.length; i += 4) {
			final byte b = bgra8888[i  ];
			final byte g = bgra8888[i+1];
			final byte r = bgra8888[i+2];
			final byte a = bgra8888[i+3];
			bgra8888[i  ] = a;
			bgra8888[i+1] = b;
			bgra8888[i+2] = g;
			bgra8888[i+3] = r;
		}

		return bgra8888;
	}

	public byte[] extract(final int sizeX, final int sizeY, final byte[] abgr8888) {
		for(int i = 0; i != abgr8888.length; i += 4) {
			final byte a = abgr8888[i  ];
			final byte b = abgr8888[i+1];
			final byte g = abgr8888[i+2];
			final byte r = abgr8888[i+3];
			abgr8888[i  ] = b;
			abgr8888[i+1] = g;
			abgr8888[i+2] = r;
			abgr8888[i+3] = a;
		}

		return abgr8888;
	}
}
