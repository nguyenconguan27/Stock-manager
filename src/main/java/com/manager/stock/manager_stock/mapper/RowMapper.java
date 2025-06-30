package com.manager.stock.manager_stock.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Trọng Hướng
 */
public interface RowMapper <T> {
    T mapRow(ResultSet resultSet) throws SQLException;
}
