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
    void updateExportPriceByImportDate(LocalDateTime importDate, long exportPriceId, LocalDateTime newImportDate, long newExportPrice);
    void delete(List<Long> ids);
    void update(List<ExportReceiptDetailModel> exportReceiptDetailModels);
    double calculateTotalPriceByImportDate(LocalDateTime importDate, LocalDateTime newImportDate);
    double calculateTotalPriceByImportDateAndBetweenDates(LocalDateTime importDate, LocalDateTime startDate, LocalDateTime endDate);
}
