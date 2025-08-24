package com.manager.stock.manager_stock.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author Trọng Hướng
 */
public class AppConfig {
     private static final Properties properties = new Properties();
     private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    static {
        Path externalConfig = Paths.get("app", "config", "config.properties");
        try (InputStream in = Files.exists(externalConfig)
                ? Files.newInputStream(externalConfig)
                : AppConfig.class.getClassLoader().getResourceAsStream("config.properties")) {

            if(Files.exists(externalConfig)) {
                logger.info("Load file config: {}", externalConfig.toString());
            }
            else {
                logger.info("Load file config from resource");
            }
            if (in == null) {
                logger.warn("⚠️ config.properties not found!");
            } else {
                properties.load(in);
                logger.info("✅ Loaded config file successfully!");
            }

        } catch (Exception e) {
            logger.error("❌ Config file loading error!", e);
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
