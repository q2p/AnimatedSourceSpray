package q2p.animatedsourcespray.engine;

import q2p.animatedsourcespray.base.*;

abstract class MipsSource {
	public short layer;
	public abstract int getLength();

	MipsSource(final short layer) {
		assert layer >= 0 && layer < Sizes.maxUInt8;
		this.layer = layer;
	}
}