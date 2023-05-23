package entity;

import lombok.Data;

import java.util.List;

@Data
public class CentextDataHashTree2Entity {
    private String resourceId;
    private boolean mockEnvironment;
    private boolean active;
    private int index;
    private String type;
    private boolean isMockEnvironment;
    private String xpathType;
    private List<String> regex;
    private List<String> xpath;
    private boolean enable;
    private List<String> hashTree;
    private List<CentextDataJsonEntity> json;
    private String id;
    private String projectId;
    private String clazzName;
}
