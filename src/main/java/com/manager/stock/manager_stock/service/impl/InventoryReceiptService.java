package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.*;
import com.manager.stock.manager_stock.dao.impl.*;

/**
 * @author Trọng Hướng
 */
public class InventoryReceiptService {

    private final IImportReceiptDao importReceiptDao;
    private final IExportPriceDao exportPriceDao;
    private final IImportReceiptDetailDao importReceiptDetailDao;
    private final IExportReceiptDetailDao exportReceiptDetailDao;
    private final IInventoryDetailDao inventoryDetailDao;
    private static InventoryReceiptService INSTANCE;

    private InventoryReceiptService() {
        importReceiptDao = ImportReceiptDaoImpl.getInstance();
        exportPriceDao = ExportPriceDaoImpl.getInstance();
        importReceiptDetailDao = ImportReceiptDetailDaoImpl.getInstance();
        inventoryDetailDao = InventoryDetailDaoImpl.getInstance();
        exportReceiptDetailDao = ExportReceiptDetailDaoImpl.getInstance();
    }

    public static InventoryReceiptService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new InventoryReceiptService();
        }
        return INSTANCE;
    }

//    public void
}
