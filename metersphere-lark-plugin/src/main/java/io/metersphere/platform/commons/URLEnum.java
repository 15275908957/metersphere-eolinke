package io.metersphere.platform.commons;

public enum URLEnum {
    PLUGIN_TOKEN("/bff/v2/authen/plugin_token"),
    PROJECTS("/open_api/projects"),
    USER("/open_api/user/query"),
    ISSUETYPES("/open_api/%s/work_item/all-types");

    private String url;


    URLEnum(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getUrl(String... pathValue) {
        return String.format(url, pathValue);
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
