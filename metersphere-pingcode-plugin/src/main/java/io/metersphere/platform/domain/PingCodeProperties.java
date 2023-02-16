package io.metersphere.platform.domain;

import lombok.Data;

@Data
public class PingCodeProperties {
    private String id;
    private String url;
    private String project_type;
    private String work_item_type;

}
