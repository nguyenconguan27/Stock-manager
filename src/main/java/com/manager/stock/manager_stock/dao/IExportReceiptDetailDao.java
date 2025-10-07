package com.manager.stock.manager_stock.dao;

import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IExportReceiptDetailDao {
    List<ExportReceiptDetailModel> findAllByExPortReceipt(long exportReceiptId);
    List<ExportReceiptDetailModel> findAllByProductAndMinTime(List<Long> productIds, LocalDateTime minTime);
    List<Long> save(List<ExportReceiptDetailModel> exportReceiptDetailModels, long exportReceiptId);
    void updateExportReceiptDetailPriceByProductAndTimeRange(long newExportPriceId, double newOriginalUnitPrice, LocalDateTime exportPriceTime, long productId, LocalDateTime startDate, LocalDateTime endDate);
    void delete(List<Long> ids);
    void update(List<ExportReceiptDetailModel> exportReceiptDetailModels);
//    double calculateTotalPriceByImportDate(LocalDateTime importDate, LocalDateTime newImportDate);
    double calculateTotalPriceByProductAndTimeRange(LocalDateTime exportPriceTime, LocalDateTime startDate, LocalDateTime endDate, long productId);
    long calculateActualQuantityByProductBetweenImportDates(LocalDateTime startDate, LocalDateTime endDate, long productId);
}
