package entity;

import lombok.Data;

@Data
public class CaseEntity {
    private String id;
    private String oldID;
    private String projectId;
    private String name;
    private String priority;
    private String apiDefinitionId;
    private String createUserId;
    private String updateUserId;
    private long createTime;
    private long updateTime;
    private long num;
    private String tags;
    private String lastResultId;
    private String status;
    private String originalStatus;
    private String deleteTime;
    private String deleteUserId;
    private String version;
    private int order;
    private String caseStatus;
    private String versionId;
    private String toBeUpdated;
    private String toBeUpdateTime;
    private String description;
    private String request;
}
