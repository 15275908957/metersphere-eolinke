package entity;

import lombok.Data;

@Data
public class CentextDataEntity {
    private String id;
    private String projectId;
    private String tags;
    private String userId;
    private String apiScenarioModuleId;
    private String modulePath;
    private String name;
    private String level;
    private String status;
    private String principal;
    private Integer stepTotal;
    private String schedule;
    private Long createTime;
    private Long updateTime;
    private String passRate;
    private String lastResult;
    private String reportId;
    private Long num;
    private String originalState;
    private String customNum;
    private String createUser;
    private Integer version;
    private String deleteTime;
    private String deleteUserId;
    private Integer executeTimes;
    private Integer order;
    private String environmentType;
    private String environmentGroupId;
    private String versionId;
    private String refId;
    private Boolean latest;
    private String scenarioDefinition;
    private String description;
    private String environmentJson;
}
