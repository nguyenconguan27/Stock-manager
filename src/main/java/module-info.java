module com.manager.stock.manager_stock {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jdk.httpserver;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires org.slf4j;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml.schemas;
    requires org.apache.poi.ooxml;
    requires java.sql;
    requires jdk.incubator.vector;
    requires java.desktop;
    requires jdk.compiler;
    requires datetime.picker.javafx;
    requires com.h2database;
    requires upfileservice;
    opens com.manager.stock.manager_stock to javafx.fxml;
    exports com.manager.stock.manager_stock;

    opens com.manager.stock.manager_stock.model to javafx.base;
}