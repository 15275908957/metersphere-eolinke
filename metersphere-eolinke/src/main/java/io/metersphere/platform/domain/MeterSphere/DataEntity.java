package io.metersphere.platform.domain.MeterSphere;

import lombok.Data;

@Data
public class DataEntity {
    private String id;
    private String projectId;
    private String name;
    private String method;
    private String modulePath;
    private String environmentId;
    private String schedule;
    private String status;
    private String moduleId;
    private String userId;
    private Long createTime;
    private Long updateTime;
    private String protocol;
    private String path;
    private Long num;
    private String tags;
    private String originalState;
    private String createUser;
    private String caseTotal;
    private String refId;
    private String order;
    private String versionId;
    private String description;
    private Boolean latest;
    private String request;
    private String response;
    private String remark;

}
