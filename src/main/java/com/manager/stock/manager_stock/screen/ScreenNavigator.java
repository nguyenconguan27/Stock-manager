package com.manager.stock.manager_stock.screen;

import com.manager.stock.manager_stock.screen.product.ProductPresenter;
import com.manager.stock.manager_stock.screen.product.ProductScreen;
import com.manager.stock.manager_stock.screen.productGroup.ProductGroupScreen;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

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
        Button btnProduct = new Button("Chuyển đến màn sản phẩm");
        btnProduct.setOnAction(e -> {
            ProductScreen productScreen = new ProductScreen();
            ProductPresenter productPresenter = new ProductPresenter();

            productPresenter.loadProductData();
            ScreenNavigator.navigateTo(productScreen);
        });

        Button btnProductGroup = new Button("Chuyển đến màn nhóm sản phẩm");
        btnProductGroup.setOnAction(e -> {
            ScreenNavigator.navigateTo(new ProductGroupScreen());
        });

        VBox leftScreen = new VBox(10);
        leftScreen.getChildren().addAll(btnProduct, btnProductGroup);
        rootPane.setLeft(leftScreen);
    }
}
