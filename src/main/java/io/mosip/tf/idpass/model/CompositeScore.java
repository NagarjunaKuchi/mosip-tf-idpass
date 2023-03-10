package io.mosip.tf.idpass.model;

import lombok.Data;

/** Added for backward compatibility 0.7 */
@Data
public class CompositeScore {

	private float scaledScore;
	private long internalScore;
	private Score[] individualScores;
	private KeyValuePair[] analyticsInfo;
	
}
