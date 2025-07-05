package com.manager.stock.manager_stock.dao;

import com.manager.stock.manager_stock.model.dto.ExportReceiptIdAndCreateDate;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IExportReceiptDao {
    List<ExportReceiptIdAndCreateDate> findExportReceiptIdAndCreatedAtByProductIdsAndYearAndMinCreatedAt(List<Long> productIds, int academicYear, LocalDateTime minCreateAt);
    List<ProductIdAndActualQuantityAndTotalPriceOfReceipt> findProductIdAndTotalPriceAndTotalQuantityByExportReceipt(List<Long> exportReceiptIds);
    void deleteByIds(List<Long> ids);
}
