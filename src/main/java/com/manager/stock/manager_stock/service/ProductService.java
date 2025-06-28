package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.dao.ProductDao;
import com.manager.stock.manager_stock.model.ProductModel;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ProductService {
    private final ProductDao productDao = new ProductDao();

    public List<ProductModel> getAllProducts() {
        return productDao.getAll();
    }
}
