package io.metersphere.platform.conver;

import com.alibaba.fastjson2.JSONObject;
import io.metersphere.platform.commons.*;
import io.metersphere.platform.domain.EOlinker.*;
import io.metersphere.platform.domain.OpenAPI3.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class EolinkerConverOpenAPI3 {

    public String toOpenAPI3ByEolinker(ProjectAPIEntity projectAPIEntity) throws Exception{
        List<ApiGroupInfoEntity> apiGroupInfoEntities = projectAPIEntity.getApiGroupList();
        OpenAPI3Entity openAPI3Entity = new OpenAPI3Entity();
        openAPI3Entity.setOpenapi("3.0.1");
        openAPI3Entity.setInfo(projectInfoEntityConverInfoEntity(projectAPIEntity.getProjectInfo()));
        Map<String, ApiGroupInfoEntity> apiGroupMap = EolinkerUtils.getTagsListByAPIFroup(apiGroupInfoEntities);
        openAPI3Entity.setPaths(apiFroupListConverPaths(apiGroupInfoEntities, apiGroupMap));
        IdentityHashMap<String, LinkedHashMap<String,APIInfoEntity>> path = openAPI3Entity.getPaths();
        LinkedHashMap<String, LinkedHashMap<String,APIInfoEntity>> linkResult = new LinkedHashMap<>();
        ListIterator<Map.Entry<String, LinkedHashMap<String,APIInfoEntity>>> iterator =
                new ArrayList<>(path.entrySet()).listIterator(path.size());
        while(iterator.hasPrevious()){
            Map.Entry<String, LinkedHashMap<String,APIInfoEntity>> previous = iterator.previous();
            String key = previous.getKey();
            LinkedHashMap<String,APIInfoEntity> value = previous.getValue();
            linkResult.put(key, value);
        }
        path.clear();
        path.putAll(linkResult);
        return openAPI3Entity.toString();
    }

    private IdentityHashMap<String, LinkedHashMap<String, APIInfoEntity>> apiFroupListConverPaths(List<ApiGroupInfoEntity> apiGroupInfoEntities, Map<String, ApiGroupInfoEntity> apiGroupMap) throws Exception{
        IdentityHashMap<String, LinkedHashMap<String, APIInfoEntity>> map = new IdentityHashMap<>();
        if(apiGroupInfoEntities != null && apiGroupInfoEntities.size() != 0){
            for(ApiGroupInfoEntity item: apiGroupInfoEntities){
                List<ApiInfoEntity> apiInfoEntities = item.getApiList();
                if(StringUtils.isNotBlank(item.getGroupName()) && item.getIsChild() != null){
                    List<String> tags = EolinkerUtils.getTagByGroupId(item.getGroupID(),apiGroupMap);
                    for(ApiInfoEntity apiInfo: apiInfoEntities){
                        ApiBaseInfoEntity apiBaseInfoEntity = apiInfo.getBaseInfo();
                        if(apiBaseInfoEntity != null){
                            APIInfoEntity apiInfoEntity = new APIInfoEntity();
                            apiInfoEntity.setSummary(apiBaseInfoEntity.getApiName());
                            apiInfoEntity.setDescription(apiBaseInfoEntity.getApiNote());
                            apiInfoEntity.setTags(tags);
                            //此方法会赋值部分请求头和请求体
                            putApiInfoRequestValue(apiInfoEntity, apiInfo);
                            putApiInfoHeaderValue(apiInfoEntity, apiInfo.getHeaderInfo());
                            putApiInfoResultValue(apiInfoEntity, apiInfo.getResultInfo());
//                            putAPIInfoResultValue(apiInfoEntity, apiInfo);
                            LinkedHashMap<String, APIInfoEntity> tempMap = new LinkedHashMap<>();
                            String requestType = RequestTypeEnum.getRequestTypeByTypeCode(apiBaseInfoEntity.getApiRequestType());
                            tempMap.put(requestType, apiInfoEntity);
                            map.put(apiBaseInfoEntity.getApiURI(),tempMap);
                        }
                    }
                }
                List<ApiGroupInfoEntity> apiGroupInfoEntityList = item.getApiGroupChildList();
                IdentityHashMap<String, LinkedHashMap<String, APIInfoEntity>> tempMap = apiFroupListConverPaths(apiGroupInfoEntityList, apiGroupMap);
                map.putAll(tempMap);
            }
        }
        return map;
    }

    private void putApiInfoResultValue(APIInfoEntity apiInfoEntity, List<ResultInfoEntity> apiInfo) {
        if(apiInfo != null && apiInfo.size() != 0){
            JSONObject schemaJson = new JSONObject();
            JSONObject schemaJsonInfo = new JSONObject();
            JSONObject propertiesJSON = new JSONObject();
            schemaJsonInfo.put("type","object");
            for(ResultInfoEntity item : apiInfo){
                PropertiesEntity propertiesEntity = new PropertiesEntity();
                propertiesEntity.setRequired(ParamNotNullEnum.getRequiredByTypeCode(item.getParamNotNull()));
                propertiesEntity.setType(ParamTypeEnum.getRequiredByTypeCode(item.getParamType()));
                propertiesEntity.setExample(item.getParamName());
                propertiesEntity.setDescription("");
                propertiesJSON.put(item.getParamKey(), propertiesEntity);
            }
            schemaJsonInfo.put("required",Arrays.asList("qq"));
            schemaJsonInfo.put("properties",propertiesJSON);
            schemaJson.put("schema", schemaJsonInfo);
            JSONObject appJson = new JSONObject();
            appJson.put("application/json", schemaJson);
            JSONObject contentJson = new JSONObject();
            contentJson.put("content",appJson);
            JSONObject responseJson = new JSONObject();
            responseJson.put("200", contentJson);
            apiInfoEntity.getResponses().putAll(responseJson);
        }
    }

    private void putApiInfoHeaderValue(APIInfoEntity apiInfoEntity, List<HeaderInfoEntity> apiInfo) {
        if(apiInfo != null && apiInfo.size() != 0){
            for(HeaderInfoEntity item: apiInfo){
                ParameterEntity parameterEntity = new ParameterEntity();
                parameterEntity.setIn("header");
                parameterEntity.setName(item.getHeaderName());
                parameterEntity.setExample(item.getHeaderValue());
                apiInfoEntity.getParameters().add(parameterEntity);
            }
        }
    }

    private void putApiInfoRequestValue(APIInfoEntity apiInfoEntity, ApiInfoEntity apiInfo) throws Exception{
        Integer type = apiInfo.getBaseInfo().getApiRequestParamType();
        if(StringUtils.equals(""+type, RequestParamTypeEnum.RAW.getTpyeCode())){
            JSONObject rawJson = new JSONObject();
            rawJson.put("type", "string");
            rawJson.put("example", apiInfo.getBaseInfo().getApiRequestRaw());
            JSONObject schemaJson = new JSONObject();
            schemaJson.put("schema", rawJson);
            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("application/urlencoded",schemaJson);
            JSONObject content = new JSONObject();
            content.put("content", requestBodyJson);
            apiInfoEntity.getRequestBody().putAll(content);
        } else if(StringUtils.equals(""+type, RequestParamTypeEnum.FORM_DATA.getTpyeCode())){
            JSONObject json = new JSONObject();
            for(RequestInfoEntity item: apiInfo.getRequestInfo()){
                PropertiesEntity propertiesEntity = new PropertiesEntity();
                propertiesEntity.setExample(item.getParamValue());
                propertiesEntity.setRequired(ParamNotNullEnum.getRequiredByTypeCode(item.getParamNotNull()));
                propertiesEntity.setType(ParamTypeFormDataEnum.getRequiredByTypeCode(item.getParamType()));
                json.put(item.getParamName(),propertiesEntity);
            }
            JSONObject propertiesJson = new JSONObject();
            propertiesJson.put("properties",json);
            JSONObject schemaJson = new JSONObject();
            schemaJson.put("schema", propertiesJson);
            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("multipart/form-data",schemaJson);
            JSONObject content = new JSONObject();
            content.put("content", requestBodyJson);
            apiInfoEntity.getRequestBody().putAll(content);
        } else if(StringUtils.equals(""+type, RequestParamTypeEnum.REST_FUL.getTpyeCode())){
            if(apiInfo.getRequestInfo() != null && apiInfo.getRequestInfo().size() != 0){
                for(RequestInfoEntity requestInfoEntity: apiInfo.getRequestInfo()){
                    ParameterEntity parameterEntity = new ParameterEntity();
                    parameterEntity.setName(requestInfoEntity.getParamKey());
                    parameterEntity.setDescription(requestInfoEntity.getParamName());
                    parameterEntity.setExample(requestInfoEntity.getParamValue());
                    parameterEntity.setRequired(ParamNotNullEnum.getRequiredByTypeCode(requestInfoEntity.getParamNotNull()));
                    parameterEntity.setIn("path");
                    apiInfoEntity.getParameters().add(parameterEntity);
                }
            }
        } else {
            throw new Exception("getApiRequestParamType:" + type);
        }
    }

    private InfoEntity projectInfoEntityConverInfoEntity(ProjectInfoEntity projectInfoEntity) {
        InfoEntity infoEntity = new InfoEntity();
        infoEntity.setTitle(projectInfoEntity.getProjectName());
        infoEntity.setVersion(projectInfoEntity.getProjectVersion());
        return infoEntity;
    }
}
