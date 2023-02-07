package io.mosip.tf.idpass.service;

import java.io.InputStream;

import io.mosip.tf.idpass.constant.UinCardType;
import io.mosip.tf.idpass.exception.ApisResourceAccessException;

/**
 * The Interface UinCardGenerator.
 * 
 * @author M1048358 Alok
 *
 * @param <I>
 *            the generic type
 */
public interface UinCardGenerator<I> {

	/**
	 * Generate uin card.
	 *
	 * @param in
	 *            the in
	 * @param type
	 *            the type
	 * @param password
	 *            the password
	 * @return the i
	 */
	public I generateUinCard(InputStream in, UinCardType type, String password) throws ApisResourceAccessException;
}
