package entity;

import lombok.Data;

@Data
public class CentextDataArgumentsEntity {
    private boolean valid;
    private boolean file;
    private boolean enable;
    private String name;
    private String type;
    private String value;
    private String contentType;
    private boolean urlEncode;
    private boolean required;
}
