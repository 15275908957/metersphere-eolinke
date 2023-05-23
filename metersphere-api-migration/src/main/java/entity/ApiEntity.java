package entity;

import lombok.Data;

@Data
public class ApiEntity {
    private String id;
    private String oldID;
    private String projectId;
    private String name;
    private String method;
    private String modulePath;
    private String environmentId;
    private String schedule;
    private String status;
    private String moduleId;
    private String userId;
    private long createTime;
    private long updateTime;
    private String protocol;
    private String path;
    private long num;
    private String tags;
    private String originalState;
    private String createUser;
    private String caseTotal;
    private String caseStatus;
    private String casePassingRate;
    private String deleteTime;
    private String deleteUserId;
    private long order;
    private String refId;
    private String versionId;
    private boolean latest;
    private String toBeUpdated;
    private String toBeUpdateTime;
    private String description;
    private String request;
    private String response;
    private String remark;
}
