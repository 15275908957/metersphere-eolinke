package entity;

import lombok.Data;

import java.util.List;

@Data
public class CentextDataBodyEntity {
    private boolean valid;
    private CentextDataJsonSchemaEntity jsonSchema;
    private boolean xml;
    private List<String> binary;
    private String format;
    private String raw;
    private List<CentextDataKvsEntity> kvs;
    private boolean json;
    private boolean kv;
    private String type;
    private boolean oldKV;
}
