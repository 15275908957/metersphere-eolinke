package io.metersphere.platform.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static long getTimeByDateStr(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date apiTime = format.parse(dateStr);
            return apiTime.getTime();
        } catch (Exception e) {

        }
        return 0l;
    }
}
