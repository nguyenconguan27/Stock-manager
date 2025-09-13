package com.manager.stock.manager_stock.model;

/**
 * @author Trọng Hướng
 */
public class ProductModel {
    private long id;
    private String name;
    private String code;
    private int quantity;
    private String unit;
    private int unitPrice;
    private String createdAt;
    private int total;
    private long groupId;
    private int startSemQ;
    private int startSemT;


    public ProductModel(long id, String name, Integer quantity, String unit, int unitPrice, String code,
                        long groupId, int total, int startSemQ, int startSemT) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.code = code;
        this.total = total;
        this.startSemQ = startSemQ;
        this.startSemT = startSemT;
        this.groupId = groupId;
    }

    public int getStartSemQ() {
        return startSemQ;
    }

    public int getStartSemT() {
        return startSemT;
    }

    public void setStartSemQ(int startSemQ) {
        this.startSemQ = startSemQ;
    }

    public void setStartSemT(int startSemT) {
        this.startSemT = startSemT;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public ProductModel() {

    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUnit() {
        return unit;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
