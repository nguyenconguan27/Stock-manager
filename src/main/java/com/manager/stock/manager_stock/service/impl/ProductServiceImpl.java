package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.impl.ProductDao;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.service.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    public static ProductServiceImpl INSTANCE;
    public final ProductDao productDao;

    public ProductServiceImpl() {
        productDao = ProductDao.getInstance();
    }
    public static ProductService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ProductServiceImpl();
        }
        return INSTANCE;
    }

    @Override
    public List<ProductModel> getAllProducts() {
        return productDao.getAll();
    }

    @Override
    public List<ProductModel> getByName(String text) {
        return productDao.findByName(text);
    }

    @Override
    public List<ProductModel> getByGroup(String groupId) {
        return productDao.findByGroup(groupId);
    }
}
