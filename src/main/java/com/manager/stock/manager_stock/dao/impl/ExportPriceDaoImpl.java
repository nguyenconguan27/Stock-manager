package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IExportPriceDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ExportPriceMapperResultSet;
import com.manager.stock.manager_stock.model.ExportPriceModel;
import com.manager.stock.manager_stock.model.dto.ExportPriceAndProductCodeAndProductName;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndExportTimeAndExportPrice;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndPrice;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;

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
    public long save(ExportPriceModel exportPriceModel) {
        String sql = "INSERT INTO export_price(product_id, export_time, export_price, quantity_in_stock, quantity_imported, total_price_import, total_price_in_stock, import_receipt_id)" +
                " values (?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> parameters = new ArrayList<>();
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
        return save(sql, parameters);
    }

    @Override
    public List<ExportPriceModel> findAllByProductAndMinTime(List<Long> productIds, LocalDateTime minTime) {
        String productIdsStr = productIds.stream().map(Object::toString).collect(Collectors.joining(","));
        String sql = "SELECT * FROM export_price WHERE export_time >= ? and product_id in (" + productIdsStr + ") order by export_time asc";
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
    public void updateExportPriceByProductIdAndImportDate(long quantityImport, double totalPriceDifference, double totalPriceImport, LocalDateTime exportTime, long productId) throws DaoException {
        String sql = "UPDATE DB.EXPORT_PRICE \n" +
                "SET \n" +
                "    DB.EXPORT_PRICE.EXPORT_PRICE = CASE \n" +
                "        WHEN (DB.EXPORT_PRICE.QUANTITY_IMPORTED + DB.EXPORT_PRICE.QUANTITY_IN_STOCK - ?) = 0 \n" +
                "        THEN 0\n" +
                "        ELSE (DB.EXPORT_PRICE.TOTAL_PRICE_IN_STOCK + ? - ? + DB.EXPORT_PRICE.TOTAL_PRICE_IMPORT) \n" +
                "             / (DB.EXPORT_PRICE.QUANTITY_IMPORTED + DB.EXPORT_PRICE.QUANTITY_IN_STOCK - ?)\n" +
                "    END,\n" +
                "    DB.EXPORT_PRICE.QUANTITY_IN_STOCK = DB.EXPORT_PRICE.QUANTITY_IN_STOCK - ?\n" +
                "WHERE DB.EXPORT_PRICE.EXPORT_TIME = ? \n" +
                "  AND DB.EXPORT_PRICE.PRODUCT_ID = ?;";
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] {
                quantityImport, totalPriceDifference, totalPriceImport, quantityImport, quantityImport, exportTime, productId
        });
        save(sql, parameters);
    }

    @Override
    public void updateExportPriceAfterImportCorrectionByProductIdAndImportDate(double totalPriceChanged, long totalQuantityChange, LocalDateTime oldImportDate, long productId) throws DaoException {
        String sql = "update DB.EXPORT_PRICE \n" +
                "set DB.EXPORT_PRICE.EXPORT_PRICE = (DB.EXPORT_PRICE.TOTAL_PRICE_IN_STOCK - ? + DB.EXPORT_PRICE.TOTAL_PRICE_IMPORT ) / (DB.EXPORT_PRICE.QUANTITY_IN_STOCK - ? + DB.EXPORT_PRICE.QUANTITY_IMPORTED),\n" +
                "DB.EXPORT_PRICE.QUANTITY_IN_STOCK = DB.EXPORT_PRICE.QUANTITY_IN_STOCK - ?,\n" +
                "DB.EXPORT_PRICE.TOTAL_PRICE_IN_STOCK = DB.EXPORT_PRICE.TOTAL_PRICE_IN_STOCK - ?\n" +
                "WHERE DB.EXPORT_PRICE.EXPORT_TIME = ? \n" +
                "AND DB.EXPORT_PRICE.PRODUCT_ID = ?;";
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] {
                totalPriceChanged, totalQuantityChange, totalQuantityChange, totalPriceChanged, oldImportDate, productId
        });
        save(sql, parameters);
    }

    @Override
    public ExportPriceIdAndPrice findExportPriceIdAndPriceByProductAndLastTime(long productId) {
        return null;
    }

    @Override
    public ExportPriceIdAndPrice findExportPriceIdAndPriceByProductAndLastTime(long productId, LocalDateTime exportDate) throws DaoException{
        String sql = "select id, export_price from export_price ep \n" +
                "where product_id = ? and export_time <= ? and quantity_imported != 0\n" +
                "order by export_time desc limit 1;";
        List<ExportPriceIdAndPrice> exportPriceIdAndPrices = query(sql, rs -> new ExportPriceIdAndPrice(
                rs.getLong("ID"), rs.getDouble("EXPORT_PRICE")
        ), productId, exportDate);
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
                rs.getLong("ID"), rs.getDouble("EXPORT_PRICE")
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
                rs -> new ExportPriceAndProductCodeAndProductName(rs.getDouble("EXPORT_PRICE"),
                                                                            rs.getString("CODE"),
                                                                            rs.getString("NAME")), productGroupId);
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
                "WHERE pg.id = ?\n" +
                "ORDER BY ep.export_price\n" +
                "LIMIT 1;";
        List<ExportPriceAndProductCodeAndProductName> maxPrice = query(sql,
                rs -> new ExportPriceAndProductCodeAndProductName(rs.getDouble("EXPORT_PRICE"),
                        rs.getString("CODE"),
                        rs.getString("NAME")), productGroupId);
        if(maxPrice.isEmpty()){
            return new ExportPriceAndProductCodeAndProductName(0, "UNKNOWN", "UNKNOWN");
        }
        return maxPrice.get(0);
    }

    @Override
    public ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndMaxTimeByImportDate(long productId, LocalDateTime maxTime, LocalDateTime oldImportDate) throws DaoException{
        String sql = "select DB.EXPORT_PRICE.id, DB.EXPORT_PRICE.export_time, DB.EXPORT_PRICE.EXPORT_PRICE from DB.EXPORT_PRICE join DB.PRODUCT \n" +
                "on DB.PRODUCT.id = DB.EXPORT_PRICE.product_id\n" +
                "where DB.EXPORT_PRICE.export_time < ? and DB.PRODUCT.id = ?\n" +
                " and DB.EXPORT_PRICE.export_time != ? \n" +
                "order by DB.EXPORT_PRICE.export_time desc\n" +
                "limit 1;";
        List<ExportPriceIdAndExportTimeAndExportPrice> exportPriceIdAndExportTimes =
                query(sql, rs -> new ExportPriceIdAndExportTimeAndExportPrice(rs.getLong("ID"),
                        rs.getTimestamp("EXPORT_TIME").toLocalDateTime(),
                        rs.getLong("EXPORT_PRICE")), maxTime, productId, oldImportDate);
        if(exportPriceIdAndExportTimes.isEmpty()){
            return null;
        }
        return exportPriceIdAndExportTimes.get(0);
    }

    @Override
    public ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndMinTimeByImportDate(long productId, LocalDateTime minTime, LocalDateTime oldImportDate) {
        String sql = "select DB.EXPORT_PRICE.id, DB.EXPORT_PRICE.export_time, DB.EXPORT_PRICE.EXPORT_PRICE from DB.EXPORT_PRICE join DB.PRODUCT \n" +
                "on DB.PRODUCT.id = DB.EXPORT_PRICE.product_id\n" +
                "where DB.EXPORT_PRICE.export_time > ? and DB.PRODUCT.id = ?\n" +
                " and DB.EXPORT_PRICE.export_time != ? \n" +
                "order by DB.EXPORT_PRICE.export_time asc\n" +
                "limit 1;";
        List<ExportPriceIdAndExportTimeAndExportPrice> exportPriceIdAndExportTimes =
                query(sql, rs -> new ExportPriceIdAndExportTimeAndExportPrice(rs.getLong("ID"),
                                                rs.getTimestamp("EXPORT_TIME").toLocalDateTime(),
                                                rs.getLong("EXPORT_PRICE")), minTime, productId, oldImportDate);
        if(exportPriceIdAndExportTimes.isEmpty()){
            return null;
        }
        return exportPriceIdAndExportTimes.get(0);
    }

    @Override
    public ExportPriceModel findByProductIdAndMinTimeByDate(long productId, LocalDateTime minTime, LocalDateTime oldImportDate) {
        String sql = "select * from DB.EXPORT_PRICE join DB.PRODUCT \n" +
                "on DB.PRODUCT.id = DB.EXPORT_PRICE.product_id\n" +
                "where DB.EXPORT_PRICE.export_time > ? and DB.PRODUCT.id = ?\n" +
                " and DB.EXPORT_PRICE.export_time != ? \n" +
                "order by DB.EXPORT_PRICE.export_time asc\n" +
                "limit 1;";
        List<ExportPriceModel> exportPriceIdAndExportTimes =
                query(sql, new ExportPriceMapperResultSet(), minTime, productId, oldImportDate);
        if(exportPriceIdAndExportTimes.isEmpty()){
            return null;
        }
        return exportPriceIdAndExportTimes.get(0);
    }

    @Override
    public ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndImportDate(long productId, LocalDateTime importDate) {
        String sql = "select DB.EXPORT_PRICE.ID, DB.EXPORT_PRICE.EXPORT_TIME, DB.EXPORT_PRICE.EXPORT_PRICE from DB.EXPORT_PRICE \n" +
                "where DB.EXPORT_PRICE.EXPORT_TIME = ? and DB.EXPORT_PRICE.PRODUCT_ID = ?;";
        List<ExportPriceIdAndExportTimeAndExportPrice> exportPriceIdAndExportTimes = query(sql, rs -> new ExportPriceIdAndExportTimeAndExportPrice(rs.getLong("ID"),
                rs.getTimestamp("EXPORT_TIME").toLocalDateTime(),
                rs.getLong("EXPORT_PRICE")), importDate, productId);
        if(exportPriceIdAndExportTimes.isEmpty()){
            return null;
        }
        return exportPriceIdAndExportTimes.get(0);
    }

    @Override
    public ExportPriceModel findOneByProductIdAndImportDate(long productId, LocalDateTime importDate) {
        String sql = "select * from DB.EXPORT_PRICE \n" +
                "where DB.EXPORT_PRICE.EXPORT_TIME = ? and DB.EXPORT_PRICE.PRODUCT_ID = ?;";
        List<ExportPriceModel> exportPriceModels = query(sql, new ExportPriceMapperResultSet(), importDate, productId);
        if(exportPriceModels.isEmpty()){
            return new ExportPriceModel();
        }
        return exportPriceModels.get(0);
    }

    @Override
    public List<LocalDateTime> findAllExportTimeByProductAndBetweenImportDates(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime importDate, long productId) throws DaoException {
        String sql = "" +
                "select DB.EXPORT_PRICE.EXPORT_TIME  from DB.EXPORT_PRICE \n" +
                "join DB.PRODUCT on DB.PRODUCT.id = DB.EXPORT_PRICE.PRODUCT_ID\n" +
                "where DB.PRODUCT.id = ? \n" +
                "and DB.EXPORT_PRICE.EXPORT_TIME >= ?\n" +
                "and DB.EXPORT_PRICE.EXPORT_TIME < ?\n" +
                "and DB.EXPORT_PRICE.EXPORT_TIME != ?;";
        return query(sql, rs -> rs.getTimestamp("EXPORT_TIME").toLocalDateTime(), productId, startDate, endDate, importDate);
    }

    @Override
    public List<ExportPriceModel> findAllExportPriceInfoByProductAndBetweenImportDates(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime importDate, long productId) throws DaoException {
        String sql = "" +
                "select DB.EXPORT_PRICE.EXPORT_TIME  from DB.EXPORT_PRICE \n" +
                "join DB.PRODUCT on DB.PRODUCT.id = DB.EXPORT_PRICE.PRODUCT_ID\n" +
                "where DB.PRODUCT.id = ? \n" +
                "and DB.EXPORT_PRICE.EXPORT_TIME >= ?\n" +
                "and DB.EXPORT_PRICE.EXPORT_TIME < ?\n" +
                "and DB.EXPORT_PRICE.EXPORT_TIME != ?;";
        return query(sql, new ExportPriceMapperResultSet(), productId, startDate, endDate, importDate);
    }

    @Override
    public List<LocalDateTime> findAllExportTimeByProductAndMoreThanImportDate(LocalDateTime startDate, LocalDateTime importDate, long productId) throws DaoException {
        String sql = "" +
                "select DB.EXPORT_PRICE.EXPORT_TIME  from DB.EXPORT_PRICE \n" +
                "join DB.PRODUCT on DB.PRODUCT.id = DB.EXPORT_PRICE.PRODUCT_ID\n" +
                "where DB.PRODUCT.id = ? \n" +
                "and DB.EXPORT_PRICE.EXPORT_TIME >= ?\n" +
                "and DB.EXPORT_PRICE.EXPORT_TIME != ?;";
        return query(sql, rs -> rs.getTimestamp("EXPORT_TIME").toLocalDateTime(), productId, startDate, importDate);
    }

    @Override
    public void updateExportPriceByImportTimeAndProduct(long newQuantityInStock, double newTotalPriceInStock, LocalDateTime importDateTime, long productId) throws DaoException{
        String sql = "update DB.EXPORT_PRICE set\n" +
                "DB.EXPORT_PRICE.QUANTITY_IN_STOCK = ?,\n" +
                "TOTAL_PRICE_IN_STOCK = ?,\n" +
                "EXPORT_PRICE = (DB.EXPORT_PRICE.TOTAL_PRICE_IMPORT  + ?) / (DB.EXPORT_PRICE.QUANTITY_IMPORTED + ?)\n" +
                "where DB.EXPORT_PRICE.EXPORT_TIME = ?\n" +
                "and DB.EXPORT_PRICE.PRODUCT_ID = ?;";
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{
                newQuantityInStock,
                newTotalPriceInStock,
                newTotalPriceInStock,
                newQuantityInStock,
                importDateTime,
                productId
        });
        save(sql, parameters);
    }

    @Override
    public long calculateTotalQuantityImportAndQuantityInStockByImportDateAndProduct(long productId, LocalDateTime importDate) {
        String sql = "" +
                "select DB.EXPORT_PRICE.QUANTITY_IN_STOCK + DB.EXPORT_PRICE.QUANTITY_IMPORTED as total_quantity from DB.EXPORT_PRICE \n" +
                "join DB.PRODUCT on DB.PRODUCT.id = DB.EXPORT_PRICE.PRODUCT_ID \n" +
                "where DB.EXPORT_PRICE.EXPORT_TIME = ?\n" +
                "and DB.PRODUCT.id = ?;";
        List<Long> totalQuantity = query(sql, rs -> rs.getLong("TOTAL_QUANTITY"), importDate,  productId);
        if(totalQuantity.isEmpty()){
            return -1;
        }
        return totalQuantity.get(0);
    }

    @Override
    public void commit() {
        super.commit();
    }

    @Override
    public void rollback() {
        super.rollback();
    }

    public List<ExportPriceModel> findByProductAndLastTime(Long productId, LocalDateTime time) {
        String sql = "SELECT TOP 1 * FROM export_price WHERE export_time < ? and product_id = ? order by export_time desc, id asc";
        return query(sql, new ExportPriceMapperResultSet(), time, productId);
    }

    @Override
    public List<ExportPriceModel> findByProductAndMinTime(Long productId, LocalDateTime time) {
        String sql = "SELECT TOP 1 * FROM export_price WHERE export_time > ? and product_id = ? order by export_time asc";
        return query(sql, new ExportPriceMapperResultSet(), time, productId);
    }

    @Override
    public List<ExportPriceModel> findByProductIdAndBeforeTime(long productId, LocalDateTime time) {
        String sql = "select top 1 * from export_price where product_id = ? and export_time < ? order by export_time desc";
        return query(sql, new ExportPriceMapperResultSet(), productId, time);
    }

    @Override
    public List<ExportPriceModel> findAllByProductIdAndAfterTime(long productId, LocalDateTime time) {
        String sql = "select * from export_price where product_id = ? and export_time > ? order by export_time asc";
        return query(sql, new ExportPriceMapperResultSet(), productId, time);
    }

    @Override
    public List<ExportPriceModel> findLastByProductIdAndYear(long productId, int year) {
        String sql = "select top 1 * from export_price where product_id = ? and year(export_time) = ? order by export_time desc";
        return query(sql, new ExportPriceMapperResultSet(), productId, year);
    }


}
