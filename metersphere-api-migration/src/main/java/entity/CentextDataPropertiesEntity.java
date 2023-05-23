package entity;

import lombok.Data;

@Data
public class CentextDataPropertiesEntity {
    private CentextDataSelectAllEntity selectAll;
    private CentextDataProjectIdEntity selectThisWeedData;
    private CentextDataProjectIdEntity selectThisWeedRelevanceData;
    private CentextDataProjectIdEntity projectId;
}
