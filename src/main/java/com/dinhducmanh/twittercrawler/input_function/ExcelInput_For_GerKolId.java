package com.dinhducmanh.twittercrawler.input_function;

import com.dinhducmanh.excel.SupportExcel;
import com.dinhducmanh.twittercrawler.input_function.Interface.Input_For_GetKolId;

public class ExcelInput_For_GerKolId implements Input_For_GetKolId {

    private final SupportExcel excel = new SupportExcel();
    private final String excelFilePath = "src\\main\\java\\com\\dinhducmanh\\resource\\Input_Initiate_Information.xlsx";

    public ExcelInput_For_GerKolId() throws Exception {
        excel.setExcelFile(excelFilePath, "Input_Initiate_Information");
    }

    @Override
    public String inputSearchString() throws Exception {
        return excel.getCellData(3, 1);
    }

    @Override
    public int inputNumberOfKol() throws Exception {
        String numberOfKolString = excel.getCellData(4, 1);
        return Integer.parseInt(numberOfKolString);
    }
}
