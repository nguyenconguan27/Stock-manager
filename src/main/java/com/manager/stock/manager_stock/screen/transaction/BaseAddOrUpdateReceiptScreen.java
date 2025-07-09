package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.interfaceActionHandler.TopBarActionHandler;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.utils.CreateTopBarOfReceiptUtil;
import com.manager.stock.manager_stock.utils.FormatMoney;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Trọng Hướng
 */
public abstract class BaseAddOrUpdateReceiptScreen<T, D> extends VBox {
    protected DatePicker dpCreateAt;
    protected final TableView<D> productTable = new TableView<>();
    protected boolean productLoaded = false;
    protected final ObservableList<ProductModel> allProducts = FXCollections.observableArrayList();
    protected FilteredList<ProductModel> filteredProducts = new FilteredList<>(allProducts, p -> true);
    protected final ObservableList<D> productDetails = FXCollections.observableArrayList();
    protected double totalPriceOfReceipt = 0;
    protected Label totalPriceLabel = new Label(FormatMoney.format(0));
    protected Set<Long> changeIdsOfReceiptDetails = new HashSet<>();
    protected HashMap<Long, Integer> changeQuantityByProductMap = new HashMap<>();
    protected HashMap<Long, Double> changeTotalPriceByProductMap = new HashMap<>();
    protected TextField tfInvoiceNumber, tfWareHouse;

    public BaseAddOrUpdateReceiptScreen(T receiptModelTable) {
        HBox topBar = CreateTopBarOfReceiptUtil.createTopBar(new TopBarActionHandler() {
            @Override
            public void onAdd() {

            }

            @Override
            public void onEdit() {

            }

            @Override
            public void onDelete() {

            }

            @Override
            public void onReload() {

            }

            @Override
            public void onPrint() {

            }

            @Override
            public void onExport() {

            }
        });

        VBox formAddNew = createFormAddNew(receiptModelTable);
        getChildren().addAll(topBar, formAddNew, createTableItemDetailByReceipt(receiptModelTable));
    }

    protected void styleLabel(Label label) {
        label.setStyle("-fx-border-width: 0; -fx-background-color: #e1f0f7; -fx-text-fill: #33536d; -fx-font-size: 15px");
    }
    protected String normalizeString(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }

    protected abstract VBox createFormAddNew(T receiptModelTable);
    protected abstract VBox createTableItemDetailByReceipt(T receiptModelTable);
}
