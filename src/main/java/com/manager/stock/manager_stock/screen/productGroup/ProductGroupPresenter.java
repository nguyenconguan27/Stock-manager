package com.manager.stock.manager_stock.screen.productGroup;

import com.manager.stock.manager_stock.model.ProductGroup;
import com.manager.stock.manager_stock.service.ProductGroupService;
import com.manager.stock.manager_stock.service.ProductService;
import com.manager.stock.manager_stock.service.impl.ProductGroupServiceImpl;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ProductGroupPresenter {

    private final ProductGroupService productGroupService;
    public static ProductGroupPresenter INSTANCE;

    public ProductGroupPresenter() {
        this.productGroupService = ProductGroupServiceImpl.getInstance();
    }

    public static ProductGroupPresenter getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ProductGroupPresenter();
        }
        return  INSTANCE;
    }

    public List<ProductGroup> loadProductGroupData() {
        List<ProductGroup> productGroups = productGroupService.getAll();
        return productGroups;
    }

    public void saveGroupProduct(ProductGroup productGroup) {
        productGroupService.save(productGroup);
        productGroupService.commit();
    }

    public ProductGroup getById(long id) {
        return productGroupService.getById(id);
    }
}
