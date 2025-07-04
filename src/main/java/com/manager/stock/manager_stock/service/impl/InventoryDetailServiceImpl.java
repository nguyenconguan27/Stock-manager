package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.IInventoryDetailDao;
import com.manager.stock.manager_stock.dao.impl.InventoryDetailDaoImpl;
import com.manager.stock.manager_stock.exception.CanNotFoundException;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.InventoryDetailModel;
import com.manager.stock.manager_stock.service.IInventoryDetailService;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class InventoryDetailServiceImpl implements IInventoryDetailService {
    private final IInventoryDetailDao inventoryDetailDao;
    private static InventoryDetailServiceImpl instance;

    private InventoryDetailServiceImpl() {
        inventoryDetailDao = InventoryDetailDaoImpl.getInstance();
    }

    public static InventoryDetailServiceImpl getInstance() {
        if (instance == null) {
            instance = new InventoryDetailServiceImpl();
        }
        return instance;
    }

    @Override
    public HashMap<Long, InventoryDetailModel> findAllByAcademicYearAndProductId(int academicYear, List<Long> productIds) throws DaoException {
        List<InventoryDetailModel> inventoryDetailModels = inventoryDetailDao.findAllByAcademicYearAndProductId(productIds, academicYear);
        return inventoryDetailModels.stream()
                .collect(Collectors.toMap(
                        InventoryDetailModel::getProductId,
                        Function.identity(),
                        (existing, replacement) -> replacement,
                        HashMap::new
                ));
    }

    @Override
    public void save(List<InventoryDetailModel> inventoryDetailModels) throws DaoException {
        inventoryDetailDao.save(inventoryDetailModels);
    }

    @Override
    public void update(List<InventoryDetailModel> inventoryDetailModels) throws DaoException{
        inventoryDetailDao.update(inventoryDetailModels);
    }
}
