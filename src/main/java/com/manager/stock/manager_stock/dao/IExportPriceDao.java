package com.manager.stock.manager_stock.dao;

import com.manager.stock.manager_stock.model.ExportPriceModel;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndPrice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IExportPriceDao {
    long findExportPriceByProductIdAndExportTime(long productId);
    void save(List<ExportPriceModel> exportPriceModels);
    List<ExportPriceModel> findAllByProductAndMinTime(List<Long> productIds, LocalDateTime minTime);
    void update(List<ExportPriceModel> exportPriceModels);
    ExportPriceIdAndPrice findExportPriceIdAndPriceByProductAndLastTime(long productId);
    void updateExportTimeByImportReceiptId(long importReceiptId, LocalDateTime importDate);
}
