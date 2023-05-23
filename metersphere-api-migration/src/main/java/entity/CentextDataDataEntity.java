package entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CentextDataDataEntity {
    private List<CentextDataHeadersEntity> headers;
    private List<CentextDataVariablesEntity> variables;
    private Boolean mockEnvironment;
    private Boolean active;
    private Map<String,String> environmentMap;
    private String type;
    private Boolean isMockEnvironment;
    private Boolean environmentEnable;
    private String referenced;
    private Boolean enable;
    private String name;
    private Boolean enableCookieShare;
    private List<CentextDataHashTreeEntity> hashTree;
    private String id;
    private String projectId;
    private String clazzName;
    private Boolean onSampleError;
}
