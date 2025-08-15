package com.manager.stock.manager_stock.utils;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class Utils {

    public static <T, R> TableColumn<T, R> createColumn(String title, String property) {
        TableColumn<T, R> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    public static void createReceiptForm(Sheet sheet, String title, String code, String s9, String s10,
                                         String s11, String s12, String createdAt, Workbook workbook) {
        Font bold = workbook.createFont();
        bold.setFontName("Times New Roman");
        bold.setBold(true);

        Font light = workbook.createFont();
        light.setFontName("Times New Roman");
        light.setBold(false);

        Font italicFont = workbook.createFont();
        italicFont.setFontName("Times New Roman");
        italicFont.setItalic(true);

        Row r1 = sheet.createRow(0);
        Row r2 = sheet.createRow(1);
        Row r3 = sheet.createRow(2);
        CellStyle style = workbook.createCellStyle();
        style.setFont(light);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(false);

        Cell cell1 = r1.createCell(0);
        cell1.setCellStyle(style);
        Cell cell2 = r2.createCell(0);
        cell2.setCellStyle(style);
        Cell cell3 = r3.createCell(0);
        CellStyle boldStyle = workbook.createCellStyle();
        boldStyle.cloneStyleFrom(style);
        boldStyle.setFont(bold);
        cell3.setCellStyle(boldStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
        cell1.setCellValue("ĐẠI HỌC QUỐC GIA TP.HCM");
        cell2.setCellValue("TRƯỜNG ĐẠI HỌC BÁCH KHOA");
        cell3.setCellValue("PHÒNG QUẢN TRỊ THIẾT BỊ");

        Cell cell1_ = r1.createCell(6);
        Cell cell2_ = r2.createCell(6);
        Cell cell3_ = r3.createCell(6);
        cell1_.setCellStyle(style); cell2_.setCellStyle(style);cell3_.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 6, 9));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 9));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 6, 9));
        cell1_.setCellValue("Mẫu số: C11 - H");
        cell2_.setCellValue("(Ban hành theo QĐ số: 999-TC/QĐ/CĐKT");
        cell3_.setCellValue("ngày 2/11/1996 của Bộ Tài Chính)");

        Row r6 = sheet.createRow(5);
        Row r7 = sheet.createRow(6);
        Row r8 = sheet.createRow(7);
        Cell cell6 = r6.createCell(0);
        Cell cell6_ = r6.createCell(6);
        Cell cell7 = r7.createCell(0);
        Cell cell7_ = r7.createCell(7);
        Cell cell8_ = r8.createCell(7);
        CellStyle rightBold = workbook.createCellStyle();
        rightBold.cloneStyleFrom(boldStyle);
        rightBold.setAlignment(HorizontalAlignment.RIGHT);
        CellStyle leftStyle = workbook.createCellStyle();
        leftStyle.cloneStyleFrom(style); leftStyle.cloneStyleFrom(style);
        leftStyle.setFont(light); leftStyle.setAlignment(HorizontalAlignment.LEFT);
        CellStyle italicStyle = workbook.createCellStyle();
        italicStyle.cloneStyleFrom(style); italicStyle.setFont(italicFont);


        sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 5));
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 6, 7));
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 5));
        cell6.setCellValue(title); cell6.setCellStyle(rightBold);
        cell6_.setCellValue("Số: " + code); cell6_.setCellStyle(leftStyle);
        cell7.setCellValue("Ngày " + "12 " + "tháng " + "7 " + "năm " + "2025"); cell7.setCellStyle(italicStyle);
        cell7_.setCellValue("Nợ: "); cell7_.setCellStyle(leftStyle);
        cell8_.setCellValue("Có: "); cell8_.setCellStyle(leftStyle);

        Row r9 = sheet.createRow(8);
        Row r10 = sheet.createRow(9);
        Row r11 = sheet.createRow(10);
        Row r12 = sheet.createRow(11);
        Cell cell9 = r9.createCell(0);
        Cell cell10 = r10.createCell(0);
        Cell cell11 = r11.createCell(0);
        Cell cell12 = r12.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(9, 9, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(10, 10, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(11, 11, 0, 3));
        cell9.setCellValue(s9); cell10.setCellValue(s10); cell11.setCellValue(s11); cell12.setCellValue(s12);
        cell9.setCellStyle(leftStyle); cell10.setCellStyle(leftStyle); cell11.setCellStyle(leftStyle); cell12.setCellStyle(leftStyle);
        Row r14 = sheet.createRow(13);
        Row r15 = sheet.createRow(14);
        Cell ocell = r14.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(13, 14, 0, 0));
        Cell ncell = r14.createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(13, 14, 1, 3));
        Cell ccell = r14.createCell(4);
        sheet.addMergedRegion(new CellRangeAddress(13, 14, 4, 4));
        Cell ucell = r14.createCell(5);
        sheet.addMergedRegion(new CellRangeAddress(13, 14, 5, 5));
        Cell qcell = r14.createCell(6);
        sheet.addMergedRegion(new CellRangeAddress(13, 13, 6, 7));
        Cell qcell1 = r15.createCell(6);
        Cell qcell2 = r15.createCell(7);
        Cell upcell = r14.createCell(8);
        sheet.addMergedRegion(new CellRangeAddress(13, 14, 8, 8));
        Cell tcell = r14.createCell(9);
        sheet.addMergedRegion(new CellRangeAddress(13, 14, 9, 9));

        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setAlignment(HorizontalAlignment.CENTER);
        borderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setWrapText(false);
        borderStyle.setFont(bold);

        setBorder(13, 14, 0, 9, borderStyle, sheet);

        ocell.setCellValue("STT");
        ncell.setCellValue("TÊN NHÃN HIỆU QUY CÁCH \n" +
                "VẬT TƯ (SP, HÀNG HÓA)");
        ccell.setCellValue("MÃ SỐ");
        ucell.setCellValue("ĐVT");
        qcell.setCellValue("SỐ LƯỢNG");
        qcell1.setCellValue("Theo CT");
        qcell2.setCellValue("Thực Nhập");
        upcell.setCellValue("ĐƠN GIÁ");
        tcell.setCellValue("THÀNH TIỀN");
    }

    public static void fillData(Sheet sheet, int r, String name, String code, String unit,
                                int preQuan, int relQuan, int unitPrice, int total, Workbook workbook) {

        Font bold = workbook.createFont();
        bold.setFontName("Times New Roman");
        bold.setBold(true);

        Font light = workbook.createFont();
        light.setFontName("Times New Roman");
        light.setBold(false);

        DataFormat format = workbook.createDataFormat();
        CellStyle priceStyle = workbook.createCellStyle();
        priceStyle.setDataFormat(format.getFormat("#,##0"));
        priceStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        priceStyle.setBorderTop(BorderStyle.THIN);
        priceStyle.setBorderBottom(BorderStyle.THIN);
        priceStyle.setBorderLeft(BorderStyle.THIN);
        priceStyle.setBorderRight(BorderStyle.THIN);
        priceStyle.setWrapText(false);
        priceStyle.setFont(light);

        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setWrapText(false);
        borderStyle.setFont(light);


        Row row = sheet.createRow(r + 15);
        Cell ocell = row.createCell(0);
        Cell ncell = row.createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(r + 15, r + 15, 1, 3));
        Cell ccell = row.createCell(4);
        Cell ucell = row.createCell(5);
        Cell qcell1 = row.createCell(6);
        Cell qcell2 = row.createCell(7);
        Cell upcell = row.createCell(8);
        Cell tcell = row.createCell(9);
        setBorder(r + 15, r + 15, 0, 9, borderStyle, sheet);
        upcell.setCellStyle(priceStyle); tcell.setCellStyle(priceStyle);

        ocell.setCellValue(r + 1); ncell.setCellValue(name); ccell.setCellValue(code);
        ucell.setCellValue(unit); qcell1.setCellValue(preQuan); qcell2.setCellValue(relQuan);
        upcell.setCellValue(unitPrice); tcell.setCellValue(total);
    }

    public static void fillFooter(Sheet sheet, int planTotal, int actualTotal,long total, String totalInword,
                                  String n1, String n2, String n3, int r, String createdAt, Workbook workbook) {
        Font bold = workbook.createFont();
        bold.setFontName("Times New Roman");
        bold.setBold(true);

        Font light = workbook.createFont();
        light.setFontName("Times New Roman");
        light.setBold(false);

        Font italicFont = workbook.createFont();
        italicFont.setFontName("Times New Roman");
        italicFont.setItalic(true);

        CellStyle style = workbook.createCellStyle();
        style.setFont(light);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(false);

        CellStyle italicStyle = workbook.createCellStyle();
        italicStyle.cloneStyleFrom(style); italicStyle.setFont(italicFont);

        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setAlignment(HorizontalAlignment.RIGHT);
        borderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setWrapText(false);
        borderStyle.setFont(bold);

        CellStyle boldStyle = workbook.createCellStyle();
        boldStyle.cloneStyleFrom(style);
        boldStyle.setFont(bold);

        CellStyle rightBold = workbook.createCellStyle();
        rightBold.cloneStyleFrom(boldStyle);
        rightBold.setAlignment(HorizontalAlignment.RIGHT);

        DataFormat format = workbook.createDataFormat();
        CellStyle priceStyle = workbook.createCellStyle();
        priceStyle.setDataFormat(format.getFormat("#,##0"));
        priceStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        priceStyle.setBorderTop(BorderStyle.THIN);
        priceStyle.setBorderBottom(BorderStyle.THIN);
        priceStyle.setBorderLeft(BorderStyle.THIN);
        priceStyle.setBorderRight(BorderStyle.THIN);
        priceStyle.setWrapText(false);
        priceStyle.setFont(light);

        Row trow = sheet.createRow(r);
        Row twrow = sheet.createRow(r + 1);
        Row drow = sheet.createRow(r + 2);
        Row strow = sheet.createRow(r + 3);
        Row srow = sheet.createRow(r + 4);
        Cell tcell = trow.createCell(0);
        Cell tpqCell = trow.createCell(6);
        Cell taqCell = trow.createCell(7);
        Cell tpCell = trow.createCell(9);
        sheet.addMergedRegion(new CellRangeAddress(r, r, 0, 5));
        Cell twcell = twrow.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(r + 1, r + 1, 0, 5));
        Cell dshell = drow.createCell(5);
        sheet.addMergedRegion(new CellRangeAddress(r + 2, r + 2, 5, 9));
        Cell scell1 = srow.createCell(0);
        Cell scell2 = srow.createCell(3);
        Cell scell3 = srow.createCell(7);
        sheet.addMergedRegion(new CellRangeAddress(r + 3, r + 3, 0, 2));
        sheet.addMergedRegion(new CellRangeAddress(r + 3, r + 3, 3, 6));
        sheet.addMergedRegion(new CellRangeAddress(r + 3, r + 3, 7, 9));
        setBorder(r, r, 0, 9, borderStyle, sheet);
        tcell.setCellStyle(priceStyle);
        twcell.setCellStyle(boldStyle);
        scell1.setCellStyle(boldStyle);
        scell2.setCellStyle(boldStyle);
        scell3.setCellStyle(boldStyle);
        dshell.setCellStyle(italicStyle);

        tcell.setCellValue("Tổng cộng");
        tpqCell.setCellValue(planTotal);
        taqCell.setCellValue(actualTotal);
        tpCell.setCellValue(total);

        twcell.setCellValue("Tổng số tiên: " + totalInword);
        dshell.setCellValue("Ngày " + "12 " + "tháng " + "7 " + "năm " + "2025");
        scell1.setCellValue("PHỤ TRÁCH BỘ PHẬN");
        scell2.setCellValue("NGƯỜI NHẬN");
        scell3.setCellValue("THỦ KHO");

        Cell scell1_ = strow.createCell(0);
        Cell scell2_ = strow.createCell(3);
        Cell scell3_ = strow.createCell(7);
        sheet.addMergedRegion(new CellRangeAddress(r + 4, r + 4, 0, 2));
        sheet.addMergedRegion(new CellRangeAddress(r + 4, r + 4, 3, 6));
        sheet.addMergedRegion(new CellRangeAddress(r + 4, r + 4, 7, 9));
    }

    static void setBorder(int sr, int er, int sc, int ec, CellStyle style, Sheet sheet) {
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

}
