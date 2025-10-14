package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.ExportPriceModel;
import com.manager.stock.manager_stock.model.dto.ExportPriceAndProductCodeAndProductName;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndExportTimeAndExportPrice;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndPrice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IExportPriceService {
    void save(List<ExportPriceModel> exportPriceModels);
    HashMap<Long, List<ExportPriceModel>> findAllByProductAndMinTime(List<Long> productIds, LocalDateTime minTime);
    void update(List<ExportPriceModel> exportPriceModels);
    void updateExportPriceByProductIdAndImportDate(long quantityImport, double totalPriceDifference, double totalPriceImport, LocalDateTime exportTime, long productId);
    void updateExportPriceAfterImportCorrectionByProductIdAndImportDate(double totalPriceChanged, long totalQuantityChange, LocalDateTime oldImportDate, long productId);
    ExportPriceIdAndPrice findExportPriceByProductAndLastTime(long productId);
    HashMap<Long, Double> findPriceById(List<Long> ids);
    ExportPriceAndProductCodeAndProductName findProductHaveMaxPriceByGroup(long productGroupId);
    ExportPriceAndProductCodeAndProductName findProductHaveMinPriceByGroup(long productGroupId);
    ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndMaxTimeByImportDate(long productId, LocalDateTime maxTime, LocalDateTime oldImportDate);
    ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndMinTimeByImportDate(long productId, LocalDateTime minTime, LocalDateTime oldImportDate);
    ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndImportDate(long productId, LocalDateTime importDate);
    List<LocalDateTime> findAllExportTimeByProductAndBetweenImportDates(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime importDate, long productId);
    long calculateTotalQuantityImportAndQuantityInStockByImportDateAndProduct(long productId, LocalDateTime importDate);
    void commit();
    void rollback();
}
