package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.interfaceActionHandler.TopBarActionHandler;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ExportReceiptDetailModelTableMapper;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ExportReceiptModelTableMapper;
import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ExportReceiptModel;
import com.manager.stock.manager_stock.model.tableData.ExportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ExportReceiptModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;
import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.screen.transaction.presenter.ExportReceiptPresenter;
import com.manager.stock.manager_stock.utils.AlertUtils;
import com.manager.stock.manager_stock.utils.CreateColumnTableUtil;
import com.manager.stock.manager_stock.utils.GenericConverterBetweenModelAndTableData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptScreen extends BaseReceiptScreen<ExportReceiptModelTable, ExportReceiptDetailModelTable> {

    private TextField tfInvoiceNumber, tfCreateAt, tfReceiver, tfProductNameExportReceipt, tfProductIdExportReceipt;

    public ExportReceiptScreen() {
        super();
    }

    @Override
    protected HBox createFilterRow() {
        tfInvoiceNumber = new TextField();
        tfInvoiceNumber.setPromptText("Tìm số hóa đơn");
        tfInvoiceNumber.textProperty().addListener((obs, oldVal, newVal) -> filterReceipts());
        tfInvoiceNumber.setStyle("-fx-background-color: white; -fx-border-color: #c0e0eb; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-background-radius: 5px;");

        tfCreateAt = new TextField();
        tfCreateAt.setPromptText("Tìm ngày tạo");
        tfCreateAt.textProperty().addListener((obs, oldVal, newVal) -> filterReceipts());
        tfCreateAt.setStyle("-fx-background-color: white; -fx-border-color: #c0e0eb; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-background-radius: 5px;");

        tfReceiver = new TextField();
        tfReceiver.setPromptText("Tìm người nhận");
        tfReceiver.textProperty().addListener((obs, oldVal, newVal) -> filterReceipts());
        tfReceiver.setStyle("-fx-background-color: white; -fx-border-color: #c0e0eb; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 5px;");
        HBox filterRow = new HBox(
                tfInvoiceNumber, tfCreateAt, tfReceiver
        );
        filterRow.setSpacing(5);
        filterRow.setPadding(new Insets(2));
        filterRow.setStyle("-fx-background-color: #e1f0f7; -fx-padding: 5px; -fx-spacing: 5px; -fx-border-width: 0 0 1px 0; -fx-border-color: #c0e0eb");
        return filterRow;
    }

    @Override
    protected void createReceiptTable() {
        TableColumn<ExportReceiptModelTable, String> colInvoiceNumber = CreateColumnTableUtil.createColumn("Số hóa đơn", ExportReceiptModelTable::invoiceNumberProperty);
        TableColumn<ExportReceiptModelTable, String> colCreateAt = CreateColumnTableUtil.createColumn("Ngày tạo", ExportReceiptModelTable::createAtProperty);
        TableColumn<ExportReceiptModelTable, String> colReceiver = CreateColumnTableUtil.createColumn("Người nhận", ExportReceiptModelTable::receiverProperty);
        TableColumn<ExportReceiptModelTable, String> colReason = CreateColumnTableUtil.createColumn("Lý do xuất", ExportReceiptModelTable::reasonProperty);
        TableColumn<ExportReceiptModelTable, String> colTotalPrice = CreateColumnTableUtil.createColumn("Thành tiền", ExportReceiptModelTable::totalPriceFormatProperty);

        setColumnPercentWidth(colInvoiceNumber,15);
        setColumnPercentWidth(colCreateAt,     20);
        setColumnPercentWidth(colReceiver,  20);
        setColumnPercentWidth(colReason,      30);
        setColumnPercentWidth(colTotalPrice,   15);

        receiptTable.getColumns().setAll(
                colInvoiceNumber, colCreateAt, colReceiver,
                colReason, colTotalPrice
        );
        receiptTable.getColumns().forEach(col -> col.setResizable(false));

        receiptTable.setItems(receiptData);
        receiptTable.setPrefHeight(600);
        receiptTable.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #c1dfee; -fx-border-width: 1px;");

        receiptTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ExportReceiptModelTable item, boolean empty) {
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

    private HBox createFilterRowProductOfImportReceipt() {
        tfProductIdExportReceipt = new TextField();
        tfProductIdExportReceipt.setPromptText("Tìm mã sản phẩm");
        tfProductIdExportReceipt.textProperty().addListener((obs, oldVal, newVal) -> filterProductOfReceipt());
        tfProductIdExportReceipt.setStyle("-fx-background-color: white; -fx-border-color: #c0e0eb; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-background-radius: 5px;");

        tfProductNameExportReceipt = new TextField();
        tfProductNameExportReceipt.setPromptText("Tìm tên sản phẩm");
        tfProductNameExportReceipt.textProperty().addListener((obs, oldVal, newVal) -> filterProductOfReceipt());
        tfProductNameExportReceipt.setStyle("-fx-background-color: white; -fx-border-color: #c0e0eb; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-background-radius: 5px;");

        HBox filterRow = new HBox(tfProductIdExportReceipt, tfProductNameExportReceipt);
        filterRow.setSpacing(5);
        filterRow.setPadding(new Insets(2));
        filterRow.setStyle("-fx-background-color: #e1f0f7; -fx-padding: 5px; -fx-spacing: 5px; -fx-border-width: 0 0 1px 0; -fx-border-color: #c0e0eb");
        return filterRow;
    }

    @Override
    protected VBox createItemDetailByReceipt() {
        TableColumn<ExportReceiptDetailModelTable, String> colProductId = CreateColumnTableUtil.createColumn("Mã SP", ExportReceiptDetailModelTable::productCodeProperty);
        TableColumn<ExportReceiptDetailModelTable, String> colProductName = CreateColumnTableUtil.createColumn("Tên SP", ExportReceiptDetailModelTable::productNameProperty);
        TableColumn<ExportReceiptDetailModelTable, Number> colPlannedQty = CreateColumnTableUtil.createColumn("SL theo CT", ExportReceiptDetailModelTable::plannedQuantityProperty);
        TableColumn<ExportReceiptDetailModelTable, Number> colActualQty = CreateColumnTableUtil.createColumn("SL thực tế", ExportReceiptDetailModelTable::actualQuantityProperty);
        TableColumn<ExportReceiptDetailModelTable, String> colUnitPrice = CreateColumnTableUtil.createColumn("Đơn giá", ExportReceiptDetailModelTable::unitPriceFormatProperty);
        TableColumn<ExportReceiptDetailModelTable, String> colTotalPrice = CreateColumnTableUtil.createColumn("Thành tiền", ExportReceiptDetailModelTable::totalPriceFormatProperty);

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

        productTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ExportReceiptDetailModelTable item, boolean empty) {
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

        HBox inputSearch = createFilterRowProductOfImportReceipt();
        box.getChildren().add(inputSearch);
        return box;
    }

    @Override
    protected TopBarActionHandler getTopBarHandler() {
        return new TopBarActionHandler() {
            @Override
            public void onAdd() {
                AddOrUpdateExportReceiptScreen addReceiptScreen = new AddOrUpdateExportReceiptScreen(null);
                ScreenNavigator.navigateTo(addReceiptScreen);
            }

            @Override
            public void onEdit() {
                try {
                    System.out.println("Chỉnh sửa hóa đơn");
                    if(selected != null) {
                        System.out.println(selected);
                        AddOrUpdateExportReceiptScreen updateReceiptScreen = new AddOrUpdateExportReceiptScreen(selected);
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
        };
    }

    private void showItemDetails(long exportReceiptId) {
        try {
            ExportReceiptPresenter presenter = ExportReceiptPresenter.getInstance();
            List<ExportReceiptDetailModel> exportReceiptDetailModels = presenter.findAllExportReceiptDetailByExportReceipt(exportReceiptId);
            List<ExportReceiptDetailModelTable> detailTables =
                    GenericConverterBetweenModelAndTableData.convertToList(exportReceiptDetailModels, ExportReceiptDetailModelTableMapper.INSTANCE::toViewModel);
            productData.setAll(detailTables);
            allProductData.setAll(detailTables);
            productTable.setItems(productData);
            updateTableHeight(productTable, productData.size());
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            AlertUtils.alert("Đã xảy ra lỗi trong quá trình xử lý. Vui lòng thử lại sau hoặc liên hệ hỗ trợ.", "ERROR", "Lỗi", "Lỗi trong quá trình xem danh sách sản phẩm của phiếu nhập: " + exportReceiptId);
        }
        catch (DaoException e) {
            e.printStackTrace();
            AlertUtils.alert(e.getMessage(), "ERROR", "Lỗi", "Lỗi trong quá trình xem danh sách sản phẩm của phiếu nhập: " + exportReceiptId);
        }
    }

    private void filterProductOfReceipt() {
        ObservableList<ExportReceiptDetailModelTable> filteredData = FXCollections.observableArrayList();
        String productIdInput = normalizeString(tfProductIdExportReceipt.getText() != null ? tfProductIdExportReceipt.getText().trim().toLowerCase() : "");
        String productNameInput = normalizeString(tfProductNameExportReceipt.getText() != null ? tfProductNameExportReceipt.getText().trim().toLowerCase() : "");
        for (ExportReceiptDetailModelTable item : allProductData) {
            boolean match = true;
            String idStr = normalizeString(item.getProductCode() != null ? item.getProductCode() : "");
            String productNameStr = normalizeString(item.getProductName() != null ? item.getProductName().toLowerCase() : "");

            if (!productIdInput.isEmpty() && !idStr.contains(productIdInput)) match = false;
            if (!productNameInput.isEmpty() && !productNameStr.contains(productNameInput)) match = false;

            if (match) {
                filteredData.add(item);
            }
        }
        productData.setAll(filteredData);
    }

    public void showTable() {
        try {
            this.getStylesheets().add(this.getClass().getResource("/com/manager/stock/manager_stock/css/importReceipt/importReceipt.css").toExternalForm());
            ExportReceiptPresenter presenter = ExportReceiptPresenter.getInstance();
            List<ExportReceiptModel> exportReceiptModels = presenter.findAllExportReceipt(Optional.empty());
            List<ExportReceiptModelTable> tableModels = GenericConverterBetweenModelAndTableData.convertToList(
                    exportReceiptModels, ExportReceiptModelTableMapper.INSTANCE::toViewModel
            );
            allReceiptData.setAll(tableModels);
            receiptData.setAll(tableModels);
            updatePagination();
        }
        catch (DaoException e) {
            AlertUtils.alert(e.getMessage(), "ERROR", "Lỗi", "Lỗi khi load dữ liệu.");
        }
    }

    private void filterReceipts() {
        ObservableList<ExportReceiptModelTable> filteredData = FXCollections.observableArrayList();
        String invoiceNumberFilter = normalizeString(tfInvoiceNumber.getText() != null ? tfInvoiceNumber.getText().trim().toLowerCase() : "");
        String createAtFilter = normalizeString(tfCreateAt.getText() != null ? tfCreateAt.getText().trim().toLowerCase() : "");
        String receiverFilter = normalizeString(tfReceiver.getText() != null ? tfReceiver.getText().trim().toLowerCase() : "");

        for (ExportReceiptModelTable item : allReceiptData) {
            boolean match = true;
            String invoiceNumber = normalizeString(item.getInvoiceNumber() != null ? item.getInvoiceNumber().toLowerCase() : "");
            String createAt = normalizeString(item.getCreateAt() != null ? item.getCreateAt().toLowerCase() : "");
            String receiver = normalizeString(item.getReceiver() != null ? item.getReceiver().toLowerCase() : "");

            if (!invoiceNumberFilter.isEmpty() && !invoiceNumber.contains(invoiceNumberFilter)) match = false;
            if (!createAtFilter.isEmpty() && !createAt.contains(createAtFilter)) match = false;
            if (!receiverFilter.isEmpty() && !receiver.contains(receiverFilter)) match = false;

            if (match) filteredData.add(item);
        }

        receiptData.setAll(filteredData);
        updatePagination();
    }
}
