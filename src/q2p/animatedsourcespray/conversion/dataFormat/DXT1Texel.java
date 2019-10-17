package q2p.animatedsourcespray.conversion.dataFormat;

import q2p.animatedsourcespray.base.*;
import q2p.animatedsourcespray.base.shorters.*;
import q2p.animatedsourcespray.engine.*;

import java.nio.*;

final class DXT1Texel implements Runnable {
	private final byte[] bgra8888;
	private final int canvasSizeX;
	private final int offsetX;
	private final int offsetY;
	private final int texelSizeX;
	private final int texelSizeY;
	private final byte[] dxt1Output;
	private final int dxt1Offset;
	private final int alphaThreshold;

	private final int[][] colorsPalleteBGRA = new int[16][4];
	private final byte[] colorsUsed = new byte[16];
	private int colorsAmount = 0;

	private int[][] bestPallete = new int[2][];
	private int minDistance = Integer.MAX_VALUE;
	private boolean secondBlendingAlgoritm = false;

	DXT1Texel(final byte[] bgra8888, final int canvasSizeX, final int offsetX, final int offsetY, final int texelSizeX, final int texelSizeY, final byte[] dxt1Output, final int dxt1Offset, int alphaThreshold) {
		this.bgra8888 = bgra8888;
		this.canvasSizeX = canvasSizeX;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.texelSizeX = texelSizeX;
		this.texelSizeY = texelSizeY;
		this.dxt1Output = dxt1Output;
		this.dxt1Offset = dxt1Offset;
		this.alphaThreshold = alphaThreshold;
	}

	public void run() {
		extractColors();

		buildLines();

		final short[] pallete = new short[2]; // RGB565 + RGB565
		final int[][] colors = new int[4][4]; // RGBA8888 x 4

		pallete[0] = (short) (
			(ColorDepth.eightBitToLowerDepth(bestPallete[0][2], 5) << 11) |// BGRA8888 -> RGB888 -> RGB565
			(ColorDepth.eightBitToLowerDepth(bestPallete[0][1], 6) <<  5) |// BGRA8888 -> RGB888 -> RGB565
			(ColorDepth.eightBitToLowerDepth(bestPallete[0][0], 5)      )  // BGRA8888 -> RGB888 -> RGB565
		);
		pallete[1] = (short) (
			(ColorDepth.eightBitToLowerDepth(bestPallete[1][2], 5) << 11) |// BGRA8888 -> RGB888 -> RGB565
			(ColorDepth.eightBitToLowerDepth(bestPallete[1][1], 6) <<  5) |// BGRA8888 -> RGB888 -> RGB565
			(ColorDepth.eightBitToLowerDepth(bestPallete[1][0], 5)      )  // BGRA8888 -> RGB888 -> RGB565
		);


		if(secondBlendingAlgoritm) {
			if((0xFFFF & pallete[0]) > (0xFFFF & pallete[1])) {
				final short t = pallete[0];
				pallete[0] = pallete[1];
				pallete[1] = t;
			}
		} else {
			if((0xFFFF & pallete[0]) <= (0xFFFF & pallete[1])) {
				final short t = pallete[0];
				pallete[0] = pallete[1];
				pallete[1] = t;
			}
		}

		expandColor(colors[0], pallete[0]);
		expandColor(colors[1], pallete[1]);

		if(secondBlendingAlgoritm) {
			interpolateColor1_1(colors[2], colors[0], colors[1]);
			colors[3][0] = 0;
			colors[3][1] = 0;
			colors[3][2] = 0;
			colors[3][3] = 0;
		} else {
			interpolateColor3_1(colors[2], colors[0], colors[1]);
			interpolateColor3_1(colors[3], colors[1], colors[0]);
		}

		final ByteBuffer bb = ByteBuffer.wrap(dxt1Output);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.position(dxt1Offset);
		bb.putShort(pallete[0]);
		bb.putShort(pallete[1]);

		for(int y = 0; y != 4; y++) {
			byte row = 0;
			for(int x = 0; x != 4; x++) {
				final int closest = findClosest(bgra8888, ((offsetY+y)*canvasSizeX+offsetX+x)*4, colors);
				row |= closest << x*2;
			}
			bb.put(row);
		}
	}

	private void buildLines() {
		if(colorsAmount == 1) {
			// TODO: если цвет прозрачный, то надо secondBlendingAlgoritm = true
			if(colorsPalleteBGRA[0][3] >= alphaThreshold) {
				bestPallete[0] = colorsPalleteBGRA[0];
				bestPallete[1] = colorsPalleteBGRA[0];
				secondBlendingAlgoritm = false;
			} else {
				bestPallete[0] = new int[] { 0, 0, 0, 0 };
				bestPallete[1] = new int[] { 0, 0, 0, 0 };
				secondBlendingAlgoritm = true;
			}
			return;
		}

		for(int i = 0; i != colorsAmount - 1; i++)
			for(int j = i + 1; j != colorsAmount; j++)
				testColors(colorsPalleteBGRA[i], colorsPalleteBGRA[j]);

		for(int i = 0; i != colorsAmount - 1; i++)
			for(int j = i + 1; j != colorsAmount; j++)
				testLine(colorsPalleteBGRA[i], colorsPalleteBGRA[j]);
	}

