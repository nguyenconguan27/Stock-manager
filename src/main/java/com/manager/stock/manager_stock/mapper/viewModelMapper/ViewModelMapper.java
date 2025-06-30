package com.manager.stock.manager_stock.mapper.viewModelMapper;

/**
 * @author Trọng Hướng
 */
@FunctionalInterface
public interface ViewModelMapper<T, R> {
    R toViewModel(T model);
}
