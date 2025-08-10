package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.dao.impl.DatasourceInitialize;
import com.manager.stock.manager_stock.dao.impl.UpFileDao;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ProductGroupMapperResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpfileService {
    static Logger logger = LoggerFactory.getLogger(UpfileService.class);
    public static void upFile() {
        com.upfileservice.UpfileService upFileService = new com.upfileservice.UpfileService();
        try {
            UpFileDao upFileDao = new UpFileDao();
            int status = upFileDao.getStatus();
            System.out.println("Upfile status:" + status);
            if(status != 1) {
                upFileService.upfile();
                upFileDao.insert();
            }
        } catch (Exception e) {
            logger.error("Up file faild: e" , e);
        }
    }
}