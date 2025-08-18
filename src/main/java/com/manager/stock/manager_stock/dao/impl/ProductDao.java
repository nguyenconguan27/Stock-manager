package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ProductMapperResultSet;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.model.dto.QuantityAndTotal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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


    public QuantityAndTotal getQuantityAndTotal(ProductModel product, int year) {
        String sqlQuantity = "select inv.quantity, inv.total_price from inventory_detail as inv " +
                "inner join product as p on p.id = inv.product_id " +
                "where p.id = ? and inv.academic_year = ? order by inv.academic_year desc limit 1";
        List<QuantityAndTotal> quantityAndTotal = query(sqlQuantity, rs -> new QuantityAndTotal(rs.getInt("quantity"),
                rs.getInt("total_price")), product.getId(), year);
        return quantityAndTotal.isEmpty() ? new QuantityAndTotal(0, 0) : quantityAndTotal.get(0);
    }

    public int getPrice(ProductModel product) {
        String sqlPrice = "select ep.export_price from export_price as ep " +
                "inner join product as p on p.id = ep.product_id " +
                "where p.id = ? order by ep.export_time desc limit 1";
        List<Integer> price = query(sqlPrice, rs -> rs.getInt("export_price"), product.getId());
        return price.isEmpty() ? 0 : price.get(0);
    }

    public List<ProductModel> findById(long id) {
        int year = LocalDate.now().getYear();
        String sqlProduct = "select p.id, p.code, p.name, p.unit, p.group_id from product as p where p.id = ?";
        List<ProductModel> productList  = query(sqlProduct, new ProductMapperResultSet(), id);
        for(ProductModel product: productList) {
            QuantityAndTotal now = getQuantityAndTotal(product, year);
            product.setTotal(now.total());
            product.setQuantity(now.quantity());
            QuantityAndTotal start = getQuantityAndTotal(product, year - 1);
            product.setStartSemQ(start.quantity());
            product.setStartSemT(start.total());
            product.setUnitPrice(getPrice(product));
        }
        return productList;
    }

    public List<ProductModel> findByCode(String text) {
        String sqlProduct = "select p.id, p.code, p.unit, p.name, p.group_id from product as p where lower(p.code) like ?";
        String sqlPrice = "select ep.export_price from export_price as ep " +
                "inner join product as p on p.id = ep.product_id " +
                "where p.id = ? order by ep.export_time desc limit 1";
        String sqlQuantity = "select inv.quantity from inventory_detail as inv " +
                "inner join product as p on p.id = inv.product_id " +
                "where p.id = ? order by inv.academic_year desc limit 1";
        List<ProductModel> productList  = query(sqlProduct, new ProductMapperResultSet(), "%" + text.toLowerCase() + "%");
        for(ProductModel product: productList) {
            int price = query(sqlPrice, rs -> rs.getInt("export_price"), product.getId()).get(0);
            int quantity = query(sqlQuantity, rs -> rs.getInt("quantity"), product.getId()).get(0);
            product.setUnitPrice(price);
            product.setQuantity(quantity);
        }
        return productList;
    }


    public List<ProductModel> getAll() {
        int year = LocalDate.now().getYear();
        String sqlProduct = "select p.id, p.code, p.name, p.group_id from product as p";
        List<ProductModel> productList  = query(sqlProduct, new ProductMapperResultSet());
        for(ProductModel product: productList) {
            QuantityAndTotal now = getQuantityAndTotal(product, year);
            product.setTotal(now.total());
            product.setQuantity(now.quantity());
            QuantityAndTotal start = getQuantityAndTotal(product, year - 1);
            product.setStartSemQ(start.quantity());
            product.setStartSemT(start.total());
            product.setUnitPrice(getPrice(product));
        }
        return productList;
    }

    public List<ProductModel> findByName(String text) {
        String sqlProduct = "select p.id, p.code, p.name, p.unit, p.group_id from product as p where lower(p.name) like ?";
        String sqlPrice = "select ep.export_price from export_price as ep " +
                "inner join product as p on p.id = ep.product_id " +
                "where p.id = ? order by ep.export_time desc limit 1";
        String sqlQuantity = "select inv.quantity from inventory_detail as inv " +
                "inner join product as p on p.id = inv.product_id " +
                "where p.id = ? order by inv.academic_year desc limit 1";
        List<ProductModel> productList  = query(sqlProduct, new ProductMapperResultSet(), "%" + text.toLowerCase() + "%");
        for(ProductModel product: productList) {
            int price = query(sqlPrice, rs -> rs.getInt("export_price"), product.getId()).get(0);
            int quantity = query(sqlQuantity, rs -> rs.getInt("quantity"), product.getId()).get(0);
            product.setUnitPrice(price);
            product.setQuantity(quantity);
        }
        return productList;
    }

    public List<ProductModel> findByGroup(long groupId) {
        String sqlProduct = "select p.id, p.code, p.name, p.unit, p.group_id from product as p where p.group_id = ?";
        String sqlPrice = "select ep.export_price from export_price as ep " +
                "inner join product as p on p.id = ep.product_id " +
                "where p.id = ? order by ep.export_time desc limit 1";
        String sqlQuantity = "select inv.quantity from inventory_detail as inv " +
                "inner join product as p on p.id = inv.product_id " +
                "where p.id = ? order by inv.academic_year desc limit 1";
        List<ProductModel> productList  = query(sqlProduct, new ProductMapperResultSet(), groupId);
        for(ProductModel product: productList) {
            int price = query(sqlPrice, rs -> rs.getInt("export_price"), product.getId()).get(0);
            int quantity = query(sqlQuantity, rs -> rs.getInt("quantity"), product.getId()).get(0);
            product.setUnitPrice(price);
            product.setQuantity(quantity);
        }
        return productList;
    }

    public ProductModel add(ProductModel p, long groupId) {
        String sql = "INSERT INTO product(id, code, name, unit, group_id) " +
                " OVERRIDING SYSTEM VALUE values(?, ?, ?, ?, ?)";
        String sqlInv = "insert into inventory_detail(id, product_id, quantity, total_price, academic_year) " +
                "OVERRIDING SYSTEM VALUE values(?, ?, ?, ?, ?)";
        String sqlPrice = "insert into export_price(id, product_id, export_time, export_price, quantity_in_stock, quantity_imported, " +
                "total_price_in_stock, total_price_import) " +
                "OVERRIDING SYSTEM VALUE values(?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> productParams = new ArrayList<>();
        List<Object[]> invParams = new ArrayList<>();
        List<Object[]> priceParams = new ArrayList<>();
        productParams.add(new Object[] {
                p.getId(), p.getCode(), p.getName(), p.getUnit(), groupId
        });
        save(sql, productParams);
        invParams.add(new Object[] {
                System.currentTimeMillis(), p.getId(), p.getQuantity(), p.getUnitPrice() * p.getQuantity(), new Date().getYear() - 1
        });
        save(sqlInv, invParams);
        priceParams.add(new Object[] {
                System.currentTimeMillis(), p.getId(), LocalDateTime.now(), p.getUnitPrice(), p.getQuantity(), 0, p.getUnitPrice() * p.getQuantity(), 0
        });
        save(sqlPrice, priceParams);
        return p;
    }

    public ProductModel update(ProductModel p, long groupId, boolean isUpdateCode) {
        String sql1 = "UPDATE product SET code = ?, name = ?, unit = ?, group_id = ?" +
                " WHERE id = ?";
        String sql2 = "UPDATE product SET name = ?, unit = ?, group_id = ?" +
                " WHERE id = ?";
        String sql;
        List<Object[]> parameters = new ArrayList<>();
        if(isUpdateCode) {
            parameters.add(new Object[] {
                    p.getCode(), p.getName(), p.getUnit(), groupId, p.getId()
            });
            sql = sql1;
        }
        else {
            parameters.add(new Object[] {
                    p.getName(), p.getUnit(), groupId, p.getId()
            });
            sql = sql2;
        }
        save(sql, parameters);
        return p;
    }

}
