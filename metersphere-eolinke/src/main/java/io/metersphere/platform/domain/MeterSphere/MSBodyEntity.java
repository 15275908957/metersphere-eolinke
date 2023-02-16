package io.metersphere.platform.domain.MeterSphere;

import lombok.Data;

import java.util.List;

@Data
public class MSBodyEntity {
    private String type;
    private String raw;
    private String format;
    private List<MSkvsEntity> kvs;
    private List<String> binary;
    private String jsonSchema;
    private String tmpFilePath;
    private boolean valid;
    private boolean xml;
    private boolean json;
    private boolean kv;
    private boolean oldKV;
}
