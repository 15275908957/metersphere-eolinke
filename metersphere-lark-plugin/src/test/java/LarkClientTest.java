
import com.lark.oapi.Client;
import com.lark.oapi.core.response.RawResponse;
import com.lark.oapi.core.token.AccessTokenType;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.docx.v1.model.CreateDocumentReq;
import com.lark.oapi.service.docx.v1.model.CreateDocumentReqBody;
import com.lark.oapi.service.docx.v1.model.CreateDocumentResp;
import com.lark.oapi.service.im.v1.enums.MsgTypeEnum;
import com.lark.oapi.service.im.v1.model.ext.MessageText;
import io.metersphere.platform.api.AbstractPlatform;
import io.metersphere.platform.domain.*;
import io.metersphere.platform.impl.*;
import io.metersphere.plugin.utils.JSON;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.*;


public class LarkClientTest {

    public class DocxSample {

    }
    private LarkPlatform client;

    private String userKey;

    private String token;

    public void a(){

    }
    LarkConfig larkConfig = new LarkConfig();

    @Before
    public void loadClient() throws Exception {
//        PlatformRequest request = new PlatformRequest();
//        LarkConfig larkConfig = new LarkConfig();
//        larkConfig.setUrl("https://project.feishu.cn");
//        larkConfig.setPluginId("MII_6368AC54F10B8002");
//        larkConfig.setPluginSecret("91B7B426685F188A222D3F3CCACB491F");
//        larkConfig.setUserKey("7161340112956145665");
//        String inConfig = JSON.toJSONString(larkConfig);
//        request.setIntegrationConfig(inConfig);
//        client = new LarkPlatform(request);

        PlatformRequest request = new PlatformRequest();

        larkConfig.setUrl("https://project.feishu.cn");
        larkConfig.setPluginId("MII_63C4D9A1E1C14003");
        larkConfig.setPluginSecret("ADEC309FEF445FD40ED830976C2E7FB5");
        larkConfig.setUserKey("7152532929715896348");
        larkConfig.setSpaceId("617907a1c6bbf993652cb1d8");
        String inConfig = JSON.toJSONString(larkConfig);
        request.setIntegrationConfig(inConfig);
        client = new LarkPlatform(request);

//        Client client = Client.newBuilder("MII_6389A2D6482C8002", "7D31E0C5D0AF0E19186BCAD66FA3D85E").build();
//        CreateDocumentResp resp = null;
//        try{
//            // 发起请求
//            resp = client.docx().document()
//                    .create(CreateDocumentReq.newBuilder()
//                            .createDocumentReqBody(CreateDocumentReqBody.newBuilder()
//                                    .title("title")
//                                    .folderToken("fldcniHf40Vcv1DoEc8SXeuA0Zd")
//                                    .build())
//                            .build()
//                    );
//        }catch (Exception e){}
//        // 业务数据处理
//        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));

//        PlatformRequest request = new PlatformRequest();
//        request.setIntegrationConfig("{\"plugInId\":\"MII_6368AC54F10B8002\",\"LarkPlugin\":\"91B7B426685F188A222D3F3CCACB491F\",\"url\":\"https://project.feishu.cn\",\"pluginId\":\"MII_6368AC54F10B8002\",\"pluginSecret\":\"91B7B426685F188A222D3F3CCACB491F\",\"userKey\":\"61340112956145665\"}");
//        client = new LarkPlatform(request);
    }

    @Test
    public void testSI(){
        LarkSearchWorkItemRequest larkWorkItemRequest = new LarkSearchWorkItemRequest();
//        larkWorkItemRequest.setPage_num(1l);
        LarkSearchGroup larkSearchGroup = new LarkSearchGroup();
        larkSearchGroup.setConjunction("AND");
        LarkSearchParam larkSearchParam = new LarkSearchParam();
        larkSearchParam.setOperator("HAS ANY OF");
        larkSearchParam.setParam_key("_field_linked_story");
        larkSearchParam.setValue(Arrays.asList(1142750));
        larkSearchGroup.getSearch_params().add(larkSearchParam);
        larkWorkItemRequest.setSearch_group(larkSearchGroup);
//        List<PlatformCustomFieldItemDTO> temp = client.getThirdPartCustomFieldIO(JSON.toJSONString(larkConfig));
        Object obj = client.searchWorkItemAll(larkWorkItemRequest,"issue");
        System.out.println(obj);
    }

    @Test
    public void tGWI(){
        LarkProjectConfig larkProjectConfig =  new LarkProjectConfig();
//        larkProjectConfig.setDemandId("1142750");
        LarkWorkItemRequest workItemRequest = new LarkWorkItemRequest(Arrays.asList("story"));
        List<LarkWorkItemInfo> larkWorkItemInfos = client.larkAbstractClient.getWorkItemAll(workItemRequest);
        client.checkIssueByDemandId(larkWorkItemInfos, larkProjectConfig.getDemandId());
        System.out.println(larkWorkItemInfos);
    }

