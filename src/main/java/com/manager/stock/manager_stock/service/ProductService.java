package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.ProductModel;

import java.util.List;

public interface ProductService {
    List<ProductModel> getAllProducts();

    List<ProductModel> getByName(String text);

    List<ProductModel> getByGroup(String groupId);
}
