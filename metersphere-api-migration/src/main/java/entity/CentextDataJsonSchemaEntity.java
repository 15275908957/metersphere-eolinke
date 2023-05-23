package entity;

import lombok.Data;

@Data
public class CentextDataJsonSchemaEntity {
    private boolean hidden;
    private String $schema;
    private CentextDataMockEntity mock;
    private String title;
    private String type;
    private CentextDataPropertiesEntity properties;
    private String $id;
}
