package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.model.ProductGroup;

import java.util.ArrayList;
import java.util.List;

public class ProductGroupDao {

    private static ProductGroupDao INSTANCE;

    public ProductGroupDao() {
    }

    public static ProductGroupDao getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ProductGroupDao();
        }
        return INSTANCE;
    }

    public List<ProductGroup> getAll() {
        List<ProductGroup> productGroups = new ArrayList<>();
        productGroups.add(new ProductGroup("1", "điên"));
        productGroups.add(new ProductGroup("2", "nước"));
        productGroups.add(new ProductGroup("3", "lửa"));
        return productGroups;
    }
}
