package com.manager.stock.manager_stock.reportservice;

import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ExportReceiptModel;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.utils.FormatMoney;
import com.manager.stock.manager_stock.utils.Utils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
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
                    detail.getPlannedQuantity(), detail.getActualQuantity(), (int) detail.getUnitPrice(), (int) detail.getTotalPrice(), workbook);
            total += detail.getTotalPrice();
            planTotal += detail.getPlannedQuantity();
            actualTotal += detail.getActualQuantity();
        }
        Utils.fillFooter(sheet, planTotal, actualTotal, total, FormatMoney.formatMoneyToWord(total),
                null, null, null, r, null, workbook);
    }

    public static void printExportDetailReceipt(Sheet sheet, ExportReceiptModel exportReceipt) {
        Utils.createReceiptForm(sheet, "PHIẾU XUẤT KHO  ", exportReceipt.getInvoiceNumber(), "Họ tên người nhận hàng " + exportReceipt.getReceiver(),
                "Địa chỉ: " + exportReceipt.getReceiveAddress(), "Lý do xuất kho: " + exportReceipt.getReason(), "Xuất tại kho " + exportReceipt.getWareHouse(), exportReceipt.getCreateAt(), workbook);
        long total = 0;
        int planTotal = 0;
        int actualTotal = 0;
        int r = 15;
        for(int i = 0; i < exportReceipt.getExportReceiptDetailModels().size(); i++) {
            r++;
            ExportReceiptDetailModel detail = exportReceipt.getExportReceiptDetailModels().get(i);
            Utils.fillData(sheet, i, detail.getProductName(), detail.getProductCode(), detail.getUnit(),
                    detail.getPlannedQuantity(), detail.getActualQuantity(), (int) detail.getDisplayUnitPrice(), (int) detail.getTotalPrice(), workbook);
            total += detail.getTotalPrice();
            planTotal += detail.getPlannedQuantity();
            actualTotal += detail.getActualQuantity();
        }

        Utils.fillFooter(sheet, planTotal, actualTotal, total, FormatMoney.formatMoneyToWord(total),
                null, exportReceipt.getReceiver(), null, r, null, workbook);
    }

}
