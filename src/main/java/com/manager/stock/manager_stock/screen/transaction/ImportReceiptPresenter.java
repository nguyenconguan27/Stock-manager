package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptDetailModelMapper;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;
import com.manager.stock.manager_stock.service.IImportReceiptDetailService;
import com.manager.stock.manager_stock.service.IImportReceiptService;
import com.manager.stock.manager_stock.service.ProductService;
import com.manager.stock.manager_stock.service.impl.ImportReceiptDetailServiceImpl;
import com.manager.stock.manager_stock.service.impl.ImportReceiptServiceImpl;
import com.manager.stock.manager_stock.service.impl.ProductServiceImpl;
import com.manager.stock.manager_stock.utils.FormatMoney;
import com.manager.stock.manager_stock.utils.GenericConverterBetweenModelAndTableData;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        List<ImportReceiptDetailModel> importReceiptDetailModels = importReceiptDetailService.findAllByImportReceiptId(importReceiptId);
        importReceiptDetailModels
                .forEach(importReceiptDetailModel -> {
                    System.out.println(importReceiptDetailModel);
                   importReceiptDetailModel.setUnitPriceFormat(FormatMoney.format(importReceiptDetailModel.getUnitPrice()));
                   importReceiptDetailModel.setTotalPriceFormat(FormatMoney.format(importReceiptDetailModel.getTotalPrice()));
                });
        return importReceiptDetailModels;
    }

    public List<ProductModel> loadAllProduct() {
        return productService.getAllProducts();
    }

    public ImportReceiptModel saveImportReceipt(ImportReceiptModel importReceiptModel, ObservableList<ImportReceiptDetailModelTable> importReceiptDetailModelsTable) throws DaoException{
        long importReceiptId = importReceiptService.save(importReceiptModel);
        importReceiptModel.setId(importReceiptId);
        List<ImportReceiptDetailModel> importReceiptDetailModels = GenericConverterBetweenModelAndTableData.convertToListModel(importReceiptDetailModelsTable, ImportReceiptDetailModelMapper.INSTANCE::fromViewModelToModel);
        try {
            importReceiptDetailService.save(importReceiptDetailModels, importReceiptId);
        }
        // rollback receipt(Xóa receipt khi lưu thành công) khi thêm mới chi tiết bị lỗi
        catch (Exception e) {
            importReceiptService.delete(importReceiptId);
            throw e;
        }
        return importReceiptModel;
    }

    public void updateImportReceipt(ImportReceiptModel importReceiptModel, ImportReceiptModel oldImportReceiptModel, List<ImportReceiptDetailModelTable> importReceiptDetailModelTables) throws DaoException {
        importReceiptService.update(importReceiptModel);
        List<ImportReceiptDetailModelTable> newImportReceiptDetailModelTable = new ArrayList<>();
        List<ImportReceiptDetailModelTable> editImportReceiptDetailModelTable = new ArrayList<>();

        for(ImportReceiptDetailModelTable importReceiptDetailModelTable : importReceiptDetailModelTables) {
            if(importReceiptDetailModelTable.getId() == -1) {
                newImportReceiptDetailModelTable.add(importReceiptDetailModelTable);
            }
            else {
                editImportReceiptDetailModelTable.add(importReceiptDetailModelTable);
            }
        }

        List<ImportReceiptDetailModel> newImportReceiptDetailModel = GenericConverterBetweenModelAndTableData.convertToListModel(newImportReceiptDetailModelTable,
                ImportReceiptDetailModelMapper.INSTANCE::fromViewModelToModel);
        List<ImportReceiptDetailModel> editImportReceiptDetailModel = GenericConverterBetweenModelAndTableData.convertToListModel(editImportReceiptDetailModelTable,
                ImportReceiptDetailModelMapper.INSTANCE::fromViewModelToModel);

        try {
            if(!newImportReceiptDetailModel.isEmpty()) {
                importReceiptDetailService.save(newImportReceiptDetailModel, importReceiptModel.getId());
            }
            else if(!editImportReceiptDetailModel.isEmpty()) {
                System.out.println(editImportReceiptDetailModel.size());
                importReceiptDetailService.update(editImportReceiptDetailModel);
            }
        }
        // rollback receipt khi cập nhật chi tiết bị lỗi
        catch (Exception e) {
            System.out.println(oldImportReceiptModel);
            importReceiptService.update(oldImportReceiptModel);
            throw e;
        }
    }
}
