package utilsss;

import commons.URL;
import entity.HeaderEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.springframework.core.io.FileSystemResource;
import java.io.IOException;
import java.nio.charset.Charset;

public class MSClient {

    private static String host;
    private static String ak;
    private static String sig;
    private static String token;
    private static String session;

    private static MSClient msClient = new MSClient();

    private MSClient(){}

    public static MSClient getMsClientAKSK(String url, String akTemp, String sigTemp){
        host = url;
        ak = akTemp;
        sig = sigTemp;
        return msClient;
    }

    public static MSClient getMsClientAccountPassword(String url, String tokenTemp, String sessionTemp){
        host = url;
        token = tokenTemp;
        session = sessionTemp;
        return msClient;
    }

    public HeaderEntity[] getHeaders(){
        HeaderEntity[] a = new HeaderEntity[2];
//        a[0] = new HeaderEntity("Content-Type", "application/json;charset=UTF-8");
        if(this.ak != null && this.sig != null){
            a[0] = new HeaderEntity("accessKey", this.ak);
            a[1] = new HeaderEntity("signature", this.sig);
        }else {
            a[0] = new HeaderEntity("CSRF-TOKEN", this.token);
            a[1] = new HeaderEntity("X-AUTH-TOKEN", this.session);
        }
        return a;
    }

    private String getURL(URL url){
        return host+url.url;
    }

    public String importAPI(String path , String fileName, String requestStr) throws IOException {
        return importMS(path, fileName, requestStr, getURL(URL.IMPORT_API));
    }

    public String importScenario(String path , String fileName, String requestStr) throws IOException {
        return importMS(path, fileName, requestStr, getURL(URL.IMPORT_SCENARIO));
    }

    public String importMS(String path , String fileName, String requestStr, String url) throws IOException {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    String result = "";
    FileSystemResource fileResource = new FileSystemResource(path+"/"+fileName);
    try {
        HttpPost httppost = new HttpPost(url);
        //构建超时等配置信息
        RequestConfig config = RequestConfig.custom().setConnectTimeout(1000) //连接超时时间
                .setConnectionRequestTimeout(1000) //从连接池中取的连接的最长时间
                .setSocketTimeout(10 * 1000) //数据传输的超时时间
                .build();
        httppost.setConfig(config);
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.addBinaryBody("file", fileResource.getInputStream(), ContentType.create("application/json"), fileName);
//            entityBuilder.addBinaryBody("filse", fileResource.getInputStream(), );
        entityBuilder.addTextBody("request", requestStr, ContentType.create("application/json"));
        entityBuilder.setCharset(Charset.forName("UTF-8"));
        HttpEntity entity = entityBuilder.build();
        httppost.setEntity(entity);
        httppost.setHeaders(getHeaders());
        CloseableHttpResponse response = httpclient.execute(httppost);
        try {
            HttpEntity resEntity = response.getEntity();
            //回复接收
            result = EntityUtils.toString(resEntity, "UTF-8");

        } finally {
            response.close();
        }
    } catch (Exception e) {
        throw e;
    } finally {
        httpclient.close();
    }
    return result;
    }
}
