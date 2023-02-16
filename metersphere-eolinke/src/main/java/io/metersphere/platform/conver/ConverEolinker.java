package io.metersphere.platform.conver;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import io.metersphere.platform.domain.EOlinker.ApiListEntity;
import io.metersphere.platform.domain.EOlinker.ProjectAPIEntity;
import io.metersphere.platform.mysql.MysqlUtils;
import io.metersphere.platform.utils.DateUtils;
import io.metersphere.platform.utils.UseFile;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConverEolinker {
    private Gson gson = new Gson();

    public File getEolinkerFile(String filePath){
        File file = UseFile.readFile(filePath);
        return file;
    }
    public String getEolinkerFileContext(String filePath){
        File file = getEolinkerFile(filePath);
        String context = UseFile.getContextByFile(file);
        return context;
    }

    public void getOpenAPI3ByEolinker(String eolinkerFile, String openAPI3File) throws Exception{
        //拿到eolinker文件
        String context = getEolinkerFileContext(eolinkerFile);
        ProjectAPIEntity projectAPIEntity = JSONObject.parseObject(context,ProjectAPIEntity.class);
        EolinkerConverOpenAPI3 e = new EolinkerConverOpenAPI3();
        context = e.toOpenAPI3ByEolinker(projectAPIEntity);
        UseFile.writeTxt(openAPI3File, context);
    }

    public void getMetersphereByEolinker(String eolinkerFile, String openAPI3File) throws Exception {
        String context = getEolinkerFileContext(eolinkerFile);
        ProjectAPIEntity projectAPIEntity = JSONObject.parseObject(context,ProjectAPIEntity.class);
        EolinkerConverMeterSphere e = new EolinkerConverMeterSphere();
        context = e.toMetersphereByEolinker(projectAPIEntity);
        UseFile.writeTxt(openAPI3File, context);
    }

    public Integer updateAPIUpdateUserAndUpdateDate(String msProjectId, String eoLinkerAPIList, String connect, String user, String password) {
        System.out.println("开始更新 ，字段长度:"+eoLinkerAPIList.length());
        List<ApiListEntity> apiListEntities = JSON.parseArray(eoLinkerAPIList, ApiListEntity.class);
        System.out.println("开始更新 ，转化后列表长度:"+apiListEntities.size());
        //组合sql
        List<String> sqlList = new ArrayList<>();
        System.out.println("创建sql列表类成功");
        //移除相同url的APIupdateAPIUpdateUser
        rmAPI(apiListEntities);
        System.out.println("移除相同url成功，剩余数量"+apiListEntities.size());
        MysqlUtils mysqlUtils = new MysqlUtils(connect, user, password);

        for(ApiListEntity item : apiListEntities){
            //统一查一下，如果有值，则录入更新语句，如果没有值，录入插入语句
            String selectRIDSql = "select id from api_definition where path = \""+item.getApiURI()+"\" and `status` !=  \"Trash\" and project_id = \""+msProjectId+"\";";
            String rid = mysqlUtils.selectIdBySql(selectRIDSql);
            String selectFIDSql = "select id from custom_field where name = \"开发人员\" and scene = \"API\" and project_id = \""+msProjectId+"\";";
            String fid = mysqlUtils.selectIdBySql(selectFIDSql);
//            String selectCVSql = "select `value` from custom_field_api where resource_id = \""+rid+"\" and field_id = \""+fid+"\";";
//            String value = mysqlUtils.selectValueBySQL(selectCVSql);
            if(StringUtils.isNotBlank(rid) && StringUtils.isNotBlank(fid)){
                String replace = "REPLACE INTO custom_field_api (resource_id,field_id,`value`) VALUES (\""+rid+"\",\""+fid+"\",\""+item.getUserNickName()+"\");";
                //添加更新修改时间sql
                String updateNameTimeSql = "update api_definition set name=\"" + item.getApiName() + "\"  , update_time="+DateUtils.getTimeByDateStr(item.getApiUpdateTime())+"   where id = \""+rid+"\";";
                sqlList.add(replace);
                sqlList.add(updateNameTimeSql);
//                if(StringUtils.isNotBlank(value)){
//                    sqlList.add("update custom_field_api set `value` = \""+item.getUserNickName()+"\" where resource_id = \""+rid+"\";");
//                } else {
//                    sqlList.add("INSERT into custom_field_api (resource_id,field_id,`value`) VALUES (\""+rid+"\",\""+fid+"\",\""+item.getUserNickName()+"\");");
//                }
            }else{
                System.out.println("获取外键失败："+rid+" "+fid);
                if(rid == null){
                    System.out.println("rid 获取sql："+selectRIDSql);
                }
                if(fid == null){
                    System.out.println("rid 获取sql："+selectFIDSql);
                }
            }
        }
        System.out.println("sql组合成功，sql数量"+sqlList.size());
        Integer count = mysqlUtils.update(sqlList);
        System.out.println("sql更新成功"+count);
        return count;
    }
    private void rmAPI(List<ApiListEntity> apiListEntities){
        System.out.println("开始过滤类");
        Map<String,ApiListEntity> map = new HashMap<>();
        for(ApiListEntity item:apiListEntities){
            try {
                ApiListEntity apiListEntity = map.get(item.getApiURI());
                if(apiListEntity != null){
                    ApiListEntity temp = isUpdateTime(apiListEntity, item);
                    map.put(temp.getApiURI(), temp);
                } else {
                    map.put(item.getApiURI(), item);
                }
            }catch (Exception e){
                System.out.println("过滤失败"+JSONObject.toJSONString(item));
            }
        }
        System.out.println("过滤类后"+map.values().size());
        List<ApiListEntity> apiListEntityList = new ArrayList<>();
        apiListEntityList.addAll(map.values());
        System.out.println("新列表"+apiListEntityList.size());
        apiListEntities = apiListEntityList;
        System.out.println("旧"+apiListEntities.size());
    }
    private ApiListEntity isUpdateTime(ApiListEntity api, ApiListEntity item){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date apiTime = format.parse(api.getApiUpdateTime());
            Date itemTime = format.parse(item.getApiUpdateTime());
            if(apiTime.getTime() > itemTime.getTime()){
                return api;
            }else{
                return item;
            }
        } catch (Exception e) {
            System.out.println("时间转化失败"+JSON.toJSONString(api)+JSON.toJSONString(item));
        }
        return item;
    }

    private void isUpdateTime(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date apiTime = format.parse(date);
            System.out.println(apiTime.getTime());
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) throws Exception {

        ConverEolinker converEolinker = new ConverEolinker();
//        String eolinkerFile = "/Users/chenjiguang/Desktop/浙江智慧普华/eoLinker_cjgTestNew.export";
        String eolinkerFile = "/Users/chenjiguang/Desktop/浙江智慧普华/eoLinker_cjg7.export";
//        String eolinkerFile = "/Users/chenjiguang/Desktop/浙江智慧普华/eoLinker_1000api.export";
//        String eolinkerFile = "/Users/chenjiguang/Desktop/浙江智慧普华/eoLinker_500api.export";
//        String eolinkerFile = "/Users/chenjiguang/Desktop/浙江智慧普华/eoLinker_ruimin.export";
        String openAPI3File = "/Users/chenjiguang/Desktop/浙江智慧普华/openAPI3_1000API.json";
        String metersphereFile = "/Users/chenjiguang/Desktop/浙江智慧普华/metersphere1000.json";
//        converEolinker.getOpenAPI3ByEolinker(eolinkerFile,openAPI3File);
        converEolinker.getMetersphereByEolinker(eolinkerFile,metersphereFile);
//        String info = "{\"id\":null,\"name\":null,\"enable\":null,\"type\":\"HTTP\",\"headers\":[{\"name\":\"\",\"value\":\"\",\"type\":null,\"files\":null,\"description\":null,\"contentType\":null,\"enable\":true,\"urlEncode\":false,\"required\":true,\"min\":null,\"max\":null,\"valid\":false,\"file\":false}],\"statusCode\":[{\"name\":\"\",\"value\":\"\",\"type\":null,\"files\":null,\"description\":null,\"contentType\":null,\"enable\":true,\"urlEncode\":false,\"required\":true,\"min\":null,\"max\":null,\"valid\":false,\"file\":false}],\"body\":{\"type\":\"Form Data\",\"raw\":\"{\\n    \\\"response\\\": \\\"bb\\\"\\n}\",\"format\":\"JSON-SCHEMA\",\"kvs\":[{\"name\":null,\"value\":null,\"type\":\"text\",\"files\":null,\"description\":null,\"contentType\":\"text/plain\",\"enable\":true,\"urlEncode\":true,\"required\":false,\"min\":null,\"max\":null,\"valid\":false,\"file\":false}],\"binary\":[],\"jsonSchema\":{\"$id\":\"http://example.com/root.json\",\"title\":\"The Root Schema\",\"hidden\":true,\"mock\":{\"mock\":\"\"},\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"type\":\"object\",\"properties\":{\"response\":{\"$id\":\"#/properties/response\",\"title\":\"The response Schema\",\"hidden\":true,\"mock\":{\"mock\":\"bb\"},\"type\":\"string\"},\"array\":{\"type\":\"array\",\"items\":[{\"type\":\"string\",\"mock\":{\"mock\":\"\"}}],\"mock\":{\"mock\":\"tt\"}},\"field_0_1_2ff69\":{\"type\":\"object\"}}},\"tmpFilePath\":null,\"kv\":true,\"oldKV\":false,\"xml\":false,\"json\":false,\"valid\":false}}";
//        System.out.println(StringEscapeUtils.unescapeJava(info));

    }

//    private void checkErrorAPI(String ef, String of){
//        String context = getEolinkerFileContext(ef);
//        ProjectAPIEntity projectAPIEntity = JSONObject.parseObject(context,ProjectAPIEntity.class);
//        context = getEolinkerFileContext(of);
//        OpenAPI3Entity openAPI3Entity = JSONObject.parseObject(context, OpenAPI3Entity.class);
//        System.out.println("aa");
//        getEOlinkerAPIInfoList(projectAPIEntity);
//    }
//    private LinkedHashMap<String, LinkedHashMap<String, APIInfoEntity>> getEOlinkerAPIInfoList(ProjectAPIEntity projectAPIEntity){
//
//    }
}
