package com.manager.stock.manager_stock;

import com.manager.stock.manager_stock.dao.impl.DatasourceInitialize;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * @author Trọng Hướng
 */
public class Test {
    public static void main(String[] args) throws Exception {
        try {
            String excelPath = "C:\\Users\\ADMIN\\Downloads\\NAM 2025_1.xlsx"; // đường dẫn file excel
            FileInputStream fis = new FileInputStream(excelPath);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            Connection conn = init();

            Long currentGroupId = null;

            PreparedStatement insertGroup = conn.prepareStatement(
                    "INSERT INTO product_group(name) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            PreparedStatement insertProduct = conn.prepareStatement(
                    "INSERT INTO product(code, name, unit, created_at, group_id) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
            PreparedStatement insertInventory = conn.prepareStatement(
                    "INSERT INTO inventory_detail(product_id, quantity, total_price, academic_year) VALUES (?, ?, ?, ?)");

            for (Row row : sheet) {
                // Bỏ qua các dòng tiêu đề đầu tiên
                if (row.getRowNum() < 5) continue;
                String colB = getCellValue(row.getCell(1), sheet);
                String colC = getCellValue(row.getCell(2), sheet);
                String colD = getCellValue(row.getCell(3), sheet);
                if("TỔNG CỘNG".equals(colB)){return;}

                if (colD.isEmpty() || (colB.equals(colC) && colC.equals(colD))) {
                    insertGroup.setString(1, colB);
                    insertGroup.executeUpdate();
                    try (ResultSet rs = insertGroup.getGeneratedKeys()) {
                        if (rs.next()) {
                            currentGroupId = rs.getLong(1);
                        }
                    }
                    System.out.println("Nhóm vật tư: " + colB);
                    continue;
                }
                System.out.println(String.format("Code: %s, Name: %s, Unit: %s, Group id: %d", colC, colB, colD, currentGroupId));

                insertProduct.setString(1, colC);
                insertProduct.setString(2, colB);
                insertProduct.setString(3, colD);
                insertProduct.setString(4, String.valueOf(System.currentTimeMillis()));
                insertProduct.setLong(5, currentGroupId);
                insertProduct.executeUpdate();

                Long productId = null;
                try (ResultSet rs = insertProduct.getGeneratedKeys()) {
                    if (rs.next()) {
                        productId = rs.getLong(1);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                String colE = getCellValue(row.getCell(4), sheet); // SL
                String colF = getCellValue(row.getCell(5), sheet); // Đơn giá
                String colG = getCellValue(row.getCell(6), sheet); // Thành tiền

                int quantity = colE.isEmpty() ? 0 : (int) Double.parseDouble(colE);
                double totalPrice = colG.isEmpty() ? 0.0 : Double.parseDouble(colG.replace(",", ""));
                double unitPrice = colF.isEmpty() ? 0.0 : Double.parseDouble(colF.replace(",", ""));
                if (productId != null) {
                    insertInventory.setLong(1, productId);
                    insertInventory.setInt(2, quantity);
                    insertInventory.setDouble(3, totalPrice);
                    insertInventory.setInt(4, 2024);
                    insertInventory.executeUpdate();

                    // Thêm export price:
                    PreparedStatement insertExportPrice = conn.prepareStatement("INSERT INTO export_price(product_id, export_time, " +
                            "export_price, quantity_in_stock, quantity_imported, total_price_in_stock, total_price_import) VALUES (?, ?, ?, ?, ?, ?, ?)");
                    Timestamp now = new Timestamp(System.currentTimeMillis());

                    // Lấy LocalDateTime rồi trừ đi 3 năm
                    LocalDateTime threeYearsAgo = now.toLocalDateTime().minusYears(3);

                    // Chuyển ngược lại về Timestamp
                    Timestamp result = Timestamp.valueOf(threeYearsAgo);
                    insertExportPrice.setLong(1, productId);
                    insertExportPrice.setTimestamp(2, result);
                    insertExportPrice.setDouble(3, unitPrice);
                    insertExportPrice.setInt(4, quantity);
                    insertExportPrice.setInt(5, 0);
                    insertExportPrice.setDouble(6, totalPrice);
                    insertExportPrice.setDouble(7, 0);
                    insertExportPrice.executeUpdate();
                }
            }

            conn.close();
            workbook.close();
            fis.close();

            System.out.println("Import thành công!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCellValue(Cell cell, Sheet sheet) {
        if (cell == null) return "";

        for (CellRangeAddress range : sheet.getMergedRegions()) {
            if (range.isInRange(cell)) {
                Row firstRow = sheet.getRow(range.getFirstRow());
                Cell firstCell = firstRow.getCell(range.getFirstColumn());
                return firstCell != null ? firstCell.getStringCellValue().trim() : "";
            }
        }

        // Nếu không nằm trong merge, đọc giá trị bình thường
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        return "";
    }

    public static Connection init() {
        String user = "sa";
        String pass = "1234";
        try {
            File scriptFile = extractSqlToTempFile("/com/manager/stock/manager_stock/database/create_table.sql");
            String jdbcUrl = "jdbc:h2:file:D:/data/db;INIT=RUNSCRIPT FROM '" + scriptFile.getAbsolutePath().replace("\\", "/") + "'";
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(jdbcUrl, user, pass);
            System.out.println("Connected to database.");
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static File extractSqlToTempFile(String resourcePath) throws IOException {
        InputStream input = DatasourceInitialize.class.getResourceAsStream(resourcePath);
        if (input == null) {
            throw new FileNotFoundException("SQL file not found: " + resourcePath);
        }

        File tempFile = File.createTempFile("init-script-", ".sql");
        tempFile.deleteOnExit();

        try (OutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }

        return tempFile;
    }

}