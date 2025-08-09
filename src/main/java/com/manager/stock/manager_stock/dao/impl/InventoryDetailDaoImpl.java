package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IInventoryDetailDao;
import com.manager.stock.manager_stock.exception.CanNotFoundException;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.InventoryDetailMapperResultSet;
import com.manager.stock.manager_stock.model.InventoryDetailModel;
import com.manager.stock.manager_stock.model.dto.ProductIdAndCodeAndNameAndQuantityInStock;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class InventoryDetailDaoImpl extends AbstractDao<InventoryDetailModel> implements IInventoryDetailDao {
    private static InventoryDetailDaoImpl instance;
    public static InventoryDetailDaoImpl getInstance() {
        if (instance == null) {
            instance = new InventoryDetailDaoImpl();
        }
        return instance;
    }

    @Override
    public List<InventoryDetailModel> findAllByAcademicYearAndProductId(List<Long> productIds, int academicYear) throws DaoException {
        String sql = "select * from inventory_detail where academic_year = ? and product_id in (";
        for(int i = 0; i < productIds.size(); i++) {
            sql += productIds.get(i);
            if(i <  productIds.size()-1) {
                sql += ",";
            }
        }
        sql += " )";
        return query(sql, new InventoryDetailMapperResultSet(), academicYear);
    }

    @Override
    public void save(List<InventoryDetailModel> inventoryDetailModels) throws DaoException {
        String sql = "INSERT INTO inventory_detail(product_id, quantity, total_price, academic_year) " +
                "values(?, ?, ?, ?)";
        List<Object[]> parameters = new ArrayList<>();
        for(InventoryDetailModel inventoryDetailModel : inventoryDetailModels){
            parameters.add(new Object[]{
                inventoryDetailModel.getProductId(),
                inventoryDetailModel.getQuantity(),
                inventoryDetailModel.getTotalPrice(),
                inventoryDetailModel.getAcademicYear()
            });
        }
        save(sql, parameters);
    }

    @Override
    public void update(List<InventoryDetailModel> inventoryDetailModels) {
        String sql = "UPDATE inventory_detail set quantity = ?, total_price = ? where id = ?";
        List<Object[]> parameters = new ArrayList<>();
        for(InventoryDetailModel inventoryDetailModel : inventoryDetailModels){
            parameters.add(new Object[]{
                inventoryDetailModel.getQuantity(),
                inventoryDetailModel.getTotalPrice(),
                inventoryDetailModel.getId()
            });
        }
        save(sql, parameters);
    }

    @Override
    public void updateWithTransaction(List<InventoryDetailModel> inventoryDetailModels, Connection connection) throws DaoException {
        String sql = "UPDATE inventory_detail set quantity = ?, total_price = ? where id = ?";
        List<Object[]> parameters = new ArrayList<>();
        for(InventoryDetailModel inventoryDetailModel : inventoryDetailModels){
            parameters.add(new Object[]{
                    inventoryDetailModel.getQuantity(),
                    inventoryDetailModel.getTotalPrice(),
                    inventoryDetailModel.getId()
            });
        }
        saveWithinTransaction(sql, connection, parameters);
    }

    @Override
    public int findQuantityInStockByProductIdAndAcademicYear(long productId, int academicYear) throws DaoException{
        String sql = "select quantity from inventory_detail where product_id = ? and academic_year = ?";
        List<Integer> quantityInStocks = query(sql, rs -> rs.getInt("quantity"), productId, academicYear);
        if(!quantityInStocks.isEmpty()){
            return quantityInStocks.get(0);
        }
        throw new CanNotFoundException("Sản phẩm chưa được nhập trong năm " + academicYear);
    }

    @Override
    public List<ProductIdAndCodeAndNameAndQuantityInStock> findProductHaveMaxQuantityByProductGroup(long productGroupId) throws DaoException{
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        String sql = "select id.product_id as pId, p.name as name, p.code as code, id.quantity as quantity from inventory_detail id \n" +
                "left join product p \n" +
                "on p.id = id.product_id \n" +
                "left join product_group pg \n" +
                "on pg.id = p.group_id \n" +
                "where pg.id = ? and id.academic_year = ?\n" +
                "order by id.quantity desc \n" +
                "limit 5;";
        List<ProductIdAndCodeAndNameAndQuantityInStock> fiveProductHaveMaxQuantity = query(sql,
                rs -> new ProductIdAndCodeAndNameAndQuantityInStock(
                                rs.getLong("pId"),
                                rs.getString("code"),
                                rs.getString("name"),
                                rs.getInt("quantity")
                        ), productGroupId, year);

        if(fiveProductHaveMaxQuantity.size() < 5){
            Set<Long> productExistsInCurrentYear = fiveProductHaveMaxQuantity
                                                    .stream().map(ProductIdAndCodeAndNameAndQuantityInStock::productId)
                                                    .collect(Collectors.toSet());
            List<ProductIdAndCodeAndNameAndQuantityInStock> fiveProductHaveMaxQuantityPreviousYear = query(sql,
                    rs -> new ProductIdAndCodeAndNameAndQuantityInStock(
                            rs.getLong("pId"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getInt("quantity")
                    ), productGroupId, year-1);
            fiveProductHaveMaxQuantity.addAll(fiveProductHaveMaxQuantityPreviousYear
                    .stream()
                    .filter(product -> !productExistsInCurrentYear.contains(product.productId()))
                    .toList()
            );
        }
        return fiveProductHaveMaxQuantity;
    }

    @Override
    public List<ProductIdAndCodeAndNameAndQuantityInStock> findProductHaveMinQuantityByProductGroup(long productGroupId) throws DaoException{
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        String sql = "select id.product_id, p.name as name, p.code as code, id.quantity as quantity from inventory_detail id \n" +
                "left join product p \n" +
                "on p.id = id.product_id \n" +
                "left join product_group pg \n" +
                "on pg.id = p.group_id \n" +
                "where pg.id = ? and id.academic_year = ?\n" +
                "order by id.quantity \n" +
                "limit 5;";
        List<ProductIdAndCodeAndNameAndQuantityInStock> fiveProductHaveMinQuantity = query(sql,
                rs -> new ProductIdAndCodeAndNameAndQuantityInStock(
                        rs.getLong("pId"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getInt("quantity")
                ), productGroupId, year);

        if(fiveProductHaveMinQuantity.size() < 5){
            Set<Long> productExistsInCurrentYear = fiveProductHaveMinQuantity
                    .stream().map(ProductIdAndCodeAndNameAndQuantityInStock::productId)
                    .collect(Collectors.toSet());
            List<ProductIdAndCodeAndNameAndQuantityInStock> fiveProductHaveMaxQuantityPreviousYear = query(sql,
                    rs -> new ProductIdAndCodeAndNameAndQuantityInStock(
                            rs.getLong("pId"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getInt("quantity")
                    ), productGroupId, year-1);
            fiveProductHaveMinQuantity.addAll(fiveProductHaveMaxQuantityPreviousYear
                    .stream()
                    .filter(product -> !productExistsInCurrentYear.contains(product.productId()))
                    .toList()
            );
        }
        return fiveProductHaveMinQuantity;
    }
}
