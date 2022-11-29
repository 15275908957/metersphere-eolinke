package io.metersphere.platform.commons;

import im.metersphere.plugin.exception.MSPluginException;
import im.metersphere.plugin.utils.JSON;

import java.util.Map;

public enum ERRCODEEnum {
    ERROR_30006(30006,"用户密钥错误");

    private int code;
    private String message;

    ERRCODEEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static void throwException(String message) throws MSPluginException{
        Map<String,Object> map = JSON.parseMap(message);
        ERRCODEEnum[] arr = ERRCODEEnum.values();
        Integer code = Integer.parseInt(map.get("err_code")+"");
        for(int i = 0; i < arr.length ;i++){
            if(code.intValue() == arr[i].code){
                MSPluginException.throwException(arr[i].message);
            }
        }
    }


    public static void throwException(int code) throws MSPluginException{
        ERRCODEEnum[] arr = ERRCODEEnum.values();
        for(int i = 0; i < arr.length ;i++){
            if(code == arr[i].code){
                MSPluginException.throwException(arr[i].message);
            }
        }
    }

}
