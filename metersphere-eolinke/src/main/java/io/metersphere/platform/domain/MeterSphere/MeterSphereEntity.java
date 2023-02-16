package io.metersphere.platform.domain.MeterSphere;

import lombok.Data;

import java.util.List;

@Data
public class MeterSphereEntity {
    private String projectName;
    private String protocol;
    private String projectId;
    private String version;
    private List<Object> nodeTree;
    private List<DataEntity> data;
    private List<Object> mocks;
}
