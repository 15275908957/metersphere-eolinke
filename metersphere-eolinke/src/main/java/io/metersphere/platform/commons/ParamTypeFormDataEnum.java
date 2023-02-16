package io.metersphere.platform.commons;

import org.apache.commons.lang3.StringUtils;

public enum ParamTypeFormDataEnum {
    TEXT("0", "string","text"),
    FILE("1", "file","file"),
    JSON("2", "json","json");

    private String tpyeCode;
    private String paramType;
    private String openApi3Type;

    ParamTypeFormDataEnum(String tpyeCode, String paramType, String openApi3Type) {
        this.tpyeCode = tpyeCode;
        this.paramType = paramType;
        this.openApi3Type = openApi3Type;
    }

    public static String getRequiredByTypeCode(String typeCode){
        ParamTypeFormDataEnum[] arr = ParamTypeFormDataEnum.values();
        for(int i = 0; i < arr.length ;i++){
            if(StringUtils.equals(typeCode, arr[i].tpyeCode)){
                return arr[i].openApi3Type;
            }
        }
        return TEXT.openApi3Type;
    }

}
