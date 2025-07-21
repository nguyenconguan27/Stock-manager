package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.impl.ProductDao;
import com.manager.stock.manager_stock.dao.impl.ProductGroupDao;
import com.manager.stock.manager_stock.model.ProductGroup;
import com.manager.stock.manager_stock.service.ProductGroupService;
import com.manager.stock.manager_stock.service.ProductService;

import java.util.List;

public class ProductGroupServiceImpl implements ProductGroupService {
    public static ProductGroupServiceImpl INSTANCE;
    private final ProductGroupDao productGroupDao;

    public ProductGroupServiceImpl() {
        productGroupDao = ProductGroupDao.getInstance();
    }

    public static ProductGroupServiceImpl getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ProductGroupServiceImpl();
        }
        return INSTANCE;
    }

    @Override
    public List<ProductGroup> getAll() {
        return productGroupDao.getAll();
    }

    @Override
    public void save(ProductGroup productGroup) {
        productGroupDao.add(productGroup);
    }

    @Override
    public ProductGroup getById(long id) {
        List<ProductGroup> productGroups = productGroupDao.findById(id);
        if(productGroups.isEmpty()) {
            return null;
        }
        return productGroups.get(0);
    }
}
