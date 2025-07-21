package com.manager.stock.manager_stock.mapper.viewModelMapper;

/**
 * @author Trọng Hướng
 */
public interface ViewModelMapper<T, R> {
    R toViewModel(T model);
    T fromViewModelToModel(R viewModel);
}
