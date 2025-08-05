package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.ProductModel;

import java.util.List;

public interface ProductService {
    List<ProductModel> getAllProducts();

    List<ProductModel> getByName(String text);
    ProductModel getByCode(String text);

    List<ProductModel> getByGroup(long groupId);

    ProductModel getById(long id);
    void add(ProductModel productModel, long groupId);

    void update(ProductModel productModel, long groupId, boolean isUpdateCode);
}
