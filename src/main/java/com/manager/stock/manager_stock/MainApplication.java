package com.manager.stock.manager_stock;

import com.manager.stock.manager_stock.dao.impl.DatasourceInitialize;
import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.screen.dashBroad.DashBoardScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();

        BorderPane topBar = new BorderPane();
        Button back = new Button("\u2190 Back");
        Button forward = new Button("Forward \u2192");
        topBar.setLeft(back);
        topBar.setRight(forward);
        topBar.setStyle("-fx-border-color: lightgray; -fx-border-width: 0 0 1 0; -fx-padding: 5 10");

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
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setTitle("Ứng dụng quản lý kho");
        stage.setScene(scene);
        stage.show();


        stage.setOnCloseRequest(e -> {
            System.out.println("Closing application");
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        System.out.println("Starting app .....");
        launch();
    }
}