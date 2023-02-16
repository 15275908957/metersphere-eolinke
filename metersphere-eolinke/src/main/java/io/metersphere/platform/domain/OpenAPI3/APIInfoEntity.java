package io.metersphere.platform.domain.OpenAPI3;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class APIInfoEntity {
    private String summary;
    private JSONObject requestBody = new JSONObject();
    private String description;
    private JSONObject responses = new JSONObject();
    private List<ParameterEntity> parameters = new ArrayList<>();
    private List<String> tags;
}
