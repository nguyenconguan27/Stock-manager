package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ProductGroupMapperResultSet;
import com.manager.stock.manager_stock.model.ProductGroup;

import java.util.ArrayList;
import java.util.List;

public class ProductGroupDao extends AbstractDao<ProductGroup>{

    private static ProductGroupDao INSTANCE;

    public ProductGroupDao() {
    }

    public static ProductGroupDao getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ProductGroupDao();
        }
        return INSTANCE;
    }

    public List<ProductGroup> getAll() {
        String sql = "SELECT * FROM product_group";
        return query(sql, new ProductGroupMapperResultSet());
    }

    public ProductGroup add(ProductGroup productGroup) {
        String sql = "INSERT INTO product_group(id, name) OVERRIDING SYSTEM VALUE VALUES(?, ?);";
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] {
                productGroup.getId(), productGroup.getName()
        });
        save(sql, parameters);
        return productGroup;
    }

    public List<ProductGroup> findById(long id) {
        String sql = "SELECT * FROM product_group WHERE id = ?";
        return query(sql, new ProductGroupMapperResultSet(), id);
    }

    public ProductGroup update(ProductGroup productGroup) {
        String sql = "UPDATE product_group SET name = ?";
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{productGroup.getName()});
        save(sql, parameters);
        return productGroup;
    }

    public ProductGroup getGroupAndProducts() {
//        String sql = "SELECT pg.id, pg.name, p.id, p.name, inv.quantity, ep.exportPrice";
        String sql = "selct pg.id, pg.name, p.id, p.name"
    }
}
