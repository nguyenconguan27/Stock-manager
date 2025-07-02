package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.service.IImportReceiptDetailService;
import com.manager.stock.manager_stock.service.IImportReceiptService;
import com.manager.stock.manager_stock.service.ProductService;
import com.manager.stock.manager_stock.service.impl.ImportReceiptDetailServiceImpl;
import com.manager.stock.manager_stock.service.impl.ImportReceiptServiceImpl;
import com.manager.stock.manager_stock.service.impl.ProductServiceImpl;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptPresenter {
    private final IImportReceiptService importReceiptService;
    private final IImportReceiptDetailService importReceiptDetailService;
    private static ImportReceiptPresenter instance;
    private final ProductService productService;

    private ImportReceiptPresenter() {
        importReceiptService = ImportReceiptServiceImpl.getInstance();
        importReceiptDetailService = ImportReceiptDetailServiceImpl.getInstance();
        productService = new ProductServiceImpl();
    }

    public static ImportReceiptPresenter getInstance() {
        if (instance == null) {
            instance = new ImportReceiptPresenter();
        }
        return instance;
    }

    public List<ImportReceiptModel> loadImportReceiptList() {
        return importReceiptService.findAll();
    }

    public List<ImportReceiptDetailModel> loadImportReceiptDetailList(long importReceiptId) {
        return importReceiptDetailService.findAllByImportReceiptId(importReceiptId);
    }

    public List<ProductModel> findAllProduct() {
        return null;
    }
}
