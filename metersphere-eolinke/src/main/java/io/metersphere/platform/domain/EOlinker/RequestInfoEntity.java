package io.metersphere.platform.domain.EOlinker;

import lombok.Data;

import java.util.List;

@Data
public class RequestInfoEntity {
    private String paramNotNull;
    private String paramType;
    private String paramName;
    private String paramKey;
    private String paramValue;
    private String paramLimit;
    private String paramNote;
    private List<ParamValueEntity> paramValueList;
//    private Integer default;
    private Integer $index;
}
