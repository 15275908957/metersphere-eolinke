package io.metersphere.platform.impl;

import io.metersphere.platform.commons.ERRCODEEnum;
import io.metersphere.platform.commons.URLEnum;
import io.metersphere.platform.domain.*;
import im.metersphere.plugin.exception.MSPluginException;
import im.metersphere.plugin.utils.JSON;
import im.metersphere.plugin.utils.LogUtil;
import io.metersphere.platform.api.BaseClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LarkAbstractClient extends BaseClient {

    protected String URL;

    protected String PLUGIN_ID;

    protected String PLUGIN_SECRET;

    protected String USER_KEY;

    protected String token;

    public void checkUserByUserKey() {
        ResponseEntity<String> response = null;

        try {
            HttpHeaders headers = getAuthHeader();
            headers.add("X-PLUGIN-TOKEN", token);
            headers.add("X-USER-KEY", USER_KEY);
            HashMap<String,Object> queryBody = new HashMap<>();
            queryBody.put("user_keys", Arrays.asList(USER_KEY));
            HttpEntity<String> requestEntity = new HttpEntity<>(JSON.toJSONString(queryBody), headers);
            response = restTemplate.exchange(getUrl(URLEnum.USER.getUrl()), URLEnum.USER.getHttpMethod(), requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        if (StringUtils.isBlank(response.getBody())) {
            MSPluginException.throwException("用户密钥错误");
        }
    }

    public void auth() {
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(URL , HttpMethod.GET, getAuthHttpEntity(), String.class);
        } catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        if (StringUtils.isBlank(response.getBody())) {
            MSPluginException.throwException("测试连接失败，请检查Lark地址是否正确");
        }
    }

    protected HttpEntity<MultiValueMap> getAuthHttpEntity() {
        return new HttpEntity<>(getAuthHeader());
    }

    protected HttpHeaders getAuthHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    protected String getUrl(String path) {
        return URL + path;
    }

    public void setConfig(LarkConfig config) {
        if (config == null) {
            MSPluginException.throwException("config is null");
        }
        URL = config.getUrl();
        PLUGIN_ID = config.getPluginId();
        PLUGIN_SECRET = config.getPluginSecret();
        USER_KEY = config.getUserKey();
        getToken();
    }

    public void getToken(){
        ResponseEntity<String> response = null;
        HashMap<String,Object> queryBody = new HashMap<>();
        queryBody.put("plugin_id",PLUGIN_ID);
        queryBody.put("plugin_secret",PLUGIN_SECRET);
        HttpEntity<String> requestEntity = new HttpEntity<>(JSON.toJSONString(queryBody), getAuthHeader());
        try {
            response = restTemplate.exchange(getUrl(URLEnum.PLUGIN_TOKEN.getUrl()), URLEnum.PLUGIN_TOKEN.getHttpMethod(), requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        if (StringUtils.isBlank(response.getBody())) {
            MSPluginException.throwException("测试连接失败，请检查Lark地址是否正确");
        }
        LarkResponseBase larkResponseBase = (LarkResponseBase)getResultForObject(LarkResponseBase.class, response);
        LarkPluginToken larkPluginToken = JSON.parseObject(larkResponseBase.getDataStr(), LarkPluginToken.class);
        token = larkPluginToken.getToken();
    }

    public List<LarkIssueType> getIssueTypes(String request) {
        ResponseEntity<String> response = null;
        HttpHeaders headers = getAuthHeader();
        headers.add("X-PLUGIN-TOKEN", token);
        headers.add("X-USER-KEY", USER_KEY);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try {
             response = restTemplate.exchange(getUrl(URLEnum.ISSUETYPES.getUrl(request)), URLEnum.ISSUETYPES.getHttpMethod(), requestEntity, String.class);
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getMessage());
        }
        LarkResponseBase larkResponseBase = (LarkResponseBase)getResultForObject(LarkResponseBase.class, response);
        List<LarkIssueType> larkIssueTypes = (List<LarkIssueType>)larkResponseBase.getData();
        return larkIssueTypes.stream().filter(i -> {
            return i.getIs_disable() == 2;
        }).collect(Collectors.toList());
    }

    public List<String> getWorkSpaceIdList () {
        ResponseEntity<String> response = null;
        HttpHeaders headers = getAuthHeader();
        headers.add("X-PLUGIN-TOKEN", token);
        headers.add("X-USER-KEY", USER_KEY);
        HashMap<String,Object> queryBody = new HashMap<>();
        queryBody.put("user_key",USER_KEY);
        HttpEntity<String> requestEntity = new HttpEntity<>(JSON.toJSONString(queryBody), headers);
        try {
            response = restTemplate.exchange(getUrl(URLEnum.PROJECTS.getUrl()), URLEnum.PROJECTS.getHttpMethod(), requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        LarkResponseBase larkResponseBase = (LarkResponseBase)getResultForObject(LarkResponseBase.class, response);
        List<String> spaceIds = JSON.parseArray(larkResponseBase.getDataStr(), String.class);
        return spaceIds;
    }



}
