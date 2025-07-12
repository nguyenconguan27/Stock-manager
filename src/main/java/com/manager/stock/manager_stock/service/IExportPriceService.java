package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.ExportPriceModel;
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
}
