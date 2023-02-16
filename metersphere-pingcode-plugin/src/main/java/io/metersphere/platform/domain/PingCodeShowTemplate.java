package io.metersphere.platform.domain;

import lombok.Data;

@Data
public class PingCodeShowTemplate {
    private String name;
    private Boolean required;
    private Object defaultValue;

    public PingCodeShowTemplate(String name, Boolean required, Object defaultValue){
        this.name = name;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    public PingCodeShowTemplate(){
        super();
    }
}
