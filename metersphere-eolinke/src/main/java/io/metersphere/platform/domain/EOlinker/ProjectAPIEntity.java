package io.metersphere.platform.domain.EOlinker;

import lombok.Data;

import java.util.List;

@Data
public class ProjectAPIEntity {
    private ProjectInfoEntity projectInfo;
    private List<ApiGroupInfoEntity> apiGroupList;
    private List<StatusCodeGroupEntity> statusCodeGroupList;
    private List<Object> env;
    private List<Object> pageGroupList;
    private List<Object> caseGroupList;
}
