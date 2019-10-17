package q2p.animatedsourcespray.engine;

import q2p.animatedsourcespray.base.*;
import q2p.animatedsourcespray.conversion.dataFormat.*;

import java.io.*;
import java.nio.*;

class Import {
	private static final byte RSRCF_HAS_NO_DATA_CHUNK = 0x02;
	private static final byte VTF_RSRC_MAX_DICTIONARY_ENTRIES = 32;

	public static void read(final String path) throws Throwable {
		DataInputStream dis = new DataInputStream(new FileInputStream(path));

		long uiFileSize = dis.available();

		if(dis.read() == 'V' && dis.read() == 'T' && dis.read() == 'F' && dis.read() == 0)
			System.out.println("Signature is OK");
		else
			System.out.println("Signature is NOT OK");

		long major = readUint32LE(dis);
		long minor = readUint32LE(dis);
		System.out.println("Major: " + major); // 0x0004
		System.out.println("Minor: " + minor); // 0x0008
		System.out.println("Header size: " + readUint32LE(dis)); // 0x000C
		System.out.println();
		System.out.println("Width: " + readUint16LE(dis)); // 0x0010
		System.out.println("Height: " + readUint16LE(dis)); // 0x0012
		System.out.println();
		System.out.println("Flags: " + Integer.toBinaryString((int) readUint32LE(dis))); // 0x0014
		System.out.println("Frames: " + readUint16LE(dis)); // 0x0018
		System.out.println("Start Frame: " + readUint16LE(dis)); // 0x001A
		System.out.println();
		dis.skipBytes(4);
		System.out.println("Reflectivity 1: " + readFloatLE(dis)); // 0x0020
		System.out.println("Reflectivity 2: " + readFloatLE(dis)); // 0x0024
		System.out.println("Reflectivity 3: " + readFloatLE(dis)); // 0x0028
		dis.skipBytes(4); // 0x002C
		System.out.println();
		System.out.println("Bump scale: " + readFloatLE(dis)); // 0x0030
		System.out.println("Image format: " + readUint32LE(dis)); // 0x0034
		System.out.println("Mipmap count: " + readUint8(dis)); // 0x0038
		System.out.println();
		long lowResImageFormat = readUint32LE(dis); // 0x0039
		System.out.println("Low res image format: " + lowResImageFormat);
		final int thumbnailSizeX = readUint8(dis); // 0x003D
		final int thumbnailSizeY = readUint8(dis); // 0x003E
		System.out.println("Low res width: " + thumbnailSizeX);
		System.out.println("Low res height: " + thumbnailSizeY);
		byte resourceCount = 0;
		if(minor >= 2) {
			System.out.println();
			System.out.println("Version 7.2+");
			System.out.println("Depth: " + readUint16LE(dis)); // 0x003F

			if(minor >= 3) {
				System.out.println();
				System.out.println("Version 7.3+");
				dis.skipBytes(3); // 0x0041
				resourceCount = (byte) Math.min(readUint32LE(dis), VTF_RSRC_MAX_DICTIONARY_ENTRIES); // 0x0044
				System.out.println("Resource count: " + resourceCount);
			}
		}

		dis = new DataInputStream(new FileInputStream(path));
		dis.skipBytes(0x0050); // Align to 0x0050

		if(minor <= 2) {
			// TODO: skipping thumbnail and ignoring image type
			dis.skipBytes(thumbnailSizeX*thumbnailSizeY/2);
			final ByteBuffer bb = ByteBuffer.allocate(8);
			dis.readFully(bb.array());

			DataFormatDXT1.parseTexel(bb);

			if(dis.available() != 0)
				System.out.println("Space unused: "+dis.available());
		} else { // 7.3+
			long uiThumbnailBufferOffset = 0, uiImageDataOffset = 0;

			final byte[][] headersData = new byte[resourceCount][];
			for(int i = 0; i != resourceCount; i++) {
				final int firstFourBytes = (int)readUint32BE(dis);
				final byte resFlags = (byte) (firstFourBytes & 0xFF);
				final int firstFourBytesLE = (int) Endianness.flipEndianess32(firstFourBytes);
				final long data = readUint32LE(dis);
				final ByteBuffer bb = ByteBuffer.allocate(4);
				bb.putInt(firstFourBytes);
				System.out.println("Resource "+i+" first: "+Coding.fromASCII(bb.array(), 0, 4)+" : "+Long.toHexString(firstFourBytesLE));
				System.out.println("Resource "+i+" data: "+Long.toHexString(data));

				if(firstFourBytesLE == VTF_LEGACY_RSRC_LOW_RES_IMAGE) {
					if(lowResImageFormat < 0)
						throw new Throwable("File may be corrupt; unexpected low resolution image directory entry.");
					if(uiThumbnailBufferOffset != 0)
						throw new Throwable("File may be corrupt; multiple low resolution image directory entries.");

					uiThumbnailBufferOffset = data;
				} else if(firstFourBytesLE == VTF_LEGACY_RSRC_IMAGE) {
					if(uiImageDataOffset != 0)
						throw new Throwable("File may be corrupt; multiple image directory entries.");

					uiImageDataOffset = data;
				} else {
					if((resFlags & RSRCF_HAS_NO_DATA_CHUNK) == 0) {
						if(data + 4 > uiFileSize)
							throw new Throwable("File may be corrupt; file to small for it's resource data.");

						dis = new DataInputStream(new FileInputStream(path));
						dis.skipBytes((int) data);
						final int uiSize = (int) readUint32LE(dis);

						if(data + 4 + uiSize > uiFileSize)
							throw new Throwable("File may be corrupt; file to small for it's resource data.");

						headersData[i] = new byte[uiSize];
						dis.readFully(headersData[i]);
					}
				}
			}
			dis.skipBytes((VTF_RSRC_MAX_DICTIONARY_ENTRIES - resourceCount) * 8);
			for(int i = 0; i != resourceCount; i++) {
			}
		}
		Abort.critical();
	}

