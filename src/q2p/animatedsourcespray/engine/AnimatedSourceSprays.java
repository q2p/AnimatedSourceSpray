package q2p.animatedsourcespray.engine;

import q2p.animatedsourcespray.base.operatingsystem.*;
import q2p.animatedsourcespray.base.time.*;
import q2p.animatedsourcespray.conversion.dataFormat.*;
import q2p.animatedsourcespray.engine.dithering.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

// TODO: test -alpha_threshold 0; test -alpha_threshold 255
public final class AnimatedSourceSprays {
	public static void main(final String[] args) throws Throwable {
		try {
			if(!Arguments.parse(args))
				return;
		} catch(final Throwable t) {
			t.printStackTrace();
			System.out.println(t.getMessage());
			return;
		}

		// Import.read();

		if(OperatingSystem.getHostingOS() == OperatingSystem.Unsupported)
			System.out.println("TODO"); // TODO:

		final int[] frameRates = simplifyFramerates();
		final int[] sizes = simplifySizes();
		final short[] mipsAmounts = simplifyMipsAmount();
		final Collection<VTFDataFormat> dataFormats = simplifyCollection(Arguments.dataFormats, VTFDataFormat.defaultFormat);
		final Collection<DitheringAlgorithm> ditheringAlgorithms = simplifyCollection(Arguments.ditheringAlgorithms, DitheringAlgorithm.defaultDithering);
		final short[] alphaThresholds = simplifyAlphaThresholds(dataFormats);

		final short maxMips = mipsAmounts[mipsAmounts.length-1];
		TimeLine.init(maxMips);

		buildRenderTargets(sizes, mipsAmounts, dataFormats, alphaThresholds, ditheringAlgorithms);

		render();

		Import.read("./out1.vtf");
	}

	private static void buildRenderTargets(final int[] sizes, final short[] mipsAmounts, final Collection<VTFDataFormat> dataFormats, final short[] alphaThresholds, final Collection<DitheringAlgorithm> ditheringAlgorithms) {
		for(final VTFDataFormat dataFormat : dataFormats) {
			for(final short alphaThreshold : alphaThresholds) {
				if(!dataFormat.ditheringApplicable) {
					buildRenderTargetsWithHints(RenderHints.create(dataFormat, DitheringAlgorithm.disabled, alphaThreshold), sizes, mipsAmounts);
				} else {
					for(final DitheringAlgorithm ditheringAlgorithm : ditheringAlgorithms)
						buildRenderTargetsWithHints(RenderHints.create(dataFormat, ditheringAlgorithm, alphaThreshold), sizes, mipsAmounts);
				}
			}
		}
	}

	private static void buildRenderTargetsWithHints(final RenderHints renderHints, final int[] sizes, final short[] mipsAmounts) {
		for(final int desiredSize : sizes) {
			for(final short mipsAmount : mipsAmounts) {
				VTFTarget.build(renderHints, desiredSize, mipsAmount);
			}
		}
	}

	private static void render() {
		ImageSource.makeAllMiniatures();

		VTFFrame.renderAllFrames();

		VTFTarget.compose();
	}

	private static int[] simplifySizes() {
		if(Arguments.sizes.isEmpty())
			Arguments.sizes.add(256);

		final LinkedList<Integer> buffer = new LinkedList<>();
		Collections.sort(Arguments.sizes);
		while(!Arguments.sizes.isEmpty()) {
			final int size = Arguments.sizes.removeFirst();
			if(buffer.contains(size))
				System.out.println("WARNING: TODO");
			else
				buffer.addLast(size);
		}

		final int[] ret = new int[buffer.size()];
		for(int i = 0; !buffer.isEmpty(); i++)
			ret[i] = buffer.removeFirst();

		return ret;
	}

	private static <T> Collection<T> simplifyCollection(final LinkedList<T> in, final T defaultValue) {
		if(in.isEmpty()) {
			in.add(defaultValue);
			System.out.println("Warning: TODO");
		}

		final Deque<T> temp = new LinkedList<>();
		while(!in.isEmpty()) {
			final T value = in.removeFirst();
			if(temp.contains(value))
				System.out.println("WARNING: TODO");
			else
				temp.addLast(value);
		}
		final Collection<T> ret = new ArrayList<>(temp.size());
		while(!temp.isEmpty())
			ret.add(temp.removeFirst());

		return ret;
	}

