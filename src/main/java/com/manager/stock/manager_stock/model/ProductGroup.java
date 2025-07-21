package com.manager.stock.manager_stock.model;

public class ProductGroup {
    private long id;
    private String name;

    public ProductGroup(long id, String name) {
        this.id = id;
        this.name = name;
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
