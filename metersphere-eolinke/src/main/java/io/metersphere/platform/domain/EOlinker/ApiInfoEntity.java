package io.metersphere.platform.domain.EOlinker;

import lombok.Data;

import java.util.List;

@Data
public class ApiInfoEntity {
    private ApiBaseInfoEntity baseInfo;
    private List<HeaderInfoEntity> headerInfo;
    private MockInfoEntity mockInfo;
    private List<RequestInfoEntity> requestInfo;
    private List<ResultInfoEntity> resultInfo;
}
