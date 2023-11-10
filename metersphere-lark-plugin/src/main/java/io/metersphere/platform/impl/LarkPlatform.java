package io.metersphere.platform.impl;

import io.metersphere.platform.commons.FieldTypeMapping;
import io.metersphere.platform.domain.*;
import io.metersphere.platform.api.AbstractPlatform;
import io.metersphere.base.domain.*;
import io.metersphere.plugin.exception.MSPluginException;
import io.metersphere.plugin.utils.JSON;
import io.metersphere.plugin.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class LarkPlatform extends AbstractPlatform {

    public LarkAbstractClient larkAbstractClient;

    public LarkPlatform(PlatformRequest request) {

        super.key = LarkPlatformMetaInfo.KEY;
        super.request = request;
        larkAbstractClient = new LarkAbstractClient();
        setConfig();
    }


    @Override
    public void validateUserConfig(String request) {
        LarkConfig config = JSON.parseObject(request, LarkConfig.class);
        larkAbstractClient.PLUGIN_ID = config.getPluginId();
        larkAbstractClient.PLUGIN_SECRET = config.getPluginSecret();
        larkAbstractClient.USER_KEY = config.getUserKey();
        validateIntegrationConfig();
    }

    public LarkConfig setConfig() {
        LarkConfig config = getIntegrationConfig(LarkConfig.class);
        larkAbstractClient.setConfig(config);
        return config;
    }

    protected SimpleDateFormat sdfWithZone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public List<DemandDTO> getDemands(String projectConfig) {
        LarkProjectConfig lpc = JSON.parseObject(projectConfig, LarkProjectConfig.class);
        //查询无迭代需求
//        LarkWorkItemRequest larkWorkItemRequest = new LarkWorkItemRequest(Arrays.asList("story"));
        List<LarkWorkItemInfo> larkWorkItemInfos= new ArrayList<>();
        if(StringUtils.isNotBlank(lpc.getDemandId())) {
            larkWorkItemInfos.addAll(larkAbstractClient.searchWorkItemAll(getLarkSWI(lpc.getDemandId(), "work_item_id"), "story"));
        }
        if(StringUtils.isNotBlank(lpc.getIterationId())) {
            //查询迭代下的需求
            larkWorkItemInfos.addAll(larkAbstractClient.searchWorkItemAll(getLarkSWI(lpc.getIterationId(), larkAbstractClient.ITERATION_FIELD_ID), "story"));

        }
        List<DemandDTO> demandDTOS = new ArrayList<>();
        for (LarkWorkItemInfo item : larkWorkItemInfos) {
           demandDTOS.add(larkWrokItemToDemands(item));
        }
        return demandDTOS;
    }

    private DemandDTO larkWrokItemToDemands(LarkWorkItemInfo larkWorkItemInfo) {
        DemandDTO demandDTO = new DemandDTO();
        demandDTO.setId(larkWorkItemInfo.getMSId());
        demandDTO.setName(larkWorkItemInfo.getName());
        demandDTO.setPlatform("Lark");
        return demandDTO;
    }

    public void refreshUserToken(String userConfig) {
        if (StringUtils.isNotEmpty(userConfig)) {
            LarkUserPlatformUserConfig larkUserPlatformUserConfig = JSON.parseObject(userConfig, LarkUserPlatformUserConfig.class);
            larkAbstractClient.PLUGIN_ID = larkUserPlatformUserConfig.getPluginId();
            larkAbstractClient.PLUGIN_SECRET = larkUserPlatformUserConfig.getPluginSecret();
            larkAbstractClient.USER_KEY = larkUserPlatformUserConfig.getUserKey();
            larkAbstractClient.getToken();
        } else {
            MSPluginException.throwException("");
        }
    }

    @Override
    public IssuesWithBLOBs addIssue(PlatformIssuesUpdateRequest issuesRequest) {
        refreshUserToken(issuesRequest.getUserPlatformUserConfig());
        IssuesWithBLOBs issues = null;
        try {
            issues = larkAbstractClient.addIssue(issuesRequest);
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getMessage());
        }
        return issues;
    }

    @Override
    public List<SelectOption> getProjectOptions(GetOptionRequest request) {
        List<SelectOption> selectOptions = null;
        try {
            List<String> idList = larkAbstractClient.getWorkSpaceIdList();
            selectOptions = larkAbstractClient.getProjectsDetail(idList);
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getMessage());
        }
        return selectOptions;
    }

    @Override
    public IssuesWithBLOBs updateIssue(PlatformIssuesUpdateRequest request) {
        refreshUserToken(request.getUserPlatformUserConfig());
        return larkAbstractClient.updateIssue(request);
    }

    @Override
    public void deleteIssue(String id) {
        larkAbstractClient.deleteIssue(id);
    }

    @Override
    public void validateIntegrationConfig() {
        larkAbstractClient.auth();
        larkAbstractClient.checkUserByUserKey();
        larkAbstractClient.checkSpaceId();
        larkAbstractClient.checkField();
    }

    public List<LarkWorkItemInfo> searchWorkItemAll(LarkSearchWorkItemRequest larkWorkItemRequest, String type) {
        return larkAbstractClient.searchWorkItemAll(larkWorkItemRequest, type);
    }


    @Override
    public void validateProjectConfig(String projectConfig) {
        try {
            LarkProjectConfig larkProjectConfig = JSON.parseObject(projectConfig, LarkProjectConfig.class);
            //婚礼纪以需求去分项目，非标，个性化的
//            List<String> spaceIds = larkAbstractClient.getWorkSpaceIdList();

            String demandId = larkProjectConfig.getDemandId();
            String iterationId=larkProjectConfig.getIterationId();
            List<String> demandList=new ArrayList<>();
            //多个需求
            //验证需求ID
            if(StringUtils.isNotBlank(demandId)) {

                demandList.addAll(Arrays.asList(demandId.split(",")));
            }
            //验证迭代id
            if(StringUtils.isNotBlank(iterationId)){
                String[] iterations = iterationId.split(",");
                demandList.addAll(Arrays.asList(iterations));
            }
            List<String> item_type=new ArrayList<>();
                item_type.add("story");
                item_type.add("sprint");
                LarkWorkItemRequest larkWorkItemRequest = new LarkWorkItemRequest(item_type);
                larkWorkItemRequest.setWork_item_ids(demandList.stream().map(Integer::valueOf).collect(Collectors.toList()));
                List<LarkWorkItemInfo> larkWorkItemInfos = larkAbstractClient.getWorkItemAll(larkWorkItemRequest);
                List<String> strings = new ArrayList<>();
                for (LarkWorkItemInfo item : larkWorkItemInfos) {
                    strings.add(String.valueOf(item.getId()));

                }
            for (int i = 0; i < demandList.size(); i++) {
                if(!strings.contains(demandList.get(i))){
                    MSPluginException.throwException("无效的id："+demandList.get(i));
                }
            }

//                if (!strings.contains(larkProjectConfig.getDemandId())) {
//                    MSPluginException.throwException("无效的需求id");
//                }
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getMessage());
        }



    }

    public List<SelectOption> getIssueTypes(GetOptionRequest request) {
        List<LarkIssueType> larkIssueTypes = null;
        try {
            larkIssueTypes = larkAbstractClient.getIssueTypes(request.getProjectConfig());
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getMessage());
        }
        List<SelectOption> selectOptions = larkIssueTypes.stream()
                .map(item -> new SelectOption(item.getName(), item.getType_key()))
                .collect(Collectors.toList());
        return selectOptions;
    }


    @Override
    public boolean isAttachmentUploadSupport() {
        return true;
    }

    public List<PlatformUser> getPlatformUser() {
        return null;
    }

    private List<IssuesWithBLOBs> getMSAddIssues(List<LarkWorkItemInfo> larkWorkItemInfos, List<PlatformIssuesDTO> platformIssuesDTOS, List<PlatformCustomFieldItemDTO> platformCustomFieldItemDTOHashList) {
        List<IssuesWithBLOBs> issuesWithBLOBsList = new ArrayList<>();
        for (LarkWorkItemInfo item : larkWorkItemInfos) {
            try {
                boolean isAdd = true;
                if (platformIssuesDTOS != null) {
                    for (PlatformIssuesDTO p : platformIssuesDTOS) {
                        if (StringUtils.equals(p.getPlatformId(), item.getMSId())) {
                            isAdd = false;
                            break;
                        }
                    }
                }
                if (isAdd) {
                    IssuesWithBLOBs issues = item.toIssuesWithBLOB(larkAbstractClient.USER_KEY, platformCustomFieldItemDTOHashList);
                    issuesWithBLOBsList.add(issues);
                }
            } catch (Exception e) {
                System.out.println("getMSAddIssues item " + JSON.toJSONString(item));
                e.printStackTrace();
            }
        }
        return issuesWithBLOBsList;
    }

    private List<IssuesWithBLOBs> getMSUpdateIssues(List<LarkWorkItemInfo> larkWorkItemInfos, List<PlatformIssuesDTO> platformIssuesDTOS, List<PlatformCustomFieldItemDTO> platformCustomFieldItemDTOHashList) {
        List<IssuesWithBLOBs> issuesWithBLOBsList = new ArrayList<>();
        for (LarkWorkItemInfo item : larkWorkItemInfos) {
            boolean isUpdate = false;
            if (platformIssuesDTOS != null) {
                for (PlatformIssuesDTO p : platformIssuesDTOS) {
                    if (StringUtils.equals(p.getPlatformId(), item.getMSId())) {
                        if (p.getUpdateTime() != item.getUpdated_at()) {
                            isUpdate = true;
                            break;
                        }
                    }
                }
            }
            if (isUpdate) {
                IssuesWithBLOBs issues = item.toIssuesWithBLOB(larkAbstractClient.USER_KEY, platformCustomFieldItemDTOHashList);
                issuesWithBLOBsList.add(issues);
            }
        }
        return issuesWithBLOBsList;
    }

    private List<String> getMSDeleteIssues(List<LarkWorkItemInfo> larkWorkItemInfos, List<PlatformIssuesDTO> platformIssuesDTOS) {
        List<String> ids = new ArrayList<>();
        for (PlatformIssuesDTO p : platformIssuesDTOS) {
            boolean isDelete = true;
            for (LarkWorkItemInfo item : larkWorkItemInfos) {
                if (StringUtils.equals(item.getProject_key() + "_" + item.getId(), p.getPlatformId())) {
                    isDelete = false;
                    break;
                }
            }
            if (isDelete) {
                ids.add(p.getId());
            }
        }
        return ids;
    }

    public void getMSAddAttachment(Map<String, List<PlatformAttachment>> attachmentMap, List<LarkWorkItemInfo> larkWorkItemInfos, List<IssuesWithBLOBs> issues) {
        for (IssuesWithBLOBs p : issues) {
            for (LarkWorkItemInfo item : larkWorkItemInfos) {
                if (StringUtils.equals(item.getMSId(), p.getPlatformId())) {
                    attachmentMap.put(item.getMSId(), new ArrayList<>());
                    List<LarkFieldValuePairs> larkFieldValuePairsList = item.getFields();
                    for (LarkFieldValuePairs larkFieldValuePairs : larkFieldValuePairsList) {
                        if (StringUtils.equals(larkFieldValuePairs.getField_type_key(), "multi_file")) {
                            if (larkFieldValuePairs.getField_value() == null) {
                                break;
                            }
                            String arrayStr = JSON.toJSONString(larkFieldValuePairs.getField_value());
                            List list = JSON.parseArray(arrayStr);
                            for (int i = 0; i < list.size(); i++) {
                                String valueStr = JSON.toJSONString(list.get(i));
                                Map<String, String> map = JSON.parseMap(valueStr);
                                if (map != null) {
                                    PlatformAttachment syncAttachment = new PlatformAttachment();
                                    // name 用于查重
                                    syncAttachment.setFileName(map.get("name"));
                                    // key 用于获取附件内容
                                    syncAttachment.setFileKey(map.get("url"));
                                    attachmentMap.get(item.getMSId()).add(syncAttachment);
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private Map<String, List<PlatformAttachment>> getMSAddAttachment(List<LarkWorkItemInfo> larkWorkItemInfos, SyncIssuesResult platformIssuesDTOS) {
        Map<String, List<PlatformAttachment>> attachmentMap = new HashMap<>();
        //更新待添加的缺陷附件
        getMSAddAttachment(attachmentMap, larkWorkItemInfos, platformIssuesDTOS.getAddIssues());
        //更新待更新的缺陷附件
        getMSAddAttachment(attachmentMap, larkWorkItemInfos, platformIssuesDTOS.getUpdateIssues());
        return attachmentMap;
    }


    @Override
    public SyncIssuesResult syncIssues(SyncIssuesRequest request) {
        MSPluginException.throwException("同步失败");
        //找出ms所需要的飞书字段模版
        List<PlatformCustomFieldItemDTO> platformCustomFieldItemDTOS = null;
        platformCustomFieldItemDTOS = getThirdPartCustomField(request.getProjectConfig());
        //获取全部飞书issues
        LarkWorkItemRequest workItemRequest = new LarkWorkItemRequest(Arrays.asList("issue"));
        List<LarkWorkItemInfo> larkWorkItemInfos = larkAbstractClient.getWorkItemAll(workItemRequest);
        //婚礼纪个性化，用需求筛选项目
//        checkIssueByDemandId(larkWorkItemInfos, larkProjectConfig.getDemandId());
        //获取全部MSissues
        List<PlatformIssuesDTO> issues = request.getIssues();
        SyncIssuesResult syncIssuesResult = new SyncIssuesResult();
        //找出ms需要添加的缺陷
        syncIssuesResult.setAddIssues(getMSAddIssues(larkWorkItemInfos, issues, platformCustomFieldItemDTOS));
        //找出ms需要更新的缺陷
        syncIssuesResult.setUpdateIssues(getMSUpdateIssues(larkWorkItemInfos, issues, platformCustomFieldItemDTOS));
        //找出ms需要删除的缺陷
        syncIssuesResult.setDeleteIssuesIds(getMSDeleteIssues(larkWorkItemInfos, issues));
        //找出ms需要添加的附件 只获取添加或更新的附件
//        syncIssuesResult.setAttachmentMap(getMSAddAttachment(larkWorkItemInfos, syncIssuesResult));
        return syncIssuesResult;
    }

    public void checkIssueByDemandId(List<LarkWorkItemInfo> larkWorkItemInfos, String demandId) {
        List<LarkWorkItemInfo> temp = new ArrayList<>();
        for (LarkWorkItemInfo item : larkWorkItemInfos) {
            try {
                List<LarkFieldValuePairs> larkFieldValuePairs = item.getFields();
                if (larkFieldValuePairs == null) {
                    temp.add(item);
                    continue;
                }
                for (LarkFieldValuePairs lfvp : larkFieldValuePairs) {
                    try {
                        if (StringUtils.equals(lfvp.getField_type_key(), "work_item_related_select")) {
                            if (!StringUtils.equals(lfvp.getField_value() + "", demandId)) {
                                temp.add(item);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("error item " + JSON.toJSONString(item) + " error lfvp " + JSON.toJSONString(lfvp));
                        temp.add(item);
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                temp.add(item);
                System.out.println("error json " + JSON.toJSONString(item));
                e.printStackTrace();
            }

        }
        for (LarkWorkItemInfo item : temp) {
            larkWorkItemInfos.remove(item);
        }
    }

    @Override
    protected String getCustomFieldsValuesString(List<PlatformCustomFieldItemDTO> thirdPartCustomField) {
        List fields = new ArrayList();
        thirdPartCustomField.forEach((item) -> {
            Map<String, Object> field = new LinkedHashMap();
            field.put("customData", item.getCustomData());
            field.put("id", item.getId());
            field.put("name", item.getName());
            field.put("type", item.getType());
            String defaultValue = item.getDefaultValue();
            if (StringUtils.isNotBlank(defaultValue)) {
                try {
                    field.put("value", JSON.parseObject(defaultValue));
                } catch (Exception e) {
                    field.put("value", defaultValue);
                }
            }
            fields.add(field);
        });
        return JSON.toJSONString(fields);
    }

    @Override
    public List<PlatformCustomFieldItemDTO> getThirdPartCustomField(String projectConfig) {
//        MSPluginException.throwException("apacheaaa");
        return getThirdPartCustomFieldIO(projectConfig);
//        LarkProjectConfig lpc = JSON.parseObject(projectConfig, LarkProjectConfig.class);
//        // json 可能等于三个值，null TIMEOUT object
//        String json = RedisSingleton.getInstance().getValue("getThirdPartCustomField", larkAbstractClient.PLUGIN_ID);
//        if(json == null){
//            List<PlatformCustomFieldItemDTO> temp = getThirdPartCustomFieldIO(projectConfig);
//            RedisSingleton.getInstance().setValue("getThirdPartCustomField", temp, larkAbstractClient.PLUGIN_ID);
//            return temp;
//        }
//
//        if(json.equals(RedisSingleton.TIMEOUT)){
//            ThreadPool tp = new ThreadPool(this, projectConfig);
//            tp.run();
//            LarkRedisPCFID larkRedisPCFID = RedisSingleton.getInstance().getLarkRedisPCFIDValue("getThirdPartCustomField", larkAbstractClient.PLUGIN_ID);
//            return larkRedisPCFID.getPlatformCustomFieldItemDTOList();
//        }
//
//        return JSON.parseArray(json, PlatformCustomFieldItemDTO.class);
    }

    public List<PlatformCustomFieldItemDTO> getThirdPartCustomFieldIO(String projectConfig) {
        List<LarkFieldConf> larkSimpleFields = null;
        Map<String, LarkSimpleField> larkSimpleFieldMap = null;
        List<LarkUserInfo> larkUserInfos = null;
        List<DemandDTO> demandDTOS = new ArrayList<>();
        try {
            larkSimpleFields = larkAbstractClient.getThirdPartCustomField();
            larkSimpleFieldMap = larkAbstractClient.getSpaceField();
            larkUserInfos = larkAbstractClient.getTameUserInfoList();
            LarkProjectConfig lpc = JSON.parseObject(projectConfig, LarkProjectConfig.class);
            LarkWorkItemRequest larkWorkItemRequest = new LarkWorkItemRequest(Arrays.asList("story"));
            //获取所有需求
            demandDTOS = getDemands(projectConfig);

        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getMessage());
        }
        List<PlatformCustomFieldItemDTO> platformCustomFieldItemDTOS = new ArrayList<>();
        List<MSOption> userList = new ArrayList<>();
        for (LarkUserInfo item : larkUserInfos) {
            MSOption msOption = new MSOption();
            msOption.setText(item.getName_en());
            msOption.setValue(item.getUser_key());
            userList.add(msOption);
        }
        for (LarkFieldConf item : larkSimpleFields) {
            //ms自带多个附件，跳过此字段
            if ("multi_file".equals(item.getField_type_key()) || "file".equals(item.getField_type_key())) {
                continue;
            }
            PlatformCustomFieldItemDTO temp = new PlatformCustomFieldItemDTO();
            settingValue(temp, item, larkSimpleFieldMap, userList, demandDTOS);
            platformCustomFieldItemDTOS.add(temp);
        }
        return platformCustomFieldItemDTOS;
    }

    public void settingValue(PlatformCustomFieldItemDTO temp, LarkFieldConf item, Map<String, LarkSimpleField> larkSimpleFieldMap, List<MSOption> userList, List<DemandDTO> demandDTOS) {
        //必填
        if (item.getIs_required() == 1) temp.setRequired(true);
        //默认值
        if (item.getDefault_value().getDefault_appear() == 1) temp.setDefaultValue(item.getDefault_value().getValue());
        //名称
        temp.setName(item.getField_name());
        //标示
        temp.setCustomData(item.getField_key());
        //类型
        temp.setType(FieldTypeMapping.getMsTypeBylarkType(item.getField_type_key()));
        //值
        LarkSimpleField larkSimpleField = larkSimpleFieldMap.get(item.getField_key());
        //id
        temp.setId(item.getField_key());

        if (StringUtils.equals(item.getField_type_key(), "bool")) {
            List<MSOption> msOptionList = new ArrayList<>();
            MSOption mTrue = new MSOption();
            MSOption mFalse = new MSOption();
            mTrue.setText("是");
            mTrue.setValue("true");
            msOptionList.add(mTrue);
            mFalse.setText("否");
            mFalse.setValue("false");
            msOptionList.add(mFalse);
            temp.setOptions(JSON.toJSONString(msOptionList));
        } else if (StringUtils.equals(item.getField_type_key(), "business")) {
            LarkSimpleField larkSimpleField1 = larkSimpleFieldMap.get("business");
            List<MSOption> msOptionList = new ArrayList<>();
            for (LarkOption ite : larkSimpleField1.getOptions()) {
                if (ite == null || ite.getChildren() == null) {
                    continue;
                }
                for (LarkOption it : ite.getChildren()) {
                    MSOption msOption = new MSOption();
                    msOption.setText(ite.getLabel() + " / " + it.getLabel());
                    msOption.setValue(it.getValue());
                    msOptionList.add(msOption);
                }
            }
            temp.setOptions(JSON.toJSONString(msOptionList));
        } else if (StringUtils.equals(item.getField_type_key(), "tree_select")) {
            List<MSOption> msOptionList = new ArrayList<>();
            for (LarkOptionConf ite : item.getOptions()) {
                for (LarkOptionConf it : ite.getChildren()) {
                    MSOption msOption = new MSOption();
                    msOption.setText(ite.getLabel() + " / " + it.getLabel());
                    msOption.setValue(ite.getValue() + "&" + it.getValue());
                    msOptionList.add(msOption);
                }
            }
            temp.setOptions(JSON.toJSONString(msOptionList));
        } else if (StringUtils.equals(item.getField_type_key(), "multi_user") ||
                StringUtils.equals("user", item.getField_type_key())) {
            temp.setOptions(JSON.toJSONString(userList));
        } else if (StringUtils.equals(item.getField_type_key(), "work_item_related_select")) {
            List<MSOption> msOptionList = new ArrayList<>();
            for (DemandDTO ite : demandDTOS) {
                MSOption mo = new MSOption();
                mo.setText(ite.getName());
                mo.setValue(ite.getId());
                msOptionList.add(mo);
            }
            temp.setOptions(JSON.toJSONString(msOptionList));
        } else {
            if (item.getOptions() != null && item.getOptions().size() != 0) {
                temp.setOptions(item.getMsLarkOption());
            } else {
                temp.setOptions(larkSimpleField.getMsLarkOption());
            }
        }

    }

    @Override
    public ResponseEntity proxyForGet(String url, Class responseEntityClazz) {
        return larkAbstractClient.proxyForGet(url, responseEntityClazz);
    }

    @Override
    public void syncIssuesAttachment(SyncIssuesAttachmentRequest request) {
        larkAbstractClient.syncIssuesAttachment(request);
    }

    @Override
    public List<PlatformStatusDTO> getStatusList(String issueKey) {
        // 飞书好像没有用到
//        List<PlatformStatusDTO> l = new ArrayList<>();
//        PlatformStatusDTO p = new PlatformStatusDTO();
//        p.setValue("待办");
//        p.setLabel("待办");
//        l.add(p);
        return null;
    }

    @Override
    public void syncAllIssues(SyncAllIssuesRequest syncRequest) {
        List<Map> zentaoIssues = new ArrayList<>();
        LarkProjectConfig larkProjectConfig = JSON.parseObject(syncRequest.getProjectConfig(), LarkProjectConfig.class);
        List<PlatformCustomFieldItemDTO> platformCustomFieldItemDTOS = getThirdPartCustomField(syncRequest.getProjectConfig());
//        ThreadPool tp = new ThreadPool(this, syncRequest.getProjectConfig());
//        tp.run();
        //获取全部飞书issues
//        LarkWorkItemRequest workItemRequest = new LarkWorkItemRequest(Arrays.asList("issue"));

        System.out.println("start get item project all issus");
//        List<LarkWorkItemInfo> larkWorkItemInfos = larkAbstractClient.getWorkItemAll(workItemRequest);
        //获取无迭代需求关联缺陷
        List<LarkWorkItemInfo> larkWorkItemInfos=new ArrayList<>();
        if(StringUtils.isNotBlank(larkProjectConfig.getDemandId())) {
            larkWorkItemInfos.addAll(larkAbstractClient.searchWorkItemAll(getLarkSWI(larkProjectConfig.getDemandId(), "_field_linked_story"), "issue"));
//        checkIssueByDemandId(larkWorkItemInfos, larkProjectConfig.getDemandId());
        }

        if(StringUtils.isNotBlank(larkProjectConfig.getIterationId())) {
            //获取迭代关联需求下的缺陷
            //获取关联迭代的需求
            List<LarkWorkItemInfo> demandInfos=new ArrayList<>();
            demandInfos.addAll(larkAbstractClient.searchWorkItemAll(getLarkSWI(larkProjectConfig.getIterationId(), larkAbstractClient.ITERATION_FIELD_ID), "story"));
            StringBuilder demandStb=new StringBuilder();
            for (int i = 0; i < demandInfos.size(); i++) {
                demandStb=demandStb.append(demandInfos.get(i).getId()).append(",");
                           }

            larkWorkItemInfos.addAll(larkAbstractClient.searchWorkItemAll(getLarkSWI((demandStb.substring(0,demandStb.length()-1).toString()), "_field_linked_story"), "issue"));
        }
        System.out.println("get work Item size:" + larkWorkItemInfos.size());
        SyncAllIssuesResult syncIssuesResult = new SyncAllIssuesResult();
        //找出ms需要添加的缺陷
        List<IssuesWithBLOBs> issuesWithBLOBsList = getMSAddIssues(larkWorkItemInfos, null, platformCustomFieldItemDTOS);
        syncIssuesResult.setAddIssues(issuesWithBLOBsList);
        System.out.println("get work Item size:" + syncIssuesResult.getAddIssues().size());
        this.defaultCustomFields = syncRequest.getDefaultCustomFields();
        for (IssuesWithBLOBs item : syncIssuesResult.getAddIssues()) {
            zentaoIssues.add(JSON.parseMap(JSON.toJSONString(item)));
        }
        try {
            List<String> allIds = zentaoIssues.stream().map(i -> i.get("id").toString()).collect(Collectors.toList());
            syncIssuesResult.setAllIds(allIds);
            if (syncRequest != null) {
                zentaoIssues = filterSyncZentaoIssuesByCreated(zentaoIssues, syncRequest);
            }
            syncIssuesResult.setUpdateIssues(getMSUpdateIssues(larkWorkItemInfos, null, platformCustomFieldItemDTOS));
            System.out.println("get work Item size:" + syncIssuesResult.getUpdateIssues().size());
            syncIssuesResult.getUpdateIssues().addAll(syncIssuesResult.getAddIssues());
            HashMap<Object, Object> syncParam = buildSyncAllParam(syncIssuesResult);
            syncRequest.getHandleSyncFunc().accept(syncParam);
            System.out.println("syncAllIssues dome add Issues size:" + syncIssuesResult.getAddIssues().size()
                    + " update Issues size:" + syncIssuesResult.getUpdateIssues().size()
                    + " all ids size:" + syncIssuesResult.getAllIds().size()
                    + " del issues size:" + syncIssuesResult.getDeleteIssuesIds().size());
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e);
        }
    }

    public LarkSearchWorkItemRequest getLarkSWI(String dId, String paramKey) {
        LarkSearchWorkItemRequest larkWorkItemRequest = new LarkSearchWorkItemRequest();
        larkWorkItemRequest.setPage_num(1l);
        LarkSearchGroup larkSearchGroup = new LarkSearchGroup();
        larkSearchGroup.setConjunction("AND");
        LarkSearchParam larkSearchParam = new LarkSearchParam();
        larkSearchParam.setOperator("HAS ANY OF");
        //
        larkSearchParam.setParam_key(paramKey);
        //多个需求以逗号分割
        String[] demandIds = dId.split(",");

        larkSearchParam.setValue(Arrays.stream(demandIds).mapToInt(Integer::valueOf).toArray());

        larkSearchGroup.getSearch_params().add(larkSearchParam);
        larkWorkItemRequest.setSearch_group(larkSearchGroup);
        return larkWorkItemRequest;
    }

    public List<Map> filterSyncZentaoIssuesByCreated(List<Map> zentaoIssues, SyncAllIssuesRequest syncRequest) {
        List<Map> filterIssues = zentaoIssues.stream().filter(item -> {
            if (syncRequest.getCreateTime() == null) {
                return true;
            } else {
                long createTimeMills = 0;
                try {
                    createTimeMills = Long.parseLong(item.get("createTime") + "");
                    if (syncRequest.isPre()) {
                        return createTimeMills <= syncRequest.getCreateTime().longValue();
                    } else {
                        return createTimeMills >= syncRequest.getCreateTime().longValue();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }).collect(Collectors.toList());
        return filterIssues;
    }
}
