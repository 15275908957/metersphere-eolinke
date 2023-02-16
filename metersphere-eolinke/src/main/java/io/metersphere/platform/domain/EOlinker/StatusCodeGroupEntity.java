package io.metersphere.platform.domain.EOlinker;

import lombok.Data;

import java.util.List;

@Data
public class StatusCodeGroupEntity {
    private Long groupID;
    private Long projectID;
    private String groupName;
    private Long parentGroupID;
    private Long isChild;
    private List<Object> statusCodeList;
    private List<Object> statusCodeGroupChildList;
}
