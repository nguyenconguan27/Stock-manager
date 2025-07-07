package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.ExportReceiptModel;
import com.manager.stock.manager_stock.model.dto.ExportReceiptIdAndCreateDate;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;

import java.util.HashMap;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IExportReceiptService {
    List<ExportReceiptIdAndCreateDate> findExportReceiptIdAndCreatedAtByProductIdsAndYearAndMinCreatedAt(List<Long> productIds, int academicYear, String minCreateAt);
    List<ProductIdAndActualQuantityAndTotalPriceOfReceipt> findProductIdAndTotalPriceAndTotalQuantityByExportReceipt(List<Long> exportReceiptIds);
    void deleteByIds(List<Long> ids);
    List<ExportReceiptModel> findAllByAcademicYear(int academicYear);
}
