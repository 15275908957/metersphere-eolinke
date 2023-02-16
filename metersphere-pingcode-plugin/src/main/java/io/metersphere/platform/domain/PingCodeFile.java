package io.metersphere.platform.domain;

import lombok.Data;

@Data
public class PingCodeFile {
    private String id;
    private String url;
    private String title;
    private Long size;
    private String type;
    private String file_type;
    private String ext;
    private String download_url;

}
