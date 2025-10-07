package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Trọng Hướng
 */
public interface IExportReceiptDetailService {
    List<ExportReceiptDetailModel> findAllByExportReceipt(long exportReceiptId);
    Map<Long, List<ExportReceiptDetailModel>> findAllByProductAndMinTime(List<Long> productIds, LocalDateTime minTime);
    List<Long> save(List<ExportReceiptDetailModel> exportReceiptDetailModels, long exportReceiptId);
//    double calculateExportPriceTotalByImportDate(LocalDateTime importDate, LocalDateTime newImportDate);
    void delete(List<Long> ids);
    void update(List<ExportReceiptDetailModel> exportReceiptDetailModels);
    void updateExportReceiptDetailPriceByProductAndTimeRange(long newExportPriceId, double newOriginalUnitPrice, LocalDateTime exportPriceTime, long productId, LocalDateTime startDate, LocalDateTime endDate);
    double calculateTotalPriceByProductAndTimeRange(LocalDateTime exportPriceTime, LocalDateTime startDate, LocalDateTime endDate, long productId);
    long calculateActualQuantityByProductBetweenImportDates(LocalDateTime startDate, LocalDateTime endDate, long productId);
}
