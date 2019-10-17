package q2p.animatedsourcespray.base.shorters;

public final class HEX {
	private static final char[] hexLowCase  = "0123456789abcdef".toCharArray();
	private static final char[] hexHighCase = "0123456789ABCDEF".toCharArray();

	private static final byte[] decodeMap = new byte['f'+1];

	static {
		for(byte i = 'f'; i != -1; i--)
			decodeMap[i] = -1;

		for(byte i = 0; i != 16; i++) {
			decodeMap[hexLowCase [i]] = i;
			decodeMap[hexHighCase[i]] = i;
		}
	}

	public static void encode(final byte[] plain, int plainOffset, final int plainLength, final StringBuilder out) {
		for(final int plainEnd = plainOffset + plainLength; plainOffset != plainEnd;) {
			final byte b = plain[plainOffset++];

			out.append(hexLowCase[(0xFF & b) >>> 4]);
			out.append(hexLowCase[b & 0xF]);
		}
	}
}