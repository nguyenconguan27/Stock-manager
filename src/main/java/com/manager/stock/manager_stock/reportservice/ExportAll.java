package com.manager.stock.manager_stock.reportservice;

import com.manager.stock.manager_stock.model.ExportReceiptModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

public class ExportAll {
    ReportService reportService = new ReportService();
    int curCol;
    List<ExportReceiptModel> exports;
    List<ImportReceiptModel> imports;
    List<ReportModel> reportModels;
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Export");
    Map<String, Integer> receiptPosMap = new HashMap<>();
    BigDecimal tmp = new BigDecimal("0.00");
    int startColumnExport = 100000;
    int endColumnExport = -1;
    int selectedYear;

    public ExportAll(int selectedYear) {
        this.selectedYear = selectedYear;
        exports = reportService.getExport(selectedYear);
        imports = reportService.getImport(selectedYear);
        reportModels = reportService.getData(selectedYear);
    }

    public void exportTotal(String pathFile) {
        createTitleRow();
        fillData();
        fieldTotal();
        for(int i = 0; i <= 100; i++) {
            sheet.autoSizeColumn(i);
        }
        try (FileOutputStream fos = new FileOutputStream(pathFile)) {
            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
        }
    }

    void fieldTotal() {
        int lastRow = sheet.getLastRowNum();
        Row totalRow = sheet.createRow(lastRow + 1);
        Cell textCell = totalRow.createCell(1);
        textCell.setCellValue("Tổng cộng");
        // ===== 1. TÍNH TỔNG THEO HÀNG DỌC (tổng thật từ các dòng xuất) =====
        BigDecimal totalTmp = BigDecimal.ZERO;
        for(int col = 4; col <= curCol ; col += 3) {
            BigDecimal tmp = new BigDecimal("0.00");
            for (int rowIdx = 7; rowIdx <= lastRow; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;
                if(col >= startColumnExport && col <= endColumnExport) {
                    BigDecimal val = BigDecimal.valueOf(getNumeric(row.getCell(col+2)));
                    tmp = tmp.add(val);
                }
            }
            if(tmp.doubleValue() > 0) {
                tmp = tmp.setScale(0, RoundingMode.HALF_UP);
                totalTmp = totalTmp.add(tmp);
            }
        }
        System.out.println("Tong xuat trong bao cao, tinh theo hang doc: " + totalTmp);
        // tmp = tổng lineAmount bạn đã tính từ DB
        BigDecimal difference = totalTmp.subtract(tmp);
        long diff = difference.longValue();
        int step = diff > 0 ? 1 : -1;
        diff = Math.abs(diff);
        Map<Integer, BigDecimal> exportColumnPrice = new LinkedHashMap<>();
        for(int col = 4; col <= curCol ; col += 3) {
            BigDecimal total = BigDecimal.ZERO;
            BigDecimal price = BigDecimal.ZERO;
            for (int rowIdx = 7; rowIdx <= lastRow; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;
                total = total.add(BigDecimal.valueOf(getNumeric(row.getCell(col))));
                price = price.add(BigDecimal.valueOf(getNumeric(row.getCell(col + 2))));
            }
            Cell qCell = totalRow.createCell(col);
            qCell.setCellValue(total.doubleValue());
            if(col >= startColumnExport && col <= endColumnExport) {
                exportColumnPrice.put(col, price);
            }
            else if(col >= endColumnExport + 6 && col < endColumnExport + 9) {
                Cell pCell = totalRow.createCell(col + 2);
                pCell.setCellValue(tmp.doubleValue());
            }
            else {
                Cell pCell = totalRow.createCell(col + 2);
                pCell.setCellValue(price.longValue());
            }
        }
        if(diff > 0 && !exportColumnPrice.isEmpty()) {
            Iterator<Map.Entry<Integer, BigDecimal>> it = exportColumnPrice.entrySet().iterator();
            while(diff > 0) {
                if(!it.hasNext())
                    it = exportColumnPrice.entrySet().iterator();
                Map.Entry<Integer, BigDecimal> entry = it.next();
                BigDecimal price = entry.getValue();
                price = price.add(BigDecimal.valueOf(step));
                entry.setValue(price);
                diff--;
            }
        }
        for(Map.Entry<Integer, BigDecimal> entry : exportColumnPrice.entrySet()) {
            int col = entry.getKey();
            BigDecimal price = entry.getValue();
            Cell pCell = totalRow.createCell(col + 2);
            pCell.setCellValue(price.longValue());
        }

        CellStyle priceStyle = workbook.createCellStyle();
        Font bold = workbook.createFont();
        bold.setFontName("Times New Roman");
        bold.setBold(true);

        DataFormat format = workbook.createDataFormat();
        priceStyle.setDataFormat(format.getFormat("#,##0"));
        priceStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        priceStyle.setBorderTop(BorderStyle.THIN);
        priceStyle.setFont(bold);
        priceStyle.setBorderBottom(BorderStyle.THIN);
        priceStyle.setBorderLeft(BorderStyle.THIN);
        priceStyle.setBorderRight(BorderStyle.THIN);

        setBorder(lastRow + 1, lastRow + 1, 0, curCol, priceStyle);

        if(diff != 0){
            System.out.println("WARNING: rounding difference still remains = " + diff);
        }
    }


