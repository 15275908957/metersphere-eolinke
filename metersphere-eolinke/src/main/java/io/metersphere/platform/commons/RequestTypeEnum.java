package io.metersphere.platform.commons;

import org.apache.commons.lang3.StringUtils;

public enum RequestTypeEnum {
    POST("0", "post", "POST"),
    GET("1", "get","GET"),
    PUT("2", "put","PUT"),
    DELETE("3", "delete","DELETE"),
    HEAD("4", "head","HEAD"),
    OPTIONS("5", "options","OPTIONS"),
    PATCH("6", "patch","PATCH");

    private String tpyeCode;
    private String requestType;
    private String msMethod;

    RequestTypeEnum(String tpyeCode , String requestType, String msMethod) {
        this.tpyeCode = tpyeCode;
        this.requestType = requestType;
        this.msMethod = msMethod;
    }

    public static String getRequestTypeByTypeCode(String typeCode){
        RequestTypeEnum[] arr = RequestTypeEnum.values();
        for(int i = 0; i < arr.length ;i++){
            if(StringUtils.equals(typeCode, arr[i].tpyeCode)){
                return arr[i].requestType;
            }
        }
        return null;
    }

    public static String getMsMethodByTypeCode(String typeCode){
        RequestTypeEnum[] arr = RequestTypeEnum.values();
        for(int i = 0; i < arr.length ;i++){
            if(StringUtils.equals(typeCode, arr[i].tpyeCode)){
                return arr[i].msMethod;
            }
        }
        return null;
    }

}
