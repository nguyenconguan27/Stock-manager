package com.manager.stock.manager_stock.model;

import java.util.List;

public class ReceiptModel {

    public ReceiptModel() {
    }

    public ReceiptModel(String createAt, Long id) {
        this.createAt = createAt;
        this.id = id;
    }
    protected String createAt;

    protected Long id;

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return ((ReceiptModel) obj).getId().equals(this.id);
    }
}
