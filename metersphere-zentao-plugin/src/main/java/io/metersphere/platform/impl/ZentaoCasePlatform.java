package io.metersphere.platform.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.metersphere.platform.commons.PriLevelMapping;
import io.metersphere.platform.domain.*;
import io.metersphere.platform.utils.JSONUtils;
import io.metersphere.plugin.utils.JSON;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;

import java.util.*;

public class ZentaoCasePlatform{
    public static ZentaoPlatform zentaoPlatform;

    public ZentaoCasePlatform(String account, String paassword, String requestType, String url, String msURL){
        PlatformRequest platformRequest = new PlatformRequest();
        ZentaoConfig zentaoConfig = new ZentaoConfig();
        zentaoConfig.setAccount(account);
        zentaoConfig.setPassword(paassword);
        zentaoConfig.setRequest(requestType);
        zentaoConfig.setUrl(url);
        String zentaoConfigStr = JSON.toJSONString(zentaoConfig);
        platformRequest.setIntegrationConfig(zentaoConfigStr);
        zentaoPlatform = new ZentaoPlatform(platformRequest);
    }


//    public void pushAllZentaoCase(String rootID, String msModules, String msCases, String msCasesFile){
//        String moduleMap = getModuleListByType("case");
//        //获取需要推送禅道的模块
//        String addModules = getZentaoCaseModulesByMSCaseModules(moduleMap, msModules);
//        //推送禅道模块
//        String addModuleIDs = puthModules(rootID,addModules);
//        //获取禅道案例信息
//        String zentaoCaseListStr = getTestCaseStr(rootID);
//        //获取需要推送禅道的模块
//        String addCase = getZentaoCaseByMSCase(zentaoCaseListStr, msCases, moduleMap);
//        //推送禅道案例
//        String response = pushTestCaseStr(addCase,rootID, msCasesFile);
//    }

//    public String getModuleListByType(String type) {
//        Object dataMapStr = zentaoPlatform.zentaoClient.getModuleListByType(type).get("data");
//        Map<String, String> object = new HashMap<>();
//        if(!StringUtils.equals("[\"\\/\"]", dataMapStr+"")){
//            object = JSON.parseMap(dataMapStr+"");
//        }else{
//            object.put("0", dataMapStr+"");
//        }
//        return JSON.toJSONString(object);
//    }

    public void putChildren(JSONObject item, JSONArray children, String pName){
        for(int i = 0 ; i < children.size(); i++){
            JSONObject itemTemp = children.getJSONObject(i);
            String name = StringEscapeUtils.unescapeXml(itemTemp.getString("name"));
            item.put(itemTemp.getString("id"), pName+"/"+name);
            JSONArray itemJSONArray = itemTemp.getJSONArray("children");
            if(itemJSONArray != null){
                putChildren(item, itemJSONArray, pName+"/"+name);
            }
        }
    }

//    public static void main(String[] args) {
//        String a = "\u767b\u5f55\";
//        System.out.println(StringEscapeUtils.unescapeXml(a));
//    }

    public String getTreeMap(String productID){
        Map<String, String> map = zentaoPlatform.zentaoClient.getProjectTree(productID);
        return JSON.toJSONString(map);
    }

