package io.metersphere.platform.domain.EOlinker;

import lombok.Data;

@Data
public class ProjectInfoEntity {
    private Long projectID;
    private Integer projectType;
    private String projectName;
    private String projectUpdateTime;
    private String projectVersion;
}