    @Test
    public void tSWI(){
//        LarkProjectConfig larkProjectConfig =  new LarkProjectConfig();
//        larkProjectConfig.setDemandId("1142750");
//        LarkWorkItemRequest workItemRequest = new LarkWorkItemRequest(Arrays.asList("issue"));
        LarkSearchWorkItemRequest workItemRequest = new LarkSearchWorkItemRequest();
        workItemRequest.setPage_num(1l);
//        workItemRequest.setSearch_group();
        List<LarkWorkItemInfo> larkWorkItemInfos = client.larkAbstractClient.searchWorkItem(workItemRequest,"issue");
        System.out.println(larkWorkItemInfos);
    }

    @Test
    public void test01() {
//        //连接redis 必须保证redis服务可以远程连接
//        //Jedis 把每个redis命令封装成对应的方法
//        Jedis jedis = new Jedis("10.1.12.13", 6379);
//        jedis.auth("Password123@redis");
//        //对字符串的操作
//        //存储一个值
//        String s = jedis.set("k1", "v1");
//        System.out.println("返回的结果:" + s);
//        //存储一个值，时间结束后自动删除
//        String setex = jedis.setex("k2", 30l, "v2");
//        System.out.println("返回的结果:" + setex);
//        //存储一个值，若已存在则不存
//        Long aLong = jedis.setnx("k3", "v3");
//        System.out.println("返回的结果:" + aLong);
//
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//
//        //对hash操作
//        Long hset = jedis.hset("k4", "name", "张三");
//        System.out.println("返回的值:" + hset);
//
//        Map<String, String> map = new HashMap<>();
//        map.put("name", "李四");
//        Long hset1 = jedis.hset("k5", map);
//        System.out.println(hset1);
//
//        //关闭
//        jedis.close();
    }
    @Test
    public void ttt() {
        String daata = "\"{\\\"data\\\":[{\\\"default_value\\\":{\\\"default_appear\\\":1,\\\"value\\\":null},\\\"field_alias\\\":\\\"description\\\",\\\"field_key\\\":\\\"description\\\",\\\"field_name\\\":\\\"缺陷描述\\\",\\\"field_type_key\\\":\\\"multi_text\\\",\\\"is_required\\\":2,\\\"is_validity\\\":1,\\\"is_visibility\\\":1,\\\"label\\\":\\\"\\\"},{\\\"default_value\\\":{\\\"default_appear\\\":1,\\\"value\\\":\\\"\\\"},\\\"field_alias\\\":\\\"name\\\",\\\"field_key\\\":\\\"name\\\",\\\"field_name\\\":\\\"缺陷名称\\\",\\\"field_type_key\\\":\\\"text\\\",\\\"is_required\\\":2,\\\"is_validity\\\":1,\\\"is_visibility\\\":1,\\\"label\\\":\\\"\\\"},{\\\"default_value\\\":{\\\"default_appear\\\":2,\\\"value\\\":null},\\\"field_alias\\\":\\\"owner\\\",\\\"field_key\\\":\\\"owner\\\",\\\"field_name\\\":\\\"创建者\\\",\\\"field_type_key\\\":\\\"user\\\",\\\"is_required\\\":2,\\\"is_validity\\\":1,\\\"is_visibility\\\":1,\\\"label\\\":\\\"\\\"},{\\\"default_value\\\":{\\\"default_appear\\\":2,\\\"value\\\":null},\\\"field_alias\\\":\\\"start_time\\\",\\\"field_key\\\":\\\"start_time\\\",\\\"field_name\\\":\\\"提出时间\\\",\\\"field_type_key\\\":\\\"date\\\",\\\"is_required\\\":2,\\\"is_validity\\\":1,\\\"is_visibility\\\":1,\\\"label\\\":\\\"\\\"},{\\\"default_value\\\":{\\\"default_appear\\\":2,\\\"value\\\":null},\\\"field_alias\\\":\\\"current_status_operator\\\",\\\"field_key\\\":\\\"current_status_operator\\\",\\\"field_name\\\":\\\"当前负责人\\\",\\\"field_type_key\\\":\\\"multi_user\\\",\\\"is_required\\\":2,\\\"is_validity\\\":1,\\\"is_visibility\\\":1,\\\"label\\\":\\\"\\\"},{\\\"default_value\\\":{\\\"default_appear\\\":2,\\\"value\\\":null},\\\"field_alias\\\":\\\"updated_at\\\",\\\"field_key\\\":\\\"updated_at\\\",\\\"field_name\\\":\\\"更新时间\\\",\\\"field_type_key\\\":\\\"date\\\",\\\"is_required\\\":2,\\\"is_validity\\\":1,\\\"is_visibility\\\":1,\\\"label\\\":\\\"\\\"},{\\\"default_value\\\":{\\\"default_appear\\\":2,\\\"value\\\":null},\\\"field_alias\\\":\\\"multi_attachment\\\",\\\"field_key\\\":\\\"multi_attachment\\\",\\\"field_name\\\":\\\"多个附件\\\",\\\"field_type_key\\\":\\\"multi_file\\\",\\\"is_required\\\":2,\\\"is_validity\\\":1,\\\"is_visibility\\\":1,\\\"label\\\":\\\"\\\"}],\\\"err\\\":{},\\\"err_code\\\":0,\\\"err_msg\\\":\\\"\\\"}\"";
//        String dd = "{\"data\":\"ee\",\"err\":{},\"err_code\":0,\"err_msg\":\"\"}";
        String dd = "{\"data\":[{\"default_value\":{\"default_appear\":1,\"value\":null},\"field_alias\":\"description\",\"field_key\":\"description\",\"field_name\":\"缺陷描述\",\"field_type_key\":\"multi_text\",\"is_required\":2,\"is_validity\":1,\"is_visibility\":1,\"label\":\"\"},{\"default_value\":{\"default_appear\":1,\"value\":\"\"},\"field_alias\":\"name\",\"field_key\":\"name\",\"field_name\":\"缺陷名称\",\"field_type_key\":\"text\",\"is_required\":2,\"is_validity\":1,\"is_visibility\":1,\"label\":\"\"},{\"default_value\":{\"default_appear\":2,\"value\":null},\"field_alias\":\"owner\",\"field_key\":\"owner\",\"field_name\":\"创建者\",\"field_type_key\":\"user\",\"is_required\":2,\"is_validity\":1,\"is_visibility\":1,\"label\":\"\"},{\"default_value\":{\"default_appear\":2,\"value\":null},\"field_alias\":\"start_time\",\"field_key\":\"start_time\",\"field_name\":\"提出时间\",\"field_type_key\":\"date\",\"is_required\":2,\"is_validity\":1,\"is_visibility\":1,\"label\":\"\"},{\"default_value\":{\"default_appear\":2,\"value\":null},\"field_alias\":\"current_status_operator\",\"field_key\":\"current_status_operator\",\"field_name\":\"当前负责人\",\"field_type_key\":\"multi_user\",\"is_required\":2,\"is_validity\":1,\"is_visibility\":1,\"label\":\"\"},{\"default_value\":{\"default_appear\":2,\"value\":null},\"field_alias\":\"updated_at\",\"field_key\":\"updated_at\",\"field_name\":\"更新时间\",\"field_type_key\":\"date\",\"is_required\":2,\"is_validity\":1,\"is_visibility\":1,\"label\":\"\"},{\"default_value\":{\"default_appear\":2,\"value\":null},\"field_alias\":\"multi_attachment\",\"field_key\":\"multi_attachment\",\"field_name\":\"多个附件\",\"field_type_key\":\"multi_file\",\"is_required\":2,\"is_validity\":1,\"is_visibility\":1,\"label\":\"\"}],\"err\":{},\"err_code\":0,\"err_msg\":\"\"}";
//        LarkResponseBase larkResponseBase = JSON.parseObject(dd, LarkResponseBase.class);
//        Map<String , String> larkResponseBase = JSON.parseMap(dd);
//        System.out.println(larkResponseBase);
    }

    @Test
    public void getThirdPartCustomField() throws Exception{
        HashMap<String,String> a = new HashMap<>();
        a.put("spaceId","6389a7347efa66176062c4fc");
        client.getThirdPartCustomField(JSON.toJSONString(a));
    }
    @Test
    public void checkCommentInfo() {
        try {
            client.validateIntegrationConfig();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void getProjectOptions() {
        GetOptionRequest getOptionRequest = new GetOptionRequest();
        getOptionRequest.setOptionMethod("aa");
        System.out.println(client.getProjectOptions(getOptionRequest));
    }

//    @Test
//    public void v() {
//        LarkProjectConfig larkProjectConfig = new LarkProjectConfig();
//        larkProjectConfig.setSpaceId("6364c89a48cdc29cab10c396");
//        client.validateProjectConfig(JSON.toJSONString(larkProjectConfig));
//    }

    @Test
    public void getIssueTypes(){
        GetOptionRequest getOptionRequest = new GetOptionRequest();
        getOptionRequest.setProjectConfig("6364c89a48cdc29cab10c396");
        List<SelectOption> selectOptions = client.getIssueTypes(getOptionRequest);
        System.out.println(JSON.toJSONString(selectOptions));
    }
}
