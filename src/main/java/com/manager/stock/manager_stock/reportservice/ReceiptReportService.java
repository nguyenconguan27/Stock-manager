package com.manager.stock.manager_stock.reportservice;

import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ExportReceiptModel;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.utils.FormatMoney;
import com.manager.stock.manager_stock.utils.Utils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReceiptReportService {

    public static ReportService reportService = new ReportService();

    static List<ImportReceiptModel> importReceiptModelList;
    static List<ExportReceiptModel> exportReceiptModelList;
    static Workbook workbook;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");

    public ReceiptReportService() {
        reportService = ReportService.getInstance();
    }

    public static void printAllExportReceipt(String fileName, int year) {
        workbook = new XSSFWorkbook();
        exportReceiptModelList = reportService.getExportDetail(year);
        for(int i = 0; i < exportReceiptModelList.size(); i++) {
            ExportReceiptModel exportReceiptModel = exportReceiptModelList.get(i);
            Sheet sheet = workbook.createSheet(exportReceiptModel.getInvoiceNumber());
            printExportDetailReceipt(sheet, exportReceiptModel);
            autoFitColumnsByDisplayedText(sheet, workbook);
        }
        try(FileOutputStream fos = new FileOutputStream(fileName)) {
            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printAllImportReceipt(String fileName, int year) {
        workbook = new XSSFWorkbook();
        importReceiptModelList = reportService.getImportDetail(year);
        for(int i = 0; i < importReceiptModelList.size(); i++) {
            ImportReceiptModel importReceipt = importReceiptModelList.get(i);
            Sheet sheet = workbook.createSheet(importReceipt.getInvoice());
            printImportDetailReceipt(sheet, importReceipt);
            autoFitColumnsByDisplayedText(sheet, workbook);
        }
        try(FileOutputStream fos = new FileOutputStream(fileName)) {
            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printImportDetailReceipt(Sheet sheet, ImportReceiptModel importReceipt) {
        Utils.createReceiptForm(sheet, "PHIẾU NHẬP KHO  ", importReceipt.getInvoice(), "Họ tên người giao " + importReceipt.getDeliveredBy(),
                "Theo hoá đơn số " + importReceipt.getInvoiceNumber(), "Của: " + importReceipt.getCompanyName(), "Nhập tại kho " + importReceipt.getWarehouseName(), importReceipt.getCreateAt(),
                workbook);
        long total = 0;
        int planTotal = 0;
        int actualTotal = 0;
        int r = 15;
        for(int i = 0; i < importReceipt.getImportReceiptDetails().size(); i++) {
            r++;
            ImportReceiptDetailModel detail = importReceipt.getImportReceiptDetails().get(i);
            Utils.fillData(sheet, i, detail.getProductName(), detail.getProductCode(), detail.getUnit(),
                    detail.getPlannedQuantity(), detail.getActualQuantity(), detail.getUnitPrice(), detail.getTotalPrice(), workbook);
            total += Math.round(detail.getTotalPrice());
            planTotal += detail.getPlannedQuantity();
            actualTotal += detail.getActualQuantity();
        }
//        autoSizeAllColumns(sheet);
        Utils.fillFooter(sheet, planTotal, actualTotal, total, FormatMoney.formatMoneyToWord(total),
                null, importReceipt.getDeliveredBy(), null, r,  importReceipt.getCreateAt(), workbook);
//        Utils.fillFooter(sheet, planTotal, actualTotal,
//                null, importReceipt.getDeliveredBy(), null, r,  importReceipt.getCreateAt(), workbook);
    }

    public static void printExportDetailReceipt(Sheet sheet, ExportReceiptModel exportReceipt) {
        Utils.createReceiptForm(sheet, "PHIẾU XUẤT KHO  ", exportReceipt.getInvoiceNumber(), "Họ tên người nhận hàng " + exportReceipt.getReceiver(),
                "Địa chỉ: " + exportReceipt.getReceiveAddress(), "Lý do xuất kho: " + exportReceipt.getReason(), "Xuất tại kho " + exportReceipt.getWareHouse(), exportReceipt.getCreateAt(), workbook);
        double total = 0;
        int planTotal = 0;
        int actualTotal = 0;
        int r = 15;
        for(int i = 0; i < exportReceipt.getExportReceiptDetailModels().size(); i++) {
            r++;
            ExportReceiptDetailModel detail = exportReceipt.getExportReceiptDetailModels().get(i);
            Utils.fillData(sheet, i, detail.getProductName(), detail.getProductCode(), detail.getUnit(),
                    detail.getPlannedQuantity(), detail.getActualQuantity(), detail.getDisplayUnitPrice(), detail.getTotalPrice(), workbook);
            total += (detail.getDisplayUnitPrice() * detail.getActualQuantity());
            planTotal += detail.getPlannedQuantity();
            actualTotal += detail.getActualQuantity();
        }
        Utils.fillFooter(sheet, planTotal, actualTotal, total,
                FormatMoney.formatMoneyToWord((long)total),
                null, exportReceipt.getReceiver(), null, r,
                exportReceipt.getCreateAt(), workbook);
//        Utils.fillFooter(sheet, planTotal, actualTotal,
//                null, exportReceipt.getReceiver(), null, r,  exportReceipt.getCreateAt(), workbook);
    }

    public static void autoFitColumnsByDisplayedText(Sheet sheet, Workbook wb) {
        DataFormatter formatter = new DataFormatter();
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        int maxCol = 0;
        for (Row row : sheet) {
            if (row.getLastCellNum() > maxCol) maxCol = row.getLastCellNum();
        }
        int[] maxChars = new int[maxCol];
        for (Row row : sheet) {
            for (int c = 0; c < maxCol; c++) {
                Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (cell == null) continue;

                String text = formatter.formatCellValue(cell, evaluator);
                if (text == null) text = "";
                int len = text.length() + 1;
                if (len > maxChars[c]) maxChars[c] = len;
            }
        }

        for (int c = 0; c < maxCol; c++) {
            int width = Math.min(255, maxChars[c]) * 256;
            width += 512;
            sheet.setColumnWidth(c, Math.min(width, 255 * 256));
        }
    }

    private static void fetchData(int selectYear) {
        List<ReportModel> reportModels = reportService.getData(selectYear);
        for (ReportModel reportModel : reportModels) {
            for (ReportModel.ReportProduct reportProduct : reportModel.getReportProducts()) {
                ReportModel.ReportDetail totalExport = reportProduct.getTotalExport();
                BigDecimal price = BigDecimal.valueOf(totalExport.getUnit_price());
                BigDecimal qty   = BigDecimal.valueOf(totalExport.getQuantity());

                BigDecimal lineAmount =
                        price.multiply(qty)
                                .setScale(0, RoundingMode.HALF_UP);
            }
        }
    }
}
