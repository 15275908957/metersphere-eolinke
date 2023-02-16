package io.metersphere.platform.domain.MeterSphere; /**
 * Copyright 2023 json.cn
 */
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2023-01-12 12:28:3
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class NodeTreeEntity {

    private String id;
    private String projectId;
    private String name;
    private String parentId;
    private int level;
    private long createTime;
    private long updateTime;
    private long pos;
    private String label;
    private List<ChildrenEntity> children;
    private int caseNum;
    private String protocol;
    private String path;
}