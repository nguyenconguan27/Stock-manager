package com.manager.stock.manager_stock;

import com.manager.stock.manager_stock.dao.impl.DatasourceInitialize;
import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.screen.dashBroad.DashBoardScreen;
import com.manager.stock.manager_stock.screen.product.productList.ProductScreen;
import com.manager.stock.manager_stock.service.UpfileService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();

        ScreenNavigator.initScreen(root);
        ProductScreen productScreen = new ProductScreen();
        productScreen.showProducts();
        ScreenNavigator.navigateTo(productScreen);

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Ứng dụng quản lý kho");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(UpfileService::upFile);
        completableFuture.thenAccept((Void v) -> {
            System.out.println("Upload file success.");
        });
        System.out.println("LOG_DIR=" + System.getProperty("LOG_DIR"));

//        File exeDir = new File(MainApplication.class
//                .getProtectionDomain()
//                .getCodeSource()
//                .getLocation()
//                .toURI())
//                .getParentFile()
//                .getParentFile();
//
//        String logDir = exeDir.getAbsolutePath() + File.separator + "logs";
//        System.setProperty("LOG_DIR", logDir);

        // Tạo thư mục logs nếu chưa có
//        new File(logDir).mkdirs();

        launch();
    }
}