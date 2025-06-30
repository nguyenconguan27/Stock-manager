package com.manager.stock.manager_stock.utils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class GenericConverterFromModelToTableData {
    public static <T, R> List<R> convertToList(List<T> inputList, Function<T, R> converter) {
        if(inputList.isEmpty()){
            return List.of();
        }
        return inputList.stream()
                        .map(converter)
                        .collect(Collectors.toList());
    }
}
