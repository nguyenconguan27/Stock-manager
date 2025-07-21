package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.transactionalDao.IReceiptTransactionDao;
import com.manager.stock.manager_stock.dao.transactionalDao.impl.ReceiptTransactionDaoImpl;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.InventoryDetailModel;
import com.manager.stock.manager_stock.service.IReceiptTransactionService;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ReceiptTransactionServiceImpl implements IReceiptTransactionService {
    private final IReceiptTransactionDao receiptTransactionDao;
    private static ReceiptTransactionServiceImpl instance;

    private ReceiptTransactionServiceImpl() {
        receiptTransactionDao = ReceiptTransactionDaoImpl.getInstance();
    }

    public static ReceiptTransactionServiceImpl getInstance() {
        if (instance == null) {
            instance = new ReceiptTransactionServiceImpl();
        }
        return instance;
    }


    @Override
    public void deleteImportReceiptWithExportsAndUpdateInventory(long importReceiptId, List<Long> exportReceiptIds, List<InventoryDetailModel> inventoryDetailModels) throws DaoException {
        receiptTransactionDao.deleteImportReceiptWithExportsAndUpdateInventory(importReceiptId, exportReceiptIds, inventoryDetailModels);
    }
}
