package entity;

import lombok.Data;

@Data
public class CentextDataJsonEntity {
    private String variable;
    private boolean valid;
    private String expression;
    private boolean multipleMatching;
    private String type;
    private String value;
}
