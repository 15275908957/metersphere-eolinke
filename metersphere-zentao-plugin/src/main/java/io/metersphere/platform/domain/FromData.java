package io.metersphere.platform.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FromData {
    private String key;
    private Object value;

    public FromData(String key, Object value){
        this.key = key;
        this.value = value;
    }
}
