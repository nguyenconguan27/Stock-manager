package com.manager.stock.manager_stock.mapper.viewModelMapper;

import com.manager.stock.manager_stock.model.ExportReceiptModel;
import com.manager.stock.manager_stock.model.tableData.ExportReceiptModelTable;
import com.manager.stock.manager_stock.utils.FormatMoney;

import java.text.Format;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptModelTableMapper implements ViewModelMapper<ExportReceiptModel, ExportReceiptModelTable> {

    public static final ExportReceiptModelTableMapper INSTANCE = new ExportReceiptModelTableMapper();

    @Override
    public ExportReceiptModelTable toViewModel(ExportReceiptModel model) {
        return new ExportReceiptModelTable(
                model.getId(),
                model.getInvoiceNumber(),
                model.getCreateAt(),
                model.getReceiver(),
                model.getReceiveAddress(),
                model.getReason(),
                model.getWareHouse(),
                model.getTotalPrice(),
                model.getTotalPriceInWord(),
                FormatMoney.format(model.getTotalPrice())
        );
    }

    @Override
    public ExportReceiptModel fromViewModelToModel(ExportReceiptModelTable viewModel) {
        return new ExportReceiptModel(
                viewModel.getId(),
                viewModel.getInvoiceNumber(),
                viewModel.getCreateAt(),
                viewModel.getReceiver(),
                viewModel.getReceiveAddress(),
                viewModel.getReason(),
                viewModel.getWareHouse(),
                viewModel.getTotalPrice(),
                viewModel.getTotalPriceInWord()
        );
    }
}
