package utilsss;

import java.io.*;

public class UseFile {

    public static void writeTxt(String fileName, String content){
        try {
            File file = new File(fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(content);
            bw.close();
            System.out.println("finish");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //把文件转为byte数组
    public byte[] InputStream2ByteArray(String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);
        byte[] data = toByteArray(in);
        in.close();
        return data;
    }

    private byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    public static String readTxt(String filePath) {
        String str = "";
        try {
            File file = new File(filePath);
            System.out.println("文件大小"+file.length()/1024+"KB");
            if(file.isFile() && file.exists()) {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int num =0;
                String context = null;
                while ((context = br.readLine()) != null) {
                    str += context;
                    num++;
                }
                System.out.println("readTxt字符长度"+str.length());
                br.close();
            } else {
                System.out.println("文件不存在!");
            }
        } catch (Exception e) {
            System.out.println("文件读取错误!");
        }
        return str;
    }

    public static File readFile(String filePath) {
        File file = new File(filePath);
        return file;
    }

    public static String getContextByFile(File file){
        String str = "";
        try {
            System.out.println("文件大小"+file.length()/1024+"KB");
            if(file.isFile() && file.exists()) {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int num =0;
                String context = null;
                while ((context = br.readLine()) != null) {
                    str += context;
                    num++;
                }
                System.out.println("getContextByFile字符长度"+str.length());
                br.close();
            } else {
                System.out.println("文件不存在!");
            }
        } catch (Exception e) {
            System.out.println("文件读取错误!");
        }
        return str;
    }

    public static void writeTxt(String path ,String fileName, String content){
        try {
            File file = new File(path);
            file.mkdirs();
            file = new File(path+"/"+fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(content);
            bw.close();
            System.out.println("finish");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
