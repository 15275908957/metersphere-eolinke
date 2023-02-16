package io.metersphere.platform.domain;

import lombok.Data;

import java.util.List;

@Data
public class PingCodeIssueProperties {
    private String id;
    private String url;
    private PingCodeProperties property_plan;
    private PingCodeIssuePropertiesProperty property;
}
