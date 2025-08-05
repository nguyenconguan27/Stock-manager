package com.manager.stock.manager_stock.screen;

import com.manager.stock.manager_stock.screen.product.productList.ProductPresenter;
import com.manager.stock.manager_stock.screen.product.productList.ProductScreen;
import com.manager.stock.manager_stock.screen.productGroup.ProductGroupScreen;
import com.manager.stock.manager_stock.screen.transaction.ExportReceiptScreen;
import com.manager.stock.manager_stock.screen.transaction.ImportReceiptScreen;
import com.manager.stock.manager_stock.utils.CreateLoadingUtil;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.Stack;

/**
 * @author Trọng Hướng
 */
public class ScreenNavigator {
    private static BorderPane rootPane;
    private static final Stack<Node> backStack = new Stack<>();
    private static final Stack<Node> forwardStack = new Stack<>();
    private static Node currentScreen;

    public static void initScreen(BorderPane root) {
        rootPane = root;
        setLeftScreen();
    }

    public static void navigateTo(Node newScreen) {
        if(currentScreen != null) {
            backStack.push(currentScreen);
        }
        forwardStack.clear();
        setScreenInternal(newScreen);
    }

    public static void showLoadingTemporary() {
        setScreenInternal(CreateLoadingUtil.createLoading());
    }

    public static void goBack() {
        if(!backStack.isEmpty()) {
            forwardStack.push(currentScreen);
            Node previousScreen = backStack.pop();
            setScreenInternal(previousScreen);
        }
    }

    public static void goForward() {
        if(!forwardStack.isEmpty()) {
            backStack.push(currentScreen);
            Node nextScreen = forwardStack.pop();
            setScreenInternal(nextScreen);
        }
    }

    private static void setScreenInternal(Node screen) {
        currentScreen = screen;
        rootPane.setCenter(screen);
    }

    private static void setLeftScreen() {
        VBox leftScreen = new VBox(10);
        leftScreen.setStyle("-fx-padding: 10 0 0 0;");

        // init treeView
        TreeItem<String> rootItem = new TreeItem<>();
        rootItem.setExpanded(true);

        rootItem.getChildren().addAll(
                createItem("Quản lý nhóm sản phẩm", "/com/manager/stock/manager_stock/icons/receipt.png", 16, 16),
                createItem("Quản lý sản phẩm", "/com/manager/stock/manager_stock/icons/receipt.png", 16, 16)
        );

        TreeItem<String> transactionManagerItem = createItem("Quản lý phiếu nhập/xuất", "/com/manager/stock/manager_stock/icons/receipt.png", 16,16);
        // tạo các item con bao gồm phiếu nhập và phiếu xuất
        transactionManagerItem.setExpanded(true);
        transactionManagerItem.getChildren().addAll(
                createItem("Phiếu nhập", "/com/manager/stock/manager_stock/icons/receipt.png", 16, 16),
                createItem("Phiếu xuất",  "/com/manager/stock/manager_stock/icons/receipt.png", 16, 16)
        );
        rootItem.getChildren().add(transactionManagerItem);
        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setShowRoot(false);
        VBox.setVgrow(treeView, Priority.ALWAYS);
        leftScreen.getChildren().add(createTitleHeader());
        leftScreen.getChildren().add(treeView);
        rootPane.setLeft(leftScreen);

        // add sự kiện click
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem == null) return;

            String selected = newItem.getValue();
            switch (selected) {
                case "Quản lý nhóm sản phẩm":
                    ScreenNavigator.navigateTo(new ProductGroupScreen());
                    break;
                case "Quản lý sản phẩm":
                    ScreenNavigator.showLoadingTemporary();
                    new Thread(() -> {
                        ProductScreen productScreen = new ProductScreen();

                        Platform.runLater(() -> {
                            productScreen.showProducts();
                            ScreenNavigator.navigateTo(productScreen);
                        });
                    }).start();
                    break;
                case "Phiếu nhập":
                    ScreenNavigator.showLoadingTemporary();
                    new Thread(() -> {
                        ImportReceiptScreen importReceiptScreen = new ImportReceiptScreen();

                        Platform.runLater(() -> {
                            importReceiptScreen.showTable();
                            ScreenNavigator.navigateTo(importReceiptScreen);
                        });
                    }).start();
                   break;
                case "Phiếu xuất":
                    ScreenNavigator.showLoadingTemporary();
                    new Thread(() -> {
                        ExportReceiptScreen exportReceiptScreen = new ExportReceiptScreen();

                        Platform.runLater(() -> {
                            exportReceiptScreen.showTable();
                            ScreenNavigator.navigateTo(exportReceiptScreen);
                        });
                    }).start();
                    break;
            }
        });
    }

    private static TreeItem<String> createItem(String name, String iconPath, int height, int width) {
        Label label = new Label();
        Image image = null;
        try {
            image = new Image(ScreenNavigator.class.getResource(iconPath).toExternalForm());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setPreserveRatio(true);
        label.setGraphic(imageView);
        return new TreeItem<>(name, label);
    }

    private static HBox createTitleHeader() {
        HBox statusBar = new HBox(10);
        statusBar.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 5px 10px; -fx-border-color: #d3d3d3; -fx-border-width: 2px; -fx-border-radius: 5px;");

        Label statusLabel = new Label("Phân hệ quản lý tồn kho.");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        statusBar.getChildren().add(statusLabel);
        return statusBar;
    }
}
