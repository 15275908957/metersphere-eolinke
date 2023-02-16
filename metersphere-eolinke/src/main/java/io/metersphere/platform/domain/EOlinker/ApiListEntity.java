package io.metersphere.platform.domain.EOlinker;

import lombok.Data;
import org.apache.commons.text.StringEscapeUtils;

@Data
public class ApiListEntity {
    private Long apiId;
    private String apiURI;
    private String apiUpdateTime;
    private String userNickName;
    private String apiName;

    public String getApiName(){
        return StringEscapeUtils.unescapeXml(this.apiName);
    }
}
