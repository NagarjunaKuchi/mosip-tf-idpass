/**
 * 
 */
package io.mosip.tf.idpass.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;


/**
 * 
 * BIR class with Builder to create data
 * 
 * @author Ramadurai Pandian
 *
 */

@Data
public class BiometricRecord implements Serializable {

	protected VersionType version;
	protected VersionType cbeffversion;
	protected BIRInfo birInfo;
	/**
	 * This can be of any modality, each subtype is an element in this list.
	 * it has type and subtype info in it
	 */
	protected List<BIR> segments;
	
	public BiometricRecord() {
		this.segments = new ArrayList<>();
	}
	
	public BiometricRecord(VersionType version, VersionType cbeffversion, BIRInfo birInfo) {
		this.version = version;
		this.cbeffversion = cbeffversion;
		this.birInfo = birInfo;
		this.segments = new ArrayList<BIR>();
	}	

}
