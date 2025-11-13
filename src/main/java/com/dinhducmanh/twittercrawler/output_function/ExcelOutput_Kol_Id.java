package com.dinhducmanh.twittercrawler.output_function;

import java.util.List;

import com.dinhducmanh.excel.SupportExcel;
import com.dinhducmanh.twittercrawler.output_function.Interface.OutPut_Kol_Id;

public class ExcelOutput_Kol_Id implements OutPut_Kol_Id {

    private final SupportExcel excel = new SupportExcel();
    private final String excelFilePath = "Big-Assignment\\src\\main\\java\\ohhello\\Resource\\Kol.xlsx";

    public ExcelOutput_Kol_Id() throws Exception {
        excel.setExcelFile(excelFilePath, "Kol_Id and Kol_numberOfFollower");
    }

    @Override
    public void outputlistOfKolId(List<String> listOfKolId) throws Exception {
        excel.setExcelFile(excelFilePath, "Kol_Id and Kol_numberOfFollower");
        for (int i = 0; i < listOfKolId.size(); i++) {
            excel.setCellData(listOfKolId.get(i), i, 0);
        }
    }

}
