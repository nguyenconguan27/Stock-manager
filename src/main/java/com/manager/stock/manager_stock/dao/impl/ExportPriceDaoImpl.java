package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IExportPriceDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ExportPriceMapperResultSet;
import com.manager.stock.manager_stock.model.ExportPriceModel;
import com.manager.stock.manager_stock.model.dto.ExportPriceAndProductCodeAndProductName;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndPrice;
import com.manager.stock.manager_stock.service.impl.ExportPriceServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ExportPriceDaoImpl extends AbstractDao<ExportPriceModel> implements IExportPriceDao {
    private static ExportPriceDaoImpl instance;
    private ExportPriceDaoImpl(){}
    public static ExportPriceDaoImpl getInstance() {
        if (instance == null) {
            instance = new ExportPriceDaoImpl();
        }
        return instance;
    }

    @Override
    public long findExportPriceByProductIdAndExportTime(long productId) {
        try {
            String sql = "select id \n" +
                    "FROM export_price\n" +
                    "WHERE product_id = ?\n" +
                    "ORDER BY export_time DESC\n" +
                    "LIMIT 1;";

            List<ExportPriceModel> exportPriceModels = query(sql, new ExportPriceMapperResultSet(), productId);
            if(exportPriceModels.isEmpty()){
                return -1;
            }
            return exportPriceModels.get(0).getId();
        }
        catch (DaoException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public void save(List<ExportPriceModel> exportPriceModels) throws DaoException {
        String sql = "INSERT INTO export_price(product_id, export_time, export_price, quantity_in_stock, quantity_imported, total_price_import, total_price_in_stock, import_receipt_id) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> parameters = new ArrayList<>();
        for(ExportPriceModel exportPriceModel : exportPriceModels){
            parameters.add(new Object[] {
                exportPriceModel.getProductId(),
                exportPriceModel.getExportTime(),
                exportPriceModel.getExportPrice(),
                exportPriceModel.getQuantityInStock(),
                exportPriceModel.getQuantityImported(),
                exportPriceModel.getTotalImportPrice(),
                exportPriceModel.getTotalPriceInStock(),
                exportPriceModel.getImportReceiptId()
            });
        }
        save(sql, parameters);
    }

    @Override
    public List<ExportPriceModel> findAllByProductAndMinTime(List<Long> productIds, LocalDateTime minTime) {
        String productIdsStr = productIds.stream().map(Object::toString).collect(Collectors.joining(","));
        String sql = "SELECT * FROM export_price WHERE export_time >= ? and product_id in (" + productIdsStr + ") order by id asc";
        return query(sql, new ExportPriceMapperResultSet(), minTime);
    }

    @Override
    public void update(List<ExportPriceModel> exportPriceModels) {
        String sql = "UPDATE export_price SET quantity_in_stock = ?, quantity_imported = ?, total_price_import = ?, export_price = ?, total_price_in_stock = ?" +
                    " WHERE id = ?";
        List<Object[]> parameters = new ArrayList<>();
        for(ExportPriceModel exportPriceModel : exportPriceModels){
            parameters.add(new Object[] {
               exportPriceModel.getQuantityInStock(),
               exportPriceModel.getQuantityImported(),
               exportPriceModel.getTotalImportPrice(),
               exportPriceModel.getExportPrice(),
               exportPriceModel.getTotalPriceInStock(),
               exportPriceModel.getId()
            });
        }
        save(sql, parameters);
    }

    @Override
    public ExportPriceIdAndPrice findExportPriceIdAndPriceByProductAndLastTime(long productId) throws DaoException{
        String sql = "select id, export_price from export_price ep \n" +
                "where product_id = ?\n" +
                "order by export_time desc limit 1;";
        List<ExportPriceIdAndPrice> exportPriceIdAndPrices = query(sql, rs -> new ExportPriceIdAndPrice(
                rs.getLong("id"), rs.getDouble("export_price")
        ), productId);
        if(!exportPriceIdAndPrices.isEmpty()){
            return exportPriceIdAndPrices.get(0);
        }
        return new ExportPriceIdAndPrice(-1,-1);
    }

    @Override
    public void updateExportTimeByImportReceiptId(long importReceiptId, LocalDateTime importDate) {
        String sql = "Update export_price set export_time = ? where import_receipt_id = ?";
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] {
                importDate,
                importReceiptId
        });
        save(sql, parameters);
    }

    @Override
    public List<ExportPriceIdAndPrice> findAllById(List<Long> ids) {
        String idsStr = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        String sql = "select export_price, id from export_price where id in (" + idsStr + ") order by id asc";
        return query(sql, rs -> new ExportPriceIdAndPrice(
                rs.getLong("id"),
                rs.getDouble("export_price")
        ));
    }

    @Override
    public ExportPriceAndProductCodeAndProductName findProductHaveMaxPriceByGroup(long productGroupId) throws DaoException{
        String sql = "WITH latest_export AS (\n" +
                "    SELECT ep.*\n" +
                "    FROM export_price ep\n" +
                "    INNER JOIN (\n" +
                "        SELECT product_id, MAX(export_time) AS latest_time\n" +
                "        FROM export_price\n" +
                "        WHERE quantity_imported != 0\n" +
                "        GROUP BY product_id\n" +
                "    ) latest ON ep.product_id = latest.product_id AND ep.export_time = latest.latest_time\n" +
                ")\n" +
                "SELECT ep.export_time, p.code AS code, p.name as name, ep.export_price\n" +
                "FROM latest_export ep\n" +
                "LEFT JOIN product p ON p.id = ep.product_id\n" +
                "LEFT JOIN product_group pg ON p.group_id = pg.id\n" +
                "WHERE pg.id = ?\n" +
                "ORDER BY ep.export_price DESC\n" +
                "LIMIT 1;";
        List<ExportPriceAndProductCodeAndProductName> maxPrice = query(sql,
                rs -> new ExportPriceAndProductCodeAndProductName(rs.getDouble("export_price"),
                                                                            rs.getString("code"),
                                                                            rs.getString("name")), productGroupId);
        if(maxPrice.isEmpty()){
           return new ExportPriceAndProductCodeAndProductName(0, "UNKNOWN", "UNKNOWN");
        }
        return maxPrice.get(0);
    }

    @Override
    public ExportPriceAndProductCodeAndProductName findProductHaveMinPriceByGroup(long productGroupId) throws DaoException{
        String sql = "WITH latest_export AS (\n" +
                "    SELECT ep.*\n" +
                "    FROM export_price ep\n" +
                "    INNER JOIN (\n" +
                "        SELECT product_id, max(export_time) AS latest_time\n" +
                "        FROM export_price\n" +
                "        WHERE quantity_imported != 0\n" +
                "        GROUP BY product_id\n" +
                "    ) latest ON ep.product_id = latest.product_id AND ep.export_time = latest.latest_time\n" +
                ")\n" +
                "SELECT ep.export_time, p.code AS code, p.name as name, ep.export_price\n" +
                "FROM latest_export ep\n" +
                "LEFT JOIN product p ON p.id = ep.product_id\n" +
                "LEFT JOIN product_group pg ON p.group_id = pg.id\n" +
                "WHERE pg.id = 1\n" +
                "ORDER BY ep.export_price\n" +
                "LIMIT 1;";
        List<ExportPriceAndProductCodeAndProductName> maxPrice = query(sql,
                rs -> new ExportPriceAndProductCodeAndProductName(rs.getDouble("export_price"),
                        rs.getString("code"),
                        rs.getString("name")), productGroupId);
        if(maxPrice.isEmpty()){
            return new ExportPriceAndProductCodeAndProductName(0, "UNKNOWN", "UNKNOWN");
        }
        return maxPrice.get(0);
    }

    @Override
    public void commit() {
        super.commit();
    }

    @Override
    public void rollback() {
        super.rollback();
    }
}
