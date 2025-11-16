package com.dinhducmanh.twittercrawler.input_function;

import com.dinhducmanh.excel.SupportExcel;
import com.dinhducmanh.twittercrawler.input_function.Interface.Input_Account_Information;

public class ExcelInput_Account_Information implements Input_Account_Information {

    private final SupportExcel excel = new SupportExcel();
    private final String excelFilePath = "src\\main\\java\\com\\dinhducmanh\\resource\\Input_Initiate_Information.xlsx";

    public ExcelInput_Account_Information() throws Exception {
        excel.setExcelFile(excelFilePath, "Input_Initiate_Information");
    }

    @Override
    public String inputEmail() throws Exception {
        return excel.getCellData(0, 1);
    }

    @Override
    public String inputPassword() throws Exception {
        return excel.getCellData(1, 1);
    }

    @Override
    public String inputTwitterAccountId() throws Exception {
        return excel.getCellData(2, 1);
    }

}
