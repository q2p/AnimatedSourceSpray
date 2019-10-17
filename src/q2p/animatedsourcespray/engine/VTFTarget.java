package q2p.animatedsourcespray.engine;

import q2p.animatedsourcespray.base.*;
import q2p.animatedsourcespray.conversion.*;
import q2p.animatedsourcespray.conversion.dataFormat.*;

import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

final class VTFTarget {
	private static final Deque<VTFTarget> targets = new LinkedList<>();

	private final RenderHints renderHints;
	private int largestSizeX, largestSizeY;
	private VTFFrame layersAndFrames[][];

	private VTFTarget(final RenderHints renderHints, final int maxSize, final short mipsAmount) {
		this.renderHints = renderHints;

		calculateSizes(renderHints, maxSize, mipsAmount);

		queueFrames();
	}

	private void calculateSizes(final RenderHints renderHints, final int maxSize, final short mipsAmount) {
		int desiredSize = maxSize;

		for(short i = 1; i != mipsAmount; i++)
			desiredSize = Assist.perfectPositiveCeil(desiredSize, 2);

		desiredSize = Assist.perfectPositiveCeil(desiredSize, renderHints.dataFormat.sizeMultipleOf) * renderHints.dataFormat.sizeMultipleOf;

		final int sizeX = TimeLine.getSizeX();
		final int sizeY = TimeLine.getSizeY();

		final int sourceSizeMin = Math.min(sizeX, sizeY);
		final int sourceSizeMax = Math.max(sizeX, sizeY);
		int smallerDesired = Assist.perfectPositiveCeil(sourceSizeMin * desiredSize, sourceSizeMax);

		for(short i = 1; i != mipsAmount; i++) {
			desiredSize *= 2;
			smallerDesired *= 2;
		}

		while(desiredSize > maxSize) {
			if(smallerDesired != 1)
				smallerDesired /= 2;
			desiredSize /= 2;
		}

		short allowedMips = 1;
		while(allowedMips != mipsAmount && smallerDesired % 2 == 0 && desiredSize % 2 == 0) {
			desiredSize /= 2;
			smallerDesired /= 2;
			allowedMips++;
		}

		for(short i = 1; i != allowedMips; i++) {
			desiredSize *= 2;
			smallerDesired *= 2;
		}

		layersAndFrames = new VTFFrame[allowedMips][TimeLine.frames()];

		if(sizeX >= sizeY) {
			largestSizeX = desiredSize;
			largestSizeY = smallerDesired;
		} else {
			largestSizeY = desiredSize;
			largestSizeX = smallerDesired;
		}
	}

	private void queueFrames() {
		int currentSizeX = largestSizeX;
		int currentSizeY = largestSizeY;
		for(int mipId = 0; mipId != layersAndFrames.length; mipId++) {
			final ImageSource[] mipLine = TimeLine.getMipLine(mipId);

			for(int frame = 0; frame != TimeLine.frames(); frame++)
				layersAndFrames[mipId][frame] = VTFFrame.build(mipLine[frame], currentSizeX, currentSizeY, renderHints);

			currentSizeX /= 2;
			currentSizeY /= 2;
		}
	}

	private static final byte VTF_RSRC_MAX_DICTIONARY_ENTRIES = 32;
	private static final float reflectivity1 = 0.323f; // TODO:
	private static final float reflectivity2 = 0.217f; // TODO:
	private static final float reflectivity3 = 0.155f; // TODO:
	private static final float bumpScale = 1f; // TODO:
	private void composeTarget() {
		final byte minorVersion = 2; // EZ
		final boolean nearestNeighbourFiltering = false; // EZ
		final int[] frameRates = { 0 }; // EZ
		boolean omitThumbnail = false;
		assert largestSizeX > 0 && largestSizeY > 0 && largestSizeX <= 0xFFFF && largestSizeY <= 0xFFFF;
		assert layersAndFrames.length > 0 && layersAndFrames.length <= 255;

		final byte[] fileData = new byte[128*Sizes.MiB]; // TODO:
		final ByteBuffer bb = ByteBuffer.wrap(fileData);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.put((byte)'V'); bb.put((byte)'T'); bb.put((byte)'F'); bb.put((byte)0); // Signature

		bb.putInt(7); // Major version
		bb.putInt(minorVersion); // Minor version
		if(minorVersion <= 3)
			bb.putInt(80); // Header Size
		else
			bb.putInt(80+8*VTF_RSRC_MAX_DICTIONARY_ENTRIES); // Header Size

		bb.putShort((short)largestSizeX);
		bb.putShort((short)largestSizeY);

		bb.putInt(VTFFlags.generate(nearestNeighbourFiltering, layersAndFrames.length == 1, layersAndFrames.length == 1, false, false, true));
		bb.putShort((short)TimeLine.frames());
		bb.putShort((short)0); // Start Frame

		bb.position(bb.position() + 4);

		bb.putFloat(reflectivity1);
		bb.putFloat(reflectivity2);
		bb.putFloat(reflectivity3);

		bb.position(bb.position() + 4);

		bb.putFloat(bumpScale);

		bb.putInt(renderHints.dataFormat.code);

		bb.put((byte)layersAndFrames.length);

		// TODO: возможность заигнорить
		bb.putInt(VTFDataFormat.dxt1.code); // Low res image format
		bb.put((byte)4); // Low res image width
		bb.put((byte)4); // Low res image height

		if(minorVersion >= 2) {
			bb.putShort((short)1); // Depth

			if(minorVersion >= 3) {
				bb.position(bb.position() + 3); // Padding
				if(omitThumbnail)
					bb.putInt(1); // Resources Amount: Image data
				else
					bb.putInt(2); // Resources Amount: Image data + Thumbnail
			}
		}

		bb.position(0x50); // Header Padding

		// TODO: placeHolderForThumbnail
		bb.putLong(0); // Thumbnail Placeholder : DXT1 4x4 = 64 bit = 1 long

		for(final VTFFrame[] mip : layersAndFrames)
			for(final VTFFrame frame : mip)
				frame.loadData(bb);

		bb.flip();

		try(final FileChannel fc = FileChannel.open(Arguments.getWorkingDirectory().resolve("out1.vtf"), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
			while(bb.hasRemaining())
				fc.write(bb);
		} catch(final Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	public static void build(final RenderHints renderHints, final int desiredSize, final short mipsAmount) {
		targets.add(new VTFTarget(renderHints, desiredSize, mipsAmount));
	}
	public static void compose() {
		while(!targets.isEmpty())
			targets.removeFirst().composeTarget();
	}
}
