package com.manager.stock.manager_stock.dao;

import com.manager.stock.manager_stock.mapper.modelMapperResultSet.RowMapper;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface GenericDao<T> {
    <T> List<T> query(String sql, RowMapper<T> mapper, Object...parameters);
    long save(String sql, Object... parameters);
    long update(String sql, Object... parameters);
    void delete(String sql, List<Long> ids);
}
