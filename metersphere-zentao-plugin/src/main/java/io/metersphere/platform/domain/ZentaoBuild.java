package io.metersphere.platform.domain;

import lombok.Data;

@Data
public class ZentaoBuild {
    private String id;
    private String name;

    public ZentaoBuild(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public ZentaoBuild() {}
}
