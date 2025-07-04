package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.interfaceActionHandler.TopBarActionHandler;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptDetailModelMapper;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptModelMapper;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;
import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.utils.AlertUtils;
import com.manager.stock.manager_stock.utils.CreateColumnTableUtil;
import com.manager.stock.manager_stock.utils.CreateTopBarOfReceiptUtil;
import com.manager.stock.manager_stock.utils.GenericConverterBetweenModelAndTableData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

public class ImportReceiptScreen extends VBox {

    private final TableView<ImportReceiptModelTable> receiptTable = new TableView<>();
    private final TableView<ImportReceiptDetailModelTable> productTable = new TableView<>();
    private Pagination receiptPagination;
    private final ObservableList<ImportReceiptModelTable> allReceiptData = FXCollections.observableArrayList(); // Original data
    private final ObservableList<ImportReceiptModelTable> receiptData = FXCollections.observableArrayList(); // Filtered data
    private final ObservableList<ImportReceiptDetailModelTable> allProductData = FXCollections.observableArrayList();
    private final ObservableList<ImportReceiptDetailModelTable> productData = FXCollections.observableArrayList();
    private int itemsPerPage = 10;
    private TextField tfId, tfInvoiceNumber, tfCreateAt, tfInvoice, tfCompany, tfWarehouse, tfProductNameImportReceipt, tfProductIdImportReceipt;
    private static final double ROW_HEIGHT = 30.0;
    private static final double HEADER_HEIGHT = 30.0;
    private static final double MAX_TABLE_HEIGHT = 400.0;
    private ImportReceiptModelTable selected;