	private static float readFloatLE(final DataInputStream dis) throws IOException {
		final ByteBuffer bb = ByteBuffer.allocate(4);
		dis.readFully(bb.array());
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getFloat();
	}

	private static long readUint32BE(final DataInputStream dis) throws IOException {
		return dis.readInt();
	}

	private static long readUint32LE(final DataInputStream dis) throws IOException {
		return Endianness.flipEndianess32(dis.readInt());
	}

	private static int readUint16LE(final DataInputStream dis) throws IOException {
		return Endianness.flipEndianess16(dis.readUnsignedShort());
	}

	private static byte readSint8(final DataInputStream dis) throws IOException {
		return (byte) dis.read();
	}

	private static short readUint8(final DataInputStream dis) throws IOException {
		return (short) dis.read();
	}

	private static final int VTF_LEGACY_RSRC_LOW_RES_IMAGE = generate(0x01, 0, 0);
	private static final int VTF_LEGACY_RSRC_IMAGE = generate(0x30, 0, 0);
	private static final int VTF_RSRC_SHEET = generate(0x10, 0, 0);
	private static final int VTF_RSRC_CRCe = generate((byte)'C', (byte)'R', (byte)'C', RSRCF_HAS_NO_DATA_CHUNK);
	private static final int VTF_RSRC_TEXTURE_LOD_SETTINGS = generate((byte)'L', (byte)'O', (byte)'D', RSRCF_HAS_NO_DATA_CHUNK);
	private static final int VTF_RSRC_TEXTURE_SETTINGS_EX = generate((byte)'T', (byte)'S', (byte)'O', RSRCF_HAS_NO_DATA_CHUNK);
	private static final int VTF_RSRC_KEY_VALUE_DATA = generate('K', 'V', 'D');
	private static int generate(int b1, int b2, int b3) {
		return b1 | (b2 << 8) | (b3 << 16);
	}
	private static int generate(byte b1, byte b2, byte b3, byte b4) {
		return b1 | (b2 << 8) | (b3 << 16) | (b4 << 24);
	}
}
