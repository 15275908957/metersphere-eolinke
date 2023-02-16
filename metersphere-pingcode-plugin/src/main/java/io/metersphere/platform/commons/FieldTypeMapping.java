package io.metersphere.platform.commons;

import io.metersphere.platform.constants.CustomFieldType;

public enum FieldTypeMapping {
    //只记录需要特殊处理的
    TEXTAREA(CustomFieldType.RICH_TEXT.getValue(),"textarea"),
    TEXT(CustomFieldType.INPUT.getValue(),"text"),
    MULTI_SELECT(CustomFieldType.MULTIPLE_SELECT.getValue(),"multi-select"),
    NUMBER(CustomFieldType.FLOAT.getValue(),"number"),
    DATE(CustomFieldType.DATE.getValue(),"date"),
    SELECT(CustomFieldType.SELECT.getValue(),"select"),
    MEMBER(CustomFieldType.SELECT.getValue(),"member"),
    MEMBERS(CustomFieldType.MULTIPLE_SELECT.getValue(),"members");

    private String msType;
    private String pingCodeType;

    FieldTypeMapping(String msType , String pingCodeType) {
        this.msType = msType;
        this.pingCodeType = pingCodeType;
    }

    public static String getMsTypeByPingCodeType(String type){
        for(FieldTypeMapping item : FieldTypeMapping.values()){
            if(item.pingCodeType.equals(type)){
                return item.msType;
            }
        }
        return type;
    }
}
