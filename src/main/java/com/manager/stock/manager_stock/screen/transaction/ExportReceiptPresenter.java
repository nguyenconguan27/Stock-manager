package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;
import com.manager.stock.manager_stock.service.IExportReceiptService;
import com.manager.stock.manager_stock.service.impl.ExportReceiptServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptPresenter {
    private final IExportReceiptService exportReceiptService;
    private static ExportReceiptPresenter instance;

    private ExportReceiptPresenter() {
        exportReceiptService = ExportReceiptServiceImpl.getInstance();
    }

    public static ExportReceiptPresenter getInstance() {
        if(instance == null) {
            instance = new ExportReceiptPresenter();
        }
        return instance;
    }

    public HashMap<Long, ProductIdAndActualQuantityAndTotalPriceOfReceipt> findProductIdAndActualQuantityAndTotalPriceOfReceipt(List<Long> exportReceiptIds) throws DaoException {
        List<ProductIdAndActualQuantityAndTotalPriceOfReceipt> productIdAndActualQuantityAndTotalPriceOfReceipts = exportReceiptService.findProductIdAndTotalPriceAndTotalQuantityByExportReceipt(exportReceiptIds);
        return productIdAndActualQuantityAndTotalPriceOfReceipts.stream()
                .collect(Collectors.toMap(
                   ProductIdAndActualQuantityAndTotalPriceOfReceipt::productId,
                   Function.identity(),
                    (existing, replacement) -> replacement,
                    HashMap::new
                ));
    }
}
