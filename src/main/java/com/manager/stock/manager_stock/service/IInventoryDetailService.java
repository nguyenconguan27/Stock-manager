package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.dao.impl.InventoryDetailDaoImpl;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.InventoryDetailModel;

import java.util.HashMap;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IInventoryDetailService {
    HashMap<Long, InventoryDetailModel> findAllByAcademicYearAndProductId(int academicYear, List<Long> productIds);
    void save(List<InventoryDetailModel> inventoryDetailModels);
    void update(List<InventoryDetailModel> inventoryDetailModels);
}
