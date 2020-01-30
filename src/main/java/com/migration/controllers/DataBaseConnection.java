package com.migration.controllers;
/** 
 * This class use to connect database using user inputs
 * @author H264637 (Arjun Gambhir)
 *
 */

import java.sql.*;

public class DataBaseConnection {
    Connection con = null;
    private String  driver;
    private String  url;
    private String  user;
    private String  password;
    private String  name;

    public Connection initConnection() throws ClassNotFoundException,SQLException{
        String dbDriver;
        switch (driver)
        {
            case "Oracle":
                dbDriver="oracle.jdbc.driver.OracleDriver";
                break;
            case "SQL MS Server":
                dbDriver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
                break;
            case "HSQLDB":
                dbDriver="org.hsqldb.jdbcDriver";
                break;
                
                
            default:
                dbDriver="com.mysql.cj.jdbc.Driver";
                break;
        }
        Class.forName(dbDriver);
        System.out.println(url+"/"+name+"?useSSL=false");
        Connection con= DriverManager.getConnection(url, user, password);
        System.out.println("Connected ....");
        return con;
    }

    public void closeConnection() throws SQLException
    {
        con.close();
        }
 
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
