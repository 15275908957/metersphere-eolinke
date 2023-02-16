package io.metersphere.platform.domain.MeterSphere;

import lombok.Data;

import java.util.List;

@Data
public class MSResponseEntity {
    private String id;
    private String name;
    private String enable;
    private String type = "HTTP";
    private List<Object> headers;
    private MSResponseBodyEntity body;

}
