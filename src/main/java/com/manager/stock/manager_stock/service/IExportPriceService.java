package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.ExportPriceModel;
import com.manager.stock.manager_stock.model.dto.ExportPriceAndProductCodeAndProductName;
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
    ExportPriceModel findByProductAndLastTime(Long productId, LocalDateTime time);
    ExportPriceModel findByProductAndMinTime(Long productId, LocalDateTime time);
    void update(List<ExportPriceModel> exportPriceModels);
    ExportPriceIdAndPrice findExportPriceByProductAndLastTime(long productId);
    long save(ExportPriceModel exportPriceModel);
    HashMap<Long, Double> findPriceById(List<Long> ids);
    ExportPriceAndProductCodeAndProductName findProductHaveMaxPriceByGroup(long productGroupId);
    ExportPriceAndProductCodeAndProductName findProductHaveMinPriceByGroup(long productGroupId);
    ExportPriceModel findByProductIdAndBeforeTime(long productId, LocalDateTime time);
    List<ExportPriceModel> findAllByProductIdAndAfterTime(long productId, LocalDateTime time);
    void commit();
    void rollback();
}
