package io.metersphere.platform.domain.OpenAPI3;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ParameterEntity {
    private JSONObject schema;
    private String in;
    private String name;
    private String description;
    private boolean required = false;
    private String example;

    public ParameterEntity (){
        JSONObject schemaJson = new JSONObject();
        schemaJson.put("type","string");
        schema = schemaJson;
    }

    public void setRequired(String bool){
        if(StringUtils.equals("true", bool)){
            this.required = true;
        } else {
            this.required = false;
        }
    }
}
