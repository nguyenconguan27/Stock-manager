package com.manager.stock.manager_stock.screen.product.productList;

import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.service.ProductGroupService;
import com.manager.stock.manager_stock.service.ProductService;
import com.manager.stock.manager_stock.service.impl.ProductGroupServiceImpl;
import com.manager.stock.manager_stock.service.impl.ProductServiceImpl;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ProductPresenter {
    private static ProductPresenter INSTANCE;
    private final ProductService productService;
    private final ProductGroupService productGroupService;


    public ProductPresenter() {
        this.productService = ProductServiceImpl.getInstance();
        this.productGroupService = ProductGroupServiceImpl.getInstance();
    }
    public List<ProductModel> loadProductListData() {
        List<ProductModel> productModels = productService.getAllProducts();
        return productModels;
    }

    public List<ProductModel> loadProductByName(String text) {
        List<ProductModel> productModels = productService.getByName(text);
        return productModels;
    }

    public List<ProductModel> loadProductByGroup(long groupId) {
        List<ProductModel> productModels = productService.getByGroup(groupId);
        return productModels;
    }

    public static ProductPresenter getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ProductPresenter();
        }
        return INSTANCE;
    }

}

