package io.metersphere.platform.conver;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.metersphere.platform.commons.*;
import io.metersphere.platform.domain.EOlinker.*;
import io.metersphere.platform.domain.MeterSphere.*;
import org.apache.commons.lang3.StringUtils;
import java.util.*;

public class EolinkerConverMeterSphere {

    public String toMetersphereByEolinker(ProjectAPIEntity projectAPIEntity){
        MeterSphereEntity meterSphereEntity = new MeterSphereEntity();
        meterSphereEntity.setProjectName(projectAPIEntity.getProjectInfo().getProjectName());
        meterSphereEntity.setProjectId(projectAPIEntity.getProjectInfo().getProjectID()+"");
        meterSphereEntity.setProtocol("HTTP");
        meterSphereEntity.setVersion("v2.5.1-8e5455a0");
        List<ApiInfoEntity> apiInfoEntities = EolinkerUtils.getEolinkerAPIList(projectAPIEntity.getApiGroupList());
        List<DataEntity> dataEntity = getDataEntityListByEolinker(apiInfoEntities,projectAPIEntity);
        Collections.reverse(dataEntity);
        meterSphereEntity.setData(dataEntity);
        return JSONObject.toJSONString(meterSphereEntity);
    }
    private List<DataEntity> getDataEntityListByEolinker(List<ApiInfoEntity> apiInfoEntities,ProjectAPIEntity projectAPIEntity) {
        List<DataEntity> dataEntityList = new ArrayList<>();
        Map<String, ApiGroupInfoEntity> apiGroupMap = EolinkerUtils.getTagsListByAPIFroup(projectAPIEntity.getApiGroupList());
        for(ApiInfoEntity item : apiInfoEntities){
            if(item.getBaseInfo().getApiStatus().intValue() == 2){
                //默认废弃的不同步
                continue;
            }
            DataEntity dataEntity = new DataEntity();
            ApiBaseInfoEntity base = item.getBaseInfo();
            String id = UUID.randomUUID().toString();
            dataEntity.setId(id);
            dataEntity.setName(base.getApiName());
            dataEntity.setProjectId(projectAPIEntity.getProjectInfo().getProjectID()+"");
            String requestType = RequestTypeEnum.getMsMethodByTypeCode(base.getApiRequestType());
            dataEntity.setMethod(requestType);
            String modulePath = getModulePath(base.getGroupId() ,apiGroupMap);
            dataEntity.setModulePath(modulePath);
            dataEntity.setStatus(APIStatusEnum.getMsStatusByTypeCode(base.getApiStatus()+""));
            Date d = DateUtil.getDateByStr(base.getApiUpdateTime());
            dataEntity.setUpdateTime(d.getTime());
            dataEntity.setProtocol(APIProtocolEnum.getMsProtocolByTypeCode(base.getApiProtocol()+""));
            dataEntity.setPath(base.getApiURI());
            dataEntity.setRequest(getRequestByApiInfo(item, id, requestType));

            String remark = "";
            if(StringUtils.isNotBlank(base.getApiSuccessMock())){
                remark += "成功：\n"+base.getApiSuccessMock()+"\n";
            }
            if(StringUtils.isNotBlank(base.getApiFailureMock())){
                remark += "失败：\n"+base.getApiFailureMock();
            }
            if(remark != null && !("").equals(remark)){
                if(remark.length() > 2000){
                    remark = remark.substring(0,2000);
                }
                dataEntity.setRemark(remark);
            }

            dataEntity.setResponse(getResponseByApiInfo(item));
            dataEntityList.add(dataEntity);
        }
        return dataEntityList;
    }

    private String getResponseByApiInfo(ApiInfoEntity item){
        if(item.getResultInfo() == null || item.getResultInfo().size() == 0){
            return null;
        }
        MSResponseEntity msResponseEntity = new MSResponseEntity();
        MSResponseBodyEntity msResponseBodyEntity = new MSResponseBodyEntity();
        JsonSchemaEntity jsonSchemaEntity = new JsonSchemaEntity();
        Map<String,PropertiesResponseEntity> map = new HashMap<>();
        List<String> requiredList = new ArrayList<>();
        for(ResultInfoEntity r:item.getResultInfo()){
            PropertiesResponseEntity p = new PropertiesResponseEntity();
            p.setType(ParamTypeEnum.getRequiredByTypeCode(r.getParamType()));
            PropertiesResponseMockEntity propertiesResponseMockEntity = new PropertiesResponseMockEntity();
            propertiesResponseMockEntity.setMock(r.getParamName());
            p.setMock(propertiesResponseMockEntity);
            if(ParamNotNullEnum.getRequiredBooleanByTypeCode(r.getParamNotNull())){
                requiredList.add(r.getParamKey());
            }
            map.put(r.getParamKey(),p);
        }
        jsonSchemaEntity.setRequired(requiredList);
        jsonSchemaEntity.setProperties(map);
        msResponseBodyEntity.setJsonSchema(jsonSchemaEntity);
        msResponseEntity.setBody(msResponseBodyEntity);
        return JSON.toJSONString(msResponseEntity);
    }

