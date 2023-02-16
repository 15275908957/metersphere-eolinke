package io.metersphere.platform.impl;

import im.metersphere.plugin.exception.MSPluginException;
import im.metersphere.plugin.utils.JSON;
import im.metersphere.plugin.utils.LogUtil;
import io.metersphere.base.domain.IssuesWithBLOBs;
import io.metersphere.platform.api.AbstractPlatform;
import io.metersphere.platform.commons.FieldTypeMapping;
import io.metersphere.platform.constants.CustomFieldType;
import io.metersphere.platform.domain.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PingCodePlatform extends AbstractPlatform {
    public PingCodeClient pingCodeClient;
    protected SimpleDateFormat sdfWithZone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    protected SimpleDateFormat sdfWithOutZone = new SimpleDateFormat("yyyy-MM-dd");

    protected Boolean isSass = false;
    protected PingCodeProjectConfig projectConfig;

    public PingCodePlatform(PlatformRequest request) {
        try{
            super.key = PingCodePlatformMetaInfo.KEY;
            super.request = request;
            pingCodeClient = new PingCodeClient();
            setConfig();
        }catch (Exception e){
            System.out.println("获取插件实体类异常"+e.getMessage());
        }
    }

    public PingCodeConfig setConfig() {
        PingCodeConfig config = getIntegrationConfig();
        validateConfig(config);
        pingCodeClient.setConfig(config);
        return config;
    }

    private void validateConfig(PingCodeConfig config) {
        pingCodeClient.setConfig(config);
        if (config == null) {
            MSPluginException.throwException("PingCode config is null");
        }
    }

    public PingCodeConfig getIntegrationConfig() {
        return getIntegrationConfig(PingCodeConfig.class);
    }

    @Override
    public List<DemandDTO> getDemands(String projectConfigStr) {

        List<DemandDTO> list = new ArrayList<>();
        projectConfig = getProjectConfig(projectConfigStr);

        int maxResults = 50, startAt = 0;
        List demands;
        do {
            demands = pingCodeClient.getDemands(projectConfig.getPingCodeKey(), projectConfig.getPingCodeStoryTypeId(), startAt, maxResults);
            for (int i = 0; i < demands.size(); i++) {
                Map o = (Map) demands.get(i);
                LogUtil.info("提取对象为：" + JSON.toJSONString(o));
                String issueKey = o.get("id").toString();
                String title = o.get("title").toString();
                String code = o.get("identifier").toString();
                DemandDTO demandDTO = new DemandDTO();
                demandDTO.setName(code+" "+title);
                demandDTO.setId(issueKey);
                demandDTO.setPlatform(key);
                list.add(demandDTO);
            }
            startAt += maxResults;
        } while (demands.size() >= maxResults);
        return list;
    }

    @Override
    public IssuesWithBLOBs addIssue(PlatformIssuesUpdateRequest request) {
        projectConfig = getProjectConfig(request.getProjectConfig());
        // 刷新个人token，如果成功则修改参数
        pingCodeClient.refreshToken(request.getUserPlatformUserConfig());
//        validateProjectKey(projectConfig.getPingCodeKey());
        PingCodeIssueInfo addPingCodeIssueParam = buildUpdateParam(request, projectConfig.getPingCodeIssueTypeId(), projectConfig.getPingCodeKey());
        PingCodeAddIssueResponse result = pingCodeClient.addIssue(JSON.toJSONString(addPingCodeIssueParam));
        PingCodeIssueInfo re = null;
        for(int i = 0; i < 10 ; i++){
            try{
                Thread.currentThread().sleep(3000 * i);
                re = pingCodeClient.getIssuesId(result.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            if(re != null) {
                LogUtil.info("获取到的缺陷内容：" + JSON.toJSONString(re));
                //查出有值跳出循环
                break;
            }
        }
        if(re == null) {
            //三次都没查出有值，视为失败。
            MSPluginException.throwException("添加缺陷失败，获取缺陷异常："+JSON.toJSONString(re));
        }
        request.setPlatformStatus("");
        request.setPlatformId(result.getId());
        request.setTitle(re.getTitle());
        request.setId(result.getId());
        return request;
    }

    @Override
    public byte[] getAttachmentContent(String fileKey) {
        return  pingCodeClient.getAttachmentContent(fileKey);
    }


    public PlatformIssuesDTO getUpdateIssueTemp(PlatformIssuesDTO issue, PingCodeGetIssue pingCodeIssue) {
        try {
            if (issue == null) {
                issue = new PlatformIssuesDTO();
                if (StringUtils.isNotBlank(defaultCustomFields)) {
                    issue.setCustomFieldList(JSON.parseArray(defaultCustomFields, PlatformCustomFieldItemDTO.class));
                } else {
                    issue.setCustomFieldList(new ArrayList<>());
                }
            } else {
                mergeCustomField(issue, defaultCustomFields);
            }
            List<PlatformCustomFieldItemDTO> fields = new ArrayList<>();
            PlatformCustomFieldItemDTO customField3 = new PlatformCustomFieldItemDTO();
            customField3.setId("title");
            customField3.setCustomData("title");
            customField3.setName("标题");
            customField3.setRequired(true);
            customField3.setValue(pingCodeIssue.getTitle());
            customField3.setType(CustomFieldType.INPUT.getValue());
            fields.add(customField3);

            PlatformCustomFieldItemDTO customField4 = new PlatformCustomFieldItemDTO();
            customField4.setId("description");
            customField4.setCustomData("description");
            customField4.setName("缺陷内容");
            customField4.setRequired(true);
            customField4.setValue(pingCodeIssue.getDescription());
            customField4.setType(CustomFieldType.RICH_TEXT.getValue());
            fields.add(customField4);


            PlatformCustomFieldItemDTO customField6 = new PlatformCustomFieldItemDTO();
            customField6.setId("start_at");
            customField6.setCustomData("start_at");
            customField6.setName("开始时间");
            customField6.setRequired(false);
            customField6.setValue(pingCodeIssue.getStart_at());
            customField6.setType(CustomFieldType.DATE.getValue());
            fields.add( customField6);

            PlatformCustomFieldItemDTO customField5 = new PlatformCustomFieldItemDTO();
            customField5.setId("end_at");
            customField5.setCustomData("end_at");
            customField5.setName("截止时间");
            customField5.setRequired(false);
            customField5.setValue(pingCodeIssue.getEnd_at());
            customField5.setType(CustomFieldType.DATE.getValue());
            fields.add(customField5);

            LogUtil.info("参数为：" + JSON.toJSONString(issue.getCustomFieldList()));
            issue.setTitle(pingCodeIssue.getTitle());
            issue.setLastmodify(null);
            issue.setDescription(pingCodeIssue.getDescription());
            issue.setPlatform(key);
            issue.setCustomFields(JSON.toJSONString(fields));
            return issue;
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e);
            return null;
        }
    }

    @Override
    public void syncAllIssues(SyncAllIssuesRequest syncRequest) {
        PingCodeProjectConfig projectConfig = getProjectConfig(syncRequest.getProjectConfig());
        System.out.println("1");
        this.isThirdPartTemplate = projectConfig.isThirdPartTemplate();
        if (projectConfig.isThirdPartTemplate()) {
            super.defaultCustomFields = getCustomFieldsValuesString(getThirdPartCustomField(syncRequest.getProjectConfig()));
        } else {
            super.defaultCustomFields = syncRequest.getDefaultCustomFields();
        }
        System.out.println("2");
        int startAt = 0;
        int maxResults = 100;
        List<PingCodeGetIssue> pingCodeIssues;
        int currentSize;
        List<PingCodeIssueProperties> properties = pingCodeClient.getIssueProperties(projectConfig.getPingCodeIssueTypeId(), "scrum");
        System.out.println("3");
        this.projectConfig = projectConfig;
        SyncAllIssuesResult syncIssuesResult = new SyncAllIssuesResult();
        do {
            String pingCodeKey = projectConfig.getPingCodeKey();
            validateProjectKey(pingCodeKey);
            System.out.println("4");
            List<PingCodeGetIssue> result = pingCodeClient.getProjectIssues(startAt, maxResults, pingCodeKey, "bug");
            System.out.println("5");
            pingCodeIssues = result;
            currentSize = pingCodeIssues.size();
            List<String> allIds = pingCodeIssues.stream().map(PingCodeGetIssue::getId).collect(Collectors.toList());
            System.out.println("6");
            syncIssuesResult.getAllIds().addAll(allIds);
            if (syncRequest != null) {
                pingCodeIssues = filterSyncJiraIssueByCreated(pingCodeIssues, syncRequest);
            }
            System.out.println("7.2");
            LogUtil.info("过滤后的缺陷信息为：" + JSON.toJSONString(pingCodeIssues));
            if (CollectionUtils.isNotEmpty(pingCodeIssues)) {
                for (PingCodeGetIssue pingCodeIssue : pingCodeIssues) {
                    PlatformIssuesDTO issue = getUpdateIssue(null, pingCodeIssue, projectConfig, properties);
                    System.out.println("8");
                    // 设置临时UUID，同步附件时需要用
                    LogUtil.info("当前转化的缺陷为：" + JSON.toJSONString(issue));
//                    List<PingCodeFile> pingCodeFileList = pingCodeClient.getFiles(pingCodeIssue.getId());
//                    List<PlatformAttachment> attachments = new ArrayList<>();
//                    for(PingCodeFile item: pingCodeFileList){
//                        PlatformAttachment platformAttachment = new PlatformAttachment();
//                        platformAttachment.setFileKey(item.getDownload_url());
//                        platformAttachment.setFileName(item.getTitle());
//                        attachments.add(platformAttachment);
//                    }
//                    if(attachments.size() != 0){
//                        issue.setAttachments(attachments);
//                        syncIssuesResult.getAttachmentMap().put(pingCodeIssue.getId(), attachments);
//                    }
                    syncIssuesResult.getUpdateIssues().add(issue);
                }
            }
            System.out.println("9");
            startAt += maxResults;
        } while (currentSize >= maxResults);
        System.out.println("实际返回实例"+JSON.toJSONString(syncIssuesResult.getUpdateIssues().get(0)));
        System.out.println("实际返回实例ALLIDS"+JSON.toJSONString(syncIssuesResult.getAllIds()));
//        System.out.println("实际返回实例ALLIDS"+JSON.toJSONString(syncIssuesResult.getAttachmentMap()));
        HashMap<Object, Object> syncParam = buildSyncAllParam(syncIssuesResult);
        System.out.println("10");
        syncRequest.getHandleSyncFunc().accept(syncParam);
        System.out.println("11");
    }

    public String getWorkItemIssue(PingCodeProjectConfig projectConfig){
        List<PingCodeGetIssue> p = pingCodeClient.getProjectIssues(0,100,projectConfig.getPingCodeKey(),projectConfig.getPingCodeIssueTypeId());
        return JSON.toJSONString(p);
    }

    @Override
    public List<SelectOption> getProjectOptions(GetOptionRequest request) {
        String method = request.getOptionMethod();
        try {
            // 这里反射调用 getIssueTypes 获取下拉框选项
            return (List<SelectOption>) this.getClass().getMethod(method, request.getClass()).invoke(this, request);
        } catch (InvocationTargetException e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getTargetException());
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e);
        }
        return null;
    }

    @Override
    public IssuesWithBLOBs updateIssue(PlatformIssuesUpdateRequest request) {
        projectConfig = getProjectConfig(request.getProjectConfig());
        // 刷新个人token，如果成功则修改参数
        pingCodeClient.refreshToken(request.getUserPlatformUserConfig());
        validateProjectKey(projectConfig.getPingCodeKey());
        PingCodeIssueInfo param = buildUpdateParam(request, projectConfig.getPingCodeIssueTypeId(), projectConfig.getPingCodeKey());
        pingCodeClient.updateIssue(request.getPlatformId(), JSON.toJSONString(param));
        return request;
    }

    @Override
    public void deleteIssue(String platformId) {
        pingCodeClient.deleteIssue(platformId);
    }

    @Override
    public void validateIntegrationConfig() {
        pingCodeClient.auth4AccessToken();
    }

    @Override
    public void validateProjectConfig(String projectConfig) {
        try {
            PingCodeProjectConfig projectConfigEntity = getProjectConfig(projectConfig);
            PingCodeProject project = pingCodeClient.getProject(projectConfigEntity.getPingCodeKey());
            if (project != null && StringUtils.isBlank(project.getId())) {
                MSPluginException.throwException("项目不存在");
            }
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getMessage());
        }
    }

    @Override
    public void validateUserConfig(String userConfig) {
        pingCodeClient.refreshToken(userConfig);
        validateIntegrationConfig();
    }

    @Override
    public boolean isAttachmentUploadSupport() {
        return false;
    }

    @Override
    public SyncIssuesResult syncIssues(SyncIssuesRequest request) {
        LogUtil.info("同步缺陷请求为：" + JSON.toJSONString(request));

        projectConfig = getProjectConfig(request.getProjectConfig());
        super.isThirdPartTemplate = projectConfig.isThirdPartTemplate();
        List<PingCodeIssueProperties> properties = pingCodeClient.getIssueProperties(projectConfig.getPingCodeIssueTypeId(), "scrum");
        if (projectConfig.isThirdPartTemplate()) {
            super.defaultCustomFields = getCustomFieldsValuesString(getThirdPartCustomField(request.getProjectConfig()));
        } else {
            super.defaultCustomFields = request.getDefaultCustomFields();
        }
        List<PlatformIssuesDTO> issues = request.getIssues();
        LogUtil.info("开始执行同步缺陷。。。。。。。。。");

        SyncIssuesResult syncIssuesResult = new SyncIssuesResult();

        issues.forEach(item -> {
            try {
                LogUtil.info("参数信息item为：" + JSON.toJSONString(item));
                PingCodeGetIssue pingCodeIssue = pingCodeClient.getIssues(item.getPlatformId());
                IssuesWithBLOBs iwb = getUpdateIssue(item, pingCodeIssue, projectConfig, properties);
                syncIssuesResult.getUpdateIssues().add(iwb);
                // 同步第三方平台附件
                List<PingCodeFile> pingCodeFileList = pingCodeClient.getFiles(pingCodeIssue.getId());
                List<PlatformAttachment> attachments = new ArrayList<>();
                for(PingCodeFile pfitem: pingCodeFileList){
                    PlatformAttachment platformAttachment = new PlatformAttachment();
                    platformAttachment.setFileKey(pfitem.getDownload_url());
                    platformAttachment.setFileName(pfitem.getTitle());
                    attachments.add(platformAttachment);
                }
                if(attachments.size() != 0){
                    syncIssuesResult.getAttachmentMap().put(pingCodeIssue.getId(), attachments);
                }
            } catch (HttpClientErrorException e) {
                if (e.getRawStatusCode() == 404) {
                    syncIssuesResult.getDeleteIssuesIds().add(item.getId());
                }
            } catch (Exception e) {
                LogUtil.error(e);
            }
        });
        LogUtil.info("结束执行同步缺陷。。。。。。。。。" + JSON.toJSONString(syncIssuesResult));
        return syncIssuesResult;
    }

    public PlatformIssuesDTO getUpdateIssue(PlatformIssuesDTO issue, PingCodeGetIssue pingCodeIssue, PingCodeProjectConfig projectConfig, List<PingCodeIssueProperties> properties) {
        if(issue == null){
            issue = new PlatformIssuesDTO();
        }
        try {
            List<PingCodeShowTemplate> pingCodeShowTemplates = JSON.parseArray(projectConfig.getParamNames(), PingCodeShowTemplate.class);
            List<PlatformCustomFieldItemDTO> fields = new ArrayList<>();
            for(PingCodeShowTemplate pingCodeShowTemplate : pingCodeShowTemplates){
                PlatformCustomFieldItemDTO customField = new PlatformCustomFieldItemDTO();
                customField.setName(pingCodeShowTemplate.getName());
                customField.setRequired(pingCodeShowTemplate.getRequired());
                customField.setDefaultValue(pingCodeShowTemplate.getDefaultValue()+"");
                // 先判断系统字段，再判断自定义字段
                switch (pingCodeShowTemplate.getName()){
                    case "标题":
                        issue.setTitle(pingCodeIssue.getTitle());
                        customField.setId("title");
                        customField.setCustomData("title");
                        customField.setType(CustomFieldType.INPUT.getValue());
                        customField.setValue(pingCodeIssue.getTitle());
                        fields.add(customField);
                        break;
                    case "所属用户故事":
                        customField.setId("parent_id");
                        customField.setCustomData("parent_id");
                        customField.setType(CustomFieldType.SELECT.getValue());
                        if(pingCodeIssue.getParent() != null){
                            customField.setValue(pingCodeIssue.getParent().getId());
                        }
                        fields.add(customField);
                        break;
                    case "负责人":
                        customField.setId("assignee");
                        customField.setCustomData("assignee");
                        customField.setType(CustomFieldType.SELECT.getValue());
                        if(pingCodeIssue.getAssignee() != null){
                            customField.setValue(pingCodeIssue.getAssignee().getId());
                        }
                        fields.add(customField);
                        break;
                    case "开始时间":
                        customField.setId("start_at");
                        customField.setCustomData("start_at");
                        customField.setType(CustomFieldType.DATE.getValue());
                        if(pingCodeIssue.getStart_at() != 0l){
                            Date date = new Date(pingCodeIssue.getStart_at()*1000l);
                            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                            try{
                                String dateStr = s.format(date);
                                customField.setValue(dateStr);
                            } catch (Exception e){
                            }
                        }
                        fields.add(customField);
                        break;
                    case "截止时间":
                        customField.setId("end_at");
                        customField.setCustomData("end_at");
                        customField.setType(CustomFieldType.DATE.getValue());
                        if(pingCodeIssue.getStart_at() != 0l){
                            Date date = new Date(pingCodeIssue.getEnd_at()*1000l);
                            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                            try{
                                String dateStr = s.format(date);
                                customField.setValue(dateStr);
                            } catch (Exception e){
                            }
                        }
                        fields.add(customField);
                        break;
                    case "优先级":
                        customField.setId("priority");
                        customField.setCustomData("priority");
                        customField.setType(CustomFieldType.SELECT.getValue());
                        if(pingCodeIssue.getPriority() != null){
                            customField.setValue(pingCodeIssue.getPriority().getId());
                        }
                        fields.add(customField);
                        break;
                    case "故事点":
                        customField.setId("story_points");
                        customField.setCustomData("story_points");
                        customField.setType(CustomFieldType.FLOAT.getValue());
                        customField.setValue(""+pingCodeIssue.getStory_points());
                        fields.add(customField);
                        break;
                    case "预估工时":
                        customField.setId("estimated_workload");
                        customField.setCustomData("estimated_workload");
                        customField.setType(CustomFieldType.FLOAT.getValue());
                        customField.setValue(""+pingCodeIssue.getEstimated_workload());
                        fields.add(customField);
                        break;
                    case "关注人":
                        customField.setId("participants");
                        customField.setCustomData("participants");
                        customField.setType(CustomFieldType.MULTIPLE_SELECT.getValue());
                        List<String> ids = null;
                        if(pingCodeIssue.getParticipants() != null && pingCodeIssue.getParticipants().size() != 0){
                            ids = new ArrayList<>();
                            for(PingCodeGetIssue.Participants item:pingCodeIssue.getParticipants()){
                                ids.add(item.getId());
                            }
                        }
                        customField.setValue(ids);
                        fields.add(customField);
                        break;
                    case "所属发布":
                        customField.setId("version");
                        customField.setCustomData("version");
                        customField.setType(CustomFieldType.SELECT.getValue());
                        if(pingCodeIssue.getVersion() != null){
                            customField.setValue(pingCodeIssue.getVersion().getId());
                        }
                        fields.add(customField);
                        break;
                    case "迭代":
                        customField.setId("iteration");
                        customField.setCustomData("iteration");
                        customField.setType(CustomFieldType.SELECT.getValue());
                        if(pingCodeIssue.getSprint() != null){
                            customField.setValue(pingCodeIssue.getSprint().getId());
                        }
                        fields.add(customField);
                        break;
                    default:
                        for(PingCodeIssueProperties item: properties){
                            if(StringUtils.equals(item.getProperty().getName(), pingCodeShowTemplate.getName())){
                                customField.setId(item.getId());
                                customField.setCustomData(item.getId());
                                customField.setType(FieldTypeMapping.getMsTypeByPingCodeType(item.getProperty().getType()));
                                Map<String, Object> map = pingCodeIssue.getProperties();
                                Object obj = map.get(item.getId());
                                if(obj == null) continue;
                                if(map != null){
                                    if(customField.getType().equals(CustomFieldType.DATE.getValue())){
                                        long time = Long.parseLong(""+map.get(item.getId()));
                                        if(time == 0l) continue;
                                        Date d = new Date(time*1000l);
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        try{
                                            String dateStr = sdf.format(d);
                                            customField.setValue(dateStr);
                                        } catch (Exception e){
                                        }
                                    } else {
                                        customField.setValue(map.get(item.getId()));
                                    }
                                    fields.add(customField);
                                }
                            }
                        }
                }

            }
            if(pingCodeIssue.getState() != null){
                issue.setPlatformStatus(pingCodeIssue.getState().getId());
            }
            issue.setCreateTime(pingCodeIssue.getCreated_at() * 1000);
            issue.setPlatformId(pingCodeIssue.getId());
            issue.setId(pingCodeIssue.getId());
//            issue.setPlatformId(pingCodeIssue.getId());
            issue.setPlatform(key);
//            IssuesWithBLOBs issues setCustomFields
            issue.setCustomFields(JSON.toJSONString(fields));
//            issue.setCustomFieldList(fields);
//
//            if (issue == null) {
//                issue = new PlatformIssuesDTO();
//                if (StringUtils.isNotBlank(defaultCustomFields)) {
//                    issue.setCustomFieldList(JSON.parseArray(defaultCustomFields, PlatformCustomFieldItemDTO.class));
//                } else {
//                    issue.setCustomFieldList(new ArrayList<>());
//                }
//            } else {
//                mergeCustomField(issue, defaultCustomFields);
//            }
//            List<PlatformCustomFieldItemDTO> fields = new ArrayList<>();
//            PlatformCustomFieldItemDTO customField3 = new PlatformCustomFieldItemDTO();
//            customField3.setId("title");
//            customField3.setCustomData("title");
//            customField3.setName("标题");
//            customField3.setRequired(true);
//            customField3.setValue(pingCodeIssue.getTitle());
//            customField3.setType(CustomFieldType.INPUT.getValue());
//            fields.add(customField3);
//
//            PlatformCustomFieldItemDTO customField4 = new PlatformCustomFieldItemDTO();
//            customField4.setId("description");
//            customField4.setCustomData("description");
//            customField4.setName("描述");
//            customField4.setRequired(true);
//            customField4.setValue(pingCodeIssue.getDescription());
//            customField4.setType(CustomFieldType.RICH_TEXT.getValue());
//            fields.add(customField4);
//
//
//            PlatformCustomFieldItemDTO customField6 = new PlatformCustomFieldItemDTO();
//            customField6.setId("start_at");
//            customField6.setCustomData("start_at");
//            customField6.setName("开始时间");
//            customField6.setRequired(false);
//            customField6.setValue(pingCodeIssue.getStart_at());
//            customField6.setType(CustomFieldType.DATE.getValue());
//            fields.add( customField6);
//
//            PlatformCustomFieldItemDTO customField5 = new PlatformCustomFieldItemDTO();
//            customField5.setId("end_at");
//            customField5.setCustomData("end_at");
//            customField5.setName("截止时间");
//            customField5.setRequired(false);
//            customField5.setValue(pingCodeIssue.getEnd_at());
//            customField5.setType(CustomFieldType.DATE.getValue());
//            fields.add(customField5);
//
//            LogUtil.info("参数为：" + JSON.toJSONString(issue.getCustomFieldList()));
//            issue.setTitle(pingCodeIssue.getTitle());
//            issue.setLastmodify(null);
//            issue.setDescription(pingCodeIssue.getDescription());
//            issue.setPlatform(key);
//            issue.setCustomFields(JSON.toJSONString(fields));
            return issue;
        } catch (Exception e) {
            e.printStackTrace();
//            LogUtil.error(e);
//            MSPluginException.throwException(e);
            return null;
        }
    }

    @Override
    public List<PlatformCustomFieldItemDTO> getThirdPartCustomField(String projectConfigStr) {
        projectConfig = getProjectConfig(projectConfigStr);
        List<PlatformCustomFieldItemDTO> fields = new ArrayList<>();
        if(StringUtils.isBlank(projectConfig.getParamNames())){
            return fields;
        }
        List<PingCodeShowTemplate> pingCodeShowTemplates = JSON.parseArray(projectConfig.getParamNames(), PingCodeShowTemplate.class);
        // 懒汉模式，不管用不用先请求一下
        List<PingCodeIssueProperties> properties = pingCodeClient.getIssueProperties(projectConfig.getPingCodeIssueTypeId(), "scrum");
        List<PingCodeProjectUser> userList = pingCodeClient.getProjectUser(projectConfig);
        for(PingCodeShowTemplate pingCodeShowTemplate:pingCodeShowTemplates){
            PlatformCustomFieldItemDTO customField = new PlatformCustomFieldItemDTO();
            customField.setName(pingCodeShowTemplate.getName());
            customField.setRequired(pingCodeShowTemplate.getRequired());
            customField.setDefaultValue(pingCodeShowTemplate.getDefaultValue()+"");
            // 先判断系统字段，再判断自定义字段
            switch (pingCodeShowTemplate.getName()){
                case "标题":
                    customField.setId("title");
                    customField.setCustomData("title");
                    customField.setType(CustomFieldType.INPUT.getValue());
                    break;
                case "描述":
                    customField.setId("description");
                    customField.setCustomData("description");
                    customField.setType(CustomFieldType.RICH_TEXT.getValue());
                    break;
                case "所属用户故事":
                    customField.setId("parent_id");
                    customField.setCustomData("parent_id");
                    customField.setType(CustomFieldType.SELECT.getValue());
                    List<SelectOption> parent_id = new ArrayList<>();
                    List<DemandDTO> demandDTOS = getDemands(projectConfigStr);
                    for(DemandDTO item: demandDTOS){
                        parent_id.add(new SelectOption(item.getName(),item.getId()));
                    }
                    customField.setOptions(JSON.toJSONString(parent_id));
                    break;
                case "负责人":
                    customField.setId("assignee");
                    customField.setCustomData("assignee");
                    customField.setType(CustomFieldType.SELECT.getValue());
                    List<SelectOption> assignee = new ArrayList<>();
                    for(PingCodeProjectUser item: userList){
                        assignee.add(new SelectOption(item.getUser().getDisplay_name(),item.getId()));
                    }
                    customField.setOptions(JSON.toJSONString(assignee));
                    break;
                case "开始时间":
                    customField.setId("start_at");
                    customField.setCustomData("start_at");
                    customField.setType(CustomFieldType.DATE.getValue());
                    break;
                case "截止时间":
                    customField.setId("end_at");
                    customField.setCustomData("end_at");
                    customField.setType(CustomFieldType.DATE.getValue());
                    break;
                case "优先级":
                    customField.setId("priority");
                    customField.setCustomData("priority");
                    customField.setType(CustomFieldType.SELECT.getValue());
                    List<SelectOption> priority = new ArrayList<>();
                    List<PingCodePriority> pingCodePriorities = pingCodeClient.getPriorityList();
                    for(PingCodePriority item: pingCodePriorities){
                        priority.add(new SelectOption(item.getName(),item.getId()));
                    }
                    customField.setOptions(JSON.toJSONString(priority));
                    break;
                case "故事点":
                    customField.setId("story_points");
                    customField.setCustomData("story_points");
                    customField.setType(CustomFieldType.FLOAT.getValue());
                    break;
                case "预估工时":
                    customField.setId("estimated_workload");
                    customField.setCustomData("estimated_workload");
                    customField.setType(CustomFieldType.FLOAT.getValue());
                    break;
                case "关注人":
                    customField.setId("participants");
                    customField.setCustomData("participants");
                    customField.setType(CustomFieldType.MULTIPLE_SELECT.getValue());
                    List<SelectOption> participants = new ArrayList<>();
                    for(PingCodeProjectUser item: userList){
                        participants.add(new SelectOption(item.getUser().getDisplay_name(),item.getId()));
                    }
                    customField.setOptions(JSON.toJSONString(participants));
                    break;
                case "所属发布":
                    customField.setId("version");
                    customField.setCustomData("version");
                    customField.setType(CustomFieldType.SELECT.getValue());
                    List<SelectOption> version = new ArrayList<>();
                    List<PingCodeVersion> pingCodeVersions = pingCodeClient.getVersion(projectConfig);
                    for(PingCodeVersion item: pingCodeVersions){
                        version.add(new SelectOption(item.getName()+" "+item.getStage().getName(),item.getId()));
                    }
                    customField.setOptions(JSON.toJSONString(version));
                    break;
                case "迭代":
                    customField.setId("iteration");
                    customField.setCustomData("iteration");
                    customField.setType(CustomFieldType.SELECT.getValue());
                    List<SelectOption> iteration = new ArrayList<>();
                    List<PingCodeIteration> pingCodeIteration = pingCodeClient.getIteration(projectConfig);
                    for(PingCodeIteration item: pingCodeIteration){
                        String statusStr = null;
                        if(StringUtils.equals(item.getStatus(), "in_progress")){
                            statusStr = "进行中";
                        } else if(StringUtils.equals(item.getStatus(), "pending")){
                            statusStr = "未开始";
                        } else if(StringUtils.equals(item.getStatus(), "completed")){
                            statusStr = "已完成";
                        } else {
                            statusStr = item.getStatus();
                        }
                        iteration.add(new SelectOption(item.getName()+" "+statusStr,item.getId()));
                    }
                    customField.setOptions(JSON.toJSONString(iteration));
                    break;
                default:
                    for(PingCodeIssueProperties item: properties){
                        if(StringUtils.equals(item.getProperty().getName(), pingCodeShowTemplate.getName())){
                            customField.setId(item.getId());
                            customField.setCustomData(item.getId());
                            customField.setType(FieldTypeMapping.getMsTypeByPingCodeType(item.getProperty().getType()));
                            if(StringUtils.equals(item.getProperty().getType(), "member") || StringUtils.equals(item.getProperty().getType(), "members")){
                                List<SelectOption> member = new ArrayList<>();
                                for(PingCodeProjectUser pcpu: userList){
                                    member.add(new SelectOption(pcpu.getUser().getDisplay_name(),pcpu.getId()));
                                }
                                customField.setOptions(JSON.toJSONString(member));
                            } else {
                                List<SelectOption> selectOptions = item.getProperty().getMSOptionList();
                                if(selectOptions.size() != 0){
                                    customField.setOptions(JSON.toJSONString(selectOptions));
                                }
                            }
                        }
                    }
            }
            fields.add(customField);
        }



//        PlatformCustomFieldItemDTO customField2 = new PlatformCustomFieldItemDTO();
//        customField2.setId("biaoji");
//        customField2.setCustomData("biaoji");
//        customField2.setName("标记");
//        customField2.setRequired(false);
//        customField2.setType(CustomFieldType.INPUT.getValue());
//        fields.add(customField2);
        // 其余字段获取属性

//        fields = fields.stream().filter(i -> StringUtils.isNotBlank(i.getType()))
//                .collect(Collectors.toList());

        // 按类型排序，富文本排最后，input 排最前面，title 排第一个
//        fields.sort((a, b) -> {
//            if (a.getType().equals(CustomFieldType.RICH_TEXT.getValue())) return 1;
//            if (b.getType().equals(CustomFieldType.RICH_TEXT.getValue())) return -1;
//            if (a.getId().equals("title")) return -1;
//            if (b.getId().equals("title")) return 1;
//            if (a.getType().equals(CustomFieldType.INPUT.getValue())) return -1;
//            if (b.getType().equals(CustomFieldType.INPUT.getValue())) return 1;
//            return a.getType().compareTo(b.getType());
//        });
        return fields;
    }

    public void rmProperties(List<PingCodeIssueProperties> properties, String... item){
        List<PingCodeIssueProperties> temp = new ArrayList<>();
        for(PingCodeIssueProperties p:properties){
            for(String i : item){
                if(p.getProperty().getName().equals(i)){
                    temp.add(p);
                    break;
                }
            }
        }
        if(temp.size() != 0){
            for(PingCodeIssueProperties p:temp){
                properties.remove(p);
            }
        }
    }


    @Override
    public ResponseEntity proxyForGet(String url, Class responseEntityClazz) {
        return null;
    }

    @Override
    public void syncIssuesAttachment(SyncIssuesAttachmentRequest request) {
        pingCodeClient.uploadFiles(request);
    }

    @Override
    public List<PlatformStatusDTO> getStatusList(String issueKey) {
        List<PlatformStatusDTO> platformStatusDTOS = new ArrayList<>();
        List statusList = pingCodeClient.getIssueStatusList();
        if (CollectionUtils.isNotEmpty(statusList)) {
            statusList.forEach(item -> {
                Map o = (Map) item;
                PlatformStatusDTO platformStatusDTO = new PlatformStatusDTO();
                platformStatusDTO.setLabel(o.get("name").toString());
                platformStatusDTO.setValue(o.get("id").toString());
                platformStatusDTOS.add(platformStatusDTO);
            });
        }
        return platformStatusDTOS;
    }

    public PingCodeProjectConfig getProjectConfig(String configStr) {
        if (StringUtils.isBlank(configStr)) {
            MSPluginException.throwException("请在项目中添加项目配置！");
        }
        PingCodeProjectConfig projectConfig = JSON.parseObject(configStr, PingCodeProjectConfig.class);
        return projectConfig;
    }

    /**
     * 由 getProjectOptions 反射调用
     *
     * @return
     */
    public List<SelectOption> getIssueTypes(GetOptionRequest request) {
        List<SelectOption> selectOptions = new ArrayList<>();
        selectOptions.add(new SelectOption("epic", "epic"));
        selectOptions.add(new SelectOption("feature", "feature"));
        selectOptions.add(new SelectOption("story", "story"));
        selectOptions.add(new SelectOption("task", "task"));
        selectOptions.add(new SelectOption("bug", "bug"));
        selectOptions.add(new SelectOption("issue", "issue"));
        LogUtil.info("返回内容为：" + JSON.toJSONString(selectOptions));
        return selectOptions;
    }

    private PingCodeIssueInfo buildUpdateParam(PlatformIssuesUpdateRequest request, String issueTypeId, String pingCodeKey) {
        request.setPlatform(key);
        LogUtil.info("请求内容为：" + JSON.toJSONString(request));
        System.out.println("请求内容为：" + JSON.toJSONString(request));
        PingCodeIssueInfo addPingCodeIssueParam = new PingCodeIssueInfo();
        addPingCodeIssueParam.setProject_id(pingCodeKey);
        if(request.getTransitions() != null){
            addPingCodeIssueParam.setState_id(request.getTransitions().getValue());
        }
        for(PlatformCustomFieldItemDTO item : request.getCustomFieldList()){
            // 跳过空值的属性
            if(item.getValue() == null || StringUtils.isBlank(item.getValue()+"")) continue;
            // 先转换系统字段
            if(item.getName().equals("所属用户故事")){
                addPingCodeIssueParam.setParent_id(item.getValue()+"");
            } else if(item.getName().equals("负责人")){
                addPingCodeIssueParam.setAssignee_id(item.getValue()+"");
            } else if(item.getName().equals("开始时间")){
                addPingCodeIssueParam.setStart_at(getTimeByDateStr(item.getValue()+""));
            } else if(item.getName().equals("截止时间")){
                addPingCodeIssueParam.setEnd_at(getTimeByDateStr(item.getValue()+""));
            } else if(item.getName().equals("优先级")){
                addPingCodeIssueParam.setPriority_id(item.getValue()+"");
            } else if(item.getName().equals("预估工时")){
                Float value = 0f;
                if(item.getValue() != null && !StringUtils.equals("",item.getValue()+"")){
                    value = Float.parseFloat(item.getValue()+"");
                }
                addPingCodeIssueParam.setEstimated_workload(value);
            } else if(item.getName().equals("关注人")){
                List<String> ids = JSON.parseArray(item.getValue()+"");
                addPingCodeIssueParam.setParticipant_ids(ids);
            } else if(item.getName().equals("所属发布")){
                addPingCodeIssueParam.setVersion_id(item.getValue()+"");
            } else if(item.getName().equals("迭代")){
                addPingCodeIssueParam.setSprint_id(item.getValue()+"");
            } else if(item.getName().equals("故事点")){
                Float value = 0f;
                if(item.getValue() != null && !StringUtils.equals("",item.getValue()+"")){
                    value = Float.parseFloat(item.getValue()+"");
                }
                addPingCodeIssueParam.setStory_points(value);
            } else if(item.getName().equals("标题")) {
                addPingCodeIssueParam.setTitle(item.getValue()+"");
            } else if(item.getName().equals("描述")) {
                addPingCodeIssueParam.setDescription(item.getValue()+"");
            } else {
                if(item.getType().equals(CustomFieldType.DATE.getValue())){
                    addPingCodeIssueParam.getProperties().put(item.getCustomData(), getTimeByDateStr(item.getValue()+""));
                } else if(item.getType().equals(CustomFieldType.MULTIPLE_SELECT.getValue())){
                    List<String> value = JSON.parseArray(item.getValue()+"", String.class);
                    addPingCodeIssueParam.getProperties().put(item.getCustomData(), value);
                } else {
                    addPingCodeIssueParam.getProperties().put(item.getCustomData(), item.getValue());
                }
            }
        }
//        if (isThirdPartTemplate) {
//            parseCustomFiled(request, addPingCodeIssueParam);
//            request.setTitle(addPingCodeIssueParam.get("title").toString());
//        } else {
//            addPingCodeIssueParam.put("project_id", pingCodeKey);
//            addPingCodeIssueParam.put("title", request.getTitle());
//            addPingCodeIssueParam.put("description", request.getDescription());
//            parseCustomFiled(request, addPingCodeIssueParam);
//        }
        return addPingCodeIssueParam;
    }

    public static long getTimeByDateStr(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date apiTime = format.parse(dateStr);
            return apiTime.getTime()/1000;
        } catch (Exception e) {

        }
        return 0l;
    }

    private void parseCustomFiled(PlatformIssuesUpdateRequest request, Map fields) {
        List<PlatformCustomFieldItemDTO> customFields = request.getCustomFieldList();
        customFields.forEach(item -> {
            String fieldName = item.getCustomData();
            String name = item.getName();
            if (StringUtils.isNotBlank(fieldName)) {
                if (ObjectUtils.isNotEmpty(item.getValue())) {
                    if (StringUtils.isNotBlank(item.getType())) {
                        if (StringUtils.equalsAny(item.getType(), "select", "radio", "member")) {
                            Map param = new LinkedHashMap<>();
                            if (fieldName.equals("assignee") || fieldName.equals("reporter")) {
                                if (isThirdPartTemplate || isSass) {
                                    param.put("id", item.getValue());
                                } else {
                                    param.put("accountId", item.getValue());
                                }
                            } else {
                                param.put("id", item.getValue());
                            }
                            fields.put(fieldName, param);
                        } else if (StringUtils.equalsAny(item.getType(), "multipleSelect", "checkbox", "multipleMember")) {
                            List attrs = new ArrayList();
                            if (item.getValue() != null) {
                                List values = JSON.parseArray((String) item.getValue());
                                values.forEach(v -> {
                                    Map param = new LinkedHashMap<>();
                                    param.put("id", v);
                                    attrs.add(param);
                                });
                            }
                            fields.put(fieldName, attrs);
                        } else if (StringUtils.equalsAny(item.getType(), "cascadingSelect")) {
                            if (item.getValue() != null) {
                                Map attr = new LinkedHashMap<>();
                                List values = JSON.parseArray((String) item.getValue());
                                if (CollectionUtils.isNotEmpty(values)) {
                                    if (values.size() > 0) {
                                        attr.put("id", values.get(0));
                                    }
                                    if (values.size() > 1) {
                                        Map param = new LinkedHashMap<>();
                                        param.put("id", values.get(1));
                                        attr.put("child", param);
                                    }
                                } else {
                                    attr.put("id", item.getValue());
                                }
                                fields.put(fieldName, attr);
                            }
                        } else if (StringUtils.equalsAny(item.getType(), "richText")) {
                            fields.put(fieldName, item.getValue().toString());
                            if (fieldName.equals("description")) {
                                request.setDescription(item.getValue().toString());
                            }
                        } else if (StringUtils.equalsAny(item.getType(), "date", "datetime")) {
                            try {
                                if (StringUtils.equals(item.getCustomData(), "start_at") && StringUtils.isNotEmpty(item.getValue().toString())) {
                                    fields.put(fieldName, sdfWithOutZone.parse(item.getValue().toString()).getTime() / 1000);
                                }
                                if (StringUtils.equals(item.getCustomData(), "end_at") && StringUtils.isNotEmpty(item.getValue().toString())) {
                                    fields.put(fieldName, sdfWithOutZone.parse(item.getValue().toString()).getTime() / 1000);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else {
                            fields.put(fieldName, item.getValue());
                        }
                    }
                }
            }
        });
    }

    private List<PingCodeGetIssue> filterSyncJiraIssueByCreated(List<PingCodeGetIssue> pingCodeIssues, SyncAllIssuesRequest syncRequest) {
        LogUtil.info("同步请求内容为：" + syncRequest.getCreateTime().longValue() + "  syncRequest:" + syncRequest.isPre());
        List<PingCodeGetIssue> filterIssues = pingCodeIssues.stream().filter(pingCodeIssue -> {
            long createTimeMills = 0;
            createTimeMills = pingCodeIssue.getCreated_at() * 1000;
            if (syncRequest.isPre()) {
                return createTimeMills <= syncRequest.getCreateTime().longValue();
            } else {
                return createTimeMills >= syncRequest.getCreateTime().longValue();
            }
        }).collect(Collectors.toList());
        return filterIssues;
    }
}
