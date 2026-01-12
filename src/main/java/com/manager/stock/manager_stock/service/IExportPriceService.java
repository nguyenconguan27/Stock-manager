package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ExportPriceModel;
import com.manager.stock.manager_stock.model.dto.ExportPriceAndProductCodeAndProductName;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndExportTimeAndExportPrice;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndPrice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IExportPriceService {
    void save(List<ExportPriceModel> exportPriceModels);
    HashMap<Long, List<ExportPriceModel>> findAllByProductAndMinTime(List<Long> productIds, LocalDateTime minTime);
    ExportPriceModel findByProductAndLastTime(Long productId, LocalDateTime time);
    ExportPriceModel findByProductAndMinTime(Long productId, LocalDateTime time);
    ExportPriceModel findAllByProductAndMinTime(Long productId, LocalDateTime time);
    void update(List<ExportPriceModel> exportPriceModels);
    void update(ExportPriceModel exportPriceModels);
    void updateExportPriceByProductIdAndImportDate(long quantityImport, double totalPriceDifference, double totalPriceImport, LocalDateTime exportTime, long productId);
    void updateExportPriceAfterImportCorrectionByProductIdAndImportDate(double totalPriceChanged, long totalQuantityChange, LocalDateTime oldImportDate, long productId);
    ExportPriceIdAndPrice findExportPriceByProductAndLastTime(long productId);
    ExportPriceIdAndPrice findExportPriceByProductAndLastTime(long productId, LocalDateTime exportDate) throws DaoException;
    long save(ExportPriceModel exportPriceModel);
    HashMap<Long, Double> findPriceById(List<Long> ids);
    ExportPriceAndProductCodeAndProductName findProductHaveMaxPriceByGroup(long productGroupId);
    ExportPriceAndProductCodeAndProductName findProductHaveMinPriceByGroup(long productGroupId);
    ExportPriceModel findByProductIdAndBeforeTime(long productId, LocalDateTime time);
    ExportPriceModel findLastByProductIdAndYear(long productId, int year);
    List<ExportPriceModel> findAllByProductIdAndAfterTime(long productId, LocalDateTime time);
    ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndMaxTimeByImportDate(long productId, LocalDateTime maxTime, LocalDateTime oldImportDate);
    ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndMinTimeByImportDate(long productId, LocalDateTime minTime, LocalDateTime oldImportDate);
    ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndImportDate(long productId, LocalDateTime importDate);
    ExportPriceModel findByProductIdAndMinTimeByDate(long productId, LocalDateTime minTime, LocalDateTime oldImportDate);
    List<ExportPriceModel> findAllExportPriceInfoByProductAndBetweenImportDates(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime importDate, long productId);
    void updateExportPriceByImportTimeAndProduct(long newQuantityInStock, double newTotalPriceInStock, LocalDateTime importDateTime, long productId);
    ExportPriceModel findByProductAndImportDate(long productId, LocalDateTime importDate);
    List<LocalDateTime> findAllExportTimeByProductAndBetweenImportDates(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime importDate, long productId);
    List<LocalDateTime> findAllExportTimeByProductAndMoreThanImportDate(LocalDateTime startDate, LocalDateTime importDate, long productId);
    long calculateTotalQuantityImportAndQuantityInStockByImportDateAndProduct(long productId, LocalDateTime importDate);
    void commit();
    void rollback();

    void delete(long productId, int year);
    List<ExportPriceModel> findByProductAndYear(long productId, int year);
    void deleteByImportReceiptId(long id);
    ExportPriceModel findByProductIdAndImportReceipt(long productId, long receiptId);
}
