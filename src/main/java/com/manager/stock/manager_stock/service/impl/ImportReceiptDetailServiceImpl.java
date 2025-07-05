package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.IImportReceiptDetailDao;
import com.manager.stock.manager_stock.dao.impl.ImportReceiptDetailDaoImpl;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;
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

    @Override
    public long save(List<ImportReceiptDetailModel> importReceiptDetailModels, long importReceiptId) throws DaoException {
        return importReceiptDetailDao.save(importReceiptDetailModels, importReceiptId);
    }

    @Override
    public void update(List<ImportReceiptDetailModel> importReceiptDetailModels) throws DaoException {
        importReceiptDetailDao.update(importReceiptDetailModels);
    }

    @Override
    public List<ProductIdAndActualQuantityAndTotalPriceOfReceipt> findAllProductIdByImportReceipt(long importReceiptId) throws DaoException {
        return importReceiptDetailDao.findAllProductIdByImportReceipt(importReceiptId);
    }

    @Override
    public void deleteImportReceiptByImportReceipt(long importReceiptId) throws DaoException {
        importReceiptDetailDao.deleteByImportReceipt(importReceiptId);
    }
}
