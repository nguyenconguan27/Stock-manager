package com.manager.stock.manager_stock.dao.impl;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.Properties;

/**
 * @author Trọng Hướng
 */
public class DatasourceInitialize {
    static Logger logger = LoggerFactory.getLogger(DatasourceInitialize.class);
    public static Connection INSTANCE;

//    public static Connection init() throws SQLException {
//        final String url = AppConfig.getString("db.url");
//        final Properties props = new Properties();
//        props.setProperty("user", AppConfig.getString("db.user"));
//        props.setProperty("password", AppConfig.getString("db.password"));
//        logger.debug("Start connect to database..., user = {}, password = {}", AppConfig.getString("db.user"), AppConfig.getString("db.password") );
//        return DriverManager.getConnection(url, props);
//    }

    private static Server webServer;

    public static void startConsole() {
        try {
            if (webServer == null || !webServer.isRunning(false)) {
                webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
                System.out.println("H2 Console started at: http://localhost:8082");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection init() {
        String jdbcUrl = "jdbc:h2:file:D:/data/test;INIT=RUNSCRIPT FROM 'database/create_table.sql'";
        String user = "sa";
        String pass = "1234";
        try {
            Connection conn = DriverManager.getConnection(jdbcUrl, user, pass);
            startConsole();
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getInstance() {
        if(INSTANCE == null) {
            try {
                INSTANCE = init();
            } catch (Exception e) {
                logger.error("Faile when connect to db e: {}", e);
            }
        }
        return INSTANCE;
    }
}
