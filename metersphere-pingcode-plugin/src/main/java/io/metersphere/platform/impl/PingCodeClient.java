package io.metersphere.platform.impl;

import im.metersphere.plugin.exception.MSPluginException;
import im.metersphere.plugin.utils.JSON;
import im.metersphere.plugin.utils.LogUtil;
import io.metersphere.platform.Utils.RedisUtils;
import io.metersphere.platform.domain.*;
import io.metersphere.platform.api.BaseClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import java.util.List;
import java.util.Map;

public class PingCodeClient extends BaseClient {

    protected String endpoint;

    protected String client_id;

    protected String client_Secret;

    protected String grant_type;

    private String access_token;

    public void refreshToken(String configStr){
        System.out.println("使用个人凭证："+configStr);
        PingCodeConfig config = JSON.parseObject(configStr, PingCodeConfig.class);
        String token = RedisUtils.getRedisUtils(config.getClientId(), config.getClientSecret()).getToken();
        if(StringUtils.isNotBlank(token)){
            this.client_Secret = config.getClientSecret();
            this.client_id = config.getClientId();
            this.access_token = token;
        } else {
            ResponseEntity<String> response = null;
            String accessToken = null;
            try {
                response = restTemplate.exchange(getBaseUrl() + "/v1/auth/token?grant_type={1}&client_id={2}&client_secret={3}", HttpMethod.GET, getAuthHttpEntity(), String.class, grant_type, config.getClientId(), config.getClientSecret());
                if (StringUtils.isBlank(response.getBody()) || (StringUtils.isNotBlank(response.getBody()) && !response.getBody().startsWith("{\"access_token\""))) {
                    MSPluginException.throwException("测试连接失败，请检查PingCode连接信息是否正确");
                }
                accessToken = (String) JSON.parseMap(response.getBody()).get("access_token");
                this.client_Secret = config.getClientSecret();
                this.client_id = config.getClientId();
                this.access_token = accessToken;
                RedisUtils.getRedisUtils(this.client_id, this.client_Secret).setToken(accessToken, 60 * 60 * 24 * 10);
            }catch (Exception e) {
                // 只捕获异常，不做任何抛出，不做任何变更，还用公用token执行操作
                LogUtil.error(e.getMessage(), e);
            }

        }
    }

    public PingCodeGetIssue getIssues(String issuesId) {
        LogUtil.info("开始获取缺陷啦。。。。: ");
        LogUtil.info("getIssues: " + issuesId);
        ResponseEntity<String> responseEntity;
        responseEntity = restTemplate.exchange(getBaseUrl() + "/v1/project/work_items/" + issuesId + "?access_token={0}", HttpMethod.GET, getAuthHttpEntity(), String.class, this.access_token);
//        return getResultForClass(PingCodeIssue.class, responseEntity);
        return getResultForClass(PingCodeGetIssue.class, responseEntity);
//        return (PingCodeIssue) getResultForObject(PingCodeIssue.class, responseEntity);
    }

    public PingCodeIssueInfo getIssuesId(String issuesId) {
        LogUtil.info("开始获取缺陷啦。。。。: ");
        LogUtil.info("getIssues: " + issuesId);
        ResponseEntity<String> responseEntity;
        responseEntity = restTemplate.exchange(getBaseUrl() + "/v1/project/work_items/" + issuesId + "?access_token={0}", HttpMethod.GET, getAuthHttpEntity(), String.class, this.access_token);
        return getResultForClass(PingCodeIssueInfo.class, responseEntity);
//        return (PingCodeIssue) getResultForObject(PingCodeIssue.class, responseEntity);
    }

    public List<PingCodeGetIssue> getProjectIssues(Integer startAt, Integer maxResults, String projectKey, String issueType) {
        return getProjectIssues(startAt, maxResults, projectKey, issueType, null);
    }

