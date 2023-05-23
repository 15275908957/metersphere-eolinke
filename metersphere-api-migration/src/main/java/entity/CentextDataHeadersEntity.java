package entity;

import lombok.Data;

@Data
public class CentextDataHeadersEntity {
    private Boolean valid;
    private Boolean file;
    private Boolean enable;
    private String name;
    private String value;
    private Boolean urlEncode;
    private Boolean required;
}
