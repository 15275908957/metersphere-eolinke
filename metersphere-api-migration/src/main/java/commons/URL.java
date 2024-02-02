package commons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URL {

    public static String IMPORT_API(String version){
        if(byVersion(version) >= 2) return "/api/api/definition/import";
        return "/api/definition/import";
    }

    public static String IMPORT_SCENARIO(String version){
        if(byVersion(version) >= 2) return "/api/api/automation/import";
        return "/api/automation/import";
    }
    public static String USER_WORKSPACE(String version){
        if(byVersion(version) >= 2) return "/api/workspace/list/userworkspace";
        return "/workspace/list/userworkspace";
    }

    public static String PROJECT(String version, String workSpaceId){
        if(byVersion(version) >= 2) return "/api/project/listAll/"+workSpaceId;
//        return "/project/list/related";
        return "/project/listAll/"+workSpaceId;
    }

    public static String API_MODULE(String version, String projectId){
        if(byVersion(version) >= 2) return "/api/api/module/list/"+projectId+"/HTTP";
        return "/api/module/list/"+projectId+"/HTTP";
    }

    public static String CENTENT_MODULE(String version, String projectId){
        if(byVersion(version) >= 2) return "/api/api/automation/module/list/"+projectId;
        return "/api/automation/module/list/"+projectId;
    }

    public static String SIGNIN(){
        return "/signin";
    }



    public static int byVersion(String version){
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(version);
        String versionStr = m.replaceAll("").trim();
        versionStr = versionStr.substring(0,1);
        return Integer.parseInt(versionStr);
    }

}
