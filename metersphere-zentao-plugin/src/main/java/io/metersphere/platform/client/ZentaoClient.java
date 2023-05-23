package io.metersphere.platform.client;

import com.alibaba.fastjson2.JSONObject;
import io.metersphere.plugin.exception.MSPluginException;
import io.metersphere.plugin.utils.JSON;
import io.metersphere.plugin.utils.LogUtil;
import io.metersphere.platform.api.BaseClient;
import io.metersphere.platform.domain.*;
import io.metersphere.platform.utils.UnicodeConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.util.*;

public abstract class ZentaoClient extends BaseClient {

    protected String ENDPOINT;

    protected String USER_NAME;

    protected String PASSWD;

    public RequestUrl requestUrl;

    public ZentaoClient(String url) {
        ENDPOINT = url;
    }

    public String login() {
        GetUserResponse getUserResponse = new GetUserResponse();
        String sessionId = "";
        try {
            sessionId = getSessionId();
            String loginUrl = requestUrl.getLogin();
            MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
            paramMap.add("account", USER_NAME);
            paramMap.add("password", PASSWD);
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(paramMap, new HttpHeaders());
            ResponseEntity<String> response = restTemplate.exchange(loginUrl + sessionId, HttpMethod.POST, requestEntity, String.class);
            getUserResponse = (GetUserResponse) getResultForObject(GetUserResponse.class, response);
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getMessage());
        }
        GetUserResponse.User user = getUserResponse.getUser();
        if (user == null) {
            LogUtil.error(JSON.toJSONString(getUserResponse));
            // 登录失败，获取的session无效，置空session
            MSPluginException.throwException("zentao login fail, user null");
        }
        if (!StringUtils.equals(user.getAccount(), USER_NAME)) {
            LogUtil.error("login fail，inconsistent users");
            MSPluginException.throwException("zentao login fail, inconsistent user");
        }
        return sessionId;
    }

    public String getSessionId() {
        String getSessionUrl = requestUrl.getSessionGet();
        ResponseEntity<String> response = restTemplate.exchange(getSessionUrl,
                HttpMethod.GET, null, String.class);
        GetSessionResponse getSessionResponse = (GetSessionResponse) getResultForObject(GetSessionResponse.class, response);
        return JSON.parseObject(getSessionResponse.getData(), GetSessionResponse.Session.class).getSessionID();
    }

    public AddIssueResponse.Issue addIssue(MultiValueMap<String, Object> paramMap) {
        String sessionId = login();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, new HttpHeaders());
        ResponseEntity<String> response = null;
        try {
            String bugCreate = requestUrl.getBugCreate();
            response = restTemplate.exchange(bugCreate + sessionId,
                    HttpMethod.POST, requestEntity, String.class);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
        AddIssueResponse addIssueResponse = (AddIssueResponse) getResultForObject(AddIssueResponse.class, response);
        AddIssueResponse.Issue issue = JSON.parseObject(addIssueResponse.getData(), AddIssueResponse.Issue.class);
        if (issue == null) {
            MSPluginException.throwException(UnicodeConvertUtils.unicodeToCn(response.getBody()));
        }
        return issue;
    }

    public void updateIssue(String id, MultiValueMap<String, Object> paramMap) {
        String sessionId = login();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, new HttpHeaders());
        try {
            restTemplate.exchange(requestUrl.getBugUpdate(),
                    HttpMethod.POST, requestEntity, String.class, id, sessionId);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
    }

    public void deleteIssue(String id) {
        String sessionId = login();
        try {
            restTemplate.exchange(requestUrl.getBugDelete(),
                    HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class, id, sessionId);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSPluginException.throwException(e.getMessage());
        }
    }

    public Map getBugById(String id) {
        String sessionId = login();
        String bugGet = requestUrl.getBugGet();
        ResponseEntity<String> response = restTemplate.exchange(bugGet,
                HttpMethod.GET, null, String.class, id, sessionId);
        GetIssueResponse getIssueResponse = (GetIssueResponse) getResultForObject(GetIssueResponse.class, response);
        if(StringUtils.equalsIgnoreCase(getIssueResponse.getStatus(),"fail")){
            GetIssueResponse.Issue issue = new GetIssueResponse.Issue();
            issue.setId(id);
            issue.setSteps(StringUtils.SPACE);
            issue.setTitle(StringUtils.SPACE);
            issue.setStatus("closed");
            issue.setDeleted("1");
            issue.setOpenedBy(StringUtils.SPACE);
            getIssueResponse.setData(JSON.toJSONString(issue));
        }
        return JSON.parseMap(getIssueResponse.getData());
    }

    public GetCreateMetaDataResponse.MetaData getCreateMetaData(String productID) {
        String sessionId = login();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getCreateMetaData(),
                HttpMethod.GET, null, String.class, productID, sessionId);
        GetCreateMetaDataResponse getCreateMetaDataResponse = (GetCreateMetaDataResponse) getResultForObject(GetCreateMetaDataResponse.class, response);
        return JSON.parseObject(getCreateMetaDataResponse.getData(), GetCreateMetaDataResponse.MetaData.class);
    }

    public Map getCustomFields(String productID) {
        return getCreateMetaData(productID).getCustomFields();
    }

    public Map<String, Object> getBuildsByCreateMetaData(String projectId) {
        return getCreateMetaData(projectId).getBuilds();
    }

    public Map<String, Object> getBuilds(String projectId) {
        String sessionId = login();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getBuildsGet(),
                HttpMethod.GET, null, String.class, projectId, sessionId);
        return (Map<String, Object>) JSON.parseMap((String) JSON.parseMap(response.getBody()).get("data"));
    }

    public Map<String, Object> getUsers() {
        String sessionId = login();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getUserGet() + sessionId,
                HttpMethod.GET, null, String.class);
        return (Map<String, Object>) JSON.parseMap(response.getBody());
    }

    public Map<String, Object> createCaseModule(String rootID, String type, String modules, Object parentModuleID){
        String sessionId = login();
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("modules[]",modules);
        paramMap.add("parentModuleID", parentModuleID);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getCreateModule() + sessionId,
                HttpMethod.POST, requestEntity, String.class, rootID, type);
        return (Map<String, Object>) JSON.parseMap(response.getBody());
    }


    public String updateTestcase(MSCase msCase, List<FromData> list){
        String sessionId = login();
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        for(FromData item : list){
            paramMap.add(item.getKey(), item.getValue());
        }
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getUpdateTestcase() + sessionId,
                HttpMethod.POST, requestEntity, String.class, msCase.getZentaoId());
        return response.getBody();
    }

    public String setMenu(Object projectID, String sessionId){
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getSetMenu() + sessionId,
                HttpMethod.GET, requestEntity, String.class, projectID);
        return response.getBody();
    }

    public String getByID(Object projectID, String sessionId){
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getGetByID() + sessionId,
                HttpMethod.GET, requestEntity, String.class, projectID);
        return response.getBody();
    }

    public String saveState(Object projectID, Object executions,  String sessionId){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie","tab=execution;");
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(new LinkedMultiValueMap<>(), headers);
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getSaveState() + sessionId,
                HttpMethod.GET, requestEntity, String.class, projectID, executions);
        return response.getBody();
    }


    public String createTestcase(Integer executionID,Object productID,Integer projectID, List<FromData> list, Integer moduleId){
          String sessionId = login();
          ajaxGet(productID, moduleId, sessionId);
//        linked2project("user", "1", "login", "", projectID, sessionId);
//        String bb = setMenu(projectID, sessionId);
//        String aa = executionTestcase(projectID, sessionId);
//        String id = getByID(15, sessionId);
          String proSi = setProjectSession(executionID, sessionId);
//        String state = saveState(productID, Arrays.asList(aa), sessionId);
//        linked2project("case", "2", "Opened", "", projectID, sessionId);
//        String a = getRelatedFields("case", 3+"", "linked2execution",  projectID, sessionId);
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        for(FromData item : list){
            paramMap.add(item.getKey(), item.getValue());
        }
        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cookie","tab=execution;");
        headers.add("Cookie","tab=project;");
        headers.add("Cookie","lastCaseModule="+moduleId+";");
//        headers.add("Cookie", "lastProject=13;");
//        headers.add("Cookie", "project=13;");
//        headers.add("session","project=13");
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, headers);
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getCreateTestcase() + sessionId,
                HttpMethod.POST, requestEntity, String.class,productID, moduleId);
        JSONObject jsonObject = JSONObject.parseObject(response.getBody());
        String caseId = jsonObject.getString("id");
