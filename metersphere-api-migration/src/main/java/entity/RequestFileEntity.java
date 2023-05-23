package entity;

import lombok.Data;

@Data
public class RequestFileEntity {
    private Object file;
    private String modeId;
    private String moduleId;
    private String modulePath;
    private String versionId;
    private String platform;
    private boolean saved;
    private String model;
    private String projectId;
    private String protocol;
}
