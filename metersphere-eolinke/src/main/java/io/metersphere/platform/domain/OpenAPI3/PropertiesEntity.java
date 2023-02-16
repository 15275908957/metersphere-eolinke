package io.metersphere.platform.domain.OpenAPI3;

import lombok.Data;

@Data
public class PropertiesEntity {
    private String description;
    private String type;
    private String required;
    private String example;
}
