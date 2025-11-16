package com.dinhducmanh.twittercrawler.input_function;

import java.util.ArrayList;
import java.util.List;

import com.dinhducmanh.excel.SupportExcel;
import com.dinhducmanh.twittercrawler.input_function.Interface.Input_KolId;

public class ExcelInput_KolId implements Input_KolId {

    private final SupportExcel excel = new SupportExcel();
    private final String excelFilePath = "src\\main\\java\\com\\dinhducmanh\\resource\\Input_Initiate_Information.xlsx";

    public ExcelInput_KolId() throws Exception {
        excel.setExcelFile(excelFilePath, "Kol_Id and Kol_numberOfFollower");
    }

    @Override
    public List<String> inputKolIdList() throws Exception {
        List<String> inputKolIdList = new ArrayList<>();
        int rownum = 0;
        while (!excel.getCellData(rownum, 0).equals("")) {
            inputKolIdList.add(excel.getCellData(rownum, 0));
            rownum++;
        }

        return inputKolIdList;
    }

}
