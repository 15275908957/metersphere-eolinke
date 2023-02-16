package io.metersphere.platform.domain.MeterSphere;

import lombok.Data;

@Data
public class MSkvsEntity {
    private String name;
    private String value;
    private String type;
    private String contentType = "text/plain";
    private boolean enable = true;
    private boolean required;
}
