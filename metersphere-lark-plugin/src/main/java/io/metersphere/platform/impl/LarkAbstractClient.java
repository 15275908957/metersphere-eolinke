package io.metersphere.platform.impl;

import io.metersphere.base.domain.User;
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
            response = restTemplate.exchange(getUrl(URLEnum.USER.getUrl()), HttpMethod.POST, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            if (e.getRawStatusCode() == 401) {
                MSPluginException.throwException("用户密钥错误");
            } else {
                LogUtil.error(e.getMessage(), e);
                ERRCODEEnum.throwException(e.getResponseBodyAsString());
            }
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
            if (e.getRawStatusCode() == 401) {
                MSPluginException.throwException("插件信息错误");
            } else {
                LogUtil.error(e.getMessage(), e);
                MSPluginException.throwException(e.getMessage());
            }
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
        String url = getUrl(URLEnum.PLUGIN_TOKEN.getUrl());
        HashMap<String,Object> queryBody = new HashMap<>();
        queryBody.put("plugin_id",PLUGIN_ID);
        queryBody.put("plugin_secret",PLUGIN_SECRET);
        HttpEntity<String> requestEntity = new HttpEntity<>(JSON.toJSONString(queryBody), getAuthHeader());
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            if (e.getRawStatusCode() == 401) {
                MSPluginException.throwException("插件信息错误");
            } else {
                LogUtil.error(e.getMessage(), e);
                MSPluginException.throwException(e.getMessage());
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        if (StringUtils.isBlank(response.getBody())) {
            MSPluginException.throwException("测试连接失败，请检查Lark地址是否正确");
        }
        LarkResponseBase larkResponseBase = (LarkResponseBase)getResultForObject(LarkResponseBase.class, response);
        LarkPluginToken larkPluginToken = (LarkPluginToken)larkResponseBase.getData();
        token = larkPluginToken.getToken();
//        JSONObject jsonObject = JSONObject.parseObject(response.getBody());
//        JSONObject jsonData = jsonObject.getJSONObject("data");
//        token = jsonData.getString("token");
    }

    public List<LarkIssueType> getIssueTypes(String request) {
        ResponseEntity<String> response = null;
        String url = getUrl(URLEnum.ISSUETYPES.getUrl(request));
        HttpHeaders headers = getAuthHeader();
        headers.add("X-PLUGIN-TOKEN", token);
        headers.add("X-USER-KEY", USER_KEY);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try {
             response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getMessage());
        }
//        JSONObject jsonObject = JSONObject.parseObject(response.getBody());
//        String jsonData = jsonObject.getString("data");
        LarkResponseBase larkResponseBase = (LarkResponseBase)getResultForObject(LarkResponseBase.class, response);
        List<LarkIssueType> larkIssueTypes = (List<LarkIssueType>)larkResponseBase.getData();
        return larkIssueTypes.stream().filter(i -> {
            return i.getIs_disable() == 2;
        }).collect(Collectors.toList());
    }


}
