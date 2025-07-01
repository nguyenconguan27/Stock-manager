package com.manager.stock.manager_stock.mapper.viewModelMapper;

import com.almasb.fxgl.core.View;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptDetailModelMapper implements ViewModelMapper<ImportReceiptDetailModel, ImportReceiptDetailModelTable> {

    public static final ImportReceiptDetailModelMapper INSTANCE = new ImportReceiptDetailModelMapper();

    @Override
    public ImportReceiptDetailModelTable toViewModel(ImportReceiptDetailModel model) {
        return new ImportReceiptDetailModelTable(
                model.getId(),
                model.getImportReceiptId(),
                model.getProductId(),
                model.getPlannedQuantity(),
                model.getActualQuantity(),
                model.getUnitPrice(),
                model.getTotalPrice(),
                model.getProductName()
        );
    }
}
