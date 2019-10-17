package q2p.animatedsourcespray.engine;

final class TextData {
	public static final String[] inHelp = { "h", "help" };
	public static final String inFfmpegPath = "-ffmpeg";
	public static final String inGamePath = "-game";
	public static final String inFPS = "-fps";
	public static final String inSize = "-size";
	public static final String inMipmapCount = "-mips";
	public static final String inRussian = "-ru";
	public static final String inVTFFormat = "-format";
	public static final String inAlphaThreshold = "-alpha_threshold";
	public static final String inMipImageSourcePrefix = "-i";
	public static final String inMipVideoSourcePrefix = "-v";
	public static final String inDithering = "-dither";
	public static final String inThreads = "-threads";
	public static final String inWorkingDir = "-working_dir";
	public static final String inTempDir = "-temp_dir";

	public static final String outEnWrongOS = "Warning: The Operating System you are using is not supported by the software. This may produce errors.\n\n";
	public static final String outRuWrongOS = "Внимание: Операционная Система, которой вы используете не поддерживается приложением. Из-за этого могут возникнуть ошибки при работе.\n\n";

	public static final String outEnInvalidArgument = "Error: Invalid argument found:\n";
	public static final String outRuInvalidArgument = "Ошибка: Найден не допустимый аргумент:\n";

	public static final String outEnHelp = "Help:\nInvalid argument found:\n";
	public static final String outRuHelp = "Помощ:\nНайден не допустимый параметр:\n";

	public static final String outRussianRequestedTooManyTimes = "Внимание: параметр \"-"+inRussian+"\" используется несколько раз.";

	public static final String outEnFfmpegPathError = "Error: Invalid ffmpeg path specified.";
	public static final String outRuFfmpegPathError = "Ошибка: Указан не допустимый путь к ffmpeg.";
	public static final String outEnFfmpegPathOverused = "Error: ffmpeg path have been specified more then once.";

	private static boolean isRussian = false;
	public static boolean setRussian() {
		if(isRussian)
			return true;

		isRussian = true;
		return false;
	}
	public static boolean isRussian() {
		return isRussian;
	}
}