package io.metersphere.platform.domain;

import io.metersphere.plugin.utils.JSON;
import lombok.Data;
import java.util.List;

@Data
public class PingCodeResponseBase {
    private int page_index;
    private int page_size;
    private int total;
    private Object values;

    public <T> List<T>  getValues(Class<T> tClass){
        String jsonStr = JSON.toJSONString(this.values);
        return JSON.parseArray(jsonStr, tClass);
    }

}
