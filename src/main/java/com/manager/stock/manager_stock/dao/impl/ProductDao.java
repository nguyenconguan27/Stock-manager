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
        String sql = "select p.id, p.code, p.unit, p.name, p.group_id, inv.quantity, ep.export_price, max(ep.export_time) from product as p" +
                " inner join inventory_detail as inv on inv.product_id = p.id" +
                " inner join export_price as ep on ep.product_id = p.id" +
                " where id = ?" +
                " group by p.id, p.id, p.name, inv.quantity, ep.export_price";
        return query(sql, new ProductMapperResultSet(), id);
    }

    public List<ProductModel> findByCode(String text) {
        String sql = "select p.id, p.code, p.unit, p.name, p.group_id, inv.quantity, ep.export_price, max(ep.export_time) from product as p" +
                " inner join inventory_detail as inv on inv.product_id = p.id" +
                " inner join export_price as ep on ep.product_id = p.id" +
                " where code like ?" +
                " group by p.id, p.id, p.name, inv.quantity, ep.export_price";
        return query(sql, new ProductMapperResultSet(), "%" + text.toLowerCase() + "%");
    }


    public List<ProductModel> getAll() {
        String sql = "select p.id, p.code, p.name, p.group_id, (select inv.quantity from test.inventory_detail as inv\n" +
                "inner join test.product p on p.id = inv.product_id order by inv.academic_year desc limit 1) as quantity, (select ep.export_price from test.export_price as ep\n" +
                "inner join test.product p on p.id = ep.product_id order by ep.export_time desc limit 1) as export_price from test.product as p";
        return query(sql, new ProductMapperResultSet());
    }

    public List<ProductModel> findByName(String text) {
        String sql = "select p.id, p.code, p.unit, p.name, p.group_id, inv.quantity, ep.export_price, max(ep.export_time) from product as p" +
                " inner join inventory_detail as inv on inv.product_id = p.id" +
                " inner join export_price as ep on ep.product_id = p.id" +
                " where lower(name) like ?" +
                " group by p.id, p.id, p.name, inv.quantity, ep.export_price";
        return query(sql, new ProductMapperResultSet(), "%" + text.toLowerCase() + "%");
    }

    public List<ProductModel> findByGroup(long groupId) {
        String sql = "select p.id, p.code, p.unit, p.name, p.group_id, inv.quantity, ep.export_price, max(ep.export_time) from product as p" +
                " inner join inventory_detail as inv on inv.product_id = p.id" +
                " inner join export_price as ep on ep.product_id = p.id" +
                " inner join product_group as pg on pg.id = p.group_id" +
                " where pg.id = ?" +
                " group by p.id, p.id, p.name, inv.quantity, ep.export_price";
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