//        String linked2project = linked2project("case", caseId, "linked2project", "", projectID, sessionId);
//        String Opened = linked2project("case", caseId, "Opened", "", projectID, sessionId);
//        String linked2project = linked2project("case", caseId, "linked2execution", "", projectID, sessionId);
//        String bbbb = setMenu(projectID, sessionId);
//            System.out.println(response);
        return response.getBody();
    }

    public String executionTestcase(Integer projectID, String sessionId){
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, headers);
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getExecutionTestcase() + sessionId,
                HttpMethod.GET, requestEntity, String.class, projectID);
        return response.getBody();
    }

    public String setProjectSession(Integer executionID, String sessionId){
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie","tab=project;");
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, headers);
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getSetProjectSession() + sessionId,
                HttpMethod.GET, requestEntity, String.class, executionID);
        return response.getBody();
    }

    public String linked2project(String type, String caseID, String functionType, String temp, Integer projectID, String sessionId) {
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, headers);
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getLinked2project() + sessionId,
                HttpMethod.GET, requestEntity, String.class, type, caseID, functionType, temp, projectID);
        return response.getBody();
    }

    public String getRelatedFields(String objectType, String objectID, String actionType, Integer extra, String sessionId){
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, headers);
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getRelatedFields() + sessionId,
                HttpMethod.GET, requestEntity, String.class, objectType, objectID, actionType, extra);
        return response.getBody();
    }

