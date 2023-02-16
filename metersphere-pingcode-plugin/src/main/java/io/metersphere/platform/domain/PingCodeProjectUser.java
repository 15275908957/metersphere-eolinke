package io.metersphere.platform.domain;

import lombok.Data;

@Data
public class PingCodeProjectUser {
    private String id;
    private String url;
    private Object project;
    private User user;

    @Data
    public static class  User{
        private String id;
        private String url;
        private String name;
        private String display_name;
        private String avatar;
    }
}

