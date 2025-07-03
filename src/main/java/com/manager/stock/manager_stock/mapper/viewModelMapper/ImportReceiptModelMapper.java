package com.manager.stock.manager_stock.mapper.viewModelMapper;

import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;
import com.manager.stock.manager_stock.utils.FormatMoney;

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
                model.getTotalPrice(),
                FormatMoney.format(model.getTotalPrice())
        );
    }

    @Override
    public ImportReceiptModel fromViewModelToModel(ImportReceiptModelTable viewModel) {
        return new ImportReceiptModel(
                viewModel.getId(),
                viewModel.getInvoiceNumber(),
                viewModel.getCreateAt(),
                viewModel.getDeliveredBy(),
                viewModel.getCompanyName(),
                viewModel.getInvoice(),
                viewModel.getWarehouseName(),
                viewModel.getTotalPrice(),
                FormatMoney.formatMoneyToWord((long)viewModel.getTotalPrice())
        );
    }
}
