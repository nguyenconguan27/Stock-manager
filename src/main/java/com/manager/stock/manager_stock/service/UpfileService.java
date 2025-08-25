package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.config.AppConfig;
import com.manager.stock.manager_stock.dao.impl.UpFileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpfileService {
    static Logger logger = LoggerFactory.getLogger(UpfileService.class);
    public static void upFile() {
        try {
            logger.info("Upload file.");
//            com.upfileservice.UpfileService upFileService = new com.upfileservice.UpfileService();
            String credential_path = AppConfig.getString("credential.path");
            String tokens_path = AppConfig.getString("tokens.path");
            String local_path = AppConfig.getString("localfile.path");
            com.upfileservice.UpfileService.TOKENS_DIR_PATH = tokens_path;
            com.upfileservice.UpfileService.CREDENTIALS_FILE_PATH = credential_path;
            com.upfileservice.UpfileService.BACKUP_FILE_PATH = local_path;
            UpFileDao upFileDao = new UpFileDao();
            int status = upFileDao.getStatus();
            logger.info("status: " + status);
            if(status != 1) {
                upFileDao.backup();
                logger.info("Backup file successfully.");
                com.upfileservice.UpfileService.upFile();
                logger.info("Starting up file.");
                upFileDao.insert();
            }
            logger.info("Upload file successfully!");
        } catch (Exception e) {
            logger.error("Up file fail: e" , e);
        }
    }
}