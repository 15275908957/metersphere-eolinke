package io.metersphere.platform.domain.MeterSphere; /**
 * Copyright 2023 json.cn
 */
import lombok.Data;

import java.util.List;
@Data
public class MSRequestEntity {

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
    private List<MSHashTreeEntity> hashTree;
    private String projectId;
    private boolean isMockEnvironment;
    private String environmentId;
    private String pluginId;
    private String stepName;
    private String parent;
    private String protocol;
    private String domain;
    private String port;
    private String method;
    private String path;
    private String connectTimeout;
    private String responseTimeout;
    private List<MSHeadersEntity> headers;
    private MSBodyEntity body;
    private List<MSRestEntity> rest;
    private String url;
    private boolean followRedirects;
    private boolean autoRedirects;
    private boolean doMultipartPost;
    private String useEnvironment;
    private List<MSArgumentsEntity> arguments;
    private String authManager;
    private String isRefEnvironment;
    private String alias;
    private boolean customizeReq;
    private String implementation;
    private boolean mockEnvironment;

}