import com.fasterxml.jackson.databind.util.JSONPObject;
import im.metersphere.plugin.utils.JSON;
import io.metersphere.platform.api.Platform;
import io.metersphere.platform.commons.URLEnum;
import io.metersphere.platform.domain.GetOptionRequest;
import io.metersphere.platform.domain.LarkConfig;
import io.metersphere.platform.domain.PlatformRequest;
import io.metersphere.platform.domain.SelectOption;
import io.metersphere.platform.impl.LarkAbstractClient;
import io.metersphere.platform.impl.LarkPlatform;
import io.metersphere.platform.loader.PlatformPluginManager;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.*;

import java.net.URI;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;

import java.util.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

public class LarkClientTest {

    private LarkPlatform client;

    private String userKey;

    private String token;


    @Before
    public void loadClient() {
//        PlatformRequest request = new PlatformRequest();
//        LarkConfig larkConfig = new LarkConfig();
//        larkConfig.setUrl("https://project.feishu.cn");
//        larkConfig.setPluginId("MII_6368AC54F10B8002");
//        larkConfig.setPluginSecret("91B7B426685F188A222D3F3CCACB491F");
//        String inConfig = JSON.toJSONString(larkConfig);
//        request.setIntegrationConfig(inConfig);
//        client = new LarkPlatform(request);
        PlatformRequest request = new PlatformRequest();
        request.setIntegrationConfig("{\"plugInId\":\"MII_6368AC54F10B8002\",\"LarkPlugin\":\"91B7B426685F188A222D3F3CCACB491F\",\"url\":\"https://project.feishu.cn\",\"pluginId\":\"MII_6368AC54F10B8002\",\"pluginSecret\":\"91B7B426685F188A222D3F3CCACB491F\",\"userKey\":\"61340112956145665\"}");
        client = new LarkPlatform(request);
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

    @Test
    public void getIssueTypes(){
        String data = "{\"error\":{\"code\":0,\"msg\":\"success\",\"display_msg\":{\"title\":\"success\",\"content\":\"success\"}},\"data\":{\"expire_time\":1088,\"token\":\"p-aefdcfaf-7254-466b-8b29-82e4bdb95080\"}}";
        int tokenindex = data.indexOf("token");
        System.out.println(data.substring(126,164));
//        GetOptionRequest getOptionRequest = new GetOptionRequest();
//        getOptionRequest.setProjectConfig("6364c89a48cdc29cab10c396");
//        List<SelectOption> selectOptions = client.getIssueTypes(getOptionRequest);
//        System.out.println(JSONObject.toJSONString(selectOptions));
    }
}
