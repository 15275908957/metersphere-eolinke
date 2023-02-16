package io.metersphere.platform.utils;

import java.io.*;

public class UseFile {
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


}
