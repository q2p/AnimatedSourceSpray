package q2p.animatedsourcespray.base.operatingsystem;

public enum OperatingSystem {
	Windows,
	Linux,
	Unsupported;

	static {
		String name;
		try {
			name = System.getProperty("os.name");
		} catch(final Throwable t) {
			name = null;
		}
		if(name == null) {
			hostingOS = Unsupported;
		} else if(name.startsWith("Windows")) {
			hostingOS = Windows;
		} else if(name.startsWith("Linux") || name.startsWith("LINUX")) {
			hostingOS = Linux;
		} else {
			hostingOS = Unsupported;
		}
	}

	private static final OperatingSystem hostingOS;
	public static OperatingSystem getHostingOS() {
		return hostingOS;
	}
}