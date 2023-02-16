package io.metersphere.platform.domain.MeterSphere;

import lombok.Data;

import java.util.List;

@Data
public class MSHashTreeEntity {
    private String type;
    private String clazzName;
    private String id;
    private String resourceId;
    private String name;
    private String label;
    private String referenced;
    private boolean active;
    private String index;
    private boolean enable;
    private String refType;
    private String hashTree;
    private String projectId;
    private boolean isMockEnvironment;
    private String environmentId;
    private String pluginId;
    private String stepName;
    private String parent;
    private boolean scenarioAss;
    private List<String> regex;
    private List<String> jsonPath;
    private List<String> jsr223;
    private List<String> xpath2;
    private MSDurationEntity duration;
    private MSDurationEntity document;
    private boolean mockEnvironment;
}
