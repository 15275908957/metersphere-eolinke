package entity;

import lombok.Data;

@Data
public class ResultEntity {
    private Boolean success;
    private String message;
    private Object data;
    private String protocol;
}
