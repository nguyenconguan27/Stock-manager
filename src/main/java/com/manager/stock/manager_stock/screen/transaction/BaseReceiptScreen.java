package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.interfaceActionHandler.TopBarActionHandler;
import com.manager.stock.manager_stock.utils.CreateTopBarOfReceiptUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.text.Normalizer;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public abstract class BaseReceiptScreen<T, D> extends VBox {
    protected final TableView<T> receiptTable = new TableView<>();
    protected final TableView<D> productTable = new TableView<>();
    protected Pagination receiptPagination;
    protected final ObservableList<T> allReceiptData = FXCollections.observableArrayList(); // Original data
    protected final ObservableList<T> receiptData = FXCollections.observableArrayList(); // Filtered data
    protected final ObservableList<D> allProductData = FXCollections.observableArrayList();
    protected final ObservableList<D> productData = FXCollections.observableArrayList();
    protected int itemsPerPage = 10;
    protected static final double HEADER_HEIGHT = 30.0;
    protected static final double MAX_TABLE_HEIGHT = 400.0;
    protected final SplitPane splitPane = new SplitPane();
    protected T selected;

    public BaseReceiptScreen() {
        setSpacing(0);
        setStyle("-fx-padding: 0 5px 0 5px; -fx-background-insets: 0; -fx-background-color: #e1f0f7");

        HBox topBar = createTopBar();
        HBox inputSearch = createFilterRow();

        createReceiptTable();
        createPagination();

        VBox receiptSection = new VBox(receiptTable, receiptPagination);
        receiptSection.setSpacing(0);
        receiptSection.setPadding(Insets.EMPTY);
        receiptSection.setStyle("-fx-padding: 0; -fx-background-insets: 0;");

        receiptTable.setPadding(Insets.EMPTY);
        receiptPagination.setPadding(Insets.EMPTY);
        receiptPagination.setStyle("-fx-padding: 0; -fx-background-insets: 0;");

        VBox itemDetailSection = createItemDetailByReceipt();
        itemDetailSection.setPadding(Insets.EMPTY);
        itemDetailSection.setStyle("-fx-padding: 0;");

        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().addAll(receiptSection, itemDetailSection);
        splitPane.setDividerPositions(0.6);
        splitPane.setPadding(Insets.EMPTY);
        splitPane.setStyle("""
            -fx-padding: 0;
            -fx-background-insets: 0;
            -fx-background-color: transparent;
            -fx-border-width: 0;
            -fx-divider-width: 1;
        """);

        VBox.setVgrow(splitPane, Priority.ALWAYS);

        getChildren().addAll(topBar, inputSearch, splitPane);
    }

    private void createPagination() {
        receiptPagination = new Pagination();
        receiptPagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, receiptData.size());
            receiptTable.setItems(FXCollections.observableArrayList(receiptData.subList(fromIndex, toIndex)));
            updateTableHeight(receiptTable, Math.min(itemsPerPage, receiptData.size()));
            return new VBox(receiptTable);
        });

        receiptPagination.getStylesheets().add(
                ImportReceiptScreen.class.getResource("/com/manager/stock/manager_stock/css/importReceipt/pagination.css").toExternalForm()
        );
    }

    protected void updateTableHeight(TableView<?> table, int itemCount) {
        table.setMaxHeight(MAX_TABLE_HEIGHT);
        table.setMinHeight(HEADER_HEIGHT);
    }

    protected String normalizeString(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }

    protected void updatePagination() {
        int pageCount = (int) Math.ceil((double) receiptData.size() / itemsPerPage);
        receiptPagination.setPageCount(pageCount > 0 ? pageCount : 1);
        receiptPagination.setCurrentPageIndex(0);
        int fromIndex = 0;
        int toIndex = Math.min(itemsPerPage, receiptData.size());
        receiptTable.setItems(FXCollections.observableArrayList(receiptData.subList(fromIndex, toIndex)));
        updateTableHeight(receiptTable, Math.min(itemsPerPage, receiptData.size()));
    }

    private HBox createTopBar() {
        return CreateTopBarOfReceiptUtil.createTopBar(getTopBarHandler());
    }

    protected void setReceiptData(List<T> data) {
        allReceiptData.setAll(data);
        receiptData.setAll(data);
        updatePagination();
    }

    protected void setProductData(List<D> data) {
        allProductData.setAll(data);
        productData.setAll(data);
        productTable.setItems(productData);
        updateTableHeight(productTable, productData.size());
    }

    protected void setColumnPercentWidth(TableColumn<?, ?> col, double percent) {
        receiptTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            col.setPrefWidth(width * percent / 100.0);
        });
    }

    protected abstract VBox createItemDetailByReceipt();
    protected abstract HBox createFilterRow();
    protected abstract void createReceiptTable();
    protected abstract TopBarActionHandler getTopBarHandler();
}
