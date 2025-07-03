package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.model.ProductModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ProductDao {
    public static ProductDao INSTANCE;

    public static ProductDao getInstance() {
        if (INSTANCE == null) {
            return new ProductDao();
        }
        return INSTANCE;
    }

    public ProductDao() {
    }

    public List<ProductModel> getAll() {
        return Arrays.asList(
                new ProductModel(1, "P001", "Thép", 50, "cái", 100),
                new ProductModel(2, "P002", "Gạch", 120, "cái", 100),
                new ProductModel(3, "P003", "Cát", 70, "cái", 23)
        );
    }

    public List<ProductModel> findByName(String text) {
        return Arrays.asList(
                new ProductModel(1, "P001", "Thép", 50, "cái", 100)
        );
    }

    public List<ProductModel> findByGroup(String groupId) {
        return Arrays.asList(
                new ProductModel(1, "P001", "Thép", 50, "cái", 100),
                new ProductModel(3, "P003", "Cát", 70, "cái", 23)
        );
    }
}