    public ImportReceiptScreen() {
        setSpacing(0);
        setStyle("-fx-padding: 0 5px 0 5px; -fx-background-insets: 0; -fx-background-color: #e1f0f7");

        HBox topBar = CreateTopBarOfReceiptUtil.createTopBar(new TopBarActionHandler() {
            @Override
            public void onAdd() {
                AddOrUpdateReceiptScreen addReceiptScreen = new AddOrUpdateReceiptScreen(null);
                ScreenNavigator.navigateTo(addReceiptScreen);
            }

            @Override
            public void onEdit() {
                try {
                    System.out.println("Chỉnh sửa hóa đơn");
                    if(selected != null) {
                        System.out.println(selected);
                        AddOrUpdateReceiptScreen updateReceiptScreen = new AddOrUpdateReceiptScreen(selected);
                        ScreenNavigator.navigateTo(updateReceiptScreen);
                    }
                    else {
                        AlertUtils.alert("Vui lòng chọn hóa đơn cần sửa.", "WARNING", "Cảnh báo", "Chưa chọn hóa đơn.");
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onDelete() {
                System.out.println("Xóa hóa đơn");
                if(selected != null) {
                    System.out.println("Xóa hóa đơn:  " + selected);
                    boolean isConfirmDelete = AlertUtils.confirm("Bạn có chắc muốn xóa phiếu số: " + selected.getInvoice());
                    if(isConfirmDelete) {

                    }
                }
                else {
                    AlertUtils.alert("Vui lòng chọn hóa đơn muốn xóa.", "WARNING", "Cảnh báo", "Chưa chọn hóa đơn nào");
                }
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
        TableColumn<ImportReceiptModelTable, String> colInvoiceNumber = CreateColumnTableUtil.createColumn("Số hóa đơn", ImportReceiptModelTable::invoiceNumberProperty);
        TableColumn<ImportReceiptModelTable, String> colCreateAt = CreateColumnTableUtil.createColumn("Ngày tạo", ImportReceiptModelTable::createAtProperty);
        TableColumn<ImportReceiptModelTable, String> colDeliveredBy = CreateColumnTableUtil.createColumn("Người giao", ImportReceiptModelTable::deliveredByProperty);
        TableColumn<ImportReceiptModelTable, String> colInvoice = CreateColumnTableUtil.createColumn("Số phiếu nhập", ImportReceiptModelTable::invoiceProperty);
        TableColumn<ImportReceiptModelTable, String> colCompany = CreateColumnTableUtil.createColumn("Công ty", ImportReceiptModelTable::companyNameProperty);
        TableColumn<ImportReceiptModelTable, String> colWarehouse = CreateColumnTableUtil.createColumn("Kho", ImportReceiptModelTable::warehouseNameProperty);
        TableColumn<ImportReceiptModelTable, String> colTotalPrice = CreateColumnTableUtil.createColumn("Thành tiền", ImportReceiptModelTable::totalPriceFormatProperty);

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
            selected = receiptTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                long id = selected.getId();
                showItemDetails(id);
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
        TableColumn<ImportReceiptDetailModelTable, String> colProductId = CreateColumnTableUtil.createColumn("Mã SP", ImportReceiptDetailModelTable::codeProperty);
        TableColumn<ImportReceiptDetailModelTable, String> colProductName = CreateColumnTableUtil.createColumn("Tên SP", ImportReceiptDetailModelTable::productNameProperty);
        TableColumn<ImportReceiptDetailModelTable, Number> colPlannedQty = CreateColumnTableUtil.createColumn("SL theo CT", ImportReceiptDetailModelTable::plannedQuantityProperty);
        TableColumn<ImportReceiptDetailModelTable, Number> colActualQty = CreateColumnTableUtil.createColumn("SL thực tế", ImportReceiptDetailModelTable::actualQuantityProperty);
        TableColumn<ImportReceiptDetailModelTable, String> colUnitPrice = CreateColumnTableUtil.createColumn("Đơn giá", ImportReceiptDetailModelTable::unitPriceFormatProperty);
        TableColumn<ImportReceiptDetailModelTable, String> colTotalPrice = CreateColumnTableUtil.createColumn("Thành tiền", ImportReceiptDetailModelTable::totalPriceFormatProperty);

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
        try {
            ImportReceiptPresenter presenter = ImportReceiptPresenter.getInstance();
            List<ImportReceiptModel> importReceiptModels = presenter.loadImportReceiptList(Optional.empty());
            List<ImportReceiptModelTable> tableModels = GenericConverterBetweenModelAndTableData.convertToList(
                    importReceiptModels, ImportReceiptModelMapper.INSTANCE::toViewModel
            );
            allReceiptData.setAll(tableModels);
            receiptData.setAll(tableModels);
            updatePagination();
        }
        catch (DaoException e) {
            AlertUtils.alert(e.getMessage(), "ERROR", "Lỗi", "Lỗi khi load dữ liệu.");
        }
    }

    private void showItemDetails(long importReceiptId) {
        try {
            ImportReceiptPresenter presenter = ImportReceiptPresenter.getInstance();
            List<ImportReceiptDetailModel> importReceiptDetailModels = presenter.loadImportReceiptDetailList(importReceiptId);
            List<ImportReceiptDetailModelTable> detailTables =
                    GenericConverterBetweenModelAndTableData.convertToList(importReceiptDetailModels, ImportReceiptDetailModelMapper.INSTANCE::toViewModel);
            productData.setAll(detailTables);
            allProductData.setAll(detailTables);
            productTable.setItems(productData);
            updateTableHeight(productTable, productData.size());
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            AlertUtils.alert("Đã xảy ra lỗi trong quá trình xử lý. Vui lòng thử lại sau hoặc liên hệ hỗ trợ.", "ERROR", "Lỗi", "Lỗi trong quá trình xem danh sách sản phẩm của phiếu nhập: " + importReceiptId);
        }
        catch (DaoException e) {
            e.printStackTrace();
            AlertUtils.alert(e.getMessage(), "ERROR", "Lỗi", "Lỗi trong quá trình xem danh sách sản phẩm của phiếu nhập: " + importReceiptId);
        }
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
        String idFilter = normalizeString(tfId.getText() != null ? tfId.getText().trim().toLowerCase() : "");
        String invoiceNumberFilter = normalizeString(tfInvoiceNumber.getText() != null ? tfInvoiceNumber.getText().trim().toLowerCase() : "");
        String createAtFilter = normalizeString(tfCreateAt.getText() != null ? tfCreateAt.getText().trim().toLowerCase() : "");
        String invoiceFilter = normalizeString(tfInvoice.getText() != null ? tfInvoice.getText().trim().toLowerCase() : "");
        String companyFilter = normalizeString(tfCompany.getText() != null ? tfCompany.getText().trim().toLowerCase() : "");
        String warehouseFilter = normalizeString(tfWarehouse.getText() != null ? tfWarehouse.getText().trim().toLowerCase() : "");

        for (ImportReceiptModelTable item : allReceiptData) {
            boolean match = true;
            String idStr = normalizeString(item.getId() != null ? String.valueOf(item.getId()).toLowerCase() : "");
            String invoiceNumber = normalizeString(item.getInvoiceNumber() != null ? item.getInvoiceNumber().toLowerCase() : "");
            String createAt = normalizeString(item.getCreateAt() != null ? item.getCreateAt().toLowerCase() : "");
            String invoice = normalizeString(item.getInvoice() != null ? item.getInvoice().toLowerCase() : "");
            String company = normalizeString(item.getCompanyName() != null ? item.getCompanyName().toLowerCase() : "");
            String warehouse = normalizeString(item.getWarehouseName() != null ? item.getWarehouseName().toLowerCase() : "");

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
        String productIdInput = normalizeString(tfProductIdImportReceipt.getText() != null ? tfProductIdImportReceipt.getText().trim().toLowerCase() : "");
        String productNameInput = normalizeString(tfProductNameImportReceipt.getText() != null ? tfProductNameImportReceipt.getText().trim().toLowerCase() : "");
        for (ImportReceiptDetailModelTable item : allProductData) {
            boolean match = true;
            String idStr = normalizeString(item.getProductId() != null ? String.valueOf(item.getId()).toLowerCase() : "");
            String productNameStr = normalizeString(item.getProductName() != null ? item.getProductName().toLowerCase() : "");

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

    private String normalizeString(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }

    public ImportReceiptModelTable getReceiptSelected () {
        return this.selected;
    }
}