package io.metersphere.platform.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PingCodeConfig {
    private String clientId;
    private String clientSecret;
    private String url;
    private String access_token;
}
