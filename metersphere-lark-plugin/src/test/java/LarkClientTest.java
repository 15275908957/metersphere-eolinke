

import io.metersphere.platform.api.Platform;
import io.metersphere.platform.domain.*;
import io.metersphere.platform.impl.*;
import io.metersphere.platform.loader.PlatformPluginManager;
import io.metersphere.plugin.utils.JSON;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


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
        larkConfig.setPluginId("MII_652515C12C0E4004");
        larkConfig.setPluginSecret("66E3AC84B81809961190008AEC9C216F");
        larkConfig.setUserKey("7241406358686416900");
        larkConfig.setSpaceId("650bab169b4ea5d284b5c5c9");
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
        //根据工作项id  查询工作项
        LarkSearchWorkItemRequest larkWorkItemRequest = new LarkSearchWorkItemRequest();
//        larkWorkItemRequest.setPage_num(1l);
        LarkSearchGroup larkSearchGroup = new LarkSearchGroup();
        larkSearchGroup.setConjunction("AND");
        LarkSearchParam larkSearchParam = new LarkSearchParam();
        larkSearchParam.setOperator("HAS ANY OF");
        //查缺陷
        //larkSearchParam.setParam_key("_field_linked_story");
//        larkSearchParam.setParam_key("work_item_id");
        larkSearchParam.setParam_key("field_87583b");
        int[] demands=new int[]{20967218,21036583};
        larkSearchParam.setValue(demands);
        larkSearchGroup.getSearch_params().add(larkSearchParam);
        larkWorkItemRequest.setSearch_group(larkSearchGroup);
//        List<PlatformCustomFieldItemDTO> temp = client.getThirdPartCustomFieldIO(JSON.toJSONString(larkConfig));
        Object obj = client.searchWorkItemAll(larkWorkItemRequest,"story");
        System.out.println(obj);
    }

    @Test
    public void tGWI(){
        //根据工作项类型，获取指定的工作项列表 成功
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

    }
    @Test
    public void ttt() {

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
