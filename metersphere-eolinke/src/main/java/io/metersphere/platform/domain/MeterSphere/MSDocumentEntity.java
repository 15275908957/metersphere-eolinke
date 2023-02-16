package io.metersphere.platform.domain.MeterSphere;

import lombok.Data;

@Data
public class MSDocumentEntity {
    private boolean enable;
    private String type;
    private MSRequestDataEntity data;
}
