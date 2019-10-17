package q2p.animatedsourcespray.engine;

import q2p.animatedsourcespray.base.*;

public class MipVideoSource extends MipsSource {
	public final String path;
	public final int start;
	private final int length;

	public int getLength() {
		return length;
	}

	MipVideoSource(final short layer, String path, final int start, final int end) {
		super(layer);

		assert path != null && start >= 0 && end >= start;

		path = path.replace('\\', '/');
		if(!path.endsWith("/"))
			path = path + '/';

		this.path = path;
		this.start = start;
		final int temp = end - start + 1;
		assert temp <= Sizes.maxUInt16;
		length =  temp;
	}
}
