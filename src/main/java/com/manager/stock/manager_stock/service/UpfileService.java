package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.dao.impl.UpFileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpfileService {
    static Logger logger = LoggerFactory.getLogger(UpfileService.class);
    public static void upFile() {
        try {
            com.upfileservice.UpfileService upFileService = new com.upfileservice.UpfileService();
            UpFileDao upFileDao = new UpFileDao();
            int status = upFileDao.getStatus();
            if(status != 1) {
                upFileDao.backup();
                upFileService.upFile();
                upFileDao.insert();
            }
        } catch (Exception e) {
            logger.error("Up file faild: e" , e);
        }
    }
}