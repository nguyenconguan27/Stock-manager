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
    ExportPriceIdAndPrice findExportPriceByProductAndLastTime(long productId);
    HashMap<Long, Double> findPriceById(List<Long> ids);
    ExportPriceAndProductCodeAndProductName findProductHaveMaxPriceByGroup(long productGroupId);
    ExportPriceAndProductCodeAndProductName findProductHaveMinPriceByGroup(long productGroupId);
    ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndMaxTimeByImportDate(long productId, LocalDateTime maxTime);
    ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndMinTimeByImportDate(long productId, LocalDateTime minTime);
    ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndImportDate(long productId, LocalDateTime importDate);
    List<LocalDateTime> findAllExportTimeByProductAndBetweenImportDates(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime importDate, long productId);
    long calculateTotalQuantityImportAndQuantityInStockByImportDateAndProduct(long productId, LocalDateTime importDate);
    void commit();
    void rollback();
}
