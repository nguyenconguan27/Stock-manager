package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.ProductGroup;

import java.util.List;

public interface ProductGroupService {
    List<ProductGroup> getAll();
    void save(ProductGroup productGroup);
    ProductGroup getById(long id);

    ProductGroup getGroupAndProduct();
    void commit();
    void rollback();
}