//    public Map<String, String> getModuleListByType(String type) {
//        String sessionId = login();
//        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getGetModuleListByType() + sessionId,
//                HttpMethod.GET, null, String.class, type);
//        return (Map<String, String>) JSON.parseMap(response.getBody());
//    }

    public String getTestCaseView(String caseId) {
        String sessionId = login();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getTestcaseView() + sessionId,
                HttpMethod.GET, null, String.class, caseId);
        return JSON.toJSONString(response.getBody());
    }



    public Map<String, String> getProjectTree(String productID) {
        String sessionId = login();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getGetTreeProject() + sessionId,
                HttpMethod.GET, null, String.class, productID);
        return (Map<String, String>) JSON.parseMap(response.getBody());
    }

    public String ajaxGet(Object productID, Integer moduleId, String sessionId) {
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getAjaxGetProductStories() + sessionId,
                HttpMethod.GET, null, String.class, productID, moduleId);
        return JSON.toJSONString(response.getBody());
    }


    public Map<String, String> getTree(String productID) {
        String sessionId = login();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getTreeBrowse() + sessionId,
                HttpMethod.GET, null, String.class, productID);
        return (Map<String, String>) JSON.parseMap(response.getBody());
    }

    public Map<String, Object> getTreeMenu(String root, String productId) {
        String sessionId = login();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getGetTreeMeun() + sessionId,
                HttpMethod.GET, null, String.class, root, productId);
        return (Map<String, Object>) JSON.parseMap(response.getBody());
    }



    public Map<String, Object> getTestCase(String productID, Integer projectID) {
        String sessionId = login();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getGetTestCase() + sessionId,
                HttpMethod.GET, null, String.class, productID, projectID);
        return (Map<String, Object>) JSON.parseMap(response.getBody());
    }

    public Map<String, Object> getDemands(String projectKey) {
        String sessionId = login();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getStoryGet() + sessionId,
                HttpMethod.GET, null, String.class, projectKey);
        return (Map<String, Object>) JSON.parseMap(response.getBody());
    }

    public Map<String, Object> getBuildsV17(String projectId) {
        String sessionId = login();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getBuildsGetV17(),
                HttpMethod.GET, null, String.class, projectId, sessionId);
        return (Map<String, Object>) JSON.parseMap(response.getBody()).get("data");
    }

    public String uploadFile(File file) {
        String id = "";
        String sessionId = login();
        HttpHeaders httpHeaders = new HttpHeaders();
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("files", new FileSystemResource(file));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, httpHeaders);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl.getFileUpload(), HttpMethod.POST, requestEntity,
                    String.class, null, sessionId);
            String body = responseEntity.getBody();
            Map obj = JSON.parseMap(body);
            Map data = (Map) JSON.parseObject(obj.get("data").toString());
            Set<String> set = data.keySet();
            if (!set.isEmpty()) {
                id = (String) set.toArray()[0];
            }
        } catch (Exception e) {
            LogUtil.error(e, e.getMessage());
        }
        LogUtil.info("upload file id: " + id);
        return id;
    }

    public Map getBugsByProjectId(String projectId, Integer pageNum, Integer pageSize) {
        String sessionId = login();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getBugList(),
                HttpMethod.GET, null, String.class, projectId, 9999999, pageSize, pageNum, sessionId);
        try {
            return JSON.parseMap(JSON.parseMap(response.getBody()).get("data").toString());
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException("请检查配置信息是否填写正确！");
        }
        return null;
    }

    public String getBaseUrl() {
        if (ENDPOINT.endsWith("/")) {
            return ENDPOINT.substring(0, ENDPOINT.length() - 1);
        }
        return ENDPOINT;
    }

    public void setConfig(ZentaoConfig config) {
        if (config == null) {
            MSPluginException.throwException("config is null");
        }
        USER_NAME = config.getAccount();
        PASSWD = config.getPassword();
        ENDPOINT = config.getUrl();
    }


    public String getReplaceImgUrl(String replaceImgUrl) {
        String baseUrl = getBaseUrl();
        String[] split = baseUrl.split("/");
        String suffix = split[split.length - 1];
        if (StringUtils.equals("biz", suffix)) {
            suffix = baseUrl;
        } else if (!StringUtils.equalsAny(suffix, "zentao", "pro", "zentaopms", "zentaopro", "zentaobiz")) {
            suffix = "";
        } else {
            suffix = "/" + suffix;
        }
        return String.format(replaceImgUrl, suffix);
    }

    public void checkProjectExist(String relateId) {
        String sessionId = login();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl.getProductGet(),
                HttpMethod.GET, null, String.class, relateId, sessionId);
        try {
            Map data = ((Map) JSON.parseObject(JSON.parseMap(response.getBody()).get("data").toString()));
            if (data.get("id") != null || ((Map) data.get("product")).get("id") != null) {
                return;
            }
        } catch (Exception e) {
            LogUtil.error("checkProjectExist error: " + response.getBody());
        }
        MSPluginException.throwException("验证失败");
    }

    public void uploadAttachment(String objectType, String objectId, File file) {
        String sessionId = login();
        HttpHeaders authHeader = new HttpHeaders();
        authHeader.setContentType(MediaType.parseMediaType("multipart/form-data; charset=UTF-8"));

        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        FileSystemResource fileResource = new FileSystemResource(file);
        paramMap.add("files", fileResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, authHeader);

        try {
            restTemplate.exchange(requestUrl.getFileUpload(), HttpMethod.POST, requestEntity,
                    String.class, objectId, sessionId);
        } catch (Exception e) {
            LogUtil.info("upload zentao attachment error");
        }
    }

    public void deleteAttachment(String fileId) {
        String sessionId = login();
        try {
            restTemplate.exchange(requestUrl.getFileDelete(), HttpMethod.GET, null, String.class, fileId, sessionId);
        } catch (Exception e) {
            LogUtil.info("delete zentao attachment error");
        }
    }

    public byte[] getAttachmentBytes(String fileId) {
        String sessionId = login();
        ResponseEntity<byte[]> response = restTemplate.exchange(requestUrl.getFileDownload(), HttpMethod.GET,
                null, byte[].class, fileId, sessionId);
        return response.getBody();
    }

    public ResponseEntity proxyForGet(String path, Class responseEntityClazz) {
        LogUtil.info("zentao proxyForGet: " + path);
        String url = this.ENDPOINT + path;
        validateProxyUrl(url, "/index.php", "/file-read-");
        return restTemplate.exchange(url, HttpMethod.GET, null, responseEntityClazz);
    }
}
