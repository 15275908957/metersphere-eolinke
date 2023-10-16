package io.metersphere.platform.impl;

import io.metersphere.base.domain.IssuesWithBLOBs;
import io.metersphere.platform.commons.ERRCODEEnum;
import io.metersphere.platform.commons.URLEnum;
import io.metersphere.platform.domain.*;
import io.metersphere.platform.api.BaseClient;
import io.metersphere.plugin.exception.MSPluginException;
import io.metersphere.plugin.utils.JSON;
import io.metersphere.plugin.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import java.util.*;
import java.util.stream.Collectors;

public class LarkAbstractClient extends BaseClient {

    protected static String URL;

    public String PLUGIN_ID;

    protected String PLUGIN_SECRET;

    protected String USER_KEY;

//    protected String SPACEID;

    protected String token;

//    public RestTemplate BaseClient() {
//        RestTemplate temp = null;
//        try {
//            TrustStrategy acceptingTrustStrategy = (chain, authType) -> {
//                return true;
//            };
//            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial((KeyStore)null, acceptingTrustStrategy).build();
//            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
//            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
//            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//            requestFactory.setHttpClient(httpClient);
//            int time = 1000 * 60 * 60;
//            requestFactory.setConnectionRequestTimeout(time);
//            requestFactory.setConnectTimeout(time);
//            requestFactory.setReadTimeout(time);
//            temp = new RestTemplate(requestFactory);
//        } catch (Exception var6) {
//            LogUtil.error(var6);
//        }
//        return temp;
//
//    }

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

//    public void checkSpaceId() {
//        try {
//            List<String> spaceIds = getWorkSpaceIdList();
//            if(!spaceIds.contains(SPACEID)){
//                MSPluginException.throwException("无效的空间id");
//            }
//        } catch (Exception e) {
//            LogUtil.error(e);
//            MSPluginException.throwException(e.getMessage());
//        }
//    }

