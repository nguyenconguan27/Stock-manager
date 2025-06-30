package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.config.AppConfig;
import com.manager.stock.manager_stock.dao.GenericDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Trọng Hướng
 */
public class AbstractDao<T> implements GenericDao<T> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Connection getConnection() throws SQLException {
        final String url = AppConfig.getString("db.url");
        final Properties props = new Properties();
        props.setProperty("user", AppConfig.getString("db.user"));
        props.setProperty("password", AppConfig.getString("db.password"));
        logger.debug("Start connect to database..., user = {}, password = {}", AppConfig.getString("db.user"), AppConfig.getString("db.password") );
        return DriverManager.getConnection(url, props);
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
    public long save(String sql, Object... parameters) {
        logger.debug("Start insert of database with sql: {}", sql);
        logger.debug("Start insert of database with params: {}", parameters);
        PreparedStatement stmt = null;
        Connection connection = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(sql);
            setParams(stmt, parameters);
            return stmt.executeUpdate();
        }
        catch (SQLException e) {
            logger.error("SQL Exception while inserting database with sql: {}", sql, e);
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

    @Override
    public long update(String sql, Object... parameters) {
        return 0;
    }

    @Override
    public void delete(String sql, List<Long> ids) {
        logger.debug("Start delete of database with sql: {}", sql);
        logger.debug("Start delete of database with ids: {}", ids);
        PreparedStatement stmt = null;
        Connection connection = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(sql);
            setParams(stmt, ids);
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
