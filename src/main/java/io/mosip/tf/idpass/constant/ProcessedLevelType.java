//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.05.13 at 03:22:53 PM IST 
//

package io.mosip.tf.idpass.constant;


import java.io.Serializable;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
@XmlType(name = "ProcessedLevelType")
@XmlEnum
public enum ProcessedLevelType implements Serializable {

	@XmlEnumValue("Raw")
	RAW("Raw"),
	@XmlEnumValue("Intermediate")
	INTERMEDIATE("Intermediate"),
	@XmlEnumValue("Processed")
	PROCESSED("Processed");

	private final String value;

	ProcessedLevelType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ProcessedLevelType fromValue(String v) {
		for (ProcessedLevelType c : ProcessedLevelType.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
