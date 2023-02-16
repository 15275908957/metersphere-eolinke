package io.metersphere.platform.domain.MeterSphere;

import lombok.Data;

import java.util.List;

@Data
public class MSResponseBodyEntity {
    private String type="JSON";
    private String format = "JSON-SCHEMA";
    private List<Object> kvs;
    private List<Object> binary;
    private boolean json = true;
    private boolean valid = true;
    private JsonSchemaEntity jsonSchema;
}
