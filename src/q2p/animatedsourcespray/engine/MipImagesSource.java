package q2p.animatedsourcespray.engine;

import q2p.animatedsourcespray.base.*;

import java.util.*;

public final class MipImagesSource extends MipsSource {
	public final LinkedList<String> paths = new LinkedList<>();

	public int getLength() {
		assert paths.size() <= Sizes.maxUInt16;
		return paths.size();
	}

	public MipImagesSource(final short layer) {
		super(layer);
	}

	public void pushPath(final String path) {
		paths.addLast(path.replace('\\', '/'));
	}
}