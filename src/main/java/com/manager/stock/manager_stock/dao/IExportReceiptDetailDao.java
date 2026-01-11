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
    List<ExportReceiptDetailModel> findByRangeTime(long productId, LocalDateTime start, LocalDateTime end);
    void updateExportPriceId(long exportPriceId, long preExportPriceId, LocalDateTime time);
    void updateExportReceiptDetailPriceByProductAndTimeRange(long newExportPriceId, double newOriginalUnitPrice, LocalDateTime exportPriceTime, long productId, LocalDateTime startDate, LocalDateTime endDate);
    void delete(List<Long> ids);
    void update(List<ExportReceiptDetailModel> exportReceiptDetailModels);
    void updateUnitPriceOriginByProductAndTimeRangeAndExceptDate(double newOriginalUnitPrice, LocalDateTime exportPriceTime, long productId, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime exceptDate);
    double calculateTotalPriceByProductAndTimeRange(LocalDateTime exportPriceTime, LocalDateTime startDate, LocalDateTime endDate, long productId);
    double calculateTotalPriceByProductAndTimeRangeAndExceptDate(LocalDateTime exportPriceTime, LocalDateTime startDate, LocalDateTime endDate, long productId, LocalDateTime exceptDate);
    long calculateActualQuantityByProductBetweenImportDates(LocalDateTime startDate, LocalDateTime endDate, long productId);
    long calculateTotalQuantityByProductAndTimeRange(LocalDateTime exportPriceTime, LocalDateTime startDate, LocalDateTime endDate, long productId);
    List<ExportReceiptDetailModel> findAllByProduct(long productId);
    List<ExportReceiptDetailModel> findAllByProductIdAndOrderByExportDateAsc(long productId, LocalDateTime createAt);
}
