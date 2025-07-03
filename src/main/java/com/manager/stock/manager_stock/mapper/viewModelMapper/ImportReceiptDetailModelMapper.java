package com.manager.stock.manager_stock.mapper.viewModelMapper;

import com.almasb.fxgl.core.View;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.utils.FormatMoney;

import java.text.Format;

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
                model.getProductName(),
                FormatMoney.format(model.getUnitPrice()),
                FormatMoney.format(model.getTotalPrice()),
                model.getProductCode()
        );
    }

    @Override
    public ImportReceiptDetailModel fromViewModelToModel(ImportReceiptDetailModelTable viewModel) {
        return new ImportReceiptDetailModel(
                viewModel.getId(),
                viewModel.getImportReceiptId(),
                viewModel.getProductId(),
                viewModel.getPlannedQuantity(),
                viewModel.getActualQuantity(),
                viewModel.getUnitPrice(),
                viewModel.getTotalPrice(),
                viewModel.getProductName(),
                viewModel.getProductCode()
        );
    }
}
