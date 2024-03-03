package com.ioc.pacman;

import org.apache.poi.ss.usermodel.*;

import java.io.*;

public class ReadBackgroundPixels {
    public static int[][] readPixelMatrixFromExcel(String filePath, int numRows, int numCols)
    {
        int[][] excelContent = new int[numRows][numCols];

        try {
            File xlFile = new File(filePath);
            InputStream inputStream = new FileInputStream(xlFile);


            final Workbook workbook = WorkbookFactory.create(inputStream);
            final Sheet sheet = workbook.getSheetAt(0);

            for (int i = 0; i < numRows; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    for (int j = 0; j < numCols; j++) {
                        Cell cell = row.getCell(j);
                        if (cell != null) {
                            if (cell.getCellType() == CellType.NUMERIC) {
                                excelContent[i][j] = (int) cell.getNumericCellValue();
                            } else {
                                throw new Exception("The pixels matrix contains illegal values");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        return excelContent;
    }
}
