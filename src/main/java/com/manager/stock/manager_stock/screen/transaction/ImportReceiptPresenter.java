package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.service.IImportReceiptService;
import com.manager.stock.manager_stock.service.impl.ImportReceiptServiceImpl;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptPresenter {
    private final ImportReceiptScreen importReceiptScreen;
    private final IImportReceiptService importReceiptService;

    public ImportReceiptPresenter() {
        importReceiptScreen = new ImportReceiptScreen();
        importReceiptService = ImportReceiptServiceImpl.getInstance();
    }

    public void loadImportReceiptList() {
        List<ImportReceiptModel> importReceiptModelList = importReceiptService.findAll();
        for(ImportReceiptModel importReceiptModel : importReceiptModelList){
            System.out.println(importReceiptModel);
        }
        importReceiptScreen.showTable(importReceiptModelList);
    }
}
