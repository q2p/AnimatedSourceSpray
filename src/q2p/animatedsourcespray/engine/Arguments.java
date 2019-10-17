package q2p.animatedsourcespray.engine;

import q2p.animatedsourcespray.base.*;
import q2p.animatedsourcespray.conversion.dataFormat.*;
import q2p.animatedsourcespray.engine.dithering.*;

import java.nio.file.*;
import java.util.*;

public final class Arguments {
	private static String ffmpegPath = null;

	public static String getFfmpegPath() {
		return ffmpegPath;
	}
	private static String gamePath = null;
	public static final LinkedList<Integer> sizes = new LinkedList<>();
	public static final LinkedList<Short> mips = new LinkedList<>();
	public static final LinkedList<Integer> frameRates = new LinkedList<>();
	public static final LinkedList<VTFDataFormat> dataFormats = new LinkedList<>();
	public static final LinkedList<Short> alphaThresholds = new LinkedList<>();
	public static final LinkedList<DitheringAlgorithm> ditheringAlgorithms = new LinkedList<>();

	public static final LinkedList<MipsSource> mipsSources = new LinkedList<>();

	private static int desiredThreadsAmount = -1;
	public static int getDesiredThreadsAmount() {
		return desiredThreadsAmount;
	}

	private static String workDir = null;
	private static Path workPath = null;
	public static Path getWorkingDirectory() {
		return workPath;
	}
	private static final String defaultTempName = "delete_me_temp_dir_for_AnimatedSourceSpray_kHSSXqgqlEW15QquTakVexnbu3nAqBaURpS2G9lk30sZsxxH";
	private static String tempDir = null;
	private static Path tempPath = null;
	public static Path getTempDirectory() {
		return tempPath;
	}

	public static boolean parse(final String[] args) throws Throwable {
		for(int i = 0; i != args.length;) {
			final String cArg = args[i];
			switch(cArg) {
				case TextData.inRussian:
					TextData.setRussian();
					i++;
					break;
				case TextData.inSize:
					sizes.add(getInteger(args, i+1, 1, Sizes.maxUInt16));
					i += 2;
					break;
				case TextData.inMipmapCount:
					mips.add((short)getInteger(args, i+1, 1, Sizes.maxUInt8));
					i += 2;
					break;
				case TextData.inFPS:
					frameRates.add(getInteger(args, i+1, 0, Integer.MAX_VALUE));
					i += 2;
					break;
				case TextData.inThreads:
					if(desiredThreadsAmount != -1)
						throw new Throwable("TODO");

					desiredThreadsAmount = getInteger(args, i+1, 0, Integer.MAX_VALUE);
					if(desiredThreadsAmount == 0)
						desiredThreadsAmount = Runtime.getRuntime().availableProcessors();
					else
						desiredThreadsAmount = Math.min(Runtime.getRuntime().availableProcessors(), desiredThreadsAmount);

					i += 2;
					break;
				case TextData.inWorkingDir:
					workDir = getString(args, i+1, workDir);
					i += 2;
					break;
				case TextData.inTempDir:
					tempDir = getString(args, i+1, tempDir);
					i += 2;
					break;
				case TextData.inFfmpegPath:
					ffmpegPath = getString(args, i+1, ffmpegPath);
					i += 2;
					break;
				case TextData.inAlphaThreshold:
					alphaThresholds.add((short) getInteger(args, i+1, 0, 255));
					i += 2;
					break;
				case TextData.inVTFFormat:
					if(i+1 == args.length)
						throw new Throwable("TODO");

					final VTFDataFormat format = VTFDataFormat.getByName(args[i+1]);
					if(format == null)
						throw new Throwable("TODO");

					dataFormats.add(format);
					i += 2;
					break;
				case TextData.inDithering:
					if(i+1 == args.length)
						throw new Throwable("TODO");

					final DitheringAlgorithm ditheringAlgorithm = DitheringAlgorithm.getByName(args[i+1]);
					if(ditheringAlgorithm == null)
						throw new Throwable("TODO: unknown dithering: "+ args[i+1]);

					ditheringAlgorithms.add(ditheringAlgorithm);
					i += 2;
					break;
				default:
					if(cArg.startsWith(TextData.inMipImageSourcePrefix)) {
						final int layer;
						try {
							layer = Integer.parseInt(cArg.substring(TextData.inMipImageSourcePrefix.length()));
						} catch(final Throwable e) {
							throw new Throwable("TODO");
						}
						if(layer < 1)
							throw new Throwable("TODO");
						if(layer > 0x100)
							throw new Throwable("TODO");

						final MipImagesSource src = new MipImagesSource((short)(layer-1));

						mipsSources.add(src);

						i++;

						while(i != args.length) {
							final String nArg = args[i];
							if(nArg.startsWith("-"))
								break;
							src.pushPath(nArg);
							i++;
						}
					} else if(cArg.startsWith(TextData.inMipVideoSourcePrefix)) {
						final int layer;
						try {
							layer = Integer.parseInt(cArg.substring(TextData.inMipVideoSourcePrefix.length()));
						} catch(final Throwable e) {
							throw new Throwable("TODO");
						}
						if(layer < 1)
							throw new Throwable("TODO");
						if(layer > 0x100)
							throw new Throwable("TODO");

						if(i+3 >= args.length)
							throw new Throwable("TODO");
						i++;

						final String source = args[i++];
						final int start = getInteger(args, i++, -1, Integer.MAX_VALUE);
						final int end = getInteger(args, i++, -1, Integer.MAX_VALUE);
						if(end <= start && end != -1)
							throw new Throwable("TODO");

						mipsSources.add(new MipVideoSource((short)(layer-1), source, start, end));
					} else {
						throw new Throwable("TODO");
					}
					break;
			}
		}

		if(desiredThreadsAmount == -1)
			desiredThreadsAmount = 1;

		workPath = Paths.get(workDir == null ? "file.txt" : workDir).toAbsolutePath().getParent().toAbsolutePath();
		tempPath = workPath.resolve(tempDir == null ? defaultTempName : tempDir).toAbsolutePath();

		try {
			tempPath = Files.createDirectories(tempPath);
		} catch(final Throwable t) {
			throw new Throwable("TODO");
		}

		return true;
	}

	private static String getString(final String[] args, final int offset, final String currentValue) throws Throwable {
		if(offset == args.length)
			throw new Throwable("TODO");

		if(currentValue != null)
			throw new Throwable("TODO");

		return args[offset];
	}

	private static int getInteger(final String[] args, final int offset, final int minValue, final int maxValue) throws Throwable {
		if(offset == args.length)
			throw new Throwable("TODO");

		final int ret;
		try {
			ret = Integer.parseInt(args[offset]);
		} catch(final Throwable e) {
			throw new Throwable("TODO");
		}
		if(ret < minValue)
			throw new Throwable("TODO");
		if(ret > maxValue)
			throw new Throwable("TODO");

		return ret;
	}
}