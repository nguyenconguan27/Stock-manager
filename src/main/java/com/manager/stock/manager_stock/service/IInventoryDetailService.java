package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.InventoryDetailModel;
import com.manager.stock.manager_stock.model.dto.ProductIdAndCodeAndNameAndQuantityInStock;

import java.util.HashMap;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IInventoryDetailService {
    HashMap<Long, InventoryDetailModel> findAllByAcademicYearAndProductId(int academicYear, List<Long> productIds);
    void save(List<InventoryDetailModel> inventoryDetailModels);
    void update(List<InventoryDetailModel> inventoryDetailModels);
    int findQuantityInStockByProductIdAndAcademicYear(long productId, int academicYear);
    List<ProductIdAndCodeAndNameAndQuantityInStock> findProductHaveMaxQuantityByProductGroup(long productGroupId);
    List<ProductIdAndCodeAndNameAndQuantityInStock> findProductHaveMinQuantityByProductGroup(long productGroupId);
}
