package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptDetailModelMapper;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptModelMapper;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;
import com.manager.stock.manager_stock.utils.CreateColumnTableUtil;
import com.manager.stock.manager_stock.utils.CreateTopBarOfReceiptUtil;
import com.manager.stock.manager_stock.utils.GenericConverterFromModelToTableData;
import com.sun.source.tree.ImportTree;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;

public class ImportReceiptScreen extends VBox {

    private final TableView<ImportReceiptModelTable> receiptTable = new TableView<>();
    private final TableView<ImportReceiptDetailModelTable> productTable = new TableView<>();
    private Pagination receiptPagination;
    private final ObservableList<ImportReceiptModelTable> allReceiptData = FXCollections.observableArrayList(); // Original data
    private final ObservableList<ImportReceiptModelTable> receiptData = FXCollections.observableArrayList(); // Filtered data
    private final ObservableList<ImportReceiptDetailModelTable> allProductData = FXCollections.observableArrayList();
    private final ObservableList<ImportReceiptDetailModelTable> productData = FXCollections.observableArrayList();
    private int itemsPerPage = 5;
    private TextField tfId, tfInvoiceNumber, tfCreateAt, tfInvoice, tfCompany, tfWarehouse, tfProductNameImportReceipt, tfProductIdImportReceipt;
    private static final double ROW_HEIGHT = 30.0;
    private static final double HEADER_HEIGHT = 30.0;
    private static final double MAX_TABLE_HEIGHT = 400.0;

    public ImportReceiptScreen() {
        setSpacing(0);
        setStyle("-fx-padding: 0 5px 0 5px; -fx-background-insets: 0; -fx-background-color: #e1f0f7");

        HBox topBar = CreateTopBarOfReceiptUtil.createTopBar();
        HBox inputSearch = createFilterRow();

        createImportReceiptTable();
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

        SplitPane splitPane = new SplitPane();
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

        Platform.runLater(() -> {
            for (Node node : splitPane.lookupAll(".split-pane-divider")) {
                node.setVisible(false);
            }
        });

        VBox.setVgrow(splitPane, Priority.ALWAYS);

        getChildren().addAll(topBar, inputSearch, splitPane);
    }

