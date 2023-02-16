package io.metersphere.platform.mysql;

import com.alibaba.fastjson2.JSONObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MysqlUtils {
     Connection conn = null;
     Statement state = null;
     ResultSet rs = null;
     String connect = null;
     String user = null;
     String password = null;

    public MysqlUtils(String connect, String user, String password){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connect = connect;
            this.user = user;
            this.password = password;
            connect();
        }catch (Exception e){
            System.out.println("数据库连接异常");
        }finally {
        }
    }

    private void connect() {
        try{
            conn = DriverManager.getConnection(connect, user, password);
            state = conn.createStatement();
        }catch (Exception e){
            System.out.println("数据库连接异常");
        }
    }

    public Integer update (String sql){
        connect();
        Integer c = null;
        try {
            c = state.executeUpdate(sql);
        }catch (Exception e){
            System.out.println("修改失败"+sql);
        }finally {
            mysqlClose();
        }
         return c;
    }

    public Integer update (List<String> sqlList){
        connect();
        Integer c = 0;
        String sql = null;
        try{
            for(String item:sqlList){
                try {
                    sql = item;
                    c += state.executeUpdate(item);
                }catch (Exception e){
                    System.out.println("update error sql:"+sql);
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mysqlClose();
        }
        return c;
    }

    public String selectIdBySql (String sql){
//        connect();
        String fId = null;
        try{
            rs = state.executeQuery(sql);
            while (rs.next()){//判断是否有下一条数据
                fId =  rs.getString("id");
            }
        }catch (Exception e){
            System.out.println("查询字段id失败"+sql);
        }finally {
//            mysqlClose();
        }
        return fId;
    }

    public String selectValueBySQL(String sql){
        connect();
        String value = null;
        try{
            rs = state.executeQuery(sql);
            while (rs.next()){//判断是否有下一条数据
                value =  rs.getString("value");
            }
        }catch (Exception e){
            System.out.println("查询字段value失败"+sql);
        }finally {
            mysqlClose();
        }
        return value;
    }

    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/metersphere", "root", "feizhiyun");
        } catch (Exception e){

        }
        Statement state = null;
        try{
            state = conn.createStatement();
        } catch (Exception e){

        }
        String sql = "update custom_field_api set `value` = \"cjgTTTTTTTT\" where resource_id in (select id from api_definition where path = \"/chenjiguang\" and `status` !=  \"Trash\")";
        Integer count = null;
        try{
            ResultSet rs = state.executeQuery(sql);
            List<String> ids = new ArrayList<>();
            while (rs.next()){//判断是否有下一条数据
               String idn =  rs.getString("id");
            }
            System.out.println(JSONObject.toJSONString(ids));
//            count = state.executeUpdate(sql);
        } catch (Exception e){

        }
        System.out.println(count);
        try {
            conn.close();
            state.close();
        }catch (Exception e){

        }
    }
    private void mysqlClose(){
        try {
            rs.close();
        }catch (Exception e){}
        try {
            state.close();
        }catch (Exception e){}
        try {
            conn.close();
        }catch (Exception e){}
    }
}
