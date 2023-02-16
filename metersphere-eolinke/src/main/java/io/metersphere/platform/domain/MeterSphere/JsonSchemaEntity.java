package io.metersphere.platform.domain.MeterSphere;

import com.alibaba.fastjson2.JSONObject;
import io.metersphere.platform.domain.OpenAPI3.PropertiesEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JsonSchemaEntity {
    private String $id = "http://example.com/root.json";
    private String title = "The Root Schema";
    private boolean hidden = true;
    private String type = "object";
    private String $schema = "http://json-schema.org/draft-07/schema#";
    private Map<String,PropertiesResponseEntity> properties;
    private List<String> required;
}
