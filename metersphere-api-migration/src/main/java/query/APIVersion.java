package query;

import utilsss.MSClient;

public class APIVersion {
    private static MSClient client;
    private static APIVersion conver = new APIVersion();

    private APIVersion() {}

    public APIVersion (String host, String ak, String sig){
        client = MSClient.getMsClientAKSK(host, ak, sig);
    }

//    public static APIVersion getInstanceAccountPassword(String host, String token, String session, String version){
//        LincenseAPI.CheckLicense.checkLicenseByUrl(host);
//        client = MSClient.getMsClientAccountPassword(host, token, session,version);
//        return conver;
//    }

    public static void getAPIList(){
        String response = null;
        try{
            response = client.getAPIList();
        }catch (Exception e){

        }
    }

}
