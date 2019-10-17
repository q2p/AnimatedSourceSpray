package q2p.animatedsourcespray.engine;

import q2p.animatedsourcespray.conversion.dataFormat.*;
import q2p.animatedsourcespray.engine.dithering.*;

import java.util.*;

public final class RenderHints {
	private static final Map<CUID, RenderHints> usedFiles = new TreeMap<>();

	public final CUID guid;
	public final VTFDataFormat dataFormat;
	public final DitheringAlgorithm ditheringAlgorithm;
	public final short alphaThreshold;

	private RenderHints(final CUID guid, final short alphaThreshold, final VTFDataFormat dataFormat, final DitheringAlgorithm ditheringAlgorithm) {
		this.guid = guid;
		this.alphaThreshold = alphaThreshold;
		this.dataFormat = dataFormat;
		this.ditheringAlgorithm = ditheringAlgorithm;
	}

	public static RenderHints create(final VTFDataFormat dataFormat, DitheringAlgorithm ditheringAlgorithm, short alphaThreshold) {
		assert dataFormat != null && ditheringAlgorithm != null && alphaThreshold >= 0 && alphaThreshold <= 0xFF;

		if(!dataFormat.ditheringApplicable)
			ditheringAlgorithm = DitheringAlgorithm.disabled;

		if(dataFormat.aBits > 1)
			alphaThreshold = AnimatedSourceSprays.defaultAlphaThreshold;

		final CUID guid = new CUID(dataFormat.code, ditheringAlgorithm.uid, (byte)alphaThreshold);

		if(usedFiles.containsKey(guid))
			return usedFiles.get(guid);

		final RenderHints ret = new RenderHints(guid, alphaThreshold, dataFormat, ditheringAlgorithm);
		usedFiles.put(guid, ret);

		return ret;
	}
}
