package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.IImportReceiptDao;
import com.manager.stock.manager_stock.dao.IImportReceiptDetailDao;
import com.manager.stock.manager_stock.dao.impl.ImportReceiptDetailDaoImpl;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.service.IImportReceiptDetailService;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptDetailServiceImpl implements IImportReceiptDetailService{
    private final IImportReceiptDetailDao importReceiptDetailDao;
    private static ImportReceiptDetailServiceImpl instance;

    private ImportReceiptDetailServiceImpl() {
        importReceiptDetailDao = ImportReceiptDetailDaoImpl.getInstance();
    }

    public static ImportReceiptDetailServiceImpl getInstance() {
        if (instance == null) {
            instance = new ImportReceiptDetailServiceImpl();
        }
        return instance;
    }

    @Override
    public List<ImportReceiptDetailModel> findAllByImportReceiptId(long importReceiptId) {
        return importReceiptDetailDao.findAllByImportReceiptId(importReceiptId);
    }
}
