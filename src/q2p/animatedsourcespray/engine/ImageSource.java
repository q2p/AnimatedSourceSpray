package q2p.animatedsourcespray.engine;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public final class ImageSource {
	private static int uidCounter = 0;
	public final int uid = uidCounter++;

	private static final Map<String, ImageSource> usedFiles = new TreeMap<>();

	private ImageSource() {}

	private String relativePath;
	public String getRelativePath() {
		return relativePath;
	}
	private String absolutePath;
	public String getAbsolutePath() {
		return absolutePath;
	}
	private int sizeX;
	public int getSizeX() {
		return sizeX;
	}
	private int sizeY;
	public int getSizeY() {
		return sizeY;
	}

	public static ImageSource getImageSource(String relativePath) {
		relativePath = relativePath.replace('\\', '/');

		String absolutePath = Arguments.getWorkingDirectory().resolve(relativePath).toAbsolutePath().toString();

		absolutePath = absolutePath.replace('\\', '/');

		final ImageSource put = new ImageSource();
		final ImageSource got = usedFiles.putIfAbsent(absolutePath, put);

		if(got != null)
			return got;

		put.absolutePath = absolutePath;
		put.relativePath = relativePath;
		return put;
	}

	public static void loadSizes() {
		usedFiles.forEach((path, imageSource) -> {
			try {
				final BufferedImage img = ImageIO.read(new File(imageSource.absolutePath));
				imageSource.sizeX = img.getWidth();
				imageSource.sizeY = img.getHeight();
				TimeLine.putSize(imageSource.sizeX, imageSource.sizeY);
			} catch(final Throwable t) {
				t.printStackTrace();
				System.out.println("TODO");
				System.exit(1);
			}
		});
	}

	private final Map<CUID, ResizedImage> sizes = new TreeMap<>();

	public ResizedImage resizeTo(final int sizeX, final int sizeY) {
		final CUID cuid = new CUID(sizeX, sizeY);

		if(sizes.containsKey(cuid))
			return sizes.get(cuid);

		final ResizedImage ret = new ResizedImage(this, sizeX, sizeY);
		sizes.put(cuid, ret);

		return ret;
	}

	public static void makeAllMiniatures() {
		usedFiles.forEach((path, imageSource) -> {
			imageSource.sizes.forEach((cuid, resizedImage) -> {
				Resizer.push(resizedImage);
			});
		});

		Resizer.process();
	}
}