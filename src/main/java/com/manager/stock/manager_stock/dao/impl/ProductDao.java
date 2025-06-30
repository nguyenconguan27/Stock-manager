package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.model.ProductModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ProductDao {
    public List<ProductModel> getAll() {
        return Arrays.asList(
                new ProductModel("P001", "Thép", 50),
                new ProductModel("P002", "Gạch", 120),
                new ProductModel("P003", "Cát", 70)
        );
    }
}
