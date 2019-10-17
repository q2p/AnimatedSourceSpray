package q2p.animatedsourcespray.engine;

import q2p.animatedsourcespray.conversion.dataFormat.*;

import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public final class ResizedImage {
	private final ImageSource imageSource;
	public final int sizeX, sizeY;
	private final String outFileName;
	Path outFilePath;
	private final boolean downScaling;

	public ResizedImage(final ImageSource imageSource, final int sizeX, final int sizeY) {
		this.imageSource = imageSource;
		this.sizeX = sizeX;
		this.sizeY = sizeY;

		downScaling = sizeX < imageSource.getSizeX() || sizeY < imageSource.getSizeY();

		outFileName = generateOutFileName();

		outFilePath = Arguments.getTempDirectory().resolve(outFileName).toAbsolutePath();
	}

	private String generateOutFileName() {
		final byte[] relPath = imageSource.getRelativePath().getBytes(StandardCharsets.UTF_8);
		final ByteBuffer bb = ByteBuffer.allocate(1 + relPath.length + 4 + 4);
		bb.put((byte) 0); // Mark the file as result of resize operation
		System.arraycopy(relPath, 0, bb.array(), 1, relPath.length);
		bb.position(1 + relPath.length);
		bb.putShort((short) sizeX);
		bb.putShort((short) sizeY);
		final byte[] base64 = Base64.getEncoder().withoutPadding().encode(bb.array());
		return new String(base64, StandardCharsets.US_ASCII);
	}

	public void resize() {
		// TODO: не менять разрешение если файл пододит 1 к 1 по разрешению
		if(Files.exists(outFilePath))
			return;

		final List<String> args = new LinkedList<>();

		args.add(Arguments.getFfmpegPath());
		args.add("-loglevel"); args.add("error"); // quiet, panic, fatal, error, warning, info

		args.add("-i"); args.add(imageSource.getAbsolutePath());

		args.add("-threads"); args.add("1");

		// args.add("-field_order"); args.add("progressive");

		args.add("-c:v"); args.add("rawvideo");

		args.add("-f"); args.add("rawvideo");

		args.add("-pix_fmt"); args.add("bgra");

		// args.add("-sws_flags"); args.add("\"accurate_rnd;"+(downScaling ? "lanczos" : "bilinear")+";full_chroma_int\"");

		// args.add("-sws_dither"); args.add("none");

		// args.add("-s"); args.add(sizeX+"x"+sizeY);

		args.add("-an");

		args.add("-y");
		args.add(outFilePath.toString().replace('\\', '/'));

		System.out.println(args);

		final ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
		pb.redirectError(ProcessBuilder.Redirect.INHERIT);

		final Process p;
		final long startTime = System.currentTimeMillis();
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

		System.out.println("FFMPEG Time: " + (System.currentTimeMillis()-startTime));
	}
}
