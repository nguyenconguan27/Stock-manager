package com.manager.stock.manager_stock.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Trọng Hướng
 */
public class AppConfig {
     private static final Properties properties = new Properties();
     private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

     static {
         try(InputStream in = AppConfig.class.getResourceAsStream("/com/manager/stock/manager_stock/config.properties");) {
             if(in == null) {
                 System.err.println("config.properties resource not found!");
                 logger.warn("config.properties resource not found!");
             }
             else {
                 logger.info("Load config file successfully!");
                 properties.load(in);
             }
         }
         catch (Exception e) {
            logger.error("Config file loading error!", e);
            e.printStackTrace();
         }
     }

     public static String getString(String key) {
         return properties.getProperty(key);
     }

     public static int getInt(String key,  int defaultValue) {
         try {
             return Integer.parseInt(properties.getProperty(key));
         }
         catch (Exception e) {
             logger.error("Get value int from config file error!", e);
             return defaultValue;
         }
     }
}
