package io.metersphere.platform.commons;

import org.springframework.http.HttpMethod;

public enum URLEnum {
    PLUGIN_TOKEN("/bff/v2/authen/plugin_token", HttpMethod.POST),
    PROJECTS("/open_api/projects", HttpMethod.POST),
    USER("/open_api/user/query", HttpMethod.POST),
    ISSUETYPES("/open_api/%s/work_item/all-types", HttpMethod.GET);

    private String url;
    private HttpMethod httpMethod;

    URLEnum(String url , HttpMethod httpMethod) {
        this.url = url;
        this.httpMethod = httpMethod;
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

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }
}
