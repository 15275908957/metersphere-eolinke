package io.metersphere.platform.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PingCodeProjectConfig {
    private String pingCodeIssueTypeId;
    private String pingCodeStoryTypeId;
    private boolean thirdPartTemplate;
    private String pingCodeKey;
    private String paramNames;
}