    public List<PingCodeGetIssue> getProjectIssues(Integer startAt, Integer maxResults, String projectKey, String issueType, String fields) {
        ResponseEntity<String> responseEntity;
        String url = getBaseUrl() + "/v1/project/work_items?access_token={0}&page_index={1}&page_size={2}&project_id={3}&type={4}";
        responseEntity = restTemplate.exchange(url,
                HttpMethod.GET, getAuthHttpEntity(), String.class, this.access_token, startAt, maxResults, projectKey, issueType);
        LogUtil.info("当前获取到的缺陷信息为：" + JSON.toJSONString(responseEntity));
        PingCodeResponseBase pingCodeResponseBase = getResultForClass(PingCodeResponseBase.class, responseEntity);
        List<PingCodeGetIssue> pingCodeIssueInfos = pingCodeResponseBase.getValues(PingCodeGetIssue.class);
        return pingCodeIssueInfos;
//        return (PingCodeIssueListResponse) getResultForObject(PingCodeIssueListResponse.class, responseEntity);
    }


    public byte[] getAttachmentContent(String key){
        System.out.println("开始获取附件"+key);
        ResponseEntity<byte[]> responseEntity;
        responseEntity = restTemplate.exchange(key,
                HttpMethod.GET, getAuthHttpEntity(), byte[].class);
        return responseEntity.getBody();
//        System.out.println("开始获取附件"+key);
//        byte[] buffer = null;
//        try {
//            URL url = new URL(key);
//            URLConnection conn = url.openConnection();
//            InputStream inStream = conn.getInputStream();
//            buffer = inStream.readAllBytes();
//        } catch (FileNotFoundException e) {
//            System.out.println("获取附件异常"+e);
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("获取附件异常io"+e);
//            e.printStackTrace();
//        }
//        return buffer;


//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Accept-Ranges", "bytes");
//        headers.add("Content-Disposition","attachment; filename*=utf-8");
//        headers.add("Content-Type","application/json");
//        HttpEntity<MultiValueMap> m = new HttpEntity<>(headers);
//        ResponseEntity<byte[]> entity = null;
//        try {
//             entity = restTemplate.exchange(key, HttpMethod.GET,m, byte[].class);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        byte[] body = entity.getBody();
//        return body;
    }

    public String auth4AccessToken() {
        String token = RedisUtils.getRedisUtils(this.client_id, this.client_Secret).getToken();
        if(StringUtils.isNotBlank(token)) return token;
        ResponseEntity<String> response = null;
        String accessToken = null;
        try {
            response = restTemplate.exchange(getBaseUrl() + "/v1/auth/token?grant_type={1}&client_id={2}&client_secret={3}", HttpMethod.GET, getAuthHttpEntity(), String.class, grant_type, client_id, client_Secret);
            if (StringUtils.isBlank(response.getBody()) || (StringUtils.isNotBlank(response.getBody()) && !response.getBody().startsWith("{\"access_token\""))) {
                MSPluginException.throwException("测试连接失败，请检查PingCode连接信息是否正确");
            }
            JSON json = new JSON();
            accessToken = (String) json.parseMap(response.getBody()).get("access_token");
        } catch (HttpClientErrorException e) {
            if (e.getRawStatusCode() == 401) {
                MSPluginException.throwException("Client_Id或密码(Client_Secret)错误");
            } else {
                LogUtil.error(e.getMessage(), e);
                MSPluginException.throwException(e.getMessage());
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        // 缓存10天
        RedisUtils.getRedisUtils(this.client_id , this.client_Secret).setToken(accessToken, 60 * 60 * 24 * 10);
        return accessToken;
    }

    public PingCodeProject getProject(String projectKey) {
        String url = getUrl("/v1/project/projects/" + projectKey + "?access_token={0}");
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, getAuthHttpEntity(), String.class, this.access_token);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        return (PingCodeProject) getResultForObject(PingCodeProject.class, response);
    }

    public List getIssueStatusList() {
        String url = getUrl("/v1/project/states?" + "access_token={0}");
        ResponseEntity<String> responseEntity = restTemplate.exchange(url,
                HttpMethod.GET, getAuthHttpEntity(), String.class, this.access_token);
        Map jsonObject = JSON.parseMap(responseEntity.getBody());
        return (List) jsonObject.get("values");
    }


    public PingCodeAddIssueResponse addIssue(String body) {
        LogUtil.info("addIssue: " + body);
        String token = this.access_token;

        HttpHeaders headers = getAuthHeader();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(getBaseUrl() + "/v1/project/bugs?access_token=" + token, HttpMethod.POST, requestEntity, String.class);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        return (PingCodeAddIssueResponse) getResultForObject(PingCodeAddIssueResponse.class, response);
    }

    public List getDemands(String projectKey, String issueType, int startAt, int maxResults) {
        String token = this.access_token;
        String url = getBaseUrl() + "/v1/project/work_items?access_token=" + token + "&page_index=" + startAt
                + "&page_size=" + maxResults + "&type=" + issueType + "&project_id=" + projectKey;
        LogUtil.info("请求URL为：" + url);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url,
                HttpMethod.GET, getAuthHttpEntity(), String.class);
        Map jsonObject = JSON.parseMap(responseEntity.getBody());
        return (List) jsonObject.get("values");
    }

