package q2p.animatedsourcespray.base;

public final class Endianness {
	public static long flipEndianess32(long value) {
		return (value&0xff)<<24 | (value&0xff00)<<8 | (value&0xff0000)>>8 | (value>>24)&0xff;
	}
	public static int flipEndianess16(long value) {
		return (int) ((value&0xff)<<8 | (value>>8)&0xff);
	}
}
