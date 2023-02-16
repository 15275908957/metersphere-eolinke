package io.metersphere.platform.domain.MeterSphere;

import lombok.Data;

@Data
public class MSHeadersEntity {
    private String name;
    private String value;
    private String type;
    private String files;
    private String description;
    private String contentType;
    private boolean enable;
    private boolean urlEncode;
    private boolean required;
    private String min;
    private String max;
    private boolean file;
    private boolean valid;
}
