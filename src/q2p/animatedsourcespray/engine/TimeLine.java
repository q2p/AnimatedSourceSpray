package q2p.animatedsourcespray.engine;

import q2p.animatedsourcespray.base.*;

import java.nio.file.*;
import java.util.*;

final class TimeLine {
	private static int frames = -1;
	public static int frames() {
		assert frames > 0 && frames <= Sizes.maxUInt16;
		return frames;
	}
	private static final LinkedList<MipmapTimeLine> layers = new LinkedList<>();

	public static void init(final short maxMips) throws Throwable {
		final LinkedList<MipsSource> sources = new LinkedList<>();
		for(final MipsSource source : Arguments.mipsSources) {
			for(final MipsSource check : sources)
				if(check.layer == source.layer)
					throw new Throwable("TODO");

			sources.add(source);
		}

		if(sources.isEmpty())
			throw new Throwable("TODO");

		sources.sort(Comparator.comparingInt(o -> o.layer));

		if(sources.getFirst().layer != 0)
			sources.getFirst().layer = 0;

		for(final MipsSource source : sources)
			frames = Math.max(frames, source.getLength());

		for(final MipsSource source : sources)
			if(frames % source.getLength() != 0)
				throw new Throwable("TODO: duration missmatch");

		sources.removeIf(source -> source.layer >= maxMips);

		transferSources(sources);

		ImageSource.loadSizes();
	}

	private static void transferSources(final Iterable<MipsSource> sources) throws Throwable {
		for(final MipsSource source : sources) {
			final int prevSize = source.getLength();
			final MipmapTimeLine timeLine = new MipmapTimeLine(source.layer);
			layers.addLast(timeLine);

			if(source instanceof MipImagesSource) {
				final MipImagesSource imagesSource = (MipImagesSource) source;

				for(int i = 0; i != prevSize; i++)
					timeLine.paths[i] = ImageSource.getImageSource(imagesSource.paths.removeFirst());
			} else {
				assert source instanceof MipVideoSource;
				final MipVideoSource videoSource = (MipVideoSource) source;

				try(final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Arguments.getWorkingDirectory().resolve(videoSource.path))) {
					for(final Path path : directoryStream) {
						final String fileName = path.getFileName().toString();

						int end = 0;
						while(end != fileName.length() && "0123456789".indexOf(fileName.charAt(end)) != -1)
							end++;

						if(end == 0)
							continue;

						final int id;
						try {
							id = Integer.parseUnsignedInt(fileName.substring(0, end)) - videoSource.start;
						} catch(final NumberFormatException e) {
							continue;
						}

						if(id < 0 || id >= timeLine.paths.length)
							continue;

						if(timeLine.paths[id] == null)
							timeLine.paths[id] = ImageSource.getImageSource(videoSource.path+fileName);
					}
				} catch(final Throwable ignore) {}

				for(int i = 0; i != prevSize; i++)
					if(timeLine.paths[i] == null)
						throw new Throwable("TODO");
			}

			// Fill with pattern
			for(int i = prevSize; i != frames; i++)
				timeLine.paths[i] = timeLine.paths[i%prevSize];
		}
	}

	private static int maxSizeX = -1, maxSizeY = -1;
	public static void putSize(final int sizeX, final int sizeY) {
		maxSizeX = Math.max(maxSizeX, sizeX);
		maxSizeY = Math.max(maxSizeY, sizeY);
	}
	public static int getSizeX() {
		return maxSizeX;
	}
	public static int getSizeY() {
		return maxSizeY;
	}

	public static ImageSource[] getMipLine(final int mipId) {
		assert mipId >= 0 && layers.getFirst().layer == 0;

		final Iterator<MipmapTimeLine> iterator = layers.descendingIterator();
		while(iterator.hasNext()) {
			final MipmapTimeLine next = iterator.next();
			if(next.layer <= mipId)
				return next.paths;
		}
		throw new AssertionError();
	}

	static final class MipmapTimeLine {
		final short layer;
		private final ImageSource[] paths = new ImageSource[frames];

		private MipmapTimeLine(final short layer) {
			this.layer = layer;
		}
	}
}