package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.config.AppConfig;
import com.manager.stock.manager_stock.dao.GenericDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author Trọng Hướng
 */
public class AbstractDao<T> implements GenericDao<T> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Connection getConnection() throws SQLException {
        try {
            final String url = AppConfig.getString("db.url");
            final Properties props = new Properties();
            props.setProperty("user", AppConfig.getString("db.user"));
            props.setProperty("password", AppConfig.getString("db.password"));
            logger.debug("Start connect to database..., user = {}, password = {}", AppConfig.getString("db.user"), AppConfig.getString("db.password") );
            System.out.println(String.format("Start connect to database..., user = {%s}, password = {%s}", AppConfig.getString("db.user"), AppConfig.getString("db.password") ));
            return DriverManager.getConnection(url, props);
        }
        catch (Exception e) {
            return null;
        }
    }

    private void setParams(PreparedStatement stmt, Object...params) {
        try {
            for(int i = 0; i < params.length; i++) {
                Object param = params[i];
                int index = i + 1;
                if(param instanceof Integer) {
                    stmt.setInt(index, (Integer) param);
                }
                else if(param instanceof String) {
                    stmt.setString(index, (String) param);
                }
                else if(param instanceof Long) {
                    stmt.setLong(index, (Long) param);
                }
                else if(param instanceof Double) {
                    stmt.setDouble(index, (Double) param);
                }
                else if(param instanceof Float) {
                    stmt.setFloat(index, (Float) param);
                }
                else if(param instanceof Boolean) {
                    stmt.setBoolean(index, (Boolean) param);
                }
                else if(param instanceof Date) {
                    stmt.setDate(index, (Date) param);
                }
                else if(param == null) {
                    stmt.setNull(index, Types.NULL);
                }
                else if(param instanceof LocalDateTime) {
                    stmt.setTimestamp(index, Timestamp.valueOf((LocalDateTime) param));
                }
            }
        }
        catch (SQLException e) {
            logger.error("Error while setting parameters: {}", e.getMessage(), e);
        }
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> mapper, Object... parameters) {
        logger.debug("Start query of database with sql: {}", sql);
        logger.debug("Start query of database with params: {}", parameters);
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement stmt = null;
        List<T> resultsList = new ArrayList<>();
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(sql);
            setParams(stmt, parameters);
            rs = stmt.executeQuery();
            while(rs.next()) {
                resultsList.add(mapper.mapRow(rs));
            }
            return resultsList;
        }
        catch (SQLException e) {
            logger.error("SQL Exception while querying database with sql: {}", sql, e);
            throw new DaoException("Lỗi khi kết nối với hệ thống, vui lòng thử lại sau.");
        }
        finally {
            try {
                if(rs != null) {
                    rs.close();
                }
                if(stmt != null) {
                    stmt.close();
                }
                if(connection != null) {
                    connection.close();
                }
            }
            catch (SQLException e) {
                logger.error("SQL Exception while closing ResultSet or Statement or Connection: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public long save(String sql, List<Object[]> parameters) {
        logger.debug("Start insert of database with sql: {}", sql);
        parameters.forEach(p -> logger.debug("Params: {}", Arrays.toString(p)));
        PreparedStatement stmt = null;
        Connection connection = null;
        try {
            connection = getConnection();
            if (parameters.size() == 1) {
                stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                setParams(stmt, parameters.get(0));
                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new DaoException("Lỗi khi kết nối với hệ thống, vui lòng thử lại sau.");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    } else {
                        throw new DaoException("Lỗi khi kết nối với hệ thống, vui lòng thử lại sau.");
                    }
                }
            }
            stmt = connection.prepareStatement(sql);
            for (Object[] params : parameters) {
                setParams(stmt, params);
                stmt.addBatch();
            }
            int[] insertResult = stmt.executeBatch();
            logger.info(String.format("Insert success: {%d} rows.", insertResult.length));
            return insertResult.length;

        } catch (SQLException e) {
            logger.error("SQL Exception while save database with sql: {}", sql, e);
            e.printStackTrace();
            throw new DaoException("Lỗi khi kết nối với hệ thống, vui lòng thử lại sau.");
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("SQL Exception while closing Statement or Connection: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public int update(String sql, Object... parameters) {
        return 0;
    }

    @Override
    public void delete(String sql, Object...params) {
        logger.debug("Start delete of database with sql: {}", sql);
        logger.debug("Start delete of database with ids: {}", params);
        PreparedStatement stmt = null;
        Connection connection = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(sql);
            for(Object id : params) {
                setParams(stmt, id);
            }
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            logger.error("SQL Exception while deleting database with sql: {}", sql, e);
            throw new DaoException("Lỗi khi kết nối với hệ thống, vui lòng thử lại sau.");
        }
        finally {
            try {
                if(stmt != null) {
                    stmt.close();
                }
                if(connection != null) {
                    connection.close();
                }
            }
            catch (SQLException e) {
                logger.error("SQL Exception while closing ResultSet or Statement or Connection: {}", e.getMessage(), e);
            }
        }
    }
}
