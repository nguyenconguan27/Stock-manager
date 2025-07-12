package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatasourceInitialize {
    static Logger logger = LoggerFactory.getLogger(DatasourceInitialize.class);
    public static Connection INSTANCE;

    public static Connection init() throws SQLException {
        final String url = AppConfig.getString("db.url");
        final Properties props = new Properties();
        props.setProperty("user", AppConfig.getString("db.user"));
        props.setProperty("password", AppConfig.getString("db.password"));
        logger.debug("Start connect to database..., user = {}, password = {}", AppConfig.getString("db.user"), AppConfig.getString("db.password") );
        return DriverManager.getConnection(url, props);
    }

    public static Connection getInstance() {
        if(INSTANCE == null) {
            try {
                INSTANCE = init();
            } catch (SQLException e) {
                logger.error("Faile when connect to db e: {}", e);
            }
        }
        return INSTANCE;
    }
}
