package entity;

import lombok.Data;

import java.util.List;

@Data
public class CentextDataHashTreeEntity {
    private String resourceId;
    private String refType;
    private boolean autoRedirects;
    private String type;
    private CentextDataBodyEntity body;
    private boolean isMockEnvironment;
    private String path;
    private String protocol;
    private String environmentId;
    private boolean enable;
    private boolean followRedirects;
    private String useEnvironment;
    private String connectTimeout;
    private List<CentextDataHashTree2Entity> hashTree;
    private String id;
    private String responseTimeout;
    private List<CentextDataHeadersEntity> headers;
    private List<String> rest;
    private String method;
    private boolean mockEnvironment;
    private boolean active;
    private int index;
    private String url;
    private boolean customizeReq;
    private String referenced;
    private String domain;
    private String name;
    private List<CentextDataArgumentsEntity> arguments;
    private String projectId;
    private String clazzName;
    private Integer num;
    private boolean doMultipartPost;
}
