package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptDetailModelMapper;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.service.IImportReceiptDetailService;
import com.manager.stock.manager_stock.service.IImportReceiptService;
import com.manager.stock.manager_stock.service.ProductService;
import com.manager.stock.manager_stock.service.impl.ImportReceiptDetailServiceImpl;
import com.manager.stock.manager_stock.service.impl.ImportReceiptServiceImpl;
import com.manager.stock.manager_stock.service.impl.ProductServiceImpl;
import com.manager.stock.manager_stock.utils.GenericConverterBetweenModelAndTableData;
import javafx.collections.ObservableList;

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

    public List<ImportReceiptModel> loadImportReceiptList() throws DaoException {
        return importReceiptService.findAll();
    }

    public List<ImportReceiptDetailModel> loadImportReceiptDetailList(long importReceiptId) {
        return importReceiptDetailService.findAllByImportReceiptId(importReceiptId);
    }

    public List<ProductModel> loadAllProduct() {
        return productService.getAllProducts();
    }

    public ImportReceiptModel saveImportReceipt(ImportReceiptModel importReceiptModel, ObservableList<ImportReceiptDetailModelTable> importReceiptDetailModelsTable) throws DaoException{
        long importReceiptId = importReceiptService.save(importReceiptModel);
        importReceiptModel.setId(importReceiptId);
        List<ImportReceiptDetailModel> importReceiptDetailModels = GenericConverterBetweenModelAndTableData.convertToListModel(importReceiptDetailModelsTable, ImportReceiptDetailModelMapper.INSTANCE::fromViewModelToModel);
        importReceiptDetailService.save(importReceiptDetailModels, importReceiptId);
        return importReceiptModel;
    }
}