    public void checkSpaceId(String spaceId) {
        try {
            List<String> spaceIds = getWorkSpaceIdList();
            if(!spaceIds.contains(spaceId)){
                MSPluginException.throwException("无效的空间id");
            }
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getMessage());
        }
    }



    public void syncIssuesAttachment(SyncIssuesAttachmentRequest request) {
        ResponseEntity<String> response = null;
        HttpHeaders authHeader = getAuthHeader();
        authHeader.add("X-PLUGIN-TOKEN", token);
        authHeader.add("X-USER-KEY", USER_KEY);
        authHeader.setContentType(MediaType.parseMediaType("multipart/form-data; charset=UTF-8"));
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        FileSystemResource fileResource = new FileSystemResource(request.getFile());
        paramMap.add("file", fileResource);
        paramMap.add("field_key", "multi_attachment");
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, authHeader);
        String[] keys = request.getPlatformId().split("_");
        try {
            response = restTemplate.exchange(getUrl(URLEnum.UPLOAD_FILE.getUrl(keys[0], "issue", keys[1])), URLEnum.UPLOAD_FILE.getHttpMethod(), requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
    }

    public void deleteIssue(String id) {
        HttpHeaders headers = getAuthHeader();
        headers.add("X-PLUGIN-TOKEN", token);
        headers.add("X-USER-KEY", USER_KEY);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String[] keys = id.split("_");
        try {
            restTemplate.exchange(getUrl(URLEnum.DELETE_WORK_ITEM.getUrl(keys[0], "issue", keys[1])), URLEnum.DELETE_WORK_ITEM.getHttpMethod(), requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
//            if(){
//                30005
//            }
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);

            MSPluginException.throwException(e.getMessage());
        }
    }

    public List<SelectOption> getProjectsDetail(List<String> ids) {
        ResponseEntity<String> response = null;
        HttpHeaders headers = getAuthHeader();
        headers.add("X-PLUGIN-TOKEN", token);
        headers.add("X-USER-KEY", USER_KEY);
        HashMap<String,Object> queryBody = new HashMap<>();
        queryBody.put("user_key",USER_KEY);
        queryBody.put("project_keys", ids);
        HttpEntity<String> requestEntity = new HttpEntity<>(JSON.toJSONString(queryBody), headers);
        try {
            response = restTemplate.exchange(getUrl(URLEnum.PROJECTS_DETAIL.getUrl()), URLEnum.ISSUETYPES.getHttpMethod(), requestEntity, String.class);
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getMessage());
        }
        LarkResponseBase larkResponseBase = (LarkResponseBase)getResultForObject(LarkResponseBase.class, response);
        Map<String, Object> projectDetail = JSON.parseMap(larkResponseBase.getDataStr());
        List<SelectOption> selectOptions = new ArrayList<>();
        for(String key : projectDetail.keySet()){
            Object value = projectDetail.get(key);
            Map<String, Object> pd = JSON.parseMap(JSON.toJSONString(value));
            SelectOption selectOption = new SelectOption(pd.get("name")+"", pd.get("project_key")+"");
            selectOptions.add(selectOption);
        }
        return selectOptions;
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
        headers.set(HttpHeaders.ACCEPT_ENCODING,"qzip,x-qzip,deflate");
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
//        SPACEID = config.getSpaceId();
//        restTemplate = BaseClient();
        getToken();
    }

    public void getToken(){
        ResponseEntity<String> response = null;
        HashMap<String,Object> queryBody = new HashMap<>();
        queryBody.put("plugin_id",PLUGIN_ID);
        queryBody.put("plugin_secret",PLUGIN_SECRET);

        System.out.println("getToken info :"+JSON.toJSONString(queryBody));
        HttpEntity<String> requestEntity = new HttpEntity<>(JSON.toJSONString(queryBody), getAuthHeader());
        try {
            response = restTemplate.exchange(getUrl(URLEnum.PLUGIN_TOKEN.getUrl()), URLEnum.PLUGIN_TOKEN.getHttpMethod(), requestEntity, String.class);
            LogUtil.info("getUrl:"+getUrl(URLEnum.PLUGIN_TOKEN.getUrl()));
            LogUtil.info("getHttpMethod:"+URLEnum.PLUGIN_TOKEN.getHttpMethod());
            LogUtil.info("queryBody:"+JSON.toJSONString(queryBody));
            LogUtil.info("queryBody:"+JSON.toJSONString(requestEntity));
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
        LogUtil.info("response:"+JSON.toJSONString(response));
        System.out.println("response:"+JSON.toJSONString(response));
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
        List<LarkIssueType> larkIssueTypes = JSON.parseArray(larkResponseBase.getDataStr(), LarkIssueType.class);
        return larkIssueTypes.stream().filter(i -> {
            return i.getIs_disable() == 2;
        }).collect(Collectors.toList());
    }

    public Map<String, LarkFieldConf> getThirdPartCustomFieldMap(String projectConfig) {
        List<LarkFieldConf> larkFieldConfList = getThirdPartCustomField(JSON.parseObject(projectConfig,LarkProjectConfig.class));
        Map<String, LarkFieldConf> larkFieldConfMap = new HashMap<>();
        for(LarkFieldConf item:larkFieldConfList){
            larkFieldConfMap.put(item.getField_key(),item);
        }
        return larkFieldConfMap;
    }

    public List<LarkFieldConf> getThirdPartCustomField(LarkProjectConfig projectConfig) {
        ResponseEntity<String> response = null;
        HttpHeaders headers = getAuthHeader();
        headers.add("X-PLUGIN-TOKEN", token);
        headers.add("X-USER-KEY", USER_KEY);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        //婚礼纪项目非标改造
//        LarkProjectConfig lpc = JSON.parseObject(projectConfig,LarkProjectConfig.class);
//        Map<String, String> map =  JSON.parseMap(projectConfig);
//        String spaceId = map.get("spaceId");
        try {
            response = restTemplate.exchange(getUrl(URLEnum.GET_TEMPLATE.getUrl(projectConfig.getSpaceId())), URLEnum.GET_TEMPLATE.getHttpMethod(), requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        LarkResponseBase larkResponseBase = JSON.parseObject(response.getBody(), LarkResponseBase.class);
        List<LarkFieldConf> larkSimpleFields = JSON.parseArray(larkResponseBase.getDataStr(), LarkFieldConf.class);
        return larkSimpleFields;
    }

    public List<LarkUserInfo> getTameUserInfoList(LarkProjectConfig projectConfig) {
        List<LarkTeam> larkTeamList = getTameUserList(projectConfig);
        Set<String> userIds = new HashSet<>();
        for(LarkTeam larkTeam : larkTeamList){
            userIds.addAll(larkTeam.getUser_keys());
        }
        ResponseEntity<String> response = null;
        HttpHeaders headers = getAuthHeader();
        headers.add("X-PLUGIN-TOKEN", token);
        headers.add("X-USER-KEY", USER_KEY);
        HashMap<String,Object> queryBody = new HashMap<>();
        queryBody.put("user_keys", userIds);
        HttpEntity<String> requestEntity = new HttpEntity<>(JSON.toJSONString(queryBody), headers);
        try {
            response = restTemplate.exchange(getUrl(URLEnum.USER.getUrl()), URLEnum.USER.getHttpMethod(), requestEntity, String.class);
        }catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        Map<String,Object> map = JSON.parseMap(response.getBody());
        Object data = map.get("data");
        List<LarkUserInfo> larkUserInfos= JSON.parseArray(JSON.toJSONString(data), LarkUserInfo.class);
        return larkUserInfos;
    }

    public List<LarkTeam> getTameUserList(LarkProjectConfig projectConfig) {
        ResponseEntity<String> response = null;
        HttpHeaders headers = getAuthHeader();
        headers.add("X-PLUGIN-TOKEN", token);
        headers.add("X-USER-KEY", USER_KEY);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

//      Map<String, String> map =  JSON.parseMap(projectConfig);
//       String spaceId = map.get("spaceId");
        try {
            response = restTemplate.exchange(getUrl(URLEnum.TEAMS_ALL.getUrl(projectConfig.getSpaceId())), URLEnum.TEAMS_ALL.getHttpMethod(), requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        LarkResponseBase larkResponseBaseField = JSON.parseObject(response.getBody(), LarkResponseBase.class);
        List<LarkTeam> larkTeamList = JSON.parseArray(larkResponseBaseField.getDataStr(), LarkTeam.class);
        return larkTeamList;
    }

    public Map<String, LarkSimpleField> getSpaceField(LarkProjectConfig projectConfig) {
        ResponseEntity<String> response = null;
        HttpHeaders headers = getAuthHeader();
        headers.add("X-PLUGIN-TOKEN", token);
        headers.add("X-USER-KEY", USER_KEY);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

//        Map<String, String> map =  JSON.parseMap(projectConfig);
//        String spaceId = map.get("spaceId");
        try {
            response = restTemplate.exchange(getUrl(URLEnum.GET_SPACE_FIELD.getUrl(projectConfig.getSpaceId())), URLEnum.GET_SPACE_FIELD.getHttpMethod(), requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        LarkResponseBase larkResponseBaseField = JSON.parseObject(response.getBody(), LarkResponseBase.class);
        List<LarkSimpleField> simpleFields = JSON.parseArray(larkResponseBaseField.getDataStr(), LarkSimpleField.class);
        Map<String, LarkSimpleField> larkSimpleFieldMap = new HashMap<>();
        for(LarkSimpleField item:simpleFields){
            larkSimpleFieldMap.put(item.getField_key(),item);
        }
        return larkSimpleFieldMap;
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

    public IssuesWithBLOBs addIssue(PlatformIssuesUpdateRequest issuesRequest) {
//        Map<String, LarkSimpleField> larkSimpleFieldMap = getSpaceField();
        String projectConfig=issuesRequest.getProjectConfig();

        Map<String, LarkFieldConf> larkSimpleFieldMap = getThirdPartCustomFieldMap(projectConfig);
        ResponseEntity<String> response = null;
        HttpHeaders headers = getAuthHeader();
        headers.add("X-PLUGIN-TOKEN", token);
        headers.add("X-USER-KEY", USER_KEY);
        LarkAddWorkItem larkAddWorkItem = new LarkAddWorkItem(issuesRequest, larkSimpleFieldMap, "issue", USER_KEY);
        HttpEntity<String> requestEntity = new HttpEntity<>(JSON.toJSONString(larkAddWorkItem), headers);

        LarkProjectConfig lpc = JSON.parseObject(projectConfig, LarkProjectConfig.class);
        String spaceId=lpc.getSpaceId();
        try {
            response = restTemplate.exchange(getUrl(URLEnum.ADD_ISSUE.getUrl(spaceId)), URLEnum.ADD_ISSUE.getHttpMethod(), requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        LarkResponseBase larkResponseBase = JSON.parseObject(response.getBody(), LarkResponseBase.class);
        IssuesWithBLOBs issues = issuesRequest;
        issues.setPlatformId(spaceId+"_"+larkResponseBase.getDataStr());
        issues.setId(spaceId+"_"+larkResponseBase.getDataStr());
        LarkWorkItemRequest larkWorkItemRequest = new LarkWorkItemRequest(Arrays.asList("issue"));
        larkWorkItemRequest.setWork_item_ids(Arrays.asList(Integer.parseInt(larkResponseBase.getDataStr())));

        List<LarkWorkItemInfo> larkWorkItemInfos = null;
        for(int i = 0; i < 10 ; i++){
            try{
                Thread.currentThread().sleep(3000 * i);
            }catch (Exception e){
            }
            larkWorkItemInfos = getWorkItem(larkWorkItemRequest,lpc);
            if(larkWorkItemInfos.size() == 1) {
                //查出有值跳出循环
                break;
            }
        }
        if(larkWorkItemInfos.size() != 1) {
            //三次都没查出有值，视为失败。
            MSPluginException.throwException("添加缺陷失败，获取缺陷异常："+JSON.toJSONString(larkWorkItemInfos));
        }
        issues.setUpdateTime(larkWorkItemInfos.get(0).getUpdated_at());
        return issues;
    }

    public List<LarkWorkItemInfo> searchWorkItemAll(LarkSearchWorkItemRequest workItemRequest,String workItemType){
        List<LarkWorkItemInfo> larkWorkItemInfos = new ArrayList<>();
        for(Long i = 1l ; i < 9223372036854775807l; i++){
            workItemRequest.setPage_num(i.intValue());
            workItemRequest.setPage_size(50);
            List<LarkWorkItemInfo> temp = searchWorkItem(workItemRequest, workItemType);
            if(temp != null && temp.size() != 0){
                larkWorkItemInfos.addAll(temp);
            } else{
                return larkWorkItemInfos;
            }
            if(temp.size() < 50){
                System.out.println("get work item num "+larkWorkItemInfos.size());
                return larkWorkItemInfos;
            } else {
                System.out.println("get work item temp "+temp.size());
            }
        }
        return larkWorkItemInfos;
    }

    public List<LarkWorkItemInfo> searchWorkItem(LarkSearchWorkItemRequest workItemRequest,String workItemType){
        ResponseEntity<String> response = null;
        HttpHeaders headers = getAuthHeader();
        headers.add("X-PLUGIN-TOKEN", token);
        headers.add("X-USER-KEY", USER_KEY);
        HttpEntity<String> requestEntity = new HttpEntity<>(JSON.toJSONString(workItemRequest), headers);
        try {
//            response = restTemplate.exchange(getUrl(URLEnum.SEARCH_WORK_ITEM.getUrl(SPACEID, workItemType)), URLEnum.GET_WORK_ITEM.getHttpMethod(), requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        LarkResponseBase larkResponseBase = JSON.parseObject(response.getBody(), LarkResponseBase.class);
        List<LarkWorkItemInfo> larkWorkItemInfos = JSON.parseArray(larkResponseBase.getDataStr(), LarkWorkItemInfo.class);
        return larkWorkItemInfos;
    }

    public List<LarkWorkItemInfo> getWorkItem(LarkWorkItemRequest workItemRequest,LarkProjectConfig projectConfig){
        ResponseEntity<String> response = null;
        HttpHeaders headers = getAuthHeader();
        headers.add("X-PLUGIN-TOKEN", token);
        headers.add("X-USER-KEY", USER_KEY);
        HttpEntity<String> requestEntity = new HttpEntity<>(JSON.toJSONString(workItemRequest), headers);



        try {
            response = restTemplate.exchange(getUrl(URLEnum.GET_WORK_ITEM.getUrl(projectConfig.getSpaceId())), URLEnum.GET_WORK_ITEM.getHttpMethod(), requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        LarkResponseBase larkResponseBase = JSON.parseObject(response.getBody(), LarkResponseBase.class);
        List<LarkWorkItemInfo> larkWorkItemInfos = JSON.parseArray(larkResponseBase.getDataStr(), LarkWorkItemInfo.class);
        return larkWorkItemInfos;
    }

    public List<LarkWorkItemInfo> getWorkItemAll(LarkWorkItemRequest workItemRequest,LarkProjectConfig projectConfig){
        List<LarkWorkItemInfo> larkWorkItemInfos = new ArrayList<>();
        for(Long i = 1l ; i < 9223372036854775807l; i++){
            workItemRequest.setPage_num(i.intValue());
            workItemRequest.setPage_size(200);
            List<LarkWorkItemInfo> temp = getWorkItem(workItemRequest,projectConfig);
            if(temp != null && temp.size() != 0){
                larkWorkItemInfos.addAll(temp);
            } else{
                return larkWorkItemInfos;
            }
            if(temp.size() < 200){
//            if(temp.size() == 200){
                System.out.println("get work item num "+larkWorkItemInfos.size());
                return larkWorkItemInfos;
            } else {
                System.out.println("get work item temp "+temp.size());
            }
        }
        return larkWorkItemInfos;
    }

    private List<LarkFieldValuePairs> deleteFieldValuePaires (List<LarkFieldValuePairs> reqeust) {
        // 直接从官网复制，偷个懒
        String notFields = "_id，created_by，created_at，updated_by，updated_at，aborted，all_states，deleted，\n" +
                "    node_schedules，states，state_times，sub_stage_times，template_version，\n" +
                "    wfState，last_update_time，work_item_type_key";
        String[] notFieldArray = notFields.split("，");
        List<LarkFieldValuePairs> temp = new ArrayList<>();
        for(LarkFieldValuePairs item : reqeust){
            boolean isNotField = false;
            for(int i = 0 ; i < notFieldArray.length; i++){
                if(StringUtils.endsWith(item.getField_key(), notFieldArray[i])){
                    isNotField = true;
                    break;
                }
            }
            //把没找到的添加进去
            if(!isNotField){
                temp.add(item);
            }
        }
        return temp;
    }

    public ResponseEntity proxyForGet(String url, Class responseEntityClazz) {
        LogUtil.info("zentao proxyForGet: " + URL+url);
        return restTemplate.exchange(URL+url, HttpMethod.GET, null, responseEntityClazz);
    }

    public IssuesWithBLOBs updateIssue(PlatformIssuesUpdateRequest issuesRequest) {
//        Map<String, LarkSimpleField> larkSimpleFieldMap = getSpaceField(issuesRequest.getProjectConfig());
        String projectConfig=issuesRequest.getProjectConfig();
        Map<String,LarkFieldConf> larkFieldConfMap = getThirdPartCustomFieldMap(projectConfig);
        HttpHeaders headers = getAuthHeader();
        headers.add("X-PLUGIN-TOKEN", token);
        headers.add("X-USER-KEY", USER_KEY);
        LarkAddWorkItem larkAddWorkItem = new LarkAddWorkItem(issuesRequest, larkFieldConfMap, "issue", USER_KEY);
        LarkUpdateWorkItemRequest larkUpdateWorkItemRequest = new LarkUpdateWorkItemRequest();
        larkUpdateWorkItemRequest.setUpdate_fields(deleteFieldValuePaires(larkAddWorkItem.getField_value_pairs()));
        HttpEntity<String> requestEntity = new HttpEntity<>(JSON.toJSONString(larkUpdateWorkItemRequest), headers);
        String[] key = issuesRequest.getPlatformId().split("_");
        try {
            restTemplate.exchange(getUrl(URLEnum.UPDATE_WORK_ITEM.getUrl(key[0], "issue", key[1])), URLEnum.UPDATE_WORK_ITEM.getHttpMethod(), requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(ERRCODEEnum.getCodeInfo(e.getResponseBodyAsString()));
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        IssuesWithBLOBs issues = issuesRequest;
        LarkProjectConfig lpc=JSON.parseObject(projectConfig, LarkProjectConfig.class);
        LarkWorkItemRequest larkWorkItemRequest = new LarkWorkItemRequest(Arrays.asList("issue"));
        larkWorkItemRequest.setWork_item_ids(Arrays.asList(Integer.parseInt(key[1])));
        List<LarkWorkItemInfo> larkWorkItemInfos = getWorkItem(larkWorkItemRequest,lpc);
        if(larkWorkItemInfos.size() != 1) {
            MSPluginException.throwException("添加缺陷失败，获取缺陷异常："+JSON.toJSONString(larkWorkItemInfos));
        }
        issues.setUpdateTime(larkWorkItemInfos.get(0).getUpdated_at());
        return issues;
    }
}
