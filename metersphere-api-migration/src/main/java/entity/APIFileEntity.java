package entity;

import lombok.Data;

import java.util.List;

@Data
public class APIFileEntity{
    private String protocol;
    private List<CaseEntity> cases;
    private List<ApiEntity> data;
    private List<String> mocks;
    private String projectName;
    private String projectId;
    private String version;

}
