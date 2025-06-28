package com.manager.stock.manager_stock;

import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.screen.dashBroad.DashBoardScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();

        HBox topBar = new HBox();
        Button back = new Button("\u2190 Back");
        Button forward = new Button("Forward \u2192");
        topBar.getChildren().addAll(back, forward);

        back.setOnAction(e -> {
            ScreenNavigator.goBack();
        });
        forward.setOnAction(e -> {
            ScreenNavigator.goForward();
        });

        root.setTop(topBar);

        ScreenNavigator.initScreen(root);
        ScreenNavigator.navigateTo(new DashBoardScreen());

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Ứng dụng quản lý kho");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}