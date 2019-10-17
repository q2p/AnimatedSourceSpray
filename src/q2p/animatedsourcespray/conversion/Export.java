package q2p.animatedsourcespray.conversion;

import java.nio.channels.*;

final class Export {
	/*

	VTF layout 7.2 {
		1. VTF Header
		2. VTF Low Resolution Image Data
		3. For Each Mipmap (Smallest to Largest)
			* For Each Frame (First to Last)
				* For Each Face (First to Last)
					* For Each Z Slice (Min to Max; Varies with Mipmap)
						* VTF High Resolution Image Data
	}

	VTF layout 7.3+ {
		1. VTF Header
		2. Resource entries
			* VTF Low Resolution Image Data
			* Other resource data
			* For Each Mipmap (Smallest to Largest)
					* For Each Frame (First to Last)
						* For Each Face (First to Last)
							* For Each Z Slice (Min to Max; Varies with Mipmap)
								* VTF High Resolution Image Data
	}



	typedef struct tagVTFHEADER
	{
		uint8 signature[4];	 // File signature ("VTF\0"). (or as little-endian integer, 0x00465456)
		uint32 version[2]; // version[0].version[1] (currently 7.2).
		uint32 headerSize; // Size of the header struct  (16 byte aligned; currently 80 bytes) + size of the resources dictionary (7.3+).
		uint16 width; // Width of the largest mipmap in pixels. Must be a power of 2.
		uint16 height; // Height of the largest mipmap in pixels. Must be a power of 2.
		uint32 flags; // VTF flags.
		uint16 frames; // Number of frames, if animated (1 for no animation).
		uint16 firstFrame; // First frame in animation (0 based).
		uint8 padding0[4]; // reflectivity padding (16 byte alignment).
		float reflectivity[3]; // reflectivity vector.
		uint8 padding1[4]; // reflectivity padding (8 byte packing).
		float bumpmapScale; // Bumpmap scale.
		uint32 highResImageFormat;	// High resolution image format.
		uint8 mipmapCount;		// Number of mipmaps.
		uint32 lowResImageFormat;	// Low resolution image format (always DXT1).
		uint8 lowResImageWidth;	// Low resolution image width.
		uint8 lowResImageHeight;	// Low resolution image height.

		// 7.2+
		uint16	depth;			// Depth of the largest mipmap in pixels.
							// Must be a power of 2. Can be 0 or 1 for a 2D texture (v7.2 only).

		// 7.3+
		uint8	padding2[3];		// depth padding (4 byte alignment).
		uint	numResources;		// Number of resources this vtf has
	} VTFHEADER;
	*/
	public static void exportVTF(final byte versionMajor, final byte versionMinor) {
		byte[] out = new byte[1024]; // TODO:

		// Signature
		out[0] = 'V';
		out[1] = 'T';
		out[2] = 'F';
		out[3] = 0;

		// Version
		out[4] = versionMajor;
		out[5] = 0;
		out[6] = 0;
		out[7] = 0;
		out[8] = versionMinor;
		out[9] = 0;
		out[10] = 0;
		out[11] = 0;
	}
}