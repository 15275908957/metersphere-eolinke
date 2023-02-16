package io.metersphere.platform.domain.OpenAPI3;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class OpenAPI3Entity {
    private String openapi;
    private InfoEntity info;
    private ExternalDocsEntity externalDocs;
    private List<Object> servers;
    private List<Object> tags;
    private IdentityHashMap<String, LinkedHashMap<String,APIInfoEntity>> paths;
    private ComponentsEntity components;

    public String toString(){
        return JSONObject.toJSONString(this);
    }
}
