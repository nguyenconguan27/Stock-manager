package com.manager.stock.manager_stock.screen.product;

import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.service.ProductService;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ProductPresenter {
    private final ProductScreen productScreen;
    private final ProductService productService;

    public ProductPresenter() {
        this.productScreen = new ProductScreen();
        this.productService = new ProductService();
    }
    public void loadProductData() {
        List<ProductModel> productModels = productService.getAllProducts();
        productScreen.showProducts(productModels);
    }
}
