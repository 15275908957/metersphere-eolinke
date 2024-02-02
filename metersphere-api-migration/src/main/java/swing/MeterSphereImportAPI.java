package swing;

import cn.hutool.core.map.TableMap;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import commons.URL;
import entity.HeaderEntity;
import entity.MSConverEntity;
import entity.RequestFileEntity;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.StringUtils;
import utilsss.AKSKUtils;
import query.Conver;
import utilsss.PropertiesUtils;
import utilsss.UseFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;

public class MeterSphereImportAPI
{
    private static JLabel 场景=new JLabel("场景文件路径：");
    private static JTextField 场景文件=new JTextField(25);
    private static JButton 场景按钮=new JButton("选择场景文件");

    private static JLabel API=new JLabel("API文件路径：");
    private static JTextField API文件=new JTextField(25);
    private static JButton API按钮=new JButton("选择API文件");

    private static JLabel 选择项目=new JLabel("选择项目：");
    private static JComboBox 项目下拉框=new JComboBox();

    private static JLabel 选择接口模块=new JLabel("选择接口模块：");
    private static JLabel 新建版本=new JLabel("新建版本：");
    private static JComboBox 接口模块下拉框=new JComboBox();
    private static JComboBox 新建版本下拉框=new JComboBox();
    private static JTree 接口树 = new JTree();

    private static JScrollPane 接口树面板 = new JScrollPane();

    private static JLabel 选择场景模块=new JLabel("选择场景模块：");
    private static JComboBox 场景模块下拉框=new JComboBox();

    private static JLabel 选择工作空间=new JLabel("选择工作空间：");
    private static JComboBox 工作空间下拉框=new JComboBox();

    private static JLabel 迁移进度=new JLabel("迁移进度  0%");

    private static JButton 迁移=new JButton("一键迁移");

    private static JButton 获取接口树=new JButton("<html>获取<br>接口<br>列表</html>");
    private static JButton 批量新建版本=new JButton("新建版本");

    private static JTextArea 提示信息=new JTextArea("所有信息为必填信息，迁移进度到达100%，迁移完成。");

    private static JLabel MS地址=new JLabel("   MeterSphere平台 地址   ");
    private static JTextField MS地址输入框=new JTextField(25);
    private static JLabel 账户=new JLabel("   MeterSphere平台 账户   ");
    private static JTextField 账户输入框=new JTextField(25);
    private static JLabel 密码=new JLabel("   MeterSphere平台 密码   ");
    private static JPasswordField  密码输入框=new JPasswordField (25);
    private static JButton 转化配置=new JButton("进入 转化配置 页面");
    private static JButton 批量创建API版本=new JButton("进入 批量创建API版本 页面");
    private static JTextArea 登录提示信息=new JTextArea("请先填写登录信息，再进行转换配置，配置信息会保存到当前目录，后续跳过本步骤。");

    public static String token = null;
    public static String session = null;

    public static String msUrl = null;
    public static String account = null;
    public static String password = null;
    public static String version = null;

    public static String accessKey = null;
    public static String secretKey = null;
    public static String signature = null;

    public static JFrame 容器 = null;
    public static JPanel 面板 = null;

    public static TableMap<String, String> workspace = new TableMap<>();
    public static TableMap<String, String> projectMap = new TableMap<>();
    public static TableMap<String, String> 接口模块 = new TableMap<>();
    public static TableMap<String, String> APIVersion = new TableMap<>();

    public static TableMap<String, CheckBoxTreeNode> treeModule = new TableMap<>();
    public static TableMap<CheckBoxTreeNode, JSONObject> treeAPI = new TableMap<>();
    public static TableMap<String, JSONObject> apiMap = new TableMap<>();

    public static TableMap<String, String> 场景模块 = new TableMap<>();
    public static TableMap<String, JSONObject> centextMap = new TableMap<>();

    public static Map<String, String> importTypeMap = new HashMap<>();


    public static String logFileName;

