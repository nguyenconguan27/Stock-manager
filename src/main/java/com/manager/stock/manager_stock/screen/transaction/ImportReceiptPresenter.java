package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.service.IImportReceiptDetailService;
import com.manager.stock.manager_stock.service.IImportReceiptService;
import com.manager.stock.manager_stock.service.impl.ImportReceiptDetailServiceImpl;
import com.manager.stock.manager_stock.service.impl.ImportReceiptServiceImpl;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptPresenter {
    private final ImportReceiptScreen importReceiptScreen;
    private final IImportReceiptService importReceiptService;
    private final IImportReceiptDetailService importReceiptDetailService;
    private static ImportReceiptPresenter instance;

    private ImportReceiptPresenter() {
        importReceiptScreen = ImportReceiptScreen.getInstance();
        importReceiptService = ImportReceiptServiceImpl.getInstance();
        importReceiptDetailService = ImportReceiptDetailServiceImpl.getInstance();
    }

    public static ImportReceiptPresenter getInstance() {
        if (instance == null) {
            instance = new ImportReceiptPresenter();
        }
        return instance;
    }

    public void loadImportReceiptList() {
        List<ImportReceiptModel> importReceiptModels = importReceiptService.findAll();
        importReceiptScreen.showTable(importReceiptModels);
    }

    public void loadImportReceiptDetailList(long importReceiptId) {
        List<ImportReceiptDetailModel> importReceiptDetailModels = importReceiptDetailService.findAllByImportReceiptId(importReceiptId);
        importReceiptScreen.showItemDetails(importReceiptDetailModels);
    }

    public void clearImportReceiptDetailsTable() {
        importReceiptScreen.clearImportReceiptDetailsTable();
    }
}
