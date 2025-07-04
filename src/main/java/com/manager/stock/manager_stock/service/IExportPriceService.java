package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.ExportPriceModel;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IExportPriceService {
    double findExportPriceByProductIdAndLastTime(long productId);
    void save(List<ExportPriceModel> exportPriceModels);
}
