package io.metersphere.platform.domain.MeterSphere; /**
 * Copyright 2023 json.cn
 */

/**
 * Auto-generated: 2023-01-12 12:28:3
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class ChildrenEntity {

    private String id;
    private String projectId;
    private String name;
    private String parentId;
    private int level;
    private long createTime;
    private long updateTime;
    private long pos;
    private String label;
    private String children;
    private int caseNum;
    private String protocol;
    private String path;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    public String getProjectId() {
        return projectId;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public String getParentId() {
        return parentId;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    public int getLevel() {
        return level;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    public long getCreateTime() {
        return createTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
    public long getUpdateTime() {
        return updateTime;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }
    public long getPos() {
        return pos;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }

    public void setChildren(String children) {
        this.children = children;
    }
    public String getChildren() {
        return children;
    }

    public void setCaseNum(int caseNum) {
        this.caseNum = caseNum;
    }
    public int getCaseNum() {
        return caseNum;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public String getProtocol() {
        return protocol;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }

}