    private String getRequestByApiInfo(ApiInfoEntity item,String rid, String requestType){
        MSRequestEntity msRestEntity = new MSRequestEntity();
        msRestEntity.setName(item.getBaseInfo().getApiName());
        msRestEntity.setPath(item.getBaseInfo().getApiURI());
        msRestEntity.setMethod(requestType);
        msRestEntity.setConnectTimeout("60000");
        msRestEntity.setResponseTimeout("60000");
        if(item.getHeaderInfo() != null && item.getHeaderInfo().size() != 0){
            List<MSHeadersEntity>msHeadersEntities = new ArrayList<>();
            for(HeaderInfoEntity header:item.getHeaderInfo()){
                MSHeadersEntity msHeadersEntity = new MSHeadersEntity();
                msHeadersEntity.setName(header.getHeaderName());
                msHeadersEntity.setValue(header.getHeaderValue());
                msHeadersEntity.setEnable(true);
                msHeadersEntities.add(msHeadersEntity);
            }
            msRestEntity.setHeaders(msHeadersEntities);
        }
        Integer type = item.getBaseInfo().getApiRequestParamType();
        if(StringUtils.equals(""+type, RequestParamTypeEnum.RAW.getTpyeCode())){
            MSBodyEntity msBodyEntity = new MSBodyEntity();
            msBodyEntity.setType("Raw");
            msBodyEntity.setRaw("\""+item.getBaseInfo().getApiRequestRaw()+"\"");
            msBodyEntity.setValid(true);
            msRestEntity.setBody(msBodyEntity);
        }
        if(item.getRequestInfo() != null && item.getRequestInfo().size() != 0){
            if(StringUtils.equals(""+type, RequestParamTypeEnum.FORM_DATA.getTpyeCode())){
                MSBodyEntity msBodyEntity = new MSBodyEntity();
                msBodyEntity.setType("Form Data");
                msBodyEntity.setFormat("JSON-SCHEMA");
                List<MSkvsEntity> mSkvsEntities = new ArrayList<>();
                for(RequestInfoEntity requestInfoEntity : item.getRequestInfo()){
                    MSkvsEntity mSkvsEntity = new MSkvsEntity();
                    mSkvsEntity.setName(requestInfoEntity.getParamKey());
                    String value = "["+ParamTypeEnum.getEolinkerByTypeCode(requestInfoEntity.getParamType())+"]-["
                            +requestInfoEntity.getParamName()+"]-["+requestInfoEntity.getParamValue()+"]";
                    mSkvsEntity.setValue(value);
                    mSkvsEntity.setRequired(ParamNotNullEnum.getRequiredBooleanByTypeCode(requestInfoEntity.getParamNotNull()));
                    mSkvsEntity.setType(ParamTypeFormDataEnum.getRequiredByTypeCode(requestInfoEntity.getParamType()));
                    mSkvsEntities.add(mSkvsEntity);
                }
                msBodyEntity.setKvs(mSkvsEntities);
                msRestEntity.setBody(msBodyEntity);
            } else if(StringUtils.equals(""+type, RequestParamTypeEnum.REST_FUL.getTpyeCode())){
                List<MSRestEntity> msRestEntities = new ArrayList<>();
                for(RequestInfoEntity requestInfoEntity : item.getRequestInfo()){
                    MSRestEntity msRe = new MSRestEntity();
                    msRe.setName(requestInfoEntity.getParamKey());
                    msRe.setValue(requestInfoEntity.getParamValue());
                    msRe.setType(ParamTypeFormDataEnum.getRequiredByTypeCode(requestInfoEntity.getParamType()));
                    msRe.setRequired(ParamNotNullEnum.getRequiredBooleanByTypeCode(requestInfoEntity.getParamNotNull()));
                    msRe.setValid(true);
                    msRestEntities.add(msRe);
                }
                msRestEntity.setRest(msRestEntities);
            } else {
            }

        }
        msRestEntity.setType("HTTPSamplerProxy");
        msRestEntity.setId(UUID.randomUUID().toString());
        msRestEntity.setResourceId(rid);
        msRestEntity.setClazzName("io.metersphere.api.dto.definition.request.sampler.MsHTTPSamplerProxy");

        return JSON.toJSONString(msRestEntity);
    }

    private String getModulePath(Long groupId, Map<String, ApiGroupInfoEntity> apiGroupMap){
        List<String> tags = EolinkerUtils.getTagByGroupId(groupId,apiGroupMap);
        String temp = "";
        for(String tag:tags){
            temp += "/"+tag;
        }
        return temp;
    }
}