    private void createImportReceiptTable() {
        TableColumn<ImportReceiptModelTable, Number> colId = CreateColumnTableUtil.createColumn("Mã phiếu", ImportReceiptModelTable::idProperty);
        TableColumn<ImportReceiptModelTable, String> colInvoiceNumber = CreateColumnTableUtil.createColumn("Số phiếu nhập", ImportReceiptModelTable::invoiceNumberProperty);
        TableColumn<ImportReceiptModelTable, String> colCreateAt = CreateColumnTableUtil.createColumn("Ngày tạo", ImportReceiptModelTable::createAtProperty);
        TableColumn<ImportReceiptModelTable, String> colDeliveredBy = CreateColumnTableUtil.createColumn("Người giao", ImportReceiptModelTable::deliveredByProperty);
        TableColumn<ImportReceiptModelTable, String> colInvoice = CreateColumnTableUtil.createColumn("Số hóa đơn", ImportReceiptModelTable::invoiceProperty);
        TableColumn<ImportReceiptModelTable, String> colCompany = CreateColumnTableUtil.createColumn("Công ty", ImportReceiptModelTable::companyNameProperty);
        TableColumn<ImportReceiptModelTable, String> colWarehouse = CreateColumnTableUtil.createColumn("Kho", ImportReceiptModelTable::warehouseNameProperty);
        TableColumn<ImportReceiptModelTable, Number> colTotalPrice = CreateColumnTableUtil.createColumn("Thành tiền", ImportReceiptModelTable::totalPriceProperty);

        setColumnPercentWidth(colId,           5);
        setColumnPercentWidth(colInvoiceNumber,10);
        setColumnPercentWidth(colCreateAt,     10);
        setColumnPercentWidth(colDeliveredBy,  10);
        setColumnPercentWidth(colInvoice,      10);
        setColumnPercentWidth(colCompany,      25);
        setColumnPercentWidth(colWarehouse,    20);
        setColumnPercentWidth(colTotalPrice,   10);

        receiptTable.getColumns().setAll(
                colId, colInvoiceNumber, colCreateAt, colDeliveredBy,
                colInvoice, colCompany, colWarehouse, colTotalPrice
        );
        receiptTable.getColumns().forEach(col -> col.setResizable(false));

        receiptTable.setItems(receiptData);
        receiptTable.setPrefHeight(600);
        receiptTable.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #c1dfee; -fx-border-width: 1px;");

        Platform.runLater(() -> {
            receiptTable.lookupAll(".column-header").forEach(node -> {
                node.setStyle("-fx-background-color: #e1f0f7; -fx-pref-height: 35px; " +
                        "-fx-border-width: 0 1px 0 0; -fx-border-color: #c1dfee");
            });

            receiptTable.lookupAll(".column-header .label").forEach(label -> {
                label.setStyle("-fx-text-fill: #34536e;");
            });
        });

        receiptTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ImportReceiptModelTable item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: #2f7a9a; -fx-text-fill: white;");
                } else {
                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color: #ffffff;");
                    } else {
                        setStyle("-fx-background-color: #e0f2f7;");
                    }
                }
            }
            {
                selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {

                    updateItem(getItem(), isEmpty());
                });
            }
        });

        receiptTable.setOnMouseClicked(event -> {
            ImportReceiptModelTable selected = receiptTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ImportReceiptPresenter presenter = ImportReceiptPresenter.getInstance();
                long id = selected.getId();
                showItemDetails(id);
                System.out.println("Clicked ID: " + id);
            }
        });

    }

    private void setColumnPercentWidth(TableColumn<?, ?> col, double percent) {
        receiptTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            col.setPrefWidth(width * percent / 100.0);
        });
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

    private VBox createItemDetailByReceipt() {
        TableColumn<ImportReceiptDetailModelTable, Number> colProductId = CreateColumnTableUtil.createColumn("Mã SP", ImportReceiptDetailModelTable::productIdProperty);
        TableColumn<ImportReceiptDetailModelTable, String> colProductName = CreateColumnTableUtil.createColumn("Tên SP", ImportReceiptDetailModelTable::productNameProperty);
        TableColumn<ImportReceiptDetailModelTable, Number> colPlannedQty = CreateColumnTableUtil.createColumn("SL theo CT", ImportReceiptDetailModelTable::plannedQuantityProperty);
        TableColumn<ImportReceiptDetailModelTable, Number> colActualQty = CreateColumnTableUtil.createColumn("SL thực tế", ImportReceiptDetailModelTable::actualQuantityProperty);
        TableColumn<ImportReceiptDetailModelTable, Number> colUnitPrice = CreateColumnTableUtil.createColumn("Đơn giá", ImportReceiptDetailModelTable::unitPriceProperty);
        TableColumn<ImportReceiptDetailModelTable, Number> colTotalPrice = CreateColumnTableUtil.createColumn("Thành tiền", ImportReceiptDetailModelTable::totalPriceProperty);

        setColumnPercentWidth(colProductId,           15);
        setColumnPercentWidth(colProductName,15);
        setColumnPercentWidth(colPlannedQty,     15);
        setColumnPercentWidth(colActualQty,  15);
        setColumnPercentWidth(colUnitPrice,      20);
        setColumnPercentWidth(colTotalPrice,      20);

        productTable.getColumns().addAll(
                colProductId, colProductName, colPlannedQty, colActualQty, colUnitPrice, colTotalPrice
        );
        productTable.setPrefHeight(600);
        productTable.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #c1dfee; -fx-border-width: 1px;");

        productTable.getColumns().forEach(col -> {
           col.setResizable(false);
        });

        VBox box = new VBox(productTable);
        box.setSpacing(0);
        box.setPadding(Insets.EMPTY);

        box.setStyle("-fx-padding: 0; -fx-background-insets: 0;");

        Platform.runLater(() -> {
            productTable.lookupAll(".column-header").forEach(node -> {
                node.setStyle("-fx-background-color: #e1f0f7; -fx-pref-height: 35px; " +
                        "-fx-border-width: 0 1px 0 0; -fx-border-color: #c1dfee");
            });

            productTable.lookupAll(".column-header .label").forEach(label -> {
                label.setStyle("-fx-text-fill: #34536e;");
            });
        });

        productTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ImportReceiptDetailModelTable item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: #2f7a9a; -fx-text-fill: white;");
                } else {
                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color: #ffffff;");
                    } else {
                        setStyle("-fx-background-color: #e0f2f7;");
                    }
                }
            }
            {
                selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {

                    updateItem(getItem(), isEmpty());
                });
            }
        });

        // set input search for product table
        HBox inputSearch = createFilterRowProductOfImportReceipt();
        box.getChildren().add(inputSearch);
        return box;
    }

    public void showTable() {
        ImportReceiptPresenter presenter = ImportReceiptPresenter.getInstance();
        List<ImportReceiptModel> importReceiptModels = presenter.loadImportReceiptList();
        List<ImportReceiptModelTable> tableModels = GenericConverterFromModelToTableData.convertToList(
                importReceiptModels, ImportReceiptModelMapper.INSTANCE::toViewModel
        );
        allReceiptData.setAll(tableModels);
        receiptData.setAll(tableModels);
        updatePagination();
    }

    private void showItemDetails(long importReceiptId) {
        ImportReceiptPresenter presenter = ImportReceiptPresenter.getInstance();
        List<ImportReceiptDetailModel> importReceiptDetailModels = presenter.loadImportReceiptDetailList(importReceiptId);
        List<ImportReceiptDetailModelTable> detailTables =
                GenericConverterFromModelToTableData.convertToList(importReceiptDetailModels, ImportReceiptDetailModelMapper.INSTANCE::toViewModel);
        productData.setAll(detailTables);
        allProductData.setAll(detailTables);
        productTable.setItems(productData);
        updateTableHeight(productTable, productData.size());
    }

    private HBox createFilterRow() {
        tfId = new TextField();
        tfId.setPromptText("Tìm mã phiếu");
        tfId.textProperty().addListener((obs, oldVal, newVal) -> filterReceipts());
        tfId.setStyle("-fx-background-color: white; -fx-border-color: #c0e0eb; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-background-radius: 5px;");

        tfInvoiceNumber = new TextField();
        tfInvoiceNumber.setPromptText("Tìm số phiếu");
        tfInvoiceNumber.textProperty().addListener((obs, oldVal, newVal) -> filterReceipts());
        tfInvoiceNumber.setStyle("-fx-background-color: white; -fx-border-color: #c0e0eb; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-background-radius: 5px;");

        tfCreateAt = new TextField();
        tfCreateAt.setPromptText("Tìm ngày tạo");
        tfCreateAt.textProperty().addListener((obs, oldVal, newVal) -> filterReceipts());
        tfCreateAt.setStyle("-fx-background-color: white; -fx-border-color: #c0e0eb; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-background-radius: 5px;");

        tfInvoice = new TextField();
        tfInvoice.setPromptText("Tìm số HĐ");
        tfInvoice.textProperty().addListener((obs, oldVal, newVal) -> filterReceipts());
        tfInvoice.setStyle("-fx-background-color: white; -fx-border-color: #c0e0eb; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 5px;");

        tfCompany = new TextField();
        tfCompany.setPromptText("Tìm công ty");
        tfCompany.textProperty().addListener((obs, oldVal, newVal) -> filterReceipts());
        tfCompany.setStyle("-fx-background-color: white; -fx-border-color: #c0e0eb; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-background-radius: 5px;");

        tfWarehouse = new TextField();
        tfWarehouse.setPromptText("Tìm kho");
        tfWarehouse.textProperty().addListener((obs, oldVal, newVal) -> filterReceipts());
        tfWarehouse.setStyle("-fx-background-color: white; -fx-border-color: #c0e0eb; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-background-radius: 5px;");

        HBox filterRow = new HBox(
                tfId, tfInvoiceNumber, tfCreateAt,
                tfInvoice, tfCompany,
                tfWarehouse
        );
        filterRow.setSpacing(5);
        filterRow.setPadding(new Insets(2));
        filterRow.setStyle("-fx-background-color: #e1f0f7; -fx-padding: 5px; -fx-spacing: 5px; -fx-border-width: 0 0 1px 0; -fx-border-color: #c0e0eb");
        return filterRow;
    }

    private HBox createFilterRowProductOfImportReceipt() {
        tfProductIdImportReceipt = new TextField();
        tfProductIdImportReceipt.setPromptText("Tìm mã sản phẩm");
        tfProductIdImportReceipt.textProperty().addListener((obs, oldVal, newVal) -> filterProductOfReceipt());
        tfProductIdImportReceipt.setStyle("-fx-background-color: white; -fx-border-color: #c0e0eb; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-background-radius: 5px;");

        tfProductNameImportReceipt = new TextField();
        tfProductNameImportReceipt.setPromptText("Tìm tên sản phẩm");
        tfProductNameImportReceipt.textProperty().addListener((obs, oldVal, newVal) -> filterProductOfReceipt());
        tfProductNameImportReceipt.setStyle("-fx-background-color: white; -fx-border-color: #c0e0eb; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-background-radius: 5px;");

        HBox filterRow = new HBox(tfProductIdImportReceipt, tfProductNameImportReceipt);
        filterRow.setSpacing(5);
        filterRow.setPadding(new Insets(2));
        filterRow.setStyle("-fx-background-color: #e1f0f7; -fx-padding: 5px; -fx-spacing: 5px; -fx-border-width: 0 0 1px 0; -fx-border-color: #c0e0eb");
        return filterRow;
    }

    private void filterReceipts() {
        ObservableList<ImportReceiptModelTable> filteredData = FXCollections.observableArrayList();
        String idFilter = tfId.getText() != null ? tfId.getText().trim().toLowerCase() : "";
        String invoiceNumberFilter = tfInvoiceNumber.getText() != null ? tfInvoiceNumber.getText().trim().toLowerCase() : "";
        String createAtFilter = tfCreateAt.getText() != null ? tfCreateAt.getText().trim().toLowerCase() : "";
        String invoiceFilter = tfInvoice.getText() != null ? tfInvoice.getText().trim().toLowerCase() : "";
        String companyFilter = tfCompany.getText() != null ? tfCompany.getText().trim().toLowerCase() : "";
        String warehouseFilter = tfWarehouse.getText() != null ? tfWarehouse.getText().trim().toLowerCase() : "";

        for (ImportReceiptModelTable item : allReceiptData) {
            boolean match = true;
            String idStr = item.getId() != null ? String.valueOf(item.getId()).toLowerCase() : "";
            String invoiceNumber = item.getInvoiceNumber() != null ? item.getInvoiceNumber().toLowerCase() : "";
            String createAt = item.getCreateAt() != null ? item.getCreateAt().toLowerCase() : "";
            String invoice = item.getInvoice() != null ? item.getInvoice().toLowerCase() : "";
            String company = item.getCompanyName() != null ? item.getCompanyName().toLowerCase() : "";
            String warehouse = item.getWarehouseName() != null ? item.getWarehouseName().toLowerCase() : "";

            if (!idFilter.isEmpty() && !idStr.contains(idFilter)) match = false;
            if (!invoiceNumberFilter.isEmpty() && !invoiceNumber.contains(invoiceNumberFilter)) match = false;
            if (!createAtFilter.isEmpty() && !createAt.contains(createAtFilter)) match = false;
            if (!invoiceFilter.isEmpty() && !invoice.contains(invoiceFilter)) match = false;
            if (!companyFilter.isEmpty() && !company.contains(companyFilter)) match = false;
            if (!warehouseFilter.isEmpty() && !warehouse.contains(warehouseFilter)) match = false;

            if (match) filteredData.add(item);
        }

        receiptData.setAll(filteredData);
        updatePagination();
    }

    private void filterProductOfReceipt() {
        ObservableList<ImportReceiptDetailModelTable> filteredData = FXCollections.observableArrayList();
        String productIdInput = tfProductIdImportReceipt.getText() != null ? tfProductIdImportReceipt.getText().trim().toLowerCase() : "";
        String productNameInput = tfProductNameImportReceipt.getText() != null ? tfProductNameImportReceipt.getText().trim().toLowerCase() : "";
        for (ImportReceiptDetailModelTable item : allProductData) {
            boolean match = true;
            String idStr = item.getProductId() != null ? String.valueOf(item.getId()).toLowerCase() : "";
            String productNameStr = item.getProductName() != null ? item.getProductName().toLowerCase() : "";

            if (!productIdInput.isEmpty() && !idStr.contains(productIdInput)) match = false;
            if (!productNameInput.isEmpty() && !productNameStr.contains(productNameInput)) match = false;

            if (match) {
                filteredData.add(item);
            }
        }
        productData.setAll(filteredData);
    }

    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) receiptData.size() / itemsPerPage);
        receiptPagination.setPageCount(pageCount > 0 ? pageCount : 1);
        receiptPagination.setCurrentPageIndex(0);
        int fromIndex = 0;
        int toIndex = Math.min(itemsPerPage, receiptData.size());
        receiptTable.setItems(FXCollections.observableArrayList(receiptData.subList(fromIndex, toIndex)));
        updateTableHeight(receiptTable, Math.min(itemsPerPage, receiptData.size()));
    }

    private void updateTableHeight(TableView<?> table, int itemCount) {
        double prefHeight = HEADER_HEIGHT + (itemCount * ROW_HEIGHT);
//        table.setPrefHeight(300);
        table.setMaxHeight(MAX_TABLE_HEIGHT);
        table.setMinHeight(HEADER_HEIGHT);
    }

}