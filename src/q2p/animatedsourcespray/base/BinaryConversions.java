package q2p.animatedsourcespray.base;

public final class BinaryConversions {
	public static void putLong(final long value, final byte[] output, final int outputOffset) {
		output[outputOffset    ] = (byte) (value >>> 56);
		output[outputOffset + 1] = (byte) (value >>> 48);
		output[outputOffset + 2] = (byte) (value >>> 40);
		output[outputOffset + 3] = (byte) (value >>> 32);
		output[outputOffset + 4] = (byte) (value >>> 24);
		output[outputOffset + 5] = (byte) (value >>> 16);
		output[outputOffset + 6] = (byte) (value >>>  8);
		output[outputOffset + 7] = (byte) (value       );
	}

	public static void putInt(final int value, final byte[] output, final int outputOffset) {
		output[outputOffset    ] = (byte) (value >>> 24);
		output[outputOffset + 1] = (byte) (value >>> 16);
		output[outputOffset + 2] = (byte) (value >>>  8);
		output[outputOffset + 3] = (byte) (value       );
	}

	public static void putShort(final short value, final byte[] output, final int outputOffset) {
		output[outputOffset    ] = (byte) (value >>> 8);
		output[outputOffset + 1] = (byte) (value      );
	}
}