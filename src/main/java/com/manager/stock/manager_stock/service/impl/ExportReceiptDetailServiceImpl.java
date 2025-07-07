package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.IExportReceiptDetailDao;
import com.manager.stock.manager_stock.dao.impl.ExportReceiptDetailDaoImpl;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;
import com.manager.stock.manager_stock.service.IExportReceiptDetailService;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptDetailServiceImpl implements IExportReceiptDetailService {
    private final IExportReceiptDetailDao exportReceiptDetailDao;
    private static ExportReceiptDetailServiceImpl instance;

    private ExportReceiptDetailServiceImpl() {
        exportReceiptDetailDao = ExportReceiptDetailDaoImpl.getInstance();
    }
    public static ExportReceiptDetailServiceImpl getInstance() {
        if (instance == null) {
            instance = new ExportReceiptDetailServiceImpl();
        }
        return instance;
    }

    @Override
    public List<ExportReceiptDetailModel> findAllByExportReceipt(long exportReceiptId) throws DaoException {
        return exportReceiptDetailDao.findAllByExPortReceipt(exportReceiptId);
    }
}
