package io.mosip.tf.idpass.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Document {

    private byte[] document;
    private String value;
    private String type;
    private String format;
}
