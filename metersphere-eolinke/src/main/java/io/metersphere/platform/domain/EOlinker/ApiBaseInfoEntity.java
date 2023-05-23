package io.metersphere.platform.domain.EOlinker;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.apache.commons.text.StringEscapeUtils;

@Data
public class ApiBaseInfoEntity {
    private String apiName;
    private String apiURI;
    private Integer apiProtocol;
    private String apiSuccessMock;
    private String apiFailureMock;
    private String apiRequestType;
    private Integer apiStatus;
    private Integer starred;
    private Integer apiNoteType;
    private String apiNoteRaw;
    private String apiNote;
    private Integer apiRequestParamType;
    private String apiRequestRaw;
    private String apiUpdateTime;
    private String apiFailureStatusCode;
    private String apiSuccessStatusCode;
    private String beforeInject;
    private String afterInject;
    private Long groupId;

    public String getApiNote() {
        return StringEscapeUtils.unescapeXml(this.apiNote);
    }

    public String getApiRequestRaw() {
        return StringEscapeUtils.unescapeXml(this.apiRequestRaw);
    }

    public String getApiName() {
        return StringEscapeUtils.unescapeXml(this.apiName);
    }
}