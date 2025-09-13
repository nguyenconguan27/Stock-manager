package com.manager.stock.manager_stock.dao.impl;
import com.manager.stock.manager_stock.config.AppConfig;
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

    private static Server webServer = null;

    static {
        try {
            init();
            webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8083").start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection init() {
        String user = "sa";
        String pass = "1234";
        try {
            File scriptFile = extractSqlToTempFile("/com/manager/stock/manager_stock/database/create_table.sql");
            String dbPath = AppConfig.getString("db.path").replace("\\", "/");
            if(dbPath.isBlank()) {
                dbPath = "./db";
            }
            String jdbcUrl = "jdbc:h2:file:"+ dbPath +";INIT=RUNSCRIPT FROM '" + scriptFile.getAbsolutePath().replace("\\", "/") + "'";
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(jdbcUrl, user, pass);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Connection getInstance() {
        Connection connection = threadConnection.get();
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
