package com.manager.stock.manager_stock.screen.product.productList;

import com.manager.stock.manager_stock.interfaceActionHandler.TopBarActionHandler;
import com.manager.stock.manager_stock.model.ProductGroup;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.model.tableData.ExportReceiptModelTable;
import com.manager.stock.manager_stock.reportservice.ExportAll;
import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.screen.product.productDetail.ProductDetailScreen;
import com.manager.stock.manager_stock.screen.productGroup.ProductGroupPresenter;
import com.manager.stock.manager_stock.screen.transaction.ImportReceiptScreen;
import com.manager.stock.manager_stock.utils.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.Normalizer;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ProductScreen extends VBox {

    private final Logger logger = LoggerFactory.getLogger(ProductScreen.class);
    private TableView<ProductModel> table;
    private ComboBox<ProductGroup> comboBox;
    ObservableList<ProductModel> productData = FXCollections.observableArrayList();
    ObservableList<ProductModel> allData = FXCollections.observableArrayList();
    ObservableList<ProductGroup> productGroupData = FXCollections.observableArrayList();
    TextField tfCode, tfName;
    Pagination pagination;
    int itemsPerPage = 10;
    protected static final double HEADER_HEIGHT = 30.0;
    protected static final double MAX_TABLE_HEIGHT = 400.0;
    private final ProductPresenter productPresenter;
    private final ProductGroupPresenter productGroupPresenter;
    private TextField tfGroupName;
    private ProductGroup currentProductGroup;

    private void createPagination() {
        pagination = new Pagination();
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, productData.size());
            table.setItems(FXCollections.observableArrayList(productData.subList(fromIndex, toIndex)));
            updateTableHeight(table, Math.min(itemsPerPage, productData.size()));
            return new VBox(table);
        });

        pagination.getStylesheets().add(
                ProductScreen.class.getResource("/com/manager/stock/manager_stock/css/importReceipt/pagination.css").toExternalForm()
        );

        pagination.setStyle("-fx-padding: 0");
    }

    protected void updateTableHeight(TableView<?> table, int itemCount) {
        table.setMaxHeight(MAX_TABLE_HEIGHT);
        table.setMinHeight(HEADER_HEIGHT);
    }

    protected void updatePagination() {
        int pageCount = (int) Math.ceil((double) productData.size() / itemsPerPage);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
        pagination.setCurrentPageIndex(0);
        int fromIndex = 0;
        int toIndex = Math.min(itemsPerPage, productData.size());
        table.setItems(FXCollections.observableArrayList(productData.subList(fromIndex, toIndex)));
        updateTableHeight(table, Math.min(itemsPerPage, productData.size()));
    }

    private VBox initAddGroupForm() {
        VBox groupForm = new VBox(15);
        Label lbGroup = new Label("Thêm mới nhóm vật tư");
        lbGroup.setStyle("-fx-font-size: 14px");
        tfGroupName = new TextField();
        tfGroupName.setStyle("-fx-max-height: 50px; -fx-pref-height: 30px; -fx-font-size: 14px;");
        VBox groupSection = new VBox(10, lbGroup, tfGroupName);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            currentProductGroup.setName(tfGroupName.getText());
            currentProductGroup.setId(System.currentTimeMillis());
            productGroupPresenter.saveGroupProduct(currentProductGroup);
            productGroupData.add(currentProductGroup);
            currentProductGroup = new ProductGroup();
            tfGroupName.setText("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Thêm nhóm vật tư thành công!");
            alert.showAndWait();
        });
        VBox btnSection = new VBox(saveButton);
        btnSection.setAlignment(Pos.CENTER_RIGHT);
        AddCssStyleForBtnUtil.addCssStyleForBtn(saveButton);
        groupForm.getChildren().addAll(groupSection, btnSection);
        groupForm.setPadding(new Insets(10));
        return groupForm;
    }

    private VBox initHeader() {
        HBox topBar = CreateTopBarOfReceiptUtil.createTopBar(new TopBarActionHandler() {
            @Override
            public void onAdd() {
                ProductDetailScreen productDetailScreen = new ProductDetailScreen(productData);
                productDetailScreen.showProduct(-1);
                ScreenNavigator.navigateTo(productDetailScreen);
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

            @Override
            public void onExportAll() {
                try {
                    File file = ChoosesFolderOutput.choosesFolderFile("Tong_hop");
                    if(file == null) {
                        return;
                    }
                    String outputPath = file.getAbsolutePath();
                    ExportAll.exportTotal(outputPath);
                    // gọi hàm tạo file xlsx
                    AlertUtils.alert("Xuất file thành công:\n" + file.getAbsolutePath(),
                            "INFORMATION", "Thành công", "Xuất dữ liệu");
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertUtils.alert("Có lỗi khi xuất file: " + e.getMessage(),
                            "ERROR", "Lỗi", "Xuất dữ liệu thất bại");
                }
            }
        });

        VBox header = new VBox();
        HBox feature = new HBox();

        // text search
        tfName = new TextField();
        tfName.setPromptText("Tìm tên");
        tfName.textProperty().addListener((obs, oldVal, newVal) -> filter());

        tfCode = new TextField();
        tfCode.setPromptText("Tìm mã");
        tfCode.textProperty().addListener((obs, oldVal, newVal) -> filter());

        HBox searchBox = new HBox(20, tfCode, tfName);
        // list product group
        comboBox = new ComboBox<>();
        //init data lần đầu hiển thị
        productGroupData.addListener((ListChangeListener<? super ProductGroup>) change -> {
            if (!productGroupData.isEmpty() && comboBox.getSelectionModel().isEmpty()) {
                comboBox.getSelectionModel().selectFirst();
            }
        });

        // hiển thị drop down
        comboBox.setCellFactory(cb -> new ListCell<>(){
            @Override
            protected void updateItem(ProductGroup item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });
        // hiển thị action được chọn
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ProductGroup item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });
        // action khi chọn item
        comboBox.setOnAction(event -> {
            ProductGroup selected = comboBox.getSelectionModel().getSelectedItem();
            logger.info("Search product group: {} - {}", selected.getId(), selected.getName());
            if (selected != null) {
                productData.clear();
                if(selected.getId() == -1) {
                    productData.addAll(productPresenter.loadProductListData());
                }
                else {
                    productData.addAll(productPresenter.loadProductByGroup(selected.getId()));
                }
            }
        });
        comboBox.prefWidthProperty().bind(feature.widthProperty().multiply(0.3));

        feature.setPadding(new Insets(10));
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        feature.getChildren().addAll(searchBox, spacer2, comboBox);
        feature.setStyle("-fx-background-color: #e1f0f7");
        header.getChildren().addAll(topBar, feature);
        header.setStyle("");
        return header;
    }

    private void initTableView() {
        table = new TableView<>();
        TableColumn<ProductModel, String> idCol = Utils.createColumn("Mã", "code");
        TableColumn<ProductModel, String> nameCol = Utils.createColumn("Tên", "name");
        TableColumn<ProductModel, Integer> qtyCol = Utils.createColumn("Số lượng", "quantity");
        TableColumn<ProductModel, Integer> unitPriceCol = Utils.createColumn("Đơn giá", "unitPrice");
        TableColumn<ProductModel, Void> actionCol = createActionColumn("Thao tác");
        TableColumn<ProductModel, Integer> startSemQCol = Utils.createColumn("Số lượng đầu kỳ", "startSemQ");
        TableColumn<ProductModel, Integer> startSemTCol = Utils.createColumn("Tổng tiền đầu kỳ", "startSemT");
        TableColumn<ProductModel, Integer> total = Utils.createColumn("Tổng tiền", "total");
        table.getColumns().addAll(idCol, nameCol, qtyCol, unitPriceCol, total, startSemQCol, startSemTCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(600);
        table.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #c1dfee; -fx-border-width: 1px;");
    }

    private TableColumn createActionColumn(String title) {
        TableColumn<ProductModel, Void> actionCol = new TableColumn<>(title);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Sửa");
            private final HBox buttons = new HBox(5, editButton);
            {
                editButton.setOnAction(event -> {
                    ProductModel product = getTableView().getItems().get(getIndex());
                    ProductDetailScreen productDetailScreen = new ProductDetailScreen(productData);
                    productDetailScreen.showProduct(product.getId());
                    ScreenNavigator.navigateTo(productDetailScreen);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
        return actionCol;
    }

    public ProductScreen() {
        VBox header = initHeader();
        initTableView();
        createPagination();
        VBox tableSection = new VBox(table, pagination);
        tableSection.setSpacing(0);
        VBox.setMargin(table, new Insets(0, 0, 10, 0));
        tableSection.setPadding(Insets.EMPTY);
        tableSection.setStyle("-fx-padding: 0; -fx-background-insets: 0;");
        comboBox.setItems(productGroupData);
        VBox groupForm = initAddGroupForm();
        groupForm.setStyle("-fx-background-color: #e1f0f7");
        VBox.setVgrow(groupForm, Priority.ALWAYS);
        this.getChildren().addAll(header, tableSection, groupForm);
        productPresenter = ProductPresenter.getInstance();
        productGroupPresenter = ProductGroupPresenter.getInstance();
        currentProductGroup = new ProductGroup();
    }

    public void showProducts() {
        List<ProductModel> productModels = productPresenter.loadProductListData();
        List<ProductGroup> productGroups = productGroupPresenter.loadProductGroupData();
        productData.addAll(productModels);
        allData.addAll(productModels);
        productGroupData.add(0, new ProductGroup(-1, "Tất cả"));
        productGroupData.addAll(productGroups);
        updatePagination();
        this.getStylesheets().add(this.getClass().getResource("/com/manager/stock/manager_stock/css/importReceipt/importReceipt.css").toExternalForm());
    }

    public void filter() {
        String code = normalizeString(tfCode.getText() == null ? "" : tfCode.getText().trim().toLowerCase());
        String name = normalizeString(tfName.getText() == null ? "" : tfName.getText().trim().toLowerCase());
        ObservableList<ProductModel> filterData  = FXCollections.observableArrayList();
        for(ProductModel productModel: allData) {
            boolean isMatch = true;
            String pcode = normalizeString(productModel.getCode() == null ? "" : productModel.getCode().trim().toLowerCase());
            String pname = normalizeString(productModel.getName() == null ? "" : productModel.getName().trim().toLowerCase());
            if(!code.isEmpty() && !pcode.contains(code)) {
                isMatch = false;
            }
            if(!pname.isEmpty() && !pname.contains(name)) {
                isMatch = false;
            }
            if(isMatch) {
                filterData.add(productModel);
            }
        }
        productData.setAll(filterData);
        updatePagination();
    }

    private String normalizeString(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }
}