	private void testLine(final int[] bgra1, final int[] bgra2) {

	}

	/*
	int inline GetIntersection( float fDst1, float fDst2, CVec3 P1, CVec3 P2, CVec3 &Hit) {
		if ( (fDst1 * fDst2) >= 0.0f)
			return false;
		if ( fDst1 == fDst2)
			return false;
		Hit = P1 + (P2-P1) * ( -fDst1/(fDst2-fDst1) );
		return true;
	}

	int inline InBox( CVec3 Hit, CVec3 B1, CVec3 B2, const int Axis) {
		if ( Axis==1 && Hit.z > B1.z && Hit.z < B2.z && Hit.y > B1.y && Hit.y < B2.y) return true;
		if ( Axis==2 && Hit.z > B1.z && Hit.z < B2.z && Hit.x > B1.x && Hit.x < B2.x) return true;
		if ( Axis==3 && Hit.x > B1.x && Hit.x < B2.x && Hit.y > B1.y && Hit.y < B2.y) return true;
		return false;
	}

	// returns true if line (L1, L2) intersects with the box (B1, B2)
	// returns intersection point in Hit
	int CheckLineBox(CVec3 B1, CVec3 B2, CVec3 L1, CVec3 L2, CVec3 &Hit) {
		if(
			L1.x > B1.x && L1.x < B2.x &&
			L1.y > B1.y && L1.y < B2.y &&
			L1.z > B1.z && L1.z < B2.z
		) {
			Hit = L1;
			return true;
		}
		if(
			(GetIntersection( L1.x-B1.x, L2.x-B1.x, L1, L2, Hit) && InBox( Hit, B1, B2, 1 )) ||
			(GetIntersection( L1.y-B1.y, L2.y-B1.y, L1, L2, Hit) && InBox( Hit, B1, B2, 2 )) ||
			(GetIntersection( L1.z-B1.z, L2.z-B1.z, L1, L2, Hit) && InBox( Hit, B1, B2, 3 )) ||
			(GetIntersection( L1.x-B2.x, L2.x-B2.x, L1, L2, Hit) && InBox( Hit, B1, B2, 1 )) ||
			(GetIntersection( L1.y-B2.y, L2.y-B2.y, L1, L2, Hit) && InBox( Hit, B1, B2, 2 )) ||
			(GetIntersection( L1.z-B2.z, L2.z-B2.z, L1, L2, Hit) && InBox( Hit, B1, B2, 3 ))
		) return true;

		return false;
	}
	*/

	private void testColors(
		final int[] bgra1,
		final int[] bgra2
	) {
		final int[][] pallete1 = new int[4][4];
		final int[][] pallete2 = new int[4][4];

		pallete1[0][0] = ColorDepth.lowDepthTo8Bit(ColorDepth.eightBitToLowerDepth(bgra1[0], 5), 5);
		pallete1[0][1] = ColorDepth.lowDepthTo8Bit(ColorDepth.eightBitToLowerDepth(bgra1[1], 6), 6);
		pallete1[0][2] = ColorDepth.lowDepthTo8Bit(ColorDepth.eightBitToLowerDepth(bgra1[2], 5), 5);
		pallete1[0][3] = 255;

		pallete1[1][0] = ColorDepth.lowDepthTo8Bit(ColorDepth.eightBitToLowerDepth(bgra2[0], 5), 5);
		pallete1[1][1] = ColorDepth.lowDepthTo8Bit(ColorDepth.eightBitToLowerDepth(bgra2[1], 6), 6);
		pallete1[1][2] = ColorDepth.lowDepthTo8Bit(ColorDepth.eightBitToLowerDepth(bgra2[2], 5), 5);
		pallete1[1][3] = 255;

		pallete2[0] = pallete1[0];
		pallete2[1] = pallete1[1];

		interpolateColor3_1(pallete1[2], pallete1[0], pallete1[1]);
		interpolateColor3_1(pallete1[3], pallete1[1], pallete1[0]);

		interpolateColor1_1(pallete2[2], pallete2[0], pallete2[1]);
		pallete2[3][0] = 0;
		pallete2[3][1] = 0;
		pallete2[3][2] = 0;
		pallete2[3][3] = 0;

		final int d1 = checkDistance(pallete1);
		final int d2 = checkDistance(pallete2);

		if(d1 < d2) {
			if(d1 < minDistance) {
				bestPallete[0] = pallete1[0];
				bestPallete[1] = pallete1[1];
				minDistance = d1;
				secondBlendingAlgoritm = false;
			}
		} else {
			if(d2 < minDistance) {
				bestPallete[0] = pallete1[0];
				bestPallete[1] = pallete1[1];
				minDistance = d2;
				secondBlendingAlgoritm = true;
			}
		}
	}

