package com.manager.stock.manager_stock.model;

import java.util.List;

public class ProductGroup {
    private long id;
    private String name;
    private List<ProductModel> productList;

    public ProductGroup(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setProductList(List<ProductModel> productList) {
        this.productList = productList;
    }

    public List<ProductModel> getProductList() {
        return productList;
    }

    public ProductGroup() {

    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
