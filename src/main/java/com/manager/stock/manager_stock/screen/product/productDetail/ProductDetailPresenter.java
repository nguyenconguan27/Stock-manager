package com.manager.stock.manager_stock.screen.product.productDetail;

import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.screen.product.productList.ProductPresenter;
import com.manager.stock.manager_stock.service.ProductService;
import com.manager.stock.manager_stock.service.impl.ProductServiceImpl;

public class ProductDetailPresenter {

    private final ProductService productService;

    public ProductDetailPresenter() {
        this.productService = ProductServiceImpl.getInstance();
    }

    private static ProductDetailPresenter INSTANCE;
    public static ProductDetailPresenter getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ProductDetailPresenter();
        }
        return  INSTANCE;
    }

    public ProductModel getProductDetail(long id) {
        return productService.getById(id);
    }

    public void add(ProductModel productModel, long groupId) {
        productService.add(productModel, groupId);
    }

    public void update(ProductModel productModel, long groupId, boolean isUpdateCode) {
        productService.update(productModel, groupId, isUpdateCode);
    }

    public ProductModel getByCode(String text) {
        return productService.getByCode(text);
    }
}
