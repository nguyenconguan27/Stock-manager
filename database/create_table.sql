CREATE SCHEMA IF NOT EXISTS db;
use db;
CREATE TABLE IF NOT EXISTS product_group (
                                             id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                             name VARCHAR(100) NOT NULL
    );

CREATE TABLE IF NOT EXISTS product (
                                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                       code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    unit VARCHAR(20),
    created_at VARCHAR(20),
    group_id BIGINT,
    CONSTRAINT fk_product_group FOREIGN KEY (group_id) REFERENCES product_group(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS import_receipt (
                                              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                              invoice_number VARCHAR(50) NOT NULL,
    create_at VARCHAR(20) NOT NULL,
    delivered_by VARCHAR(100),
    invoice TEXT,
    company_name VARCHAR(100),
    warehouse_name VARCHAR(100),
    total_price FLOAT,
    total_price_in_word text,
    academic_year INT
    );

CREATE TABLE IF NOT EXISTS import_receipt_detail (
                                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                                     import_receipt_id BIGINT NOT NULL,
                                                     product_id BIGINT NOT NULL,
                                                     planned_quantity INTEGER,
                                                     actual_quantity INTEGER,
                                                     unit_price FLOAT,
                                                     product_name VARCHAR(255),
    CONSTRAINT fk_import_receipt FOREIGN KEY (import_receipt_id) REFERENCES import_receipt(id) ON DELETE CASCADE,
    CONSTRAINT fk_import_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS export_receipt (
                                              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                              invoice_number VARCHAR(50) NOT NULL,
    create_at VARCHAR(20) NOT NULL,
    receiver VARCHAR(100),
    receive_address VARCHAR(255),
    reason TEXT,
    warehouse VARCHAR(100),
    total_price FLOAT,
    total_price_in_word text,
    academic_year INT
    );

CREATE TABLE IF NOT EXISTS export_price (
                                            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                            product_id BIGINT NOT NULL,
                                            export_time TIMESTAMP NOT NULL,
                                            export_price DOUBLE PRECISION,
                                            quantity_in_stock INT,
                                            quantity_imported INT,
                                            total_price_in_stock DOUBLE precision,
                                            total_price_import DOUBLE PRECISION,
                                            import_receipt_id BIGINT,
                                            CONSTRAINT fk_export_price_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE SET NULL,
    CONSTRAINT fk_import_export_price FOREIGN KEY (import_receipt_id) REFERENCES import_receipt(id) ON DELETE SET null
    );

CREATE TABLE IF NOT EXISTS export_receipt_detail (
                                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                                     export_receipt_id BIGINT NOT NULL,
                                                     product_id BIGINT NOT NULL,
                                                     planned_quantity INTEGER,
                                                     actual_quantity INTEGER,
                                                     export_price_id BIGINT not null,
                                                     CONSTRAINT fk_export_receipt FOREIGN KEY (export_receipt_id) REFERENCES export_receipt(id) ON DELETE CASCADE,
    CONSTRAINT fk_export_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE SET null,
    CONSTRAINT fk_export_price FOREIGN KEY (export_price_id) REFERENCES export_price(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS inventory_detail (
                                                id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                                product_id BIGINT NOT NULL,
                                                quantity INT,
                                                total_price DOUBLE PRECISION,
                                                academic_year INT,
                                                CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE SET NULL
    );