    private static double getNumeric(Cell cell) {
        if (cell == null) return 0;
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                String v = cell.getStringCellValue().replace(",", "");
                yield v.isBlank() ? 0 : Double.parseDouble(v);
            }
            default -> 0;
        };
    }

    void setBorder(int sr, int er, int sc, int ec, CellStyle style) {
        for (int i = sr; i <= er; i++) {
            Row row = sheet.getRow(i);
            for (int j = sc; j <= ec; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                cell.setCellStyle(style);
            }
        }
    }

    void infoProductCol(int r, int c, String title) {
        Row row = sheet.getRow(r);
        Cell cellTT = row.createCell(c);
        cellTT.setCellValue(title);

        // merge an toàn
        mergeSafe(sheet, r, r + 1, c, c);
    }

    private void mergeSafe(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        CellRangeAddress newRegion = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);

        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            if (sheet.getMergedRegion(i).equals(newRegion)) {
                return;
            }
        }

        sheet.addMergedRegion(newRegion);
    }


    void detailCol(int r, int c, String title) {
        Row row = sheet.getRow(r);
        Cell titleCell = row.createCell(c);
        titleCell.setCellValue(title);
        if (!isMergedRegionExists(sheet, r, r, c, c + 2)) {
            sheet.addMergedRegion(new CellRangeAddress(r, r, c, c + 2));
        }
        Row infoRow = sheet.getRow(r + 1);
        Cell qCol = infoRow.createCell(c);
        Cell pCol = infoRow.createCell(c + 1);
        Cell tCol = infoRow.createCell(c + 2);
        qCol.setCellValue("SL");
        pCol.setCellValue("ĐG");
        tCol.setCellValue("TT");
    }

    void createTitleRow() {
        int rTemp = 4;
        int cTemp = 0;
        sheet.createRow(4);
        sheet.createRow(5);
        infoProductCol(rTemp, cTemp, "TT");
        cTemp++;
        infoProductCol(rTemp, cTemp, "TÊN VT/CC");
        cTemp++;
        infoProductCol(rTemp, cTemp, "MÃ VT/CC");
        cTemp++;
        infoProductCol(rTemp, cTemp, "ĐVT");
        cTemp++;
        detailCol(rTemp, cTemp, "TỒN ĐẦU KỲ");
        receiptPosMap.put("startsem", cTemp);
        cTemp += 3;
        for (ImportReceiptModel receiptModel : imports) {
            detailCol(rTemp, cTemp, receiptModel.getInvoice());
            receiptPosMap.put("i" + receiptModel.getId(), cTemp);
            cTemp += 3;
        }
        for (ExportReceiptModel receiptModel : exports) {
            detailCol(rTemp, cTemp, receiptModel.getInvoiceNumber());
            receiptPosMap.put("e" + receiptModel.getId(), cTemp);
            cTemp += 3;
        }
        detailCol(rTemp, cTemp, "TỔNG NHẬP TRONG KỲ");
        receiptPosMap.put("totalimport", cTemp);
        cTemp += 3;
        detailCol(rTemp, cTemp, "TỔNG XUẤT TRONG KỲ");
        receiptPosMap.put("totalexport", cTemp);
        cTemp += 3;
        detailCol(rTemp, cTemp, "TỒN CUỐI KỲ");
        receiptPosMap.put("endsem", cTemp);
        cTemp += 3;
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
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setWrapText(false);
        setBorder(rTemp, rTemp + 1, 0, curCol, style);
    }

    void fillDetailData(int r, int c, ReportModel.ReportDetail data, boolean isUsingRound) {
        Row row = sheet.getRow(r);
        if (row == null) row = sheet.createRow(r);
        Cell qCol = row.createCell(c, CellType.NUMERIC);
        Cell pCol = row.createCell(c + 1, CellType.NUMERIC);
        Cell tCol = row.createCell(c + 2, CellType.FORMULA);
        qCol.setCellValue(data.getQuantity());
        pCol.setCellValue(data.getUnit_price());
        String qColName = CellReference.convertNumToColString(c);
        String pColName = CellReference.convertNumToColString(c + 1);
        int excelRow = r + 1;
        // thành tiền từng phiếu (đúng: có ROUND)
        if(isUsingRound) {
            String formula = String.format(
                    "ROUND(%s%d*%s%d,0)",
                    qColName, excelRow,
                    pColName, excelRow
            );

            tCol.setCellFormula(formula);
        }
        else {
            tCol.setCellValue(data.getQuantity() * data.getUnit_price());
        }
    }
    void fillData() {
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
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setWrapText(false);
        for (ReportModel reportModel : reportModels) {
            int c = 0;
            Row row = sheet.createRow(r);
            Cell gCell = row.createCell(c);
            gCell.setCellValue(reportModel.getGroupName());
            sheet.addMergedRegion(new CellRangeAddress(r, r, c, c + 3));
            style.setAlignment(HorizontalAlignment.LEFT);
            setBorder(r, r, 0, curCol, style);
            r++;
            for (ReportModel.ReportProduct reportProduct : reportModel.getReportProducts()) {
                c = 0;
                row = sheet.createRow(r);
                Cell oCell = row.createCell(c);
                c++;
                Cell nameCell = row.createCell(c);
                c++;
                Cell idCell = row.createCell(c);
                c++;
                Cell unitCell = row.createCell(c);
                c++;
                oCell.setCellValue(ord);
                ord++;
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
                fillDetailData(r, c, startSem, true);
                for (ReportModel.ReportDetail reportDetail : importList) {
                    fillDetailData(r, receiptPosMap.get(reportDetail.getId()), reportDetail, false);
                }
                for (ReportModel.ReportDetail reportDetail : exportList) {
//                    if(startColumnExport == -1) {
                    startColumnExport = Math.min(receiptPosMap.get(reportDetail.getId()), startColumnExport);
//                    }
//                    if(endColumnExport == -1) {
                    endColumnExport = Math.max(receiptPosMap.get(reportDetail.getId()), endColumnExport);
//                    }
                    fillDetailData(r, receiptPosMap.get(reportDetail.getId()), reportDetail, false);
                }
                System.out.println("start: " + startColumnExport + ", end: " + endColumnExport);
                fillDetailData(r, receiptPosMap.get("totalimport"), totalImport, true);
                fillDetailData(r, receiptPosMap.get("totalexport"), totalExport, true);
                BigDecimal price = BigDecimal.valueOf(totalExport.getUnit_price());
                BigDecimal qty   = BigDecimal.valueOf(totalExport.getQuantity());

                BigDecimal lineAmount =
                        price.multiply(qty)
                                .setScale(0, RoundingMode.HALF_UP);
                tmp = tmp.add(lineAmount);
                fillDetailData(r, receiptPosMap.get("endsem"), endSem, true);
                CellStyle style1 = workbook.createCellStyle();
                style1.cloneStyleFrom(style);
                style1.setAlignment(HorizontalAlignment.RIGHT);
                style1.setVerticalAlignment(VerticalAlignment.CENTER);
                style1.setWrapText(false);
                Font light = workbook.createFont();
                light.setFontName("Times New Roman");
                light.setBold(false);
                style1.setFont(light);
                DataFormat format = workbook.createDataFormat();
                style1.setDataFormat(format.getFormat("#,##0"));
                setBorder(r, r, c, curCol, style1);
                r++;
            }
        }
        System.out.println("Tong xua trong bao cao tong hop: " + tmp);
    }

    private boolean isMergedRegionExists(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            if (region.getFirstRow() == firstRow &&
                    region.getLastRow() == lastRow &&
                    region.getFirstColumn() == firstCol &&
                    region.getLastColumn() == lastCol) {
                return true;
            }
        }
        return false;
    }

}