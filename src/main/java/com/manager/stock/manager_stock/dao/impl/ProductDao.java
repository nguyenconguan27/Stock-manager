package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ProductMapperResultSet;
import com.manager.stock.manager_stock.model.ProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ProductDao extends AbstractDao<ProductModel>{
    public static ProductDao INSTANCE;

    public static ProductDao getInstance() {
        if (INSTANCE == null) {
            return new ProductDao();
        }
        return INSTANCE;
    }

    public ProductDao() {
    }

    public List<ProductModel> findById(long id) {
        String sql = "SELECT * FROM product WHERE id = ?";
        return query(sql, new ProductMapperResultSet(), id);
    }

    public List<ProductModel> findByCode(String text) {
        String sql = "SELECT * FROM product WHERE code = ?";
        return query(sql, new ProductMapperResultSet(), "%" + text.toLowerCase() + "%");
    }


    public List<ProductModel> getAll() {
        String sql = "SELECT * FROM product";
        return query(sql, new ProductMapperResultSet());
    }

    public List<ProductModel> findByName(String text) {
        String sql = "SELECT * FROM product WHERE LOWER(name) like ?";
        return query(sql, new ProductMapperResultSet(), "%" + text.toLowerCase() + "%");
    }

    public List<ProductModel> findByGroup(long groupId) {
        String sql = "SELECT p.id, p.name, p.unit_price, p.unit, p.quantity FROM product AS p" +
                " INNER JOIN product_group as pg ON p.group_id = pg.id " +
                " WHERE pg.id = ?";
        return  query(sql, new ProductMapperResultSet(), groupId);
    }

    public ProductModel add(ProductModel p, long groupId) {
        String sql = "INSERT INTO product(id, code, name, quantity, unit, group_id) " +
                " OVERRIDING SYSTEM VALUE values(?, ?, ?, ?, ?, ?)";
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] {
                p.getId(), p.getCode(), p.getName(), p.getQuantity(), p.getUnit(), groupId
        });
        save(sql, parameters);
        return p;
    }

    public ProductModel update(ProductModel p, long groupId, boolean isUpdateCode) {
        String sql1 = "UPDATE product SET code = ?, name = ?, unit = ?, unit_price = ?, group_id = ?" +
                " WHERE id = ?";
        String sql2 = "UPDATE product SET name = ?, unit = ?, unit_price = ?, group_id = ?" +
                " WHERE id = ?";
        String sql;
        List<Object[]> parameters = new ArrayList<>();
        if(isUpdateCode) {
            parameters.add(new Object[] {
                    p.getCode(), p.getName(), p.getUnit(), p.getUnitPrice(), groupId, p.getId()
            });
            sql = sql1;
        }
        else {
            parameters.add(new Object[] {
                    p.getName(), p.getUnit(),  p.getUnitPrice(), groupId, p.getId()
            });
            sql = sql2;
        }
        save(sql, parameters);
        return p;
    }

}
