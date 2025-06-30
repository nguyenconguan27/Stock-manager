module com.manager.stock.manager_stock {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires org.slf4j;
    requires java.sql;
    requires jdk.incubator.vector;

    opens com.manager.stock.manager_stock to javafx.fxml;
    exports com.manager.stock.manager_stock;
}