    public String getTree(String productID){
        Map<String, String> map = zentaoPlatform.zentaoClient.getProjectTree(productID);
        String data = map.get("data");
        JSONObject temp = new JSONObject();
        if(data != null && !data.equals("")){
            JSONObject jsonObject = JSONObject.parseObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("tree");
            if(jsonArray != null && jsonArray.size() != 0){
                for(int i = 0 ; i < jsonArray.size(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    String name = StringEscapeUtils.unescapeXml(item.getString("name"));
                    temp.put(item.getString("id"), "/"+name);
                    JSONArray itemJSONArray = item.getJSONArray("children");
                    if(itemJSONArray != null){
                        putChildren(temp, itemJSONArray, "/"+name);
                    }
                }
            }
        }
        return temp.toString();
//        return JSON.toJSONString(a);
    }

    public Object createCaseModule(String projectId, String type, String modules, Object parentModuleID){
        Object dataMapStr = zentaoPlatform.zentaoClient.createCaseModule(projectId, type, modules, parentModuleID).get("data");
//        return JSON.parseArray(dataMapStr+"", String.class);
        return dataMapStr;
    }


    public List<GetTestCaseResponse> getTestCase(String productID, Integer projectID){
        Object dataMapStr = zentaoPlatform.zentaoClient.getTestCase(productID, projectID).get("data");
        if(StringUtils.equals(dataMapStr+"", "[]")){
            return new ArrayList<>();
        }
//        JSONObject dataMap = JSONObject.parseObject(dataMapStr+"");
        Map<String, Object> dataMap = JSON.parseMap(dataMapStr+"");
//        JSONObject.parseObject(dataMapStr+"");
        List<Object> dataObjList = new ArrayList<>();
        for(Object obj : dataMap.values()){
            dataObjList.add(obj);
        }
        List<GetTestCaseResponse> getTestCaseResponseListTemp = JSON.parseArray(JSON.toJSONString(dataObjList), GetTestCaseResponse.class);
        List<GetTestCaseResponse> getTestCaseResponseList = new ArrayList<>();
        for(GetTestCaseResponse item: getTestCaseResponseListTemp){
            if (projectID.intValue() != Integer.parseInt(item.getProject())) continue;
            getTestCaseResponseList.add(item);
        }
        return getTestCaseResponseList;
    }

    public String getTestCaseStr(String productID, Integer projectID){
        List<GetTestCaseResponse> getTestCaseResponseList = new ArrayList<>();
        try {
            getTestCaseResponseList = getTestCase(productID, projectID);
        }catch (Exception e) {
            return e.getMessage();
        }
        return JSON.toJSONString(getTestCaseResponseList);
    }

    public String pushTestCaseStr(String addCases, Integer projectID, Integer executionID, String productID, String fileStr){
        try{
            if(addCases == null || StringUtils.equals("[]",addCases) || StringUtils.isBlank(addCases)){
                return "同步完成";
            }
            List<MSCase> addMSCases = JSON.parseArray(addCases, MSCase.class);
            JSONObject jsonObject = new JSONObject();
            if(fileStr != null && !StringUtils.equals("void", fileStr) && !StringUtils.equals("", fileStr)){
                jsonObject = JSONUtils.getFileJSONObject(fileStr);
            }
            buildMSCase(addMSCases, jsonObject, null, null, null, null);
            Object list = pushTestCase(addMSCases, executionID, productID, projectID);
            return JSONObject.toJSONString(list);
        }catch (Exception e){
            return e.getMessage();
        }
    }

    public String updateZentaoCaseByMSCase(String MSCases, String zentaoCases, String fileStr, String casePOSTData, String casePREData, String caseCommentData, String product){
        List<MSCase> msCases = JSON.parseArray(MSCases, MSCase.class);
        JSONObject fileStrJsonObject = new JSONObject();
        if(fileStr != null && !StringUtils.equals("void", fileStr) && !StringUtils.equals("", fileStr)){
            fileStrJsonObject = JSONUtils.getFileJSONObject(fileStr);
        }

        JSONObject casePOSTDataJsonObject = new JSONObject();
        if(casePOSTData != null && !StringUtils.equals("void", casePOSTData) && !StringUtils.equals("", casePOSTData)){
            casePOSTDataJsonObject = JSONUtils.getFileJSONObject(casePOSTData);
        }

        JSONObject casePREDataJsonObject = new JSONObject();
        if(casePREData != null && !StringUtils.equals("void", casePREData) && !StringUtils.equals("", casePREData)){
            casePREDataJsonObject = JSONUtils.getFileJSONObject(casePREData);
        }

        JSONObject caseCommentDataJsonObject = new JSONObject();
        if(caseCommentData != null && !StringUtils.equals("void", caseCommentData) && !StringUtils.equals("", caseCommentData)){
            caseCommentDataJsonObject = JSONUtils.getFileJSONObject(caseCommentData);
        }
        List<GetTestCaseResponse> getTestCaseResponseList = JSON.parseArray(zentaoCases, GetTestCaseResponse.class);
        Map<String,String> moduleMap = JSON.parseMap(getTree(product));
//        Map<String,String> moduleMap = JSON.parseMap(getModuleListByType("case"));
        Map<String, GetTestCaseResponse> zenTaoMap = addZentaoModule(getTestCaseResponseList, moduleMap);
        buildMSCase(msCases, fileStrJsonObject, casePOSTDataJsonObject, casePREDataJsonObject, caseCommentDataJsonObject, zenTaoMap);
        updateTestCase(msCases, product);
        return "同步完成";
    }

    private void buildMSCase(List<MSCase> msCases, JSONObject fileStrJsonObject, JSONObject casePOSTDataJsonObject, JSONObject casePREDataJsonObject, JSONObject caseCommentDataJsonObject, Map<String, GetTestCaseResponse> zenTaoMap){
        for(MSCase item: msCases){
//            if(fileStrJsonObject != null) item.setFilesByJSONObject(fileStrJsonObject.getJSONObject(item.getId()));
//            if(casePOSTDataJsonObject != null) item.setCasePostByJSONObject(casePOSTDataJsonObject.getJSONObject(item.getId()));
//            if(casePREDataJsonObject != null) item.setCasePreByJSONObject(casePREDataJsonObject.getJSONObject(item.getId()));
//            if(caseCommentDataJsonObject != null) item.setCaseCommentsByJSONObject(caseCommentDataJsonObject.getJSONObject(item.getId()));
            if(zenTaoMap != null){
                GetTestCaseResponse zentaoCase = zenTaoMap.get(item.getNodePath()+"/"+item.getName());
                if(zentaoCase != null) {
                    item.setZentaoId(zentaoCase.getId());
                    item.setZentaoModuleId(zentaoCase.getModule());
                }
            }
        }
    }

    private Object updateTestCase(List<MSCase> msCases, Object product){
        Map<String, MSCase> msCaseMap = new HashMap<>();
        for(MSCase item: msCases){
            msCaseMap.put(item.getId(), item);
        }
        List<Object> responseList = new ArrayList<>();
        for(MSCase item: msCases){
            List<FromData> fromData = converUpdateZentaoCaseByMSCase(item, product, Integer.parseInt(item.getZentaoModuleId()), msCaseMap);
            String response = null;
            try{
                response = zentaoPlatform.zentaoClient.updateTestcase(item, fromData);
            }catch (Exception e){
                responseList.add(e.getMessage()+JSON.toJSONString(item));
            }
            responseList.add(response);
        }
        return JSON.toJSONString(responseList);
    }

    public Map<String, GetTestCaseResponse> addZentaoModule(List<GetTestCaseResponse> getTestCaseResponseList, Map<String,String> moduleMap){
        Map<String, GetTestCaseResponse> map = new HashMap<>();
        for(GetTestCaseResponse item: getTestCaseResponseList){
            String path = moduleMap.get(item.getModule());
            map.put(path+"/"+item.getTitle(), item);
        }
        return map;
    }

    public String getTestCaseView(String caseId){
        return zentaoPlatform.zentaoClient.getTestCaseView(caseId);
    }

    public List<String> pushTestCase(List<MSCase> addMSCases, Integer executionID, Object productID, Integer projectID){
//        Object dataMapStr = zentaoPlatform.zentaoClient.getModuleListByType("case").get("data");
        Object dataMapStr = getTree(productID+"");
        Map<String, String> dataMap = JSON.parseMap(dataMapStr+"");
        List<String> temp = new ArrayList<>();
        for(MSCase item : addMSCases){
            Integer pid = getModulePid(item, dataMap);
            List<FromData> list = converAddZentaoCaseByMSCase(item, productID, pid, projectID);
            String response = null;
            try{
                response = zentaoPlatform.zentaoClient.createTestcase(executionID, productID,projectID, list, pid);
            }catch (Exception e){
                response = e.getMessage()+JSON.toJSONString(item);
            }
            temp.add(response);
        }
        return temp;
    }

    public Integer getModulePid(MSCase item, Map<String, String> map){
        Integer pid = null;
        for(String key : map.keySet()){
            String name = map.get(key);
            if(StringUtils.equals(name, item.getNodePath())){
                pid = Integer.parseInt(key);
                break;
            }
        }
        return pid;
    }

    public MSCase converMSCaseCaseByZentao(){
        return null;
    }

    public List<FromData> converUpdateZentaoCaseByMSCase(MSCase msCase,Object product, Object module, Map<String, MSCase> msCaseMap){
        List<FromData> temp = converAddZentaoCaseByMSCase(msCase, product, module, null);
        String comment = msCase.getRemark();
        if(msCase.getCaseComments() != null){
            for(MSCase.CaseComment item: msCase.getCaseComments()){
                comment += "<p>评论</p>" + item.getDescription();
            }
        }
        if(StringUtils.isNotEmpty(comment)){
            temp.add(new FromData("comment", comment));
        }
        if(msCase.getCasePost() != null){
            for(MSCase.CaseCorrelation item: msCase.getCasePost()){
                MSCase tempMSCase = msCaseMap.get(item.getSourceId());
                if(tempMSCase.getZentaoId() != null) {
                    temp.add(new FromData("linkCase[]", tempMSCase.getZentaoId()));
                }
            }
        }
        if(msCase.getCasePre() != null){
            for(MSCase.CaseCorrelation item: msCase.getCasePre()){
                MSCase tempMSCase = msCaseMap.get(item.getSourceId());
                if(tempMSCase.getZentaoId() != null){
                    temp.add(new FromData("linkCase[]", tempMSCase.getZentaoId()));
                }
            }
        }
        return temp;
    }

    public List<FromData> converAddZentaoCaseByMSCase(MSCase msCase, Object product, Object module, Integer project){
        List<FromData> list = new ArrayList<>();
        if(project != null)        list.add(new FromData("project", project));
        list.add(new FromData("product",product));
        list.add(new FromData("module",module));
        list.add(new FromData("title",msCase.getName()));
        list.add(new FromData("type","feature"));
        list.add(new FromData("keywords",msCase.getTags()));
        list.add(new FromData("pri", PriLevelMapping.getPriByLevel(msCase.getPriority())));
        list.add(new FromData("status","normal"));
        list.add(new FromData("precondition",msCase.getPrerequisite()));

        if(StringUtils.equals("stepModel",msCase.getStepModel())){
            String arrayStr = "[1]";
            list.add(new FromData("steps"+arrayStr, msCase.getStepDescription()));
            list.add(new FromData("stepType"+arrayStr, "item"));
            list.add(new FromData("expects"+arrayStr, msCase.getExpectedResult()));
        } else {
            if(msCase.getSteps() != null){
                List<MSCaseStep> steps = JSON.parseArray(msCase.getSteps(), MSCaseStep.class);
                for(MSCaseStep item: steps){
                    String arrayStr = "["+item.getNum()+"]";
                    list.add(new FromData("steps"+arrayStr, item.getDesc()));
                    list.add(new FromData("stepType"+arrayStr, "item"));
                    list.add(new FromData("expects"+arrayStr, item.getResult()));
                }
            }
        }
        if(msCase.getFiles() != null && msCase.getFields().size() != 0){
            for(MSCase.File file: msCase.getFiles()){
                try {
//                    FileSystemResource fileResource = new FileSystemResource(file.getFilePath()+"/"+file.getName());
//                    list.add(new FromData("labels[]", file.getName()));
//                    list.add(new FromData("files[]",fileResource));
                    FileSystemResource fileResource = new FileSystemResource("/Users/chenjiguang/ideaMeterspherePr2/metersphere-platform-plugin/Jenkinsfile");
                    list.add(new FromData("labels[]", "Jenkinsfile"));
                    list.add(new FromData("files[]", fileResource));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
    public String getZentaoCaseByMSCase(String zentaoCaseList, String msCaseList, String msModules){
        List<GetTestCaseResponse> getTestCaseResponseList = JSON.parseArray(zentaoCaseList, GetTestCaseResponse.class);
        List<MSCase> msCases = JSON.parseArray(msCaseList, MSCase.class);
        Map<String,String> map = JSON.parseMap(msModules);
//        JSONObject map = JSONObject.parseObject(msModules+"");
        List<MSCase> addMSCases = new ArrayList<>();
        for(MSCase item: msCases){
            boolean is = false;
            for(GetTestCaseResponse zentaoCase: getTestCaseResponseList){
                String msPathName = item.getNodePath()+"/"+item.getName();
                String zentaoPathName = map.get(zentaoCase.getModule())+"/"+zentaoCase.getTitle();
                if(StringUtils.equals(msPathName, zentaoPathName)){
                    is = true;
                    break;
                }
            }
            if(!is){
                addMSCases.add(item);
            }
        }
        return JSON.toJSONString(addMSCases);
    }

    public String puthModules(String productID, String modules){
        List<String> zentaoCaseIds = new ArrayList<>();
        try {
            if (modules == null || StringUtils.equals(modules, "") || StringUtils.equals(modules, "[]"))
                return "没有要推送的模块";
            List<MSModule> addName = JSON.parseArray(modules, MSModule.class);
            List<MSModule> deleteTemp = new ArrayList<>();
            //先推送有父id的模块
            for (MSModule item : addName) {
                if (item.getParentId() != null) {
                    deleteTemp.add(item);
                    //推送
                    Object responseIds = createCaseModule(productID, "case", item.getName(), item.getParentId());
                    zentaoCaseIds.add(responseIds+"");
                }
            }
            for (MSModule item : deleteTemp) {
                addName.remove(item);
            }
            //循环8次，ms模块等级最多8,level是1的父id默认为0
            for (int i = 2; i < 8; i++) {
                List<MSModule> temp = getMSModulesByLevel(i, addName);
                if (temp == null || temp.size() == 0) continue;
//                String moduleMap = getModuleListByType("case");
                String moduleMap = getTree(productID);
                Map<String, String> map = JSON.parseMap(moduleMap);
//            Map<String,String> map = JSONObject.parseObject(moduleMap, Map.class);
                List<MSModule> moduleTempList = getModuleItemName(temp, map);
                for (MSModule item : moduleTempList) {
                    if (item.getParentId() != null) {
                        //推送
                        Object responseIds = createCaseModule(productID, "case", item.getName(), item.getParentId());
                        zentaoCaseIds.add(responseIds+"");
                    }
                }
            }
        }catch (Exception e){

            return e.getMessage();
        }
        return JSON.toJSONString(zentaoCaseIds);
    }

    private List<MSModule> getMSModulesByLevel(int level, List<MSModule> addName){
        List<MSModule> msModules = new ArrayList<>();
        for(MSModule item : addName){
            if(item.getLevel() == level){
                msModules.add(item);
            }
        }
        return msModules;
    }

    public String getZentaoCaseModulesByMSCaseModules(String zentaoModules ,String msModules){
        Map<String,String> map = JSON.parseMap(zentaoModules);
//        Map<String,String> map = JSONObject.parseObject(zentaoModules, Map.class);
        List<MSModule> msModuleList = JSON.parseArray(msModules, MSModule.class);
        List<MSModule> moduleTempList = getModuleName(msModuleList,"", map);
        List<MSModule> addName = new ArrayList<>();
        for(MSModule m:moduleTempList){
            boolean is = false;
            for(String key : map.keySet()){
                String name = map.get(key);
                if(StringUtils.equals(m.getLabel(), name)){
                    is = true;
                    break;
                }
            }
            if(!is){
                addName.add(m);
            }
        }
        return JSON.toJSONString(addName);
    }

    private List<MSModule> getModuleItemName(List<MSModule> msModules, Map<String,String> map){
        List<MSModule> moduleTempList = new ArrayList<>();
        for(MSModule item: msModules){
            MSModule msModule = new MSModule();
            msModule.setName(item.getName());
            msModule.setLevel(item.getLevel());
            msModule.setParentId(getZentaoParentID(item.getPath(), map));
            moduleTempList.add(msModule);
            if(item.getChildren() != null && item.getChildren().size() != 0){
                moduleTempList.addAll(getModuleItemName(item.getChildren(), map));
            }
        }
        return moduleTempList;
    }

    private List<MSModule> getModuleName(List<MSModule> msModules,String path, Map<String,String> map){
        List<MSModule> moduleTempList = new ArrayList<>();
        for(MSModule item: msModules){
            MSModule msModule = new MSModule();
            msModule.setName(item.getName());
            msModule.setLevel(item.getLevel());
            msModule.setPath(path);
            msModule.setLabel(path+"/"+item.getName());
            msModule.setParentId(getZentaoParentID(path, map));
            moduleTempList.add(msModule);
            if(item.getChildren() != null && item.getChildren().size() != 0){
                moduleTempList.addAll(getModuleName(item.getChildren(), path+"/"+item.getName(), map));
            }
        }
        return moduleTempList;
    }
    private String getZentaoParentID(String path, Map<String,String> map){
        if(StringUtils.equals(path, "")) return "0";
        String parentId = null;
        for(String key : map.keySet()){
            String name = map.get(key);
            if(StringUtils.equals(path, name)) {
                parentId = key;
                break;
            }
        }
        return parentId;
    }
}
