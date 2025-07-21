package com.manager.stock.manager_stock.mapper.viewModelMapper;

import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;
import com.manager.stock.manager_stock.model.tableData.ExportReceiptDetailModelTable;
import com.manager.stock.manager_stock.utils.FormatMoney;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptDetailModelTableMapper implements ViewModelMapper<ExportReceiptDetailModel, ExportReceiptDetailModelTable> {
    public static final ExportReceiptDetailModelTableMapper INSTANCE = new ExportReceiptDetailModelTableMapper();

    @Override
    public ExportReceiptDetailModelTable toViewModel(ExportReceiptDetailModel model) {
        return new ExportReceiptDetailModelTable(
                model.getId(),
                model.getExportReceiptId(),
                model.getProductId(),
                model.getPlannedQuantity(),
                model.getActualQuantity(),
                model.getTotalPrice(),
                model.getExportPrice(),
                model.getProductName(),
                FormatMoney.format(model.getExportPrice()),
                FormatMoney.format(model.getExportPrice() * model.getActualQuantity()),
                model.getProductCode(),
                model.getExportPriceId(),
                model.getUnitPrice()
        );
    }

    @Override
    public ExportReceiptDetailModel fromViewModelToModel(ExportReceiptDetailModelTable viewModel) {
        return new ExportReceiptDetailModel(
                viewModel.getId(),
                viewModel.getExportReceiptId(),
                viewModel.getProductId(),
                viewModel.getPlannedQuantity(),
                viewModel.getActualQuantity(),
                viewModel.getUnitPrice(),
                viewModel.getTotalPrice(),
                viewModel.getProductName(),
                viewModel.getProductCode(),
                viewModel.getExportPriceId(),
                -1,
                null,
                -1
        );
    }
}
