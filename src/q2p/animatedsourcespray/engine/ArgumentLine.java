package q2p.animatedsourcespray.engine;

import java.util.*;

class ArgumentLine {
	private final String argumentTitle;
	private final LinkedList<String> arguments = new LinkedList<>();

	public ArgumentLine(String argumentTitle) {
		this.argumentTitle = argumentTitle;
	}

	public void put(final String argument) {
		arguments.add(argument);
	}
}
