package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptDetailModelMapper;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptModelMapper;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;
import com.manager.stock.manager_stock.utils.GenericConverterFromModelToTableData;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ImportReceiptScreen extends VBox {

    private final TableView<ImportReceiptModelTable> receiptTable = new TableView<>();
    private final TableView<ImportReceiptDetailModelTable> productTable = new TableView<>();

    public ImportReceiptScreen() {
        setSpacing(10);
        setPadding(new Insets(10));

        // Tạo từng phần
        HBox topBar = createTopBar();
        VBox receiptSection = createImportReceiptTable();
        VBox itemDetailSection = createItemDetailByReceipt();

        // Thêm vào VBox chính
        getChildren().addAll(topBar, receiptSection, itemDetailSection);
        VBox.setVgrow(receiptSection, Priority.ALWAYS);
    }

    private HBox createTopBar() {
        Button btnAdd = new Button("Thêm");
        Button btnEdit = new Button("Sửa");
        Button btnDelete = new Button("Xóa");
        Button btnReload = new Button("Tải lại");
        Button btnPrint = new Button("In");
        Button btnExport = new Button("Xuất");

        HBox topBar = new HBox(10, btnAdd, btnEdit, btnDelete, btnReload, btnPrint, btnExport);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(5));
        return topBar;
    }

    private VBox createImportReceiptTable() {
        TableColumn<ImportReceiptModelTable, Number> colId = createColumn("Mã phiếu", ImportReceiptModelTable::idProperty);
        TableColumn<ImportReceiptModelTable, String> colInvoiceNumber = createColumn("Số phiếu nhập", ImportReceiptModelTable::invoiceNumberProperty);
        TableColumn<ImportReceiptModelTable, String> colCreateAt = createColumn("Ngày tạo", ImportReceiptModelTable::createAtProperty);
        TableColumn<ImportReceiptModelTable, String> colDeliveredBy = createColumn("Người giao", ImportReceiptModelTable::deliveredByProperty);
        TableColumn<ImportReceiptModelTable, String> colInvoice = createColumn("Số hóa đơn", ImportReceiptModelTable::invoiceProperty);
        TableColumn<ImportReceiptModelTable, String> colCompany = createColumn("Công ty", ImportReceiptModelTable::companyNameProperty);
        TableColumn<ImportReceiptModelTable, String> colWarehouse = createColumn("Kho", ImportReceiptModelTable::warehouseNameProperty);
        TableColumn<ImportReceiptModelTable, Number> colTotalPrice = createColumn("Tổng tiền", ImportReceiptModelTable::totalPriceProperty);
        TableColumn<ImportReceiptModelTable, String> colTotalPriceInWord = createColumn("Bằng chữ", ImportReceiptModelTable::totalPriceInWordProperty);

        receiptTable.getColumns().setAll(
                colId, colInvoiceNumber, colCreateAt, colDeliveredBy,
                colInvoice, colCompany, colWarehouse, colTotalPrice, colTotalPriceInWord
        );
        receiptTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); // ⚠ Cho phép scroll ngang
        receiptTable.setPrefHeight(250);

        // Khi chọn dòng → hiện chi tiết
        receiptTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                List<ImportReceiptDetailModel> importReceiptDetailModels = new ArrayList<>();
                showItemDetails(importReceiptDetailModels);
            }
        });

        // Bọc table trong ScrollPane để có scroll ngang
        ScrollPane scrollPane = new ScrollPane(receiptTable);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true); // Cho phép scroll ngang khi cần
        scrollPane.setPadding(new Insets(5));
        scrollPane.setStyle("-fx-background-color:transparent;");

        VBox box = new VBox(new Label("Danh sách phiếu nhập"), createSearchRow(), scrollPane);
        box.setSpacing(5);
        return box;
    }



    private VBox createItemDetailByReceipt() {
        TableColumn<ImportReceiptDetailModelTable, Number> colProductId = new TableColumn<>("Mã SP");
        colProductId.setCellValueFactory(cell -> cell.getValue().productIdProperty());

        TableColumn<ImportReceiptDetailModelTable, Number> colPlannedQty = new TableColumn<>("SL theo CT");
        colPlannedQty.setCellValueFactory(cell -> cell.getValue().plannedQuantityProperty());

        TableColumn<ImportReceiptDetailModelTable, Number> colActualQty = new TableColumn<>("SL thực tế");
        colActualQty.setCellValueFactory(cell -> cell.getValue().actualQuantityProperty());

        TableColumn<ImportReceiptDetailModelTable, Number> colUnitPrice = new TableColumn<>("Đơn giá");
        colUnitPrice.setCellValueFactory(cell -> cell.getValue().unitPriceProperty());

        TableColumn<ImportReceiptDetailModelTable, Number> colTotalPrice = new TableColumn<>("Thành tiền");
        colTotalPrice.setCellValueFactory(cell -> cell.getValue().totalPriceProperty());

        productTable.getColumns().addAll(
                colProductId, colPlannedQty, colActualQty, colUnitPrice, colTotalPrice
        );
        productTable.setPrefHeight(180);

        VBox box = new VBox(new Label("Chi tiết phiếu nhập"), productTable);
        return box;
    }


    private <T, R> TableColumn<T, R> createColumn(String title, Function<T, ObservableValue<R>> propertyFunc) {
        TableColumn<T, R> col = new TableColumn<>(title);
        col.setCellValueFactory(cell -> propertyFunc.apply(cell.getValue()));
        return col;
    }


    public void showTable(List<ImportReceiptModel> importReceiptModels) {
        List<ImportReceiptModelTable> tableModels = GenericConverterFromModelToTableData.convertToList(
                importReceiptModels, ImportReceiptModelMapper.INSTANCE::toViewModel
        );
        receiptTable.getItems().setAll(tableModels);
    }

    private void showItemDetails(List<ImportReceiptDetailModel> receiptTableModel) {
        List<ImportReceiptDetailModelTable> detailTables =
                GenericConverterFromModelToTableData.convertToList(receiptTableModel, ImportReceiptDetailModelMapper.INSTANCE::toViewModel);

        productTable.getItems().setAll(detailTables);
    }

    private HBox createSearchRow() {
        TextField tfId = new TextField();
        tfId.setPromptText("Tìm mã phiếu");

        TextField tfInvoiceNumber = new TextField();
        tfInvoiceNumber.setPromptText("Tìm số phiếu");

        TextField tfCreateAt = new TextField();
        tfCreateAt.setPromptText("Tìm ngày tạo");

        TextField tfDeliveredBy = new TextField();
        tfDeliveredBy.setPromptText("Tìm người giao");

        TextField tfInvoice = new TextField();
        tfInvoice.setPromptText("Tìm số HĐ");

        TextField tfCompany = new TextField();
        tfCompany.setPromptText("Tìm công ty");

        TextField tfWarehouse = new TextField();
        tfWarehouse.setPromptText("Tìm kho");

        TextField tfTotal = new TextField();
        tfTotal.setPromptText("Tìm tổng tiền");

        TextField tfInWord = new TextField();
        tfInWord.setPromptText("Tìm chữ");

        HBox searchRow = new HBox(
                tfId, tfInvoiceNumber, tfCreateAt, tfDeliveredBy,
                tfInvoice, tfCompany, tfWarehouse, tfTotal, tfInWord
        );
        searchRow.setSpacing(5);
        searchRow.setPadding(new Insets(5));

        return searchRow;
    }

}
