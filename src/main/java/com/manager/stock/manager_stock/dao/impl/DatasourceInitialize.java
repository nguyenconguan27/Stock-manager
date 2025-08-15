package com.manager.stock.manager_stock.dao.impl;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * @author Trọng Hướng
 */
public class DatasourceInitialize {
    static Logger logger = LoggerFactory.getLogger(DatasourceInitialize.class);
    public static Connection INSTANCE;
    private static final ThreadLocal<Connection> threadConnection = new ThreadLocal<>();

//    public static Connection init() throws SQLException {
//        final String url = AppConfig.getString("db.url");
//        final Properties props = new Properties();
//        props.setProperty("user", AppConfig.getString("db.user"));
//        props.setProperty("password", AppConfig.getString("db.password"));
//        logger.debug("Start connect to database..., user = {}, password = {}", AppConfig.getString("db.user"), AppConfig.getString("db.password") );
//        return DriverManager.getConnection(url, props);
//    }

    private static Server webServer = null;

    static {
        try {
            if (webServer == null || !webServer.isRunning(false)) {
                webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8083").start();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void startConsole() {
//        try {
//            if (webServer == null || !webServer.isRunning(false)) {
//                webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8083").start();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public static Connection init() {
//        String jdbcUrl = "jdbc:h2:file:D:/data/test;INIT=RUNSCRIPT FROM 'database/create_table.sql'";
        String user = "sa";
        String pass = "1234";
        try {
            File scriptFile = extractSqlToTempFile("/com/manager/stock/manager_stock/database/create_table.sql");

            String jdbcUrl = "jdbc:h2:file:D:/data/db;INIT=RUNSCRIPT FROM '" + scriptFile.getAbsolutePath().replace("\\", "/") + "'";
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(jdbcUrl, user, pass);
//            startConsole();
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Connection getInstance() {
        Connection connection = threadConnection.get();
//        if(INSTANCE == null) {
//            try {
//                INSTANCE = init();
//            } catch (Exception e) {
//                logger.error("Faile when connect to db e: {}", e);
//            }
//        }
        try {
            if(connection == null || connection.isClosed()) {
                connection = init();
                threadConnection.set(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    private static File extractSqlToTempFile(String resourcePath) throws IOException {
        InputStream input = DatasourceInitialize.class.getResourceAsStream(resourcePath);
        if (input == null) {
            throw new FileNotFoundException("SQL file not found: " + resourcePath);
        }

        File tempFile = File.createTempFile("init-script-", ".sql");
        tempFile.deleteOnExit();

        try (OutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }

        return tempFile;
    }

}
