package q2p.animatedsourcespray.engine;

import q2p.animatedsourcespray.base.*;
import q2p.animatedsourcespray.conversion.dataFormat.*;

import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public final class VTFFrame {
	private static final Map<CUID, VTFFrame> usedFiles = new TreeMap<>();

	private final RenderHints renderHints;
	private final ResizedImage resizedImage;
	private final int canvasSizeX;
	private final int canvasSizeY;

	private final CUID guid;

	private VTFFrame(final ImageSource source, final RenderHints renderHints, int canvasSizeX, int canvasSizeY) {
		this.renderHints = renderHints;
		this.canvasSizeX = canvasSizeX;
		this.canvasSizeY = canvasSizeY;

		guid = new CUID(
			source.getRelativePath().getBytes(StandardCharsets.UTF_8),
			renderHints.guid,
			source.getSizeX(), source.getSizeY(),
			canvasSizeX, canvasSizeY
		);

		final double canvasAspect = (double) canvasSizeX / (double) canvasSizeY;
		final double imageAspect = (double) source.getSizeX() / (double) source.getSizeY();

		final int newSizeX, newSizeY;

		if(canvasAspect > imageAspect) { // Canvas is wider than image
			newSizeX = Assist.limit(1, source.getSizeX() * canvasSizeY / source.getSizeY(), canvasSizeX);
			newSizeY = canvasSizeY;
		} else { // Canvas is taller than image
			newSizeX = canvasSizeX;
			newSizeY = Assist.limit(1, source.getSizeY() * canvasSizeX / source.getSizeX(), canvasSizeY);
		}

		resizedImage = source.resizeTo(newSizeX, newSizeY);
	}

	public static VTFFrame build(final ImageSource source, final int canvasSizeX, final int canvasSizeY, final RenderHints renderHints) {
		final CUID cuid = new CUID(source.uid, renderHints.guid, canvasSizeX, canvasSizeY);

		if(usedFiles.containsKey(cuid))
			return usedFiles.get(cuid);

		final VTFFrame ret = new VTFFrame(source, renderHints, canvasSizeX, canvasSizeY);
		usedFiles.put(cuid, ret);

		return ret;
	}

	public static void renderAllFrames() {
		usedFiles.forEach((cuid, vtfFrame) -> vtfFrame.renderFrame());
	}

	private void renderFrame() {
		byte[] imagePixelData = new byte[resizedImage.sizeX * resizedImage.sizeY * 4];
		readRaw(imagePixelData);

		imagePixelData = renderHints.dataFormat.convert(resizedImage.sizeX, resizedImage.sizeY, imagePixelData, canvasSizeX, canvasSizeY, renderHints);

		exportResult(imagePixelData);

		// final byte[] originalBGRA8888 = renderHints.dataFormat.extract(canvasSizeX, canvasSizeY, imagePixelData);

		// displayBGRA(canvasSizeX, canvasSizeY, originalBGRA8888, resizedImage.imageSource.uid, renderHints.dataFormat, renderHints.ditheringAlgorithm.name);
	}

	private void exportResult(final byte[] result) {
		final ByteBuffer bb = ByteBuffer.wrap(result);

		try(final FileChannel fc = FileChannel.open(Arguments.getTempDirectory().resolve("1"+guid.toBase64()), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
			while(bb.hasRemaining())
				fc.write(bb);
		} catch(final Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	public void loadData(final ByteBuffer bb) {
		try(final FileChannel fc = FileChannel.open(Arguments.getTempDirectory().resolve("1"+guid.toBase64()), StandardOpenOption.READ)) {
			bb.limit((int)(bb.position()+fc.size()));
			while(bb.hasRemaining())
				fc.read(bb);
		} catch(final Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
		bb.limit(bb.capacity());
	}

	private static int debugUID = 0;
	private void displayBGRA(final int sizeX, final int sizeY, final byte[] bgra8888, final int imageUID, final VTFDataFormat dataFormat, final String ditheringName) {
		final ByteBuffer bb = ByteBuffer.wrap(bgra8888);
		final Path bgraPost = Arguments.getTempDirectory().resolve("bgra8888_" + debugUID).toAbsolutePath();
		try(final FileChannel fc = FileChannel.open(bgraPost, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
			while(bb.hasRemaining())
				fc.write(bb);
		} catch(final Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}

		final List<String> args = new LinkedList<>();

		args.add(Arguments.getFfmpegPath());
		args.add("-loglevel"); args.add("warning"); // quiet, panic, fatal, error, warning, info

		args.add("-threads"); args.add("1");

		args.add("-f"); args.add("rawvideo");

		args.add("-pix_fmt"); args.add("bgra");

		args.add("-s:v"); args.add(sizeX+"x"+sizeY);

		args.add("-r"); args.add("1");

		args.add("-i"); args.add(bgraPost.toString());

		args.add("-c:v"); args.add("png");

		args.add("-field_order"); args.add("progressive");

		args.add("-pix_fmt"); args.add("rgba");

		args.add("-an");

		args.add("-y");
		args.add(Arguments.getTempDirectory().resolve(imageUID+"_"+dataFormat.externalName+"_"+ditheringName+".png").toAbsolutePath().toString());

		System.out.println(args);

		final ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
		pb.redirectError(ProcessBuilder.Redirect.INHERIT);

		final Process p;
		try {
			p = pb.start();
			try {
				p.waitFor();
			} catch(final InterruptedException ignore) {
				assert false;
			}
		} catch(final Throwable e) {
			e.printStackTrace(); // TODO:
			System.out.println("TODO");
			System.exit(1);
		}

		debugUID++;
	}

	private void readRaw(final byte[] array) {
		final ByteBuffer bb = ByteBuffer.wrap(array);

		try(final FileChannel fc = FileChannel.open(resizedImage.outFilePath, StandardOpenOption.READ)) {
			if(fc.size() != bb.capacity()) {
				System.out.println("TODO: SIZE MISSMATCH");
				System.exit(1);
			}

			while(bb.hasRemaining())
				fc.read(bb);
		} catch(final Throwable t) {
			System.out.println("TODO");
			System.exit(1);
		}
	}
}