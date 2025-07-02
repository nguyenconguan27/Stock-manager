package com.manager.stock.manager_stock.mapper.viewModelMapper;

import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptModelMapper implements ViewModelMapper<ImportReceiptModel, ImportReceiptModelTable> {

    public static final ImportReceiptModelMapper INSTANCE = new ImportReceiptModelMapper();

    @Override
    public ImportReceiptModelTable toViewModel(ImportReceiptModel model) {
        return new ImportReceiptModelTable(
                model.getId(),
                model.getInvoiceNumber(), // số phiếu ==> mã phiếu nhập
                model.getCreateAt(),
                model.getDeliveredBy(),
                model.getInvoice(),
                model.getCompanyName(),
                model.getWarehouseName(),
                model.getTotalPrice()
        );
    }

    @Override
    public ImportReceiptModel fromViewModelToModel(ImportReceiptModelTable viewModel) {
        return null;
    }
}
