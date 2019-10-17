package q2p.animatedsourcespray.engine.dithering;

import q2p.animatedsourcespray.base.*;

public abstract class DitheringAlgorithm {
	public static final DitheringAlgorithm disabled = new DitheringDisabledAlgorithm();
	private static final DitheringAlgorithm[] ditheringAlgorithms = {
		disabled,
		new PurelyRandomDithering(),
		new RandomSymmetricDithering(),
		new OrderedDithering(5, 2, new int[] {
			1,3,
			4,2
		}),
		new OrderedDithering(10, 3, new int[] {
			1,8,4,
			7,6,3,
			5,2,9
		}),
		new OrderedDithering(17, 4, new int[] {
			 1,  9,  3, 11,
			13,  5, 15,  7,
			 4, 12,  2, 10,
			16,  8, 14,  6,
		}),
		new OrderedDithering(65, 8, new int[] {
			 1, 49, 13, 61,  4, 52, 16, 64,
			33, 17, 45, 29, 36, 20, 48, 32,
			 9, 57,  5, 53, 12, 60,  8, 56,
			41, 25, 37, 21, 44, 28, 40, 24,
			 3, 51, 15, 63,  2, 50, 14, 62,
			35, 19, 47, 31, 34, 18, 46, 30,
			11, 59,  7, 55, 10, 58,  6, 54,
			43, 27, 39, 23, 42, 26, 38, 22
		}),
		new ErrorDiffusionDithering("floyd_steinberg_dithering", 16, 3, new int[] {
			0, 0, 7,
			3, 5, 1
		}),
		new ErrorDiffusionDithering("jarvis_judice_ninke_dithering", 48, 5, new int[] {
			0, 0, 0, 7, 5,
			3, 5, 7, 5, 3,
			1, 3, 5, 3, 1
		}),
		new ErrorDiffusionDithering("sierra_2", 16, 5, new int[] {
			0, 0, 0, 4, 3,
			1, 2, 3, 2, 1
		}),
		new ErrorDiffusionDithering("sierra_2_4a", 4, 3, new int[] {
			0, 0, 2,
			1, 1, 0
		}),
		new ErrorDiffusionDithering("sierra_3", 32, 5, new int[] {
			0, 0, 0, 5, 3,
			2, 4, 5, 4, 2,
			0, 2, 3, 2, 0
		}),
		new ErrorDiffusionDithering("stucki", 42, 5, new int[] {
			0, 0, 0, 8, 4,
			2, 4, 8, 4, 2,
			1, 2, 4, 2, 1
		}),
		new ErrorDiffusionDithering("burkes", 32, 5, new int[] {
			0, 0, 0, 8, 4,
			2, 4, 8, 4, 2
		}),
		new ErrorDiffusionDithering("stevenson-arce", 200, 7, new int[] {
			 0,  0,  0,  0,  0, 32,  0,
			12,  0, 26,  0, 30,  0, 16,
			 0, 12,  0, 26,  0, 12,  0,
			 5,  0, 12,  0, 12,  0,  5
		}),
		new ErrorDiffusionDithering("atkinson", 8, 5, new int[] {
			0, 0, 0, 1, 1,
			0, 1, 1, 1, 0,
			0, 0, 1, 0, 0
		})
	};
	public static final DitheringAlgorithm defaultDithering = getByName("burkes");

	private static byte uidCounter = 0;
	public final byte uid = uidCounter++;
	private final String name;

	DitheringAlgorithm(final String name) {
		this.name = name;
	}

	public static DitheringAlgorithm getByName(String name) {
		name = name.toLowerCase();
		for(final DitheringAlgorithm algorithm : ditheringAlgorithms)
			if(algorithm.name.toLowerCase().equals(name))
				return algorithm;

		return null;
	}

	public abstract void dither(final int sizeX, final int sizeY, final byte[] intensity, final int offset, final byte step, final byte bitDepth);
}