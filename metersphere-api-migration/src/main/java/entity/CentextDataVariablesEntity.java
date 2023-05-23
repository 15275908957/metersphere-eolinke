package entity;

import lombok.Data;

import java.util.List;

@Data
public class CentextDataVariablesEntity {
    private Boolean counterValid;
    private Boolean constantValid;
    private String type;
    private Boolean quotedData;
    private Boolean random;
    private String delimiter;
    private Boolean enable;
    private String name;
    private Object files;
    private String id;
    private Boolean listValid;
    private String value;
    private Boolean csvvalid;
}
