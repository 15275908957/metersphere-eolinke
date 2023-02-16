package io.metersphere.platform.domain;

import lombok.Data;

@Data
public class PingCodeVersion {
    private String id;
    private String url;
    private String name;
    private Stage stage;

    @Data
    public class Stage{
        private String name;
    }
}
