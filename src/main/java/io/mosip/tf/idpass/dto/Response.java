package io.mosip.tf.idpass.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Response {
	String userId;
	String mobile;
	String mail;
	String langCode;
	String userPassword;
	String name;
	String role;
}
