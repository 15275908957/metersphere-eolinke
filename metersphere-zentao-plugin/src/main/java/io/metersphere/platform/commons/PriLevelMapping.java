package io.metersphere.platform.commons;

public enum PriLevelMapping {
    //只记录需要特殊处理的
    ONE("1","P0"),
    TWO("2","P1"),
    THREE("3","P2"),
    FOUR("4","P3");

    private String pri;
    private String level;

    PriLevelMapping(String pri , String level) {
        this.pri = pri;
        this.level = level;
    }

    public static String getPriByLevel(String type){
        for(PriLevelMapping item : PriLevelMapping.values()){
            if(item.level.equals(type)){
                return item.pri;
            }
        }
        return "0";
    }

    public static String getLevelByPri(String type){
        for(PriLevelMapping item : PriLevelMapping.values()){
            if(item.pri.equals(type)){
                return item.level;
            }
        }
        return "P0";
    }
}
