package io.metersphere.platform.domain.MeterSphere;

import lombok.Data;

@Data
public class MSDurationEntity {
    private boolean enable;
    private String type;
    private int value;
    private boolean valid;
}
