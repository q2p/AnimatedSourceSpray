package q2p.animatedsourcespray.conversion;

public final class VTFFlags {
	/*
		// TODO: depricated?
		PWL Corrected                              0x000040 Purpose unknown.
		SRGB                                       n/a      Uses space RGB. Useful for High Gamuts. Deprecated in 7.5.
		No Compress                                0x000040 No DXT compression used. Deprecated
		One Over Mipmap Level In Alpha             0x080000 Fill the alpha channel with 1/Mipmap Level. Deprecated (Internal to VTEX?)
		Premultiply Color By One Over Mipmap Level 0x100000 (Internal to VTEX?)
		Normal To DuDv                             0x200000 Texture is a DuDv map. (Internal to VTEX?)
    Alpha Test Mipmap Generation               0x400000 (Internal to VTEX?)
    SINGLECOPY                                 0x040000 // Newer flags from the *.txt config file
    NODEBUGOVERRIDE                            0x020000 // Newer flags from the *.txt config file
    // TODO: unused?
		ClampS                        (0x00000004, "Clamp S coordinates"), // CLAMPS
		ClampT                        (0x00000008, "Clamp T coordinates"), // CLAMPT
		ClampU                        (0x02000000, "Clamp U coordinates (for volumetric textures)"), // CLAMPU
		HintDXT5                      (0x00000020, "Used in skyboxes (makes sure edges are seamless)"), // HINT_DXT5
		Procedural                    (0x00000800, "Texture is an procedural texture (code can modify it)."),
		EnvironmentMap                (0x00004000, "Texture is an environment map"), // ENVMAP // Newer flags from the *.txt config file
		VertexTexture                 (0x04000000, "Usable as a vertex texture"), // VERTEXTEXTURE
		TrilinearFiltering            (0x00000002, "Trilinear filtering"), // TRILINEAR
		AnisotropicFiltering          (0x00000010, "Anisotropic filtering"), // ANISOTROPIC
		RenderMipmapsBelow32x32       (0x00000400, "If set, load mipmaps below 32x32 pixels"),
		RenderTarget                  (0x00008000, "Texture is a render target"), // RENDERTARGET // Newer flags from the *.txt config file
		DepthRenderTarget             (0x00010000, "Texture is a depth render target"), // DEPTHRENDERTARGET // Newer flags from the *.txt config file
		NoDepthBuffer                 (0x00800000, "Do not buffer for Video Processing, generally render distance (Z-Buffer)"), // NODEPTHBUFFER
		SSBump                        (0x08000000, "Texture is a SSBump (SSB)"), // SSBUMP
		Border                        (0x20000000, "Clamp to border colour on all texture coordinates"); // BORDER
	*/
	private static final int NearestNeighborFiltering = 0x00000001; // "Nearest neighbor filtering (pixel art)"), // POINTSAMPLE
	private static final int DisableMipmaps           = 0x00000100; // "Render largest mipmap only (ignores other mipmaps in game)"), // NOMIP
	private static final int NoLevelOfDetail          = 0x00000200; // "Not affected by texture resolution settings."), // NOLOD
	private static final int Alpha1Bit                = 0x00001000; // "One bit alpha channel used"), // ONEBITALPHA // These are automatically generated by vtex from the texture data.
	private static final int Alpha8Bit                = 0x00002000; // "Eight bit alpha channel used"), // EIGHTBITALPHA // These are automatically generated by vtex from the texture data.
	private static final int PreSRGB                  = 0x00080000; // "SRGB correction has already been applied"), // PRE_SRGB // Newer flags from the *.txt config file

	public static int generate(
		final boolean nearestNeighbourFiltering,
		final boolean disableMipMaps,
		final boolean noLOD,
		final boolean oneBitAlpha,
		final boolean eightBitAlpha,
		final boolean preSRGB
	) {
		int temp = 0;

		if(nearestNeighbourFiltering)
			temp |= NearestNeighborFiltering;
		if(disableMipMaps)
			temp |= DisableMipmaps;
		if(noLOD)
			temp |= NoLevelOfDetail;
		if(oneBitAlpha)
			temp |= Alpha1Bit;
		if(eightBitAlpha)
			temp |= Alpha8Bit;
		if(preSRGB)
			temp |= PreSRGB;

		return temp;
	}
}