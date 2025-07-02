package com.manager.stock.manager_stock.screen.product;

import com.manager.stock.manager_stock.model.ProductGroup;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.screen.productGroup.ProductGroupPresenter;
import com.manager.stock.manager_stock.screen.transaction.ImportReceiptPresenter;
import com.manager.stock.manager_stock.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
    private final ProductPresenter productPresenter;
    private final ProductGroupPresenter productGroupPresenter;

    private VBox initHeader() {
        VBox header = new VBox();
        HBox feature = new HBox();
        Label label = new Label("Danh sách sản phẩm");
        label.setId("product-list-label");

        // add button
        Button addButton = new Button("Thêm sản phẩm");
        addButton.setOnAction(e -> {
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
                if("N".equals(selected.getId())) {
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
        table.setId("inventory-product-table");
        TableColumn<ProductModel, String> idCol = Utils.createColumn("Mã", "id");
        TableColumn<ProductModel, String> nameCol = Utils.createColumn("Tên", "name");
        TableColumn<ProductModel, Integer> qtyCol = Utils.createColumn("Số lượng", "quantity");
        TableColumn<ProductModel, Integer> unitPriceCol = Utils.createColumn("Đơn giá", "unitPrice");
        TableColumn<ProductModel, Void> actionCol = createActionColumn("Thao tác");
        table.getColumns().addAll(idCol, nameCol, qtyCol, unitPriceCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        table.setItems(productData);
        comboBox.setItems(productGroupData);
        this.getChildren().addAll(header, table);
        productPresenter = ProductPresenter.getInstance();
        productGroupPresenter = ProductGroupPresenter.getInstance();
    }

    public void showProducts() {
        List<ProductModel> productModels = productPresenter.loadProductListData();
        List<ProductGroup> productGroups = productGroupPresenter.loadProductGroupData();
        productData.addAll(productModels);
        productGroupData.add(0, new ProductGroup("N", "Tất cả"));
        productGroupData.addAll(productGroups);
    }
}
