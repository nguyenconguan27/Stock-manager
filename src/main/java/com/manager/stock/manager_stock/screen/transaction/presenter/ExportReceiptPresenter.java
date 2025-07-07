package com.manager.stock.manager_stock.screen.transaction.presenter;

import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ExportReceiptModel;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;
import com.manager.stock.manager_stock.service.IExportReceiptDetailService;
import com.manager.stock.manager_stock.service.IExportReceiptService;
import com.manager.stock.manager_stock.service.impl.ExportReceiptDetailServiceImpl;
import com.manager.stock.manager_stock.service.impl.ExportReceiptServiceImpl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptPresenter {
    private final IExportReceiptService exportReceiptService;
    private final IExportReceiptDetailService exportReceiptDetailService;
    private static ExportReceiptPresenter instance;

    private ExportReceiptPresenter() {
        exportReceiptDetailService = ExportReceiptDetailServiceImpl.getInstance();
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

    public void deleteById(List<Long> exportReceiptIds) throws DaoException {
        exportReceiptService.deleteByIds(exportReceiptIds);
    }

    public List<ExportReceiptModel> findAllExportReceipt(Optional<Integer> academicYear) throws DaoException {
        int academicYearValue = academicYear.orElse(Calendar.getInstance().get(Calendar.YEAR));
        return exportReceiptService.findAllByAcademicYear(academicYearValue);
    }

    public List<ExportReceiptDetailModel> findAllExportReceiptDetailByExportReceipt(long exportReceiptId) throws DaoException {
        return exportReceiptDetailService.findAllByExportReceipt(exportReceiptId);
    }
}
