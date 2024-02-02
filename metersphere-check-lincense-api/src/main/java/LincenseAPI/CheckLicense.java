package LincenseAPI;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckLicense {

    public static void checkLicenseByUrlAndDate(String url, String date) {
        String data = HttpRequest.get(url + "/license/validate").execute().body();
        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONObject jsonData = jsonObject.getJSONObject("data");
        JSONObject jsonLicense = jsonData.getJSONObject("license");
        String a = jsonLicense.getString("expired");
        long expired = getTimeByDateStr(a);
        long newTime = (new Date()).getTime();
        long nextYest = getTimeByDateStr(date);
        if (expired < newTime) {
            System.out.println("许可过期");
            if (newTime > nextYest) {
                System.out.println("预设过期");
                throw new RuntimeException("许可校验失败");
            }
        }

    }

    public static void checkLicenseByUrl(String url) {
        String data = HttpRequest.get(url + "/license/validate").execute().body();
        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONObject jsonData = jsonObject.getJSONObject("data");
        JSONObject jsonLicense = jsonData.getJSONObject("license");
        String a = jsonLicense.getString("expired");
        long expired = getTimeByDateStr(a);
        long newTime = (new Date()).getTime();
        if (expired < newTime) {
            System.out.println("许可过期");
            throw new RuntimeException("许可校验失败");
        }
    }


    public static long getTimeByDateStr(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date apiTime = format.parse(dateStr);
            return apiTime.getTime();
        } catch (Exception var3) {
            return 0L;
        }
    }
}
