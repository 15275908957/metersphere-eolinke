package entity;

import lombok.Data;

@Data
public class CentextDataKvsEntity {
    private boolean valid;
    private boolean file;
    private boolean enable;
    private String type;
    private String contentType;
    private boolean urlEncode;
    private boolean required;
}
