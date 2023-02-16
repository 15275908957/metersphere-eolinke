package io.metersphere.platform.domain.EOlinker;

import lombok.Data;

import java.util.List;

@Data
public class ResultInfoEntity {
    private String paramNotNull;
    private String paramName;
    private String paramKey;
    private String type;
    private String paramType;
    private List<ParamValueEntity> paramValueList;
    private Integer $index;
}
