package com.manager.stock.manager_stock.screen.product.productDetail;

public class ProductDetailPresenter {
    private static ProductDetailPresenter INSTANCE;
    public static ProductDetailPresenter getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ProductDetailPresenter();
        }
        return  INSTANCE;
    }
}
