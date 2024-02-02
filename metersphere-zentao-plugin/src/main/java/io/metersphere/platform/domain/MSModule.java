package io.metersphere.platform.domain;

import lombok.Data;

import java.util.List;

@Data
public class MSModule {
    private String id;
    private String projectId;
    private String name;
    private String parentId;
    private String path;
    private int level;
    private long createTime;
    private long updateTime;
    private float pos;
    private String label;
    private List<MSModule> children;
    private int caseNum;
}
