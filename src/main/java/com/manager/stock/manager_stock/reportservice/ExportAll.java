package com.manager.stock.manager_stock.reportservice;

import com.manager.stock.manager_stock.model.ExportReceiptModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportAll {
    static ReportService reportService = new ReportService();
    static int curCol;
    static List<ExportReceiptModel> exports = reportService.getExport(LocalDate.now().getYear());
    static List<ImportReceiptModel> imports = reportService.getImport(LocalDate.now().getYear());
    static List<ReportModel> reportModels = reportService.getData(LocalDate.now().getYear());
    static Workbook workbook = new XSSFWorkbook();
    static Sheet sheet = workbook.createSheet("Export");
    static Map<String, Integer> receiptPosMap = new HashMap<>();


    public static void exportTotal(String pathFile) {
        createTitleRow();
        fillData();
        try(FileOutputStream fos = new FileOutputStream(pathFile)) {
            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
        }
    }

    static void setBorder(int sr, int er, int sc, int ec, CellStyle style) {
        for(int i = sr; i <= er; i++) {
            Row row = sheet.getRow(i);
            for(int j = sc; j <= ec; j++) {
                Cell cell = row.getCell(j);
                if(cell == null) {
                    cell = row.createCell(j);
                }
                cell.setCellStyle(style);
            }
        }
    }

    static void infoProductCol(int r, int c, String title) {
        Row row = sheet.getRow(r);
        Cell cellTT = row.createCell(c);
        cellTT.setCellValue(title);
        sheet.addMergedRegion(new CellRangeAddress(r, r + 1, c, c));
    }
    static void detailCol(int r, int c, String title) {
        Row row = sheet.getRow(r);
        Cell titleCell =  row.createCell(c);
        titleCell.setCellValue(title);
        sheet.addMergedRegion(new CellRangeAddress(r, r, c, c + 2));
        Row infoRow = sheet.getRow(r + 1);
        Cell qCol = infoRow.createCell(c);
        Cell pCol = infoRow.createCell(c + 1);
        Cell tCol = infoRow.createCell(c + 2);
        qCol.setCellValue("SL");
        pCol.setCellValue("ĐG");
        tCol.setCellValue("TT");
    }

    static void createTitleRow() {
        int rTemp = 4;
        int cTemp = 0;
        sheet.createRow(4);
        sheet.createRow(5);
        infoProductCol(rTemp, cTemp, "TT"); cTemp++;
        infoProductCol(rTemp, cTemp, "TÊN VT/CC"); cTemp++;
        infoProductCol(rTemp, cTemp, "MÃ VT/CC"); cTemp++;
        infoProductCol(rTemp, cTemp, "ĐVT"); cTemp++;
        detailCol(rTemp, cTemp, "TỒN ĐẦU KỲ");
        receiptPosMap.put("startsem", cTemp); cTemp+=3;
        for(ImportReceiptModel receiptModel: imports) {
            detailCol(rTemp, cTemp, receiptModel.getInvoiceNumber());
            receiptPosMap.put("i" + receiptModel.getId(), cTemp); cTemp+=3;
        }
        for(ExportReceiptModel receiptModel: exports) {
            detailCol(rTemp, cTemp, receiptModel.getInvoiceNumber());;
            receiptPosMap.put("e" + receiptModel.getId(), cTemp); cTemp+=3;
        }
        detailCol(rTemp, cTemp, "TỔNG NHẬP TRONG KỲ");
        receiptPosMap.put("totalimport", cTemp); cTemp+=3;
        detailCol(rTemp, cTemp, "TỔNG XUẤT TRONG KỲ");
        receiptPosMap.put("totalexport", cTemp); cTemp+=3;
        detailCol(rTemp, cTemp, "TỒN CUỐI KỲ");
        receiptPosMap.put("endsem", cTemp); cTemp+=3;
        curCol = cTemp - 1;
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(false);
        setBorder(rTemp,  rTemp + 1, 0, curCol, style);
    }

    static void fillDetailData(int r, int c, ReportModel.ReportDetail data) {
        Row row = sheet.getRow(r);
        Cell qCol = row.createCell(c);
        Cell pCol = row.createCell(c + 1);
        Cell tCol = row.createCell(c + 2);
        qCol.setCellValue(data.getQuantity());
        pCol.setCellValue(data.getUnit_price());
        tCol.setCellValue(data.getTotal());
    }

    static void fillData() {
        int r = 6;
        int ord = 1;
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setBold(false);
        style.setFont(font);
        style.setWrapText(false);
        for(ReportModel reportModel: reportModels) {
            int c = 0;
            Row row = sheet.createRow(r);
            Cell gCell = row.createCell(c);
            gCell.setCellValue(reportModel.getGroupName());
            sheet.addMergedRegion(new CellRangeAddress(r, r, c, c + 3));
            style.setAlignment(HorizontalAlignment.LEFT);
            setBorder(r, r, 0, curCol, style); r++;
            for(ReportModel.ReportProduct reportProduct: reportModel.getReportProducts()) {
                c = 0;
                row = sheet.createRow(r);
                Cell oCell = row.createCell(c); c++;
                Cell nameCell = row.createCell(c); c++;
                Cell idCell = row.createCell(c); c++;
                Cell unitCell = row.createCell(c); c++;
                oCell.setCellValue(ord); ord++;
                nameCell.setCellValue(reportProduct.getName());
                idCell.setCellValue(reportProduct.getCode());
                unitCell.setCellValue(reportProduct.getUnit());
                setBorder(r, r, 0, c - 1, style);
                ReportModel.ReportDetail startSem = reportProduct.getStartSem();
                ReportModel.ReportDetail totalImport = reportProduct.getTotalImport();
                ReportModel.ReportDetail totalExport = reportProduct.getTotalExport();
                ReportModel.ReportDetail endSem = reportProduct.getEndSem();
                List<ReportModel.ReportDetail> importList = reportProduct.getImportDetail();
                List<ReportModel.ReportDetail> exportList = reportProduct.getExportDetail();
                fillDetailData(r, c, startSem);
                for(ReportModel.ReportDetail reportDetail: importList) {
                    fillDetailData(r, receiptPosMap.get(reportDetail.getId()), reportDetail);
                }
                for(ReportModel.ReportDetail reportDetail: exportList) {
                    fillDetailData(r, receiptPosMap.get(reportDetail.getId()), reportDetail);
                }
                fillDetailData(r, receiptPosMap.get("totalimport"), totalImport);
                fillDetailData(r, receiptPosMap.get("totalexport"), totalExport);
                fillDetailData(r, receiptPosMap.get("endsem"), endSem);
                CellStyle style1 = workbook.createCellStyle();
                style1.cloneStyleFrom(style);
                style1.setAlignment(HorizontalAlignment.RIGHT);
                setBorder(r, r, c, curCol, style1);
                r++;
            }
        }
    }
}
