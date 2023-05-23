package entity;

import lombok.Data;

import java.util.List;

@Data
public class CentextEntity {
    private List<CentextDataEntity> data;
    private String projectId;
    private String version;
    private List<NodeTree> nodeTree;

    @Data
    public static class NodeTree{
        private String id;
        private String projectId;
        private String name;
        private String parentId;
        private Integer level;
        private Long createTime;
        private Long updateTime;
        private Integer pos;
        private String label;
        private List<NodeTree> children;
        private Integer caseNum;
        private String path;
    }
}
