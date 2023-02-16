package io.metersphere.platform.domain.OpenAPI3;

import lombok.Data;

@Data
public class InfoEntity {
    private String version;
    private String title;
    private String description;
    private String termsOfService;
}
