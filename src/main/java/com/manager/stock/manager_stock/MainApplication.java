package com.manager.stock.manager_stock;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;
import com.manager.stock.manager_stock.dao.impl.DatasourceInitialize;
import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.screen.dashBroad.DashBoardScreen;
import com.manager.stock.manager_stock.screen.product.productList.ProductScreen;
import com.manager.stock.manager_stock.service.UpfileService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApplication extends Application {
    private static final ExecutorService uploadExecutor =
            Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("upload-thread");
                return t;
            });


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

//        stage.setOnCloseRequest(e -> {
//            e.consume();
//            stage.close();
//            System.out.println("Uploading before exit...");
//            CompletableFuture
//                .runAsync(() -> UpfileService.upFile())
//                .whenComplete((v, ex) -> {
//                    if (ex != null) {
//                        ex.printStackTrace();
//                    } else {
//                        System.out.println("Upload done.");
//                    }
////                    Platform.runLater(() -> {
//                    uploadExecutor.shutdown();
//                    Platform.exit();
////                    });
//                });
//        });
    }

    public static void main(String[] args) {
//        CompletableFuture<Void> completableFuture =
//                CompletableFuture.runAsync(UpfileService::upFile);
//        completableFuture.whenComplete((v, ex) -> {
//            if (ex != null) {
//                System.out.println("Upload failed: " + ex.getMessage());
//            } else {
//                System.out.println("Upload file success.");
//            }
//            uploadExecutor.shutdownNow();
//        });
        System.out.println("LOG_DIR=" + System.getProperty("LOG_DIR"));
        System.out.println("LoggerFactory implementation = " + LoggerFactory.getILoggerFactory().getClass());
        launch();
    }

    @Override
    public void stop() {
        System.out.println("Uploading before exit...");
        UpfileService.upFile();
        System.out.println("Upload done.");
        Platform.exit();
        System.exit(0);
    }

}