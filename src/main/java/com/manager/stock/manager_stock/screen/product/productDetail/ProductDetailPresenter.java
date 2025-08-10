package com.manager.stock.manager_stock.screen.product.productDetail;

import com.almasb.fxgl.physics.box2d.dynamics.joints.LimitState;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.model.dto.ExportPriceAndProductCodeAndProductName;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;
import com.manager.stock.manager_stock.model.dto.ProductIdAndCodeAndNameAndQuantityInStock;
import com.manager.stock.manager_stock.screen.product.productList.ProductPresenter;
import com.manager.stock.manager_stock.service.IExportPriceService;
import com.manager.stock.manager_stock.service.IInventoryDetailService;
import com.manager.stock.manager_stock.service.ProductService;
import com.manager.stock.manager_stock.service.impl.ExportPriceServiceImpl;
import com.manager.stock.manager_stock.service.impl.InventoryDetailServiceImpl;
import com.manager.stock.manager_stock.service.impl.ProductServiceImpl;

import java.util.List;

public class ProductDetailPresenter {

    private final ProductService productService;
    private final IExportPriceService exportPriceService;
    private final IInventoryDetailService inventoryDetailService;

    public ProductDetailPresenter() {
        this.productService = ProductServiceImpl.getInstance();
        this.exportPriceService = ExportPriceServiceImpl.getInstance();
        this.inventoryDetailService = InventoryDetailServiceImpl.getInstance();
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

    public ExportPriceAndProductCodeAndProductName findMaxExportPriceByGroup(long productGroupId) throws DaoException {
        return exportPriceService.findProductHaveMaxPriceByGroup(productGroupId);
    }

    public ExportPriceAndProductCodeAndProductName findMinExportPriceByGroup(long productGroupId) throws DaoException {
        return exportPriceService.findProductHaveMinPriceByGroup(productGroupId);
    }

    public List<ProductIdAndCodeAndNameAndQuantityInStock> findListProductHaveMaxQuantityInStockByGroup(long productGroupId) throws DaoException {
        return inventoryDetailService.findProductHaveMaxQuantityByProductGroup(productGroupId);
    }

    public List<ProductIdAndCodeAndNameAndQuantityInStock> findListProductHaveMinQuantityInStockByGroup(long productGroupId) throws DaoException {
        return inventoryDetailService.findProductHaveMinQuantityByProductGroup(productGroupId);
    }
}
