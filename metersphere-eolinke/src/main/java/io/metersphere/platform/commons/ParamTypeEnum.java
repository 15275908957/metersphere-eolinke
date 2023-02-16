package io.metersphere.platform.commons;

import org.apache.commons.lang3.StringUtils;

public enum ParamTypeEnum {
    STRING("0", "string","string"),
    FILE("1", "file","file"),
    JSON("2", "json","json"),
    INT("3", "int","integer"),
    FLOAT("4", "float","number"),
    DOUBLE("5", "double","number"),
    DATE("6", "date","string"),
    DATETIME("7", "datetime","string"),
    BOOLEAN("8", "boolean","boolean"),
    BYTE("9", "byte","integer"),
    SHORT("10", "short","integer"),
    LONG("11", "long","integer"),
    ARRAY("12", "array","array"),
    OBJECT("13", "object","object"),
    NUMBER("14", "number","number");

    private String tpyeCode;
    private String paramType;
    private String openApi3Type;

    ParamTypeEnum(String tpyeCode, String paramType, String openApi3Type) {
        this.tpyeCode = tpyeCode;
        this.paramType = paramType;
        this.openApi3Type = openApi3Type;
    }

    public static String getRequiredByTypeCode(String typeCode){
        ParamTypeEnum[] arr = ParamTypeEnum.values();
        for(int i = 0; i < arr.length ;i++){
            if(StringUtils.equals(typeCode, arr[i].tpyeCode)){
                return arr[i].openApi3Type;
            }
        }
        return STRING.openApi3Type;
    }

    public static String getEolinkerByTypeCode(String typeCode){
        ParamTypeEnum[] arr = ParamTypeEnum.values();
        for(int i = 0; i < arr.length ;i++){
            if(StringUtils.equals(typeCode, arr[i].tpyeCode)){
                return arr[i].paramType;
            }
        }
        return STRING.paramType;
    }

}
