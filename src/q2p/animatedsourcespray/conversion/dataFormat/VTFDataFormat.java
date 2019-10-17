package q2p.animatedsourcespray.conversion.dataFormat;

import q2p.animatedsourcespray.engine.*;

public abstract class VTFDataFormat {
	public static VTFDataFormat defaultFormat = null; // TODO:
	private static byte uidCounter = 0;
	public final byte uid = uidCounter++;

	public static final VTFDataFormat dxt1 = new DataFormatDXT1();

	private static final VTFDataFormat[] dataFormats = {
		new DataFormatBGR888(),
		new DataFormatABGR8888(),

		new DataFormatBGR565(),
		new DataFormatBGRA5551(),
		new DataFormatBGRA4444(),

		new DataFormatI8(),
		new DataFormatIA88(),

		dxt1,
//		DXT1_A1(20, false, -1, -1, -1, 1, 4, true, "DXT1_A1", "DXT1_ONEBITALPHA", "", (byte) 4),
//		DXT3_A4(14, false, -1, -1, -1, 4, 8, true, "DXT3_A4", "DXT3", "Uninterpolated Alpha", (byte) 4),
//		DXT5_A4(15, false, -1, -1, -1, 4, 8, true, "DXT5_A4", "DXT5", "Interpolated Alpha", (byte) 4),
	};

	public final byte code;
	public final boolean ditheringApplicable;
	private final boolean grayscale;
	private final byte rBits;
	private final byte gBits;
	private final byte bBits;
	public final byte aBits;
	final byte[] bgraDepth;
	private final int totalBits;
	private final boolean lossy;
	private final String localName;
	public final String externalName;
	private final String comment;
	public final byte sizeMultipleOf;

	VTFDataFormat(
		final int code,
		final boolean grayscale,
		final int rBits,
		final int gBits,
		final int bBits,
		final int aBits,
		final int totalBits,
		final boolean lossy,
		final String localName,
		final String externalName,
		final String comment,
		final int sizeMultipleOf
	) {
		this.code = (byte) code;
		this.ditheringApplicable = (rBits < 8 && rBits != -1) || (gBits < 8 && gBits != -1) || (bBits < 8 && bBits != -1);
		this.grayscale = grayscale;
		this.rBits = (byte) rBits;
		this.gBits = (byte) gBits;
		this.bBits = (byte) bBits;
		this.aBits = (byte) aBits;
		bgraDepth = new byte[] { this.bBits, this.gBits, this.rBits, this.aBits };
		this.totalBits = totalBits;
		this.lossy = lossy;
		this.localName = localName;
		this.externalName = externalName;
		this.comment = comment;
		this.sizeMultipleOf = (byte)sizeMultipleOf;
	}

	public static VTFDataFormat getByName(String name) {
		name = name.toLowerCase();
		for(final VTFDataFormat format : dataFormats)
			if(format.localName.toLowerCase().equals(name))
				return format;

		return null;
	}

	public abstract byte[] convert(final int inputSizeX, final int inputSizeY, byte[] bgra8888, int outputSizeX, int outputSizeY, final RenderHints renderHints);

	public abstract byte[] extract(final int sizeX, final int sizeY, byte[] pixelData);
}