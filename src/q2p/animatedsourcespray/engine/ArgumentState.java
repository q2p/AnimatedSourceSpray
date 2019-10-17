package q2p.animatedsourcespray.engine;

final class ArgumentState {
	private final String title;

	private final boolean exactOrMore;
	private final int subArgumentsAmount;

	public ArgumentState(final String title, boolean exactOrMore, int subArgumentsAmount) {
		this.title = '-'+title;
		this.exactOrMore = exactOrMore;
		this.subArgumentsAmount = subArgumentsAmount;
	}
}