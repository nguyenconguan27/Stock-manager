package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.impl.ProductDao;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    public static ProductServiceImpl INSTANCE;
    public final ProductDao productDao;
    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

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
    public ProductModel getByCode(String text) {
        List<ProductModel> productModels = productDao.findByCode(text);
        if(productModels.isEmpty()) {
            return null;
        }
        return productModels.get(0);
    }

    @Override
    public List<ProductModel> getByGroup(long groupId) {
        return productDao.findByGroup(groupId);
    }

    @Override
    public ProductModel getById(long id) {
        List<ProductModel> productModels = productDao.findById(id);
        if(productModels.isEmpty()) {
            return new ProductModel();
        }
        return productModels.get(0);
    }

    @Override
    public void add(ProductModel productModel, long groupId) {
        productDao.add(productModel, groupId);
    }

    @Override
    public void update(ProductModel productModel, long groupId, boolean isUpdateCode) {
        productDao.update(productModel, groupId, isUpdateCode);
    }
}
