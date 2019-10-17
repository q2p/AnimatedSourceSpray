package q2p.animatedsourcespray.engine;

import q2p.animatedsourcespray.base.*;

import java.nio.charset.*;
import java.util.*;

public final class CUID implements Comparable<CUID> {
	private final byte[] uid;

	CUID(final Object... uniqueProperties) {
		assert uniqueProperties.length != 0;

		int length = 0;
		for(final Object object : uniqueProperties) {
			assert object != null;
			if(object instanceof Byte) {
				length++;
			} else if (object instanceof Short) {
				length += 2;
			} else if (object instanceof Integer) {
				length += 4;
			} else if (object instanceof Long) {
				length += 8;
			} else if (object instanceof byte[]) {
				length += ((byte[]) object).length;
			} else if (object instanceof CUID) {
				length += ((CUID) object).uid.length;
			} else {
				assert false;
			}
		}
		uid = new byte[length];
		int offset = 0;
		for(final Object object : uniqueProperties) {
			if(object instanceof Byte) {
				uid[offset++] = (byte) object;
			} else if (object instanceof Short) {
				BinaryConversions.putShort((short) object, uid, offset);
				offset += 2;
			} else if (object instanceof Integer) {
				BinaryConversions.putInt((int) object, uid, offset);
				offset += 4;
			} else if (object instanceof Long) {
				BinaryConversions.putLong((long) object, uid, offset);
				offset += 8;
			} else if (object instanceof byte[]) {
				final int len = ((byte[]) object).length;
				System.arraycopy(object, 0, uid, offset, len);
				offset += len;
			} else if (object instanceof CUID) {
				final byte[] src = ((CUID) object).uid;
				System.arraycopy(src, 0, uid, offset, src.length);
				offset += src.length;
			}
		}
	}

	public String toBase64() {
		return new String(Base64.getEncoder().encode(uid), StandardCharsets.US_ASCII).replace('/', '_').replace('\\', '_');
	}

	public int compareTo(final CUID o) {
			return Arrays.compare(uid, o.uid);
		}
}