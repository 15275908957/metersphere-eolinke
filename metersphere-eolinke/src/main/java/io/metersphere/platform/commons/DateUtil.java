package io.metersphere.platform.commons;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static Date getDateByStr (String d,String f) {
        SimpleDateFormat format = null;
        if(StringUtils.isNotBlank(f)){
            format = new SimpleDateFormat(f);
        } else {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        Date apiTime = null;
        try {
             apiTime = format.parse(d);
        }catch (Exception e){

        }
        return apiTime;
    }

    public static Date getDateByStr (String d) {
        return getDateByStr(d, null);
    }
}