    public List<PingCodePriority> getPriorityList(){
        String url = getBaseUrl()+"/v1/project/priorities?access_token=" + this.access_token;
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url,
                    HttpMethod.GET, getAuthHttpEntity(), String.class);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        PingCodeResponseBase p = getResultForClass(PingCodeResponseBase.class, response);
        return p.getValues(PingCodePriority.class);
    }

    public List<PingCodeVersion> getVersion(PingCodeProjectConfig projectConfig){
        String url = getBaseUrl()+"/v1/project/projects/"+ projectConfig.getPingCodeKey() +"/versions?access_token=" + this.access_token;
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url,
                    HttpMethod.GET, getAuthHttpEntity(), String.class);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        PingCodeResponseBase p = getResultForClass(PingCodeResponseBase.class, response);
        return p.getValues(PingCodeVersion.class);
    }

    public List<PingCodeIteration> getIteration(PingCodeProjectConfig projectConfig){
        String url = getBaseUrl()+"/v1/project/projects/"+ projectConfig.getPingCodeKey() +"/sprints?access_token=" + this.access_token;
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url,
                    HttpMethod.GET, getAuthHttpEntity(), String.class);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        PingCodeResponseBase p = getResultForClass(PingCodeResponseBase.class, response);
        return p.getValues(PingCodeIteration.class);
    }


    private List<PingCodeProperties> getProperties() {
        String url = getBaseUrl()+"/v1/project/property_plans?access_token=" + this.access_token;
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url,
                    HttpMethod.GET, getAuthHttpEntity(), String.class);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        PingCodeResponseBase p = getResultForClass(PingCodeResponseBase.class, response);
        return p.getValues(PingCodeProperties.class);
    }

    public List<PingCodeProjectUser> getProjectUser(PingCodeProjectConfig projectConfig) {
        String url = getBaseUrl() + "/v1/project/projects/"+ projectConfig.getPingCodeKey() +"/members?access_token=" + this.access_token;
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url,
                    HttpMethod.GET, getAuthHttpEntity(), String.class);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        PingCodeResponseBase p = getResultForClass(PingCodeResponseBase.class, response);
        return p.getValues(PingCodeProjectUser.class);

    }

    public List<PingCodeFile> getFiles(String workItemId){
        String url = getBaseUrl()+"/v1/project/work_items/"+workItemId+"/attachments?access_token=" + this.access_token;
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url,
                    HttpMethod.GET, getAuthHttpEntity(), String.class);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        PingCodeResponseBase p = getResultForClass(PingCodeResponseBase.class, response);
        List<PingCodeFile> pingCodeFileList = p.getValues(PingCodeFile.class);
        return pingCodeFileList;
    }

    public void uploadFiles(SyncIssuesAttachmentRequest request){
        // 名称相同不再上传 如果平台删除附件，也跳过，pingcode没有删除附件API
        List<PingCodeFile> pingCodeFileList = getFiles(request.getPlatformId());
        for(PingCodeFile item:pingCodeFileList){
            if(StringUtils.equals(item.getTitle(), request.getFile().getName())){
                return;
            }
        }
        HttpHeaders authHeader = getAuthHeader();
        authHeader.setContentType(MediaType.parseMediaType("multipart/form-data; charset=UTF-8"));
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        FileSystemResource fileResource = new FileSystemResource(request.getFile());
        paramMap.add("file", fileResource);
        paramMap.add("field_key", "multi_attachment");
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, authHeader);
        String url = getBaseUrl()+"/v1/project/work_items/"+request.getPlatformId()+"/files?access_token=" + this.access_token;
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url,
                    HttpMethod.POST, requestEntity, String.class);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        System.out.println(response);
    }

    private <T> T getResultForClass(Class<T> tClass, ResponseEntity<String> response){
        getResultBase(response);
        return JSON.parseObject(response.getBody(), tClass);
    }

    private void getResultBase(ResponseEntity<String> response){
        int statusCodeValue = response.getStatusCodeValue();
        LogUtil.info("responseCode: " + statusCodeValue);
        if (statusCodeValue >= 400) {
            MSPluginException.throwException(response.getBody());
        }
        LogUtil.info("result: " + response.getBody());
    }

    private <T> List<T>  getResultForListClass(Class<T> tClass, ResponseEntity<String> response){
        getResultBase(response);
        return JSON.parseArray(response.getBody(), tClass);
    }

    private PingCodeProperties getPingCodePropertieByType(List<PingCodeProperties> properties,String workItemType, String projectType){
        for(PingCodeProperties item:properties){
            if(item.getProject_type().equals(projectType) && item.getWork_item_type().equals(workItemType)){
                return item;
            }
        }
        return null;
    }

    public List<PingCodeIssueProperties> getIssueProperties(String workItemType, String projectType) {
        List<PingCodeProperties> properties = getProperties();
        PingCodeProperties pingCodeProperties = getPingCodePropertieByType(properties,workItemType, projectType);
        String url = getBaseUrl()+"/v1/project/property_plans/"+pingCodeProperties.getId()+"/properties?access_token=" + this.access_token;
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url,
                    HttpMethod.GET, getAuthHttpEntity(), String.class);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        PingCodeResponseBase p = getResultForClass(PingCodeResponseBase.class, response);
        return p.getValues(PingCodeIssueProperties.class);
    }



    public void updateIssue(String issuesId, String body) {
        LogUtil.info("update Issue: " + body);
        String token = this.access_token;
        HttpHeaders headers = getAuthHeader();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        try {
            restTemplate.exchange(getBaseUrl() + "/v1/project/bugs/" + issuesId + "?access_token=" + token, HttpMethod.PATCH, requestEntity, String.class);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
    }


    public void deleteIssue(String issuesId) {
        LogUtil.info("deleteIssue: " + issuesId);
        String token = this.access_token;

        try {
            restTemplate.exchange(getBaseUrl() + "/v1/project/work_items/" + issuesId + "?access_token={0}", HttpMethod.DELETE, getAuthHttpEntity(), String.class, token);
        } catch (HttpClientErrorException e) {
            MSPluginException.throwException(e.getMessage());
//            if (e.getRawStatusCode() != 404) {
//                MSPluginException.throwException(e.getMessage());
//            }
        }
    }


    protected HttpEntity<MultiValueMap> getAuthHttpEntity() {
        return new HttpEntity<>(getBasicHttpHeaders());
    }

    protected HttpHeaders getAuthHeader() {
        return getBasicHttpHeaders(client_id, client_Secret);
    }

    protected HttpHeaders getAuthJsonHeader() {
        HttpHeaders headers = getAuthHeader();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    protected HttpHeaders getBasicHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        return headers;
    }

    protected String getBaseUrl() {
        return endpoint;
    }

    protected String getUrl(String path) {
        return getBaseUrl() + path;
    }

    public void setConfig(PingCodeConfig config) {
        if (config == null) {
            MSPluginException.throwException("config is null");
        }
        String url = config.getUrl();

        if (StringUtils.isNotBlank(url) && url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        endpoint = url;
        client_id = config.getClientId();
        client_Secret = config.getClientSecret();
        grant_type = "client_credentials";
        access_token = this.auth4AccessToken();
    }
}
