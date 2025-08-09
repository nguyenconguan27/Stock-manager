package com.manager.stock.manager_stock.screen.product.productList;

import com.manager.stock.manager_stock.model.ProductGroup;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.screen.product.productDetail.ProductDetailScreen;
import com.manager.stock.manager_stock.screen.productGroup.ProductGroupPresenter;
import com.manager.stock.manager_stock.screen.transaction.ImportReceiptScreen;
import com.manager.stock.manager_stock.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ProductScreen extends VBox {

    private final Logger logger = LoggerFactory.getLogger(ProductScreen.class);
    private TableView<ProductModel> table;
    private ComboBox<ProductGroup> comboBox;
    ObservableList<ProductModel> productData = FXCollections.observableArrayList();
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
        System.out.println(productData.size());
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
        VBox groupForm = new VBox();
        tfGroupName = new TextField();
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
        groupForm.getChildren().addAll(tfGroupName, saveButton);
        return groupForm;
    }

    private void initTextFied() {

    }

    private VBox initHeader() {
        VBox header = new VBox();
        HBox feature = new HBox();
        Label label = new Label("Danh sách sản phẩm");
        label.setId("product-list-label");

        // add button
        Button addButton = new Button("Thêm vật tư");
        addButton.setOnAction(e -> {
            ProductDetailScreen productDetailScreen = new ProductDetailScreen();
            productDetailScreen.showProduct(-1);
            ScreenNavigator.navigateTo(productDetailScreen);
        });

        // text search
        TextField searchText = new TextField();
        searchText.setPromptText("Nhập từ khóa...");
        Button searchButton = new Button("🔍");
        searchButton.setOnAction(e -> {
            String keyword = searchText.getText();
            logger.info("Search product name: {}", keyword);
            productData.clear();
            productData.addAll(productPresenter.loadProductByName(keyword));
        });

        HBox searchBox = new HBox(5, searchText, searchButton);
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
        feature.getChildren().addAll(addButton, spacer1, searchBox, spacer2, comboBox);

        header.getChildren().addAll(label, feature);
        return header;
    }

    private void initTableView() {
        table = new TableView<>();
        TableColumn<ProductModel, String> idCol = Utils.createColumn("Mã", "code");
        TableColumn<ProductModel, String> nameCol = Utils.createColumn("Tên", "name");
        TableColumn<ProductModel, Integer> qtyCol = Utils.createColumn("Số lượng", "quantity");
        TableColumn<ProductModel, Integer> unitPriceCol = Utils.createColumn("Đơn giá", "unitPrice");
        TableColumn<ProductModel, Void> actionCol = createActionColumn("Thao tác");
        table.getColumns().addAll(idCol, nameCol, qtyCol, unitPriceCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(600);
        table.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #c1dfee; -fx-border-width: 1px;");
    }

    private TableColumn createActionColumn(String title) {
        TableColumn<ProductModel, Void> actionCol = new TableColumn<>(title);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Sửa");
            private final Button deleteButton = new Button("Xóa");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    ProductModel product = getTableView().getItems().get(getIndex());
                    ProductDetailScreen productDetailScreen = new ProductDetailScreen();
                    productDetailScreen.showProduct(product.getId());
                    ScreenNavigator.navigateTo(productDetailScreen);
                });
                deleteButton.setOnAction(event -> {
                    ProductModel product = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(product);
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
        this.getChildren().addAll(header, tableSection, groupForm);
        productPresenter = ProductPresenter.getInstance();
        productGroupPresenter = ProductGroupPresenter.getInstance();
        currentProductGroup = new ProductGroup();
    }

    public void showProducts() {
        List<ProductModel> productModels = productPresenter.loadProductListData();
        List<ProductGroup> productGroups = productGroupPresenter.loadProductGroupData();
        productData.addAll(productModels);
        productGroupData.add(0, new ProductGroup(-1, "Tất cả"));
        productGroupData.addAll(productGroups);
        updatePagination();
        this.getStylesheets().add(this.getClass().getResource("/com/manager/stock/manager_stock/css/importReceipt/importReceipt.css").toExternalForm());
    }
}