	private int checkDistance(final int[][] pallete) {
		int distance = 0;
		for(byte i = 0; i != colorsAmount; i++) {
			int mDistance = Integer.MAX_VALUE;
			for(byte j = 0; j != 4; j++) {
				final int d1 = pallete[j][0] - colorsPalleteBGRA[i][0];
				final int d2 = pallete[j][1] - colorsPalleteBGRA[i][1];
				final int d3 = pallete[j][2] - colorsPalleteBGRA[i][2];
				final int d4 = pallete[j][3] - colorsPalleteBGRA[i][3];
				final int d = d1*d1 + d2*d2 + d3*d3 + d4*d4;
				if(d < mDistance)
					mDistance = d;
			}
			distance += mDistance * colorsUsed[i];
		}
		return distance;
	}

	private void extractColors() {
		for(int y = 0; y != texelSizeY; y++) {
			for(int x = 0; x != texelSizeX; x++) {
				final byte b  = bgra8888[((offsetY+y)*canvasSizeX+offsetX+x)*4  ];
				final byte g  = bgra8888[((offsetY+y)*canvasSizeX+offsetX+x)*4+1];
				final byte r  = bgra8888[((offsetY+y)*canvasSizeX+offsetX+x)*4+2];
				final byte a  = bgra8888[((offsetY+y)*canvasSizeX+offsetX+x)*4+3];

				for(int i = 0;; i++) {
					if(colorsUsed[i] == 0) {
						colorsPalleteBGRA[i][0] = 0xFF & b;
						colorsPalleteBGRA[i][1] = 0xFF & g;
						colorsPalleteBGRA[i][2] = 0xFF & r;
						colorsPalleteBGRA[i][3] = 0xFF & a;
						colorsUsed[i] = 1;
						colorsAmount++;
						break;
					}
					if(
						colorsPalleteBGRA[i][0] == (0xFF & b) &&
						colorsPalleteBGRA[i][1] == (0xFF & g) &&
						colorsPalleteBGRA[i][2] == (0xFF & r) &&
						colorsPalleteBGRA[i][3] == (0xFF & a)
					) {
						colorsUsed[i]++;
						break;
					}
				}
			}
		}
	}

	static String hex565Color(final short ... colors) {
		final StringBuilder sb = new StringBuilder();
		for(int i = 0; i != colors.length; i++) {
			sb.append(Integer.toBinaryString(0xFFFF & colors[i]));
			sb.append(" ");
		}
		sb.append("\n");
		for(int i = 0; i != colors.length; i++) {
			byte[] rgb888 = rgb565to888(colors[i]);
			HEX.encode(rgb888, 0, 3, sb);
			sb.append(" ");
		}
		return sb.toString();
	}

	private static byte[] rgb565to888(final short rgb565) {
		return new byte[] {
			(byte) ColorDepth.lowDepthTo8Bit((0b1111100000000000 & rgb565) >>> 11, 5),
			(byte) ColorDepth.lowDepthTo8Bit((0b0000011111100000 & rgb565) >>>  5, 6),
			(byte) ColorDepth.lowDepthTo8Bit((0b0000000000011111 & rgb565)       , 5),
		};
	}

	private static String hexColor(final byte[] ... colors) {
		final StringBuilder sb = new StringBuilder();
		for(int i = 0; i != colors.length; i++) {
			HEX.encode(colors[i], 0, 4, sb);
			if(i != colors.length-1)
				sb.append(", ");
		}
		return sb.toString();
	}

	private static int findClosest(final byte[] bgra8888, final int offset, final int[][] colors) {
		int best = 0;
		int minDistance = Integer.MAX_VALUE;
		for(byte i = 0; i != 4; i++) {
			final int d1 = (0xFF & bgra8888[offset+2]) - colors[i][0]; // bgra - rgba
			final int d2 = (0xFF & bgra8888[offset+1]) - colors[i][1]; // bgra - rgba
			final int d3 = (0xFF & bgra8888[offset  ]) - colors[i][2]; // bgra - rgba
			final int d4 = (0xFF & bgra8888[offset+3]) - colors[i][3]; // bgra - rgba
			final int d = d1*d1 + d2*d2 + d3*d3 + d4*d4;
			if(d < minDistance) {
				best = i;
				minDistance = d;
			}
		}

		return best;
	}

	private static void expandColor(final int[] color, final short rgb565) {
		color[0] = ColorDepth.lowDepthTo8Bit((0b1111100000000000 & rgb565) >>> 11, 5);
		color[1] = ColorDepth.lowDepthTo8Bit((0b0000011111100000 & rgb565) >>>  5, 6);
		color[2] = ColorDepth.lowDepthTo8Bit((0b0000000000011111 & rgb565)       , 5);
		color[3] = 255;
	}

	private static void interpolateColor3_1(final int[] output, final int[] color2_3, final int[] color1_3) {
		for(byte i = 0; i != 4; i++) {
			output[i] = Assist.limit(
				0,
				(int)Math.round(
					( 2 * color2_3[i] + color1_3[i] )
						/ 3.0d
				),
				255
			);
		}
	}

	private static void interpolateColor1_1(final int[] output, final int[] color1, final int[] color2) {
		for(byte i = 0; i != 4; i++) {
			output[i] = Assist.limit(
				0,
				(int)Math.round(
					(color1[i] + color2[i]) / 2.0d
				),
				255
			);
		}
	}
}