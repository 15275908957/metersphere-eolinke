package io.metersphere.platform.domain.MeterSphere;

import lombok.Data;

import java.util.List;

@Data
public class MSRequestDataEntity {
    private String jsonFollowAPI;
    private String xmlFollowAPI;
    private List<String> json;
    private List<String> xml;
    private String assertionName;
}