    {
        logFileName = "log" + DateUtils.formatDate(new Date(), DateUtils.PATTERN_RFC1036);
        importTypeMap.put("覆盖", "fullCoverage");
        importTypeMap.put("不覆盖", "incrementalMerge");
    }
    public static void 填写登录面板信息(){
        try {
            MS地址输入框.setText(PropertiesUtils.getInst().getValue("msUrl"));
            账户输入框.setText(PropertiesUtils.getInst().getValue("account"));
            密码输入框.setText(PropertiesUtils.getInst().getValue("password"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void 获取登录面板(JPanel jPanel){
        MS地址.setBounds(50,20,300,30);
        MS地址输入框.setBounds(210,20,300,30);
        账户.setBounds(50,50,300,30);
        账户输入框.setBounds(210,50,300,30);
        密码.setBounds(50,80,300,30);
        密码输入框.setBounds(210,80,300,30);
        密码输入框.setEchoChar('*');
        转化配置.setBounds(65,120,200,30);
        批量创建API版本.setBounds(305,120,200,30);
        登录提示信息.setBounds(50,150,500,100);
        登录提示信息.setForeground(Color.red);
        登录提示信息.setBackground(null);
        登录提示信息.setBorder(null);

        jPanel.add(MS地址);
        jPanel.add(MS地址输入框);

        jPanel.add(账户);
        jPanel.add(账户输入框);

        jPanel.add(密码);
        jPanel.add(密码输入框);
        转化配置.addActionListener(new 进入转化配置页面());
        jPanel.add(转化配置);
        批量创建API版本.addActionListener(new 进入批量创建API版本页面());
        jPanel.add(批量创建API版本);

        jPanel.add(登录提示信息);
    }

    public static void 获取批量创建API版本面板(JPanel panel)
    {

        选择工作空间.setBounds(20,20,100,30);
        工作空间下拉框.setBounds(108,20,365,30);

        选择项目.setBounds(20,50,100,30);
        项目下拉框.setBounds(108,50,365,30);

        新建版本.setBounds(20,80,100,30);
        新建版本下拉框.setBounds(108,80,365,30);

        获取接口树.setBounds(485,20,90,55);

        批量新建版本.setBounds(485,82,90,25);

        接口树面板 = new JScrollPane(接口树);
        接口树面板.setBounds(20,120,555,400);

        提示信息.setBounds(20,520,600,100);
        提示信息.setText("请先选择工作空间和项目，再点击获取接口列表，然后选择需要新建的版本号\n最后勾选需要新建版本的api，点击新建版本，完成批量创建。");
        提示信息.setForeground(Color.red);
        提示信息.setBackground(null);
        提示信息.setBorder(null);

        try{
            panel.add(选择工作空间);
            panel.add(工作空间下拉框);

            panel.add(选择项目);
            panel.add(项目下拉框);

            Object[] project = getMSWorkSpace();
            for(int i = 0 ; i < project.length; i++){
                工作空间下拉框.addItem(project[i]);
            }

            工作空间下拉框.addActionListener(e -> {
                JComboBox cb = (JComboBox) e.getSource();
                //  获得选择项目
                String itemString = (String) cb.getSelectedItem();
                String workSpaceId = workspace.get(itemString);
                JSONArray projectArray = getMSProject(workSpaceId);
                projectMap = new TableMap<>();
                for(int i = 0 ; i < projectArray.size(); i++){
                    JSONObject temp = projectArray.getJSONObject(i);
                    projectMap.put(temp.getString("name"), temp.getString("id"));
                }
                项目下拉框.removeAllItems();
                for(int i = 0 ; i < projectArray.size(); i++){
                    JSONObject temp = projectArray.getJSONObject(i);
                    项目下拉框.addItem(temp.getString("name"));
                }
            });
            工作空间下拉框.setSelectedIndex(0);

            项目下拉框.addActionListener(e -> {
//                新建版本.removeAllItems();

                JComboBox cb = (JComboBox) e.getSource();
                String itemString = (String) cb.getSelectedItem();
                if(itemString == null)return;
                String projectId = projectMap.get(itemString);
                getAPIVersion(projectId);
//                JSONArray apiModuleArray = getAPIModule(projectId);
//                loadAPIModule(apiModuleArray);
//                JSONArray cententModuleArray = getCententModule(projectId);
//                loadCentestModule(cententModuleArray);
            });


            获取接口树.addActionListener(new 获取接口树事件());
            批量新建版本.addActionListener(new 批量新建版本事件());

            CheckBoxTreeNode rootNode = new CheckBoxTreeNode("root");
            DefaultTreeModel model = new DefaultTreeModel(rootNode);
            接口树.addMouseListener(new CheckBoxTreeNodeSelectionListener());
            接口树.setModel(model);
            接口树.setCellRenderer(new CheckBoxTreeCellRenderer());
            //隐藏根节点
            接口树.setRootVisible(false);
            接口树.setShowsRootHandles(true);
            容器.getContentPane().add(接口树面板);

            panel.add(批量新建版本);
            panel.add(新建版本);
            panel.add(新建版本下拉框);
            panel.add(接口树面板);
            panel.add(获取接口树);
            panel.add(提示信息);
        }catch (Exception e) {
            e.printStackTrace();
            提示信息.setText(e.getMessage());
//            wLog(e.getMessage());
        }
    }
    public static void 获取转化配置面板(JPanel panel)
    {
        API.setBounds(20,20,100,30);
        API文件.setBounds(110,20,360,30);
        API按钮.setBounds(480,20,100,30);

        场景.setBounds(20,50,100,30);
        场景文件.setBounds(110,50,360,30);
        场景按钮.setBounds(480,50,100,30);

        选择工作空间.setBounds(20,80,100,30);
        工作空间下拉框.setBounds(108,80,365,30);

        选择项目.setBounds(20,110,100,30);
        项目下拉框.setBounds(108,110,365,30);

        选择接口模块.setBounds(20,140,100,30);
        接口模块下拉框.setBounds(108,140,365,30);

        选择场景模块.setBounds(20,170,100,30);
        场景模块下拉框.setBounds(108,170,365,30);

        迁移.setBounds(485,80,90,90);
//        迁移.setBackground(Color.CYAN);
        迁移进度.setBounds(487,170,100,30);
        提示信息.setBounds(20,210,600,100);
        提示信息.setForeground(Color.red);
        提示信息.setBackground(null);
        提示信息.setBorder(null);
//        panel.add(选择接口模块);
//        panel.add(接口模块下拉框);
//
//        panel.add(选择场景模块);
//        panel.add(场景模块下拉框);
//
//        迁移.addActionListener(new 迁移事件());
//        panel.add(迁移);
//        panel.add(迁移进度);
//        提示信息.setForeground(Color.red);

        try{
            panel.add(API);
            panel.add(API文件);
            panel.add(API按钮);
            API按钮.addActionListener(new API输入框()); //监听按钮事件

            panel.add(场景);
            panel.add(场景文件);
            panel.add(场景按钮);
            场景按钮.addActionListener(new 场景输入框()); //监听按钮事件

            panel.add(选择工作空间);
            panel.add(工作空间下拉框);

            panel.add(选择项目);
            panel.add(项目下拉框);

            Object[] project = getMSWorkSpace();
            for(int i = 0 ; i < project.length; i++){
                工作空间下拉框.addItem(project[i]);
            }



            工作空间下拉框.addActionListener(e -> {
                JComboBox cb = (JComboBox) e.getSource();
//            获得选择项目
                String itemString = (String) cb.getSelectedItem();
                String workSpaceId = workspace.get(itemString);
                JSONArray projectArray = getMSProject(workSpaceId);
//                for(String key:projectMap.keys()){
//                    项目下拉框.removeItem(key);
//                }
                projectMap = new TableMap<>();
                for(int i = 0 ; i < projectArray.size(); i++){
                    JSONObject temp = projectArray.getJSONObject(i);
                    projectMap.put(temp.getString("name"), temp.getString("id"));
                }
                项目下拉框.removeAllItems();
                for(int i = 0 ; i < projectArray.size(); i++){
                    JSONObject temp = projectArray.getJSONObject(i);
                    项目下拉框.addItem(temp.getString("name"));
                }
            });
            工作空间下拉框.setSelectedIndex(0);

            项目下拉框.addActionListener(e -> {
                接口模块下拉框.removeAllItems();
                场景模块下拉框.removeAllItems();

                JComboBox cb = (JComboBox) e.getSource();
                String itemString = (String) cb.getSelectedItem();
                if(itemString == null)return;
                String projectId = projectMap.get(itemString);

                JSONArray apiModuleArray = getAPIModule(projectId);
                loadAPIModule(apiModuleArray);
                JSONArray cententModuleArray = getCententModule(projectId);
                loadCentestModule(cententModuleArray);
            });

            panel.add(选择接口模块);
            panel.add(接口模块下拉框);

            panel.add(选择场景模块);
            panel.add(场景模块下拉框);

            迁移.addActionListener(new 迁移事件());
            panel.add(迁移);
            panel.add(迁移进度);
            panel.add(提示信息);
        }catch (Exception e) {
            e.printStackTrace();
            提示信息.setText(e.getMessage());
//            wLog(e.getMessage());
        }
    }

    public static void loadCentestModule(JSONArray jsonArray){
        场景模块 = new TableMap<>();
        centextMap = new TableMap<>();
        putModuleMap(jsonArray, centextMap);
        centextMap.forEach(item -> {
            String name = getName(item.getValue(), centextMap);
            场景模块.put(name, item.getKey());
            场景模块下拉框.addItem(name);
        });
    }

    public static void loadAPIModule(JSONArray jsonArray){
        apiMap = new TableMap<>();
        接口模块 = new TableMap<>();
        putModuleMap(jsonArray, apiMap);
        apiMap.forEach(item -> {
            String name = getName(item.getValue(), apiMap);
            接口模块.put(name, item.getKey());
            接口模块下拉框.addItem(name);
        });
    }

    public static String getName(JSONObject value, TableMap<String, JSONObject> map){
        String parentId = value.getString("parentId");
        String name = value.getString("name");
        if(parentId != null){
            JSONObject jsonObject = map.get(parentId);
            if(jsonObject != null){
                name = getName(jsonObject, map)+"->"+name;
            }
        }
        return name;
    }

    public static void putModuleMap(JSONArray jsonArray, TableMap<String, JSONObject> centextMap){
        for(int i = 0 ; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String id = jsonObject.getString("id");
            centextMap.put(id, jsonObject);
            JSONArray sub = jsonObject.getJSONArray("children");
            if(sub != null) putModuleMap(sub, centextMap);
        }
    }

    private static Map<String, String > getHeads(){
        Map<String, String > heads = new HashMap<>();
        heads.put("Content-Type", "application/json;charset=UTF-8");
        if(signature == null){
            heads.put("CSRF-TOKEN", token);
            heads.put("X-AUTH-TOKEN", session);
            heads.put("Cookie", "SESSION="+session);
        } else {
            heads.put("accessKey", accessKey);
            heads.put("signature", signature);
        }
        return heads;
    }

    public static HeaderEntity[] getHeaders(){
//        a[0] = new HeaderEntity("Content-Type", "application/json;charset=UTF-8");
        HeaderEntity[] a = new HeaderEntity[4];
        String boundary = UUID.randomUUID().toString().replace("-", "");
        a[0] = new HeaderEntity("CSRF-TOKEN", token);
        a[1] = new HeaderEntity("X-AUTH-TOKEN", session);
        a[2] = new HeaderEntity("Cookie", "SESSION="+session);
        a[3] = new HeaderEntity("Content-Type", "multipart/form-data; boundary="+boundary);
        return a;
    }



    public static Object[] getMSWorkSpace(){
        HttpResponse response = HttpRequest.get(msUrl+ URL.USER_WORKSPACE(version))
                .headerMap(getHeads(),true)
                .timeout(5 * 60 * 1000)
                .execute();
        JSONObject jo = JSONObject.parseObject(response.body());
        JSONArray ja = jo.getJSONArray("data");
        for(int i = 0 ; i < ja.size(); i++){
            JSONObject temp = ja.getJSONObject(i);
            workspace.put(temp.getString("name"), temp.getString("id"));
        }
        return workspace.keys().toArray();
    }

    public static JSONArray getMSProject(String workSpaceId){
        HttpResponse response = null;
        String url = msUrl+URL.PROJECT(version, workSpaceId);
        Map<String, String > map = getHeads();
//        if(URL.byVersion(version) >= 2){
            response = HttpRequest.get(url)
                .headerMap(map, true)
                .timeout(5 * 60 * 1000)
                .execute();
//        }else{
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("workspaceId", workSpaceId);
//            jsonObject.put("userId", account);
//            response = HttpRequest.post(url)
//                .headerMap(getHeads(), false)
//                .body(String.valueOf(jsonObject))
//                .timeout(5 * 60 * 1000)
//                .execute();
//        }
        JSONObject jo = JSONObject.parseObject(response.body());
        JSONArray ja = jo.getJSONArray("data");
        return ja;
    }

    private static StringBuilder StrCBuilder(String str){
        char[] charArray = str.toCharArray();

        StringBuilder binaryStringBuilder = new StringBuilder();

        for (char c : charArray) {
            String binaryString = Integer.toBinaryString((int) c);
            binaryStringBuilder.append(binaryString);
        }
        return binaryStringBuilder;
    }


    public static void caeateAPIVersionClient(String requestStr, String url) throws IOException, URISyntaxException {

        String boundary = UUID.randomUUID().toString().replace("-", "");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("request", null,
                        RequestBody.create(MediaType.parse("application/json"), requestStr.getBytes()))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("CSRF-TOKEN", token)
                .addHeader("X-AUTH-TOKEN", session)
                .addHeader("Cookie", "SESSION="+session)
                .addHeader("Content-Type", "multipart/form-data; boundary="+boundary)
                .build();
        Response response = client.newCall(request).execute();

//        CloseableHttpClient httpclient = HttpClients.createDefault();
////        requestStr = "{\"id\":\"7f5f99bf-04ec-c399-326a-83641037ed81\",\"projectId\":\"ed92a0e6-8267-4138-961f-95dbc6ba1793\",\"name\":\"测试创建版本\",\"method\":\"POST\",\"modulePath\":\"/node1/node1_2\",\"environmentId\":\"\",\"schedule\":null,\"status\":\"Underway\",\"moduleId\":\"73cd131a-78db-49c1-bcc4-e03c68c8f942\",\"userId\":\"admin\",\"createTime\":1698138083154,\"updateTime\":1698214447773,\"protocol\":\"HTTP\",\"path\":\"/api/api/definition/update\",\"num\":100001,\"tags\":\"[]\",\"originalState\":null,\"createUser\":\"Administrator\",\"caseTotal\":0,\"caseStatus\":null,\"casePassingRate\":null,\"deleteTime\":null,\"deleteUserId\":null,\"order\":null,\"refId\":\"7f5f99bf-04ec-c399-326a-83641037ed81\",\"versionId\":\"5253dc41-8c95-470c-93cb-7032393bf4d2\",\"latest\":true,\"toBeUpdated\":null,\"toBeUpdateTime\":null,\"description\":null,\"request\":{\"type\":\"HTTPSamplerProxy\",\"clazzName\":\"io.metersphere.api.dto.definition.request.sampler.MsHTTPSamplerProxy\",\"id\":\"7f5f99bf-04ec-c399-326a-83641037ed81\",\"resourceId\":null,\"name\":\"测试创建版本\",\"label\":null,\"referenced\":null,\"active\":false,\"index\":null,\"enable\":true,\"refType\":null,\"hashTree\":[{\"type\":\"Assertions\",\"clazzName\":\"io.metersphere.api.dto.definition.request.assertions.MsAssertions\",\"id\":\"60afdda8-0bca-7d66-457a-e5d82d72f42b\",\"resourceId\":\"1a817715-8f20-47ae-9e98-18f600925a72\",\"name\":null,\"label\":null,\"referenced\":null,\"active\":false,\"index\":null,\"enable\":true,\"refType\":null,\"hashTree\":null,\"projectId\":\"ed92a0e6-8267-4138-961f-95dbc6ba1793\",\"isMockEnvironment\":false,\"environmentId\":null,\"pluginId\":null,\"stepName\":null,\"parent\":null,\"xpathType\":null,\"scenarioAss\":false,\"regex\":[],\"jsonPath\":[],\"jsr223\":[],\"xpath2\":[],\"duration\":{\"enable\":true,\"label\":null,\"type\":\"Duration\",\"value\":0,\"valid\":false},\"document\":{\"enable\":true,\"type\":\"JSON\",\"data\":{\"jsonFollowAPI\":\"false\",\"xmlFollowAPI\":\"false\",\"json\":[],\"xml\":[],\"assertionName\":null,\"include\":false,\"typeVerification\":false},\"label\":null},\"mockEnvironment\":false}],\"projectId\":null,\"isMockEnvironment\":false,\"environmentId\":null,\"pluginId\":null,\"stepName\":null,\"parent\":null,\"protocol\":\"HTTP\",\"domain\":null,\"port\":null,\"method\":\"POST\",\"path\":\"/api/api/definition/update\",\"connectTimeout\":\"60000\",\"responseTimeout\":\"60000\",\"headers\":[{\"name\":\"\",\"value\":\"\",\"type\":null,\"files\":null,\"description\":null,\"contentType\":null,\"enable\":true,\"urlEncode\":false,\"required\":true,\"min\":0,\"max\":0,\"file\":false,\"valid\":false}],\"body\":{\"type\":\"KeyValue\",\"raw\":null,\"format\":null,\"kvs\":[],\"binary\":[],\"jsonSchema\":null,\"tmpFilePath\":null,\"valid\":false,\"kv\":false,\"xml\":false,\"json\":false},\"rest\":[],\"url\":null,\"followRedirects\":true,\"autoRedirects\":false,\"doMultipartPost\":false,\"useEnvironment\":null,\"arguments\":[{\"name\":null,\"value\":null,\"type\":\"text\",\"files\":null,\"description\":null,\"contentType\":\"text/plain\",\"enable\":true,\"urlEncode\":false,\"required\":false,\"min\":0,\"max\":0,\"file\":false,\"valid\":false}],\"authManager\":null,\"isRefEnvironment\":null,\"alias\":null,\"customizeReq\":false,\"implementation\":null,\"mockEnvironment\":false,\"preSize\":0,\"postSize\":0,\"ruleSize\":0},\"response\":{\"id\":null,\"name\":null,\"enable\":null,\"type\":\"HTTP\",\"headers\":[],\"statusCode\":[],\"body\":{\"type\":\"KeyValue\",\"raw\":null,\"kvs\":[],\"binary\":[],\"format\":null,\"jsonSchema\":null,\"tmpFilePath\":null,\"valid\":false,\"kv\":false,\"xml\":false,\"json\":false}},\"remark\":\"\",\"projectName\":\"禅道对接测试\",\"userName\":\"Administrator\",\"scenarioTotal\":0,\"deleteUser\":null,\"scenarioIds\":null,\"caseType\":\"apiCase\",\"apiType\":null,\"versionName\":\"v1.0.1\",\"versionEnable\":true,\"updated\":false,\"fields\":null,\"follows\":[],\"newVersionRemark\":false,\"newVersionDeps\":false,\"newVersionCase\":false,\"newVersionMock\":false,\"addFields\":[],\"editFields\":[],\"requestFields\":[],\"bodyUploadIds\":[],\"requestId\":\"7f5f99bf-04ec-c399-326a-83641037ed81\"}";
//        try {
//            HttpPost httppost = new HttpPost(url);
//            //构建超时等配置信息
//            RequestConfig config = RequestConfig.custom().setConnectTimeout(1000) //连接超时时间
//                    .setConnectionRequestTimeout(1000) //从连接池中取的连接的最长时间
//                    .setSocketTimeout(10 * 1000) //数据传输的超时时间
//                    .build();
//            httppost.setConfig(config);
//            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
//            entityBuilder.addTextBody("request", requestStr, ContentType.APPLICATION_JSON);
////            entityBuilder.setCharset(Charset.forName("UTF-8"));
////            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//            HttpEntity entity = entityBuilder.build();
//            httppost.setEntity(entity);
//            httppost.setHeaders(getHeaders());
//            CloseableHttpResponse response = httpclient.execute(httppost);
//            try {
//                HttpEntity resEntity = response.getEntity();
//                //回复接收
//                String tt = EntityUtils.toString(resEntity, "UTF-8");
//
//                System.out.println(tt);
//            } finally {
//                response.close();
//            }
//        } catch (Exception e) {
//            throw e;
//        } finally {
//            httpclient.close();
//        }

//        String boundary = UUID.randomUUID().toString().replace("-", "");
//        Map<String, String > map = getHeads();
//        map.put("Content-Type", "multipart/form-data; boundary="+boundary);
//        HttpResponse response = null;
//        JSONObject jsonObject = new JSONObject();
//        Map<String,Object>a = new HashMap<>();
//        a.put("request", requestStr);
//        a.put("Content-Type", "application/json");
//        response = HttpRequest.post(url)
//                .headerMap(map, false)
//                .form
//                .timeout(5 * 60 * 1000)
//                .execute();
//        System.out.println(response.getStatus());
    }

    private static JSONObject getAPIInfoByID (String id){
        String url = msUrl+"/api/api/definition/versions/"+id;
        HttpResponse response = HttpRequest.get(url)
                .headerMap(getHeads(), false)
                .timeout(5 * 60 * 1000)
                .execute();
        JSONObject jo = JSONObject.parseObject(response.body());
        JSONArray ja = jo.getJSONArray("data");
        return ja.getJSONObject(0);
    }
    public static void createAPIVersion(){
        String url = msUrl+"/api/api/definition/update";
        //获取所有勾选的api
        treeAPI.forEach((key, value) -> {
            if(key.isSelected){
//                JSONObject APIInfo = getAPIInfoByID(value.getString("id"));
//                APIInfo.put("versionName", 新建版本下拉框.getSelectedItem());
//                APIInfo.put("versionId", 获取新建版本ID());
//                APIInfo.put("newVersionRemark",false);
//                APIInfo.put("newVersionDeps",false);
//                APIInfo.put("newVersionCase",false);
//                APIInfo.put("newVersionMock",false);

                value.put("versionName", 新建版本下拉框.getSelectedItem());
                value.put("versionId", 获取新建版本ID());
                value.put("newVersionRemark",false);
                value.put("newVersionDeps",false);
                value.put("newVersionCase",false);
                value.put("newVersionMock",false);

                String request = value.getString("request");
                String response = value.getString("response");
                value.put("request",JSONObject.parseObject(request));
                value.put("response",JSONObject.parseObject(response));

                try {
                    caeateAPIVersionClient(value.toJSONString(), url);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public static void getAPIList(){
        String url = msUrl+"/api/api/definition/list/1/10000";
        HttpResponse response = null;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", 获取项目下拉框内的项目ID());
        JSONObject status = new JSONObject();
        status.put("status",Arrays.asList("Prepare","Underway","Completed"));
        jsonObject.put("filters", status);
        response = HttpRequest.post(url)
                .headerMap(getHeads(), false)
                .body(jsonObject.toString())
                .timeout(5 * 60 * 1000)
                .execute();
        JSONObject jo = JSONObject.parseObject(response.body()).getJSONObject("data");;
        JSONArray ja = jo.getJSONArray("listObject");
        加载接口树(ja);
    }

    private static void addTreeModule(JSONObject jsonObject , CheckBoxTreeNode rootNode){
        CheckBoxTreeNode temp = new CheckBoxTreeNode(jsonObject.getString("name"));
        treeModule.put(jsonObject.getString("id"), temp);
        rootNode.add(temp);
        if(!StringUtils.isEmpty(jsonObject.getString("children"))){
            JSONArray jsonArray = jsonObject.getJSONArray("children");
            for(int i = 0 ; i < jsonArray.size(); i++){
                addTreeModule(jsonArray.getJSONObject(i), temp);
            }
        }
    }

    private static void addTreeAPI(JSONArray jsonArray){
        for(int i = 0; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            CheckBoxTreeNode moduleTemp = treeModule.get(jsonObject.getString("moduleId"));
            CheckBoxTreeNode temp = new CheckBoxTreeNode(jsonObject.getString("name"));
            treeAPI.put(temp, jsonObject);
            moduleTemp.add(temp);
        }
    }

    private static void 加载接口树(JSONArray ja){
        JSONArray jsonArray = getAPIModule(获取项目下拉框内的项目ID());
        CheckBoxTreeNode rootNode = new CheckBoxTreeNode("root");
        treeModule = new TableMap<>();
        for(int i = 0 ; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            addTreeModule(jsonObject,rootNode);
        }
        treeAPI = new TableMap<>();
        addTreeAPI(ja);
        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        接口树.setModel(model);
    }

    public static void getAPIVersion(String projectId){
        if(projectId == null) return;
        String url = msUrl+"/project/project/version/list/1/50";
        HttpResponse response = null;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId",projectId);
        response = HttpRequest.post(url)
                .headerMap(getHeads(), false)
                .body(jsonObject.toString())
                .timeout(5 * 60 * 1000)
                .execute();
        JSONObject jo = JSONObject.parseObject(response.body()).getJSONObject("data");;
        JSONArray ja = jo.getJSONArray("listObject");
        新建版本下拉框.removeAllItems();
        APIVersion = new TableMap<>();
        for(int i = 0 ; i < ja.size(); i++){
            String versionName = ja.getJSONObject(i).getString("name");
            String versionId = ja.getJSONObject(i).getString("id");
            新建版本下拉框.addItem(versionName);
            APIVersion.put(versionName , versionId);
        }
    }

    public static JSONArray getAPIModule(String projectId){
        if(projectId == null) return null;
        String url = msUrl+URL.API_MODULE(version, projectId);
        HttpResponse response = null;
        if(URL.byVersion(version) >= 2) {
            response = HttpRequest.post(url)
                .headerMap(getHeads(), false)
                .body("{}")
                .timeout(5 * 60 * 1000)
                .execute();
        }else{
            response = HttpRequest.get(url)
                .headerMap(getHeads(), false)
                .timeout(5 * 60 * 1000)
                .execute();
        }
        JSONObject jo = JSONObject.parseObject(response.body());
        JSONArray ja = jo.getJSONArray("data");
        return ja;
    }

    public static JSONArray getCententModule(String projectId){
        String url = msUrl+URL.CENTENT_MODULE(version, projectId);
        HttpResponse response = HttpRequest.get(url)
                .headerMap(getHeads(), false)
                .timeout(5 * 60 * 1000)
                .execute();
        JSONObject jo = JSONObject.parseObject(response.body());
        JSONArray ja = jo.getJSONArray("data");
        return ja;
    }

    private static void writeLoginInfo() {
        转化配置.setEnabled(false);
        try {
            msUrl = MS地址输入框.getText();
            account = 账户输入框.getText();
            password = 密码输入框.getText();
//                signature = AKSKUtils.getSignature(accessKey, secretKey);
            loginMS();
            loadVersion();
        }catch (Exception e){
            e.printStackTrace();
//                wLog(e.getMessage());
            登录提示信息.setText("请检查地址、端口、网络、账户、密码是否正确。\n返回信息：\n"+e.getMessage());
            throw e;
        }finally {
            转化配置.setEnabled(true);
        }
        MSConverEntity msConverEntity = new MSConverEntity();
        msConverEntity.setAccount(account);
        msConverEntity.setMsUrl(msUrl);
        msConverEntity.setPassword(password);
        msConverEntity.setMsVersion(version);
        UseFile.writeTxt(getUrl()+"/loninMS.properties", msConverEntity.toProperties());
    }

    static class 进入批量创建API版本页面 implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            writeLoginInfo();
            面板.removeAll();
            获取批量创建API版本面板(面板);
            面板.validate();
            面板.repaint();
        }
    }
    static class 进入转化配置页面 implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            writeLoginInfo();
            面板.removeAll();
            获取转化配置面板(面板);
            面板.validate();
            面板.repaint();
        }
    }

    private static void loadVersion(){
        String versionUrl = "/system/version";
        HttpResponse response = null;
        Map<String, String > map = getHeads();
        try{
            response = HttpRequest.get(msUrl+versionUrl)
            .headerMap(map,true)
            .timeout(5 * 60 * 1000)
            .execute();
            JSONObject jo = JSONObject.parseObject(response.body());
            version = jo.getString("data");
        }catch (Exception e){

        }
        if(version != null) return;
        versionUrl = "/project/system/version";
        try{
            response = HttpRequest.get(msUrl+versionUrl)
                    .headerMap(map,true)
                    .timeout(5 * 60 * 1000)
                    .execute();
            JSONObject jo = JSONObject.parseObject(response.body());
            version = jo.getString("data");
        }catch (Exception e){

        }
        if(version != null) return;
        throw new RuntimeException("获取版本失败");
//        JSONArray ja = jo.getJSONArray("data");
//        for(int i = 0 ; i < ja.size(); i++){
//            JSONObject temp = ja.getJSONObject(i);
//            workspace.put(temp.getString("name"), temp.getString("id"));
//        }
    }
    static class 批量新建版本事件 implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            批量新建版本.setEnabled(false);
            try {
                createAPIVersion();
            }catch (Exception e){
                提示信息.setText(e.getMessage());
                e.printStackTrace();
            }finally {
                批量新建版本.setEnabled(true);
                容器.validate();
                容器.repaint();
            }
        }
    }

    static class 获取接口树事件 implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            获取接口树.setEnabled(false);
            try {
                getAPIList();
            }catch (Exception e){
                提示信息.setText(e.getMessage());
                e.printStackTrace();
            }finally {
                获取接口树.setEnabled(true);
                容器.validate();
                容器.repaint();
            }
        }
    }

    //Action事件处理
    static class 迁移事件 implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            迁移.setEnabled(false);
            try {
                conver();
            }catch (Exception e){
                提示信息.setText(e.getMessage());
                e.printStackTrace();
            }finally {
                迁移.setEnabled(true);
                容器.validate();
                容器.repaint();
            }
        }
    }

    private static String 获取新建版本ID(){
        String itemString = (String) 新建版本下拉框.getSelectedItem();
        String versionId = APIVersion.get(itemString);
        return versionId;
    }

    private static String 获取项目下拉框内的项目ID(){
        String itemString = (String) 项目下拉框.getSelectedItem();
        String projectId = projectMap.get(itemString);
        return projectId;
    }

    private static String 获取API模块ID(){
        String moduleName = 接口模块下拉框.getSelectedItem()+"";
        return 接口模块.get(moduleName);
    }

    private static String 获取场景模块ID(){
        String moduleName = 场景模块下拉框.getSelectedItem()+"";
        return 场景模块.get(moduleName);
    }


    private static void 修改迁移进度(int i){
        迁移进度.setText("迁移进度  "+i+"%");
        容器.validate();
        容器.repaint();
    }
    private static void conver(){
        修改迁移进度(1);

        String API文件路径 = API文件.getText();
        String 场景文件路径 = 场景文件.getText();

        String API文件信息 = UseFile.readTxt(API文件路径);
        String 场景文件信息 = UseFile.readTxt(场景文件路径);
        修改迁移进度(10);
        String toProjectID = 获取项目下拉框内的项目ID();
        String toAPIModuleID = 获取API模块ID();
        String toCentextModuleId = 获取场景模块ID();
        Conver conver = Conver.getInstanceAccountPassword(msUrl, token, session,version);
        修改迁移进度(15);
        try{
            String apiData = conver.apiConver(API文件信息, toProjectID, toAPIModuleID, 获取导入请求参数(toProjectID, toAPIModuleID), getUrl());
            修改迁移进度(65);
            conver.contextConver(场景文件信息, apiData, 获取导入请求参数(toProjectID, toCentextModuleId), toProjectID, toCentextModuleId, getUrl());
            修改迁移进度(100);
        }catch (Exception e){
            e.printStackTrace();
            提示信息.setText(e.getMessage());
//            wLog(e.getMessage());
        }
    }

    private static String 获取导入请求参数(String projectId, String moduleId){
        RequestFileEntity requestFileEntity = new RequestFileEntity();
        requestFileEntity.setModuleId(moduleId);
        requestFileEntity.setProjectId(projectId);
        requestFileEntity.setProtocol("HTTP");
        requestFileEntity.setSaved(true);
        requestFileEntity.setPlatform("Metersphere");
        try {
            String importType = importTypeMap.get(获取配置文件值("importType"));
            requestFileEntity.setModeId(importType);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JSON.toJSONString(requestFileEntity);
    }

    private static String getUrl(){
        return System.getProperty("user.dir");
    }

    //Action事件处理
    static class 场景输入框 implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            JFileChooser fc=new JFileChooser();
            int val=fc.showOpenDialog(null); //文件打开对话框
            if(val==fc.APPROVE_OPTION)
            {
                //正常选择文件
                场景文件.setText(fc.getSelectedFile().toString());
            }
            else
            {
                //未正常选择文件，如选择取消按钮
                场景文件.setText("未选择文件");
            }
        }
    }

    //Action事件处理
    static class API输入框 implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
            JFileChooser fc=new JFileChooser();
            int val=fc.showOpenDialog(null); //文件打开对话框
            if(val==fc.APPROVE_OPTION)
            {
                //正常选择文件
                API文件.setText(fc.getSelectedFile().toString());
            }
            else
            {
                //未正常选择文件，如选择取消按钮
                API文件.setText("未选择文件");
            }
        }
    }

    public static Properties 获取配置文件() throws IOException {
        String url = getUrl()+"/loninMS.properties";
        InputStream in = new FileInputStream(url);
        Properties ps = new Properties();
        ps.load(in);
        return ps;
    }

    public static String 获取配置文件值(String name) throws IOException {
        Properties ps = 获取配置文件();
        String value = ps.getProperty(name);
        return new String(value.getBytes("iso8859-1"),"utf-8");
    }

    public static boolean 校验配置文件() throws IOException {
        Properties ps = 获取配置文件();
        msUrl = ps.getProperty("msUrl");
        accessKey = ps.getProperty("accessKey");
        secretKey = ps.getProperty("secretKey");
        if(accessKey != null && secretKey != null){
            signature = AKSKUtils.getSignature(accessKey, secretKey);
        }else{
            account = ps.getProperty("account");
            password = ps.getProperty("password");
            version = ps.getProperty("msVersion");
            loginMS();
        }
        return true;
    }

    public static void loginMS() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username",account);
        jsonObject.put("password",password);
        jsonObject.put("authenticate","LOCAL");
        Map<String, String > heads = new HashMap<>();
        heads.put("Content-Type", "application/json;charset=UTF-8");
        HttpResponse response = HttpRequest.post(msUrl+URL.SIGNIN())
                .headerMap(heads, false)
                .body(String.valueOf(jsonObject))
                .timeout(5 * 60 * 1000)
                .execute();
        try {
            JSONObject jo = JSONObject.parseObject(response.body());
            token = jo.getJSONObject("data").getString("csrfToken");
            session = jo.getJSONObject("data").getString("sessionId");
            if(session == null){
                // tempList 为空则表示为1X版本
                Map<String, List<String>> headers = response.headers();
                List<String> tempList = headers.get("Set-Cookie");
                session = tempList.get(0).substring(8,8+48);
            }
//            if(version != null){
//                if(URL.byVersion(version) >= 2){
//
//                }else{
//
//                    session = response.getCookieStr();
//                }
//            }

        }catch (Exception e){
            throw new RuntimeException(response.body());
        }
    }

    private static Image getImage(){
        ClassPathResource classPathResource = new ClassPathResource("img/tubiao.png");
        java.awt.Image image = null;
        try {
            File file = classPathResource.getFile();
            ImageIcon imageIcon = new ImageIcon(file.toURL());
            image = imageIcon.getImage();
        }catch (Exception e){
            e.printStackTrace();
        }
        return image;
    }


    public static void main(String[] args)
    {
        容器 = new JFrame("MeterSphere 实用工具");
        容器.setIconImage(getImage());
        容器.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        面板 = new JPanel();
        面板.setLayout(null);
        //检测配置文件
//        try {
//            if(校验配置文件()){
//                获取转化配置面板(面板);
//            } else {
//                获取登录面板(面板);
//            }
//        }catch (Exception e) {
            获取登录面板(面板);
            填写登录面板信息();
//        }
        容器.add(面板);
        容器.setSize(600,600);
        容器.setVisible(true);
//        容器.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }




}