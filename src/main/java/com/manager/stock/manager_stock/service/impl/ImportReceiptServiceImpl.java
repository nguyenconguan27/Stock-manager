package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.IImportReceiptDao;
import com.manager.stock.manager_stock.dao.impl.ImportReceiptDaoImpl;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.screen.transaction.ImportReceiptPresenter;
import com.manager.stock.manager_stock.service.IImportReceiptService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptServiceImpl implements IImportReceiptService {
    private final IImportReceiptDao importReceiptDao;
    private static ImportReceiptServiceImpl instance;

    private ImportReceiptServiceImpl() {
        importReceiptDao = ImportReceiptDaoImpl.getInstance();
    }

    public static ImportReceiptServiceImpl getInstance() {
        if (instance == null) {
            instance = new ImportReceiptServiceImpl();
        }
        return instance;
    }

    @Override
    public List<ImportReceiptModel> findAll() throws DaoException{
        return importReceiptDao.findAll();
    }

    @Override
    public long save(ImportReceiptModel importReceiptModel) throws DaoException {
        return importReceiptDao.save(importReceiptModel);
    }

    @Override
    public void update(ImportReceiptModel importReceiptModel) throws DaoException {
        importReceiptDao.update(importReceiptModel);
    }

    @Override
    public void delete(long id) throws  DaoException {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        importReceiptDao.delete(ids);
    }
}