	public static final short defaultAlphaThreshold = 1;
	private static short[] simplifyAlphaThresholds(final Collection<VTFDataFormat> dataFormats) {
		for(final VTFDataFormat format : dataFormats) {
			if(format.aBits < 2) {
				if(Arguments.alphaThresholds.isEmpty())
					Arguments.alphaThresholds.add(defaultAlphaThreshold);

				final LinkedList<Short> alphaThresholdsBuffer = new LinkedList<>();
				while(!Arguments.alphaThresholds.isEmpty()) {
					final short threshold = Arguments.alphaThresholds.removeFirst();
					if(alphaThresholdsBuffer.contains(threshold))
						System.out.println("WARNING: TODO");
					else
						alphaThresholdsBuffer.addLast(threshold);
				}

				final short[] ret = new short[alphaThresholdsBuffer.size()];
				for(int i = 0; !alphaThresholdsBuffer.isEmpty(); i++)
					ret[i] = alphaThresholdsBuffer.removeFirst();

				return ret;
			}
		}

		return new short[] { 0 };
	}

	private static short[] simplifyMipsAmount() {
		if(Arguments.mips.isEmpty())
			Arguments.mips.add((short)1);

		final LinkedList<Short> buffer = new LinkedList<>();
		Collections.sort(Arguments.mips);
		while(!Arguments.mips.isEmpty()) {
			final short mipsCount = Arguments.mips.removeFirst();
			if(buffer.contains(mipsCount))
				System.out.println("WARNING: TODO");
			else
				buffer.addLast(mipsCount);
		}

		final short[] ret = new short[buffer.size()];
		for(int i = 0; !buffer.isEmpty(); i++)
			ret[i] = buffer.removeFirst();

		return ret;
	}

	private static int[] simplifyFramerates() {
		if(Arguments.frameRates.isEmpty())
			Arguments.frameRates.add(0);

		final LinkedList<Integer> buffer = new LinkedList<>();
		Collections.sort(Arguments.frameRates);
		while(!Arguments.frameRates.isEmpty()) {
			final int fps = Arguments.frameRates.removeFirst();
			if(buffer.contains(fps))
				System.out.println("WARNING: TODO");
			else
				buffer.addLast(fps);
		}

		final int[] ret = new int[buffer.size()];
		for(int i = 0; !buffer.isEmpty(); i++)
			ret[i] = buffer.removeFirst();

		return ret;
	}

	private static RawImageArrayBGRA getImageData(final File file) throws IOException {
		assert file != null;

		ProfilingTimer read = new ProfilingTimer("Read");
		read.start();
		final BufferedImage img = ImageIO.read(file);
		read.pause();
		System.out.println(read.toString());

		int width = img.getWidth();
		int height = img.getHeight();

		final BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		ProfilingTimer draw = new ProfilingTimer("Draw");
		draw.start();
		img2.getGraphics().clearRect(0, 0, width, height);
		img2.getGraphics().drawImage(img, 0, 0, null);
		draw.pause();
		System.out.println(draw.toString());

		final int[] dataBuffInt = img2.getRGB(0, 0, width, height, null, 0, width);

		final RawImageArrayBGRA raw = new RawImageArrayBGRA(width, height);

		ProfilingTimer copy = new ProfilingTimer("Copy");
		copy.start();
		for(int y = 0; y != raw.sizeY; y++) {
			for(int x = 0; x != raw.sizeX; x++) {
				final int offset = y*raw.sizeX + x;
				raw.data[offset*4  ] = (byte) (0xFF &  dataBuffInt[offset]);
				raw.data[offset*4+1] = (byte) (0xFF & (dataBuffInt[offset] >> 8));
				raw.data[offset*4+2] = (byte) (0xFF & (dataBuffInt[offset] >> 16));
				raw.data[offset*4+3] = (byte) (0xFF & (dataBuffInt[offset] >> 24));
			}
		}
		copy.pause();
		System.out.println(copy.toString());
		return raw;
	}
}