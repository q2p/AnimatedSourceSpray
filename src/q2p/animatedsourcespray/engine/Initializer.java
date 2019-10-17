package q2p.animatedsourcespray.engine;

public interface Initializer {
	boolean initialize();
	default void deInitialize() {}
}
