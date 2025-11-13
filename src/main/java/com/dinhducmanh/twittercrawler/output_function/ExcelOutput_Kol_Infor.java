package com.dinhducmanh.twittercrawler.output_function;

import java.util.List;

import com.dinhducmanh.excel.SupportExcel;
import com.dinhducmanh.twittercrawler.output_function.Interface.OutPut_Kol_Infor;

public class ExcelOutput_Kol_Infor implements OutPut_Kol_Infor {

    private int currentUserIndex = 0;
    private final SupportExcel excel = new SupportExcel();
    private final String excelFilePath = "Big-Assignment\\src\\main\\java\\ohhello\\Resource\\Kol.xlsx";

    public ExcelOutput_Kol_Infor() throws Exception {
        excel.setExcelFile(excelFilePath, "Kol_Id and Kol_numberOfFollower");
    }

    //tẹo làm tiếp
    @Override
    public void outputListNumberOfFollower(int numberOfFollower) throws Exception {
        excel.setExcelFile(excelFilePath, "Kol_Id and Kol_numberOfFollower");

        excel.setCellData(Integer.toString(numberOfFollower), currentUserIndex, 1);
    }

    @Override
    public void outputCommentedUserIdList(List<String> commentedUserIdList) throws Exception {
        excel.setExcelFile(excelFilePath, "Commented_User_Id");

        for (int i = 0; i < commentedUserIdList.size(); i++) {
            excel.setCellData(commentedUserIdList.get(i), currentUserIndex, i);
        }
    }

    @Override
    public void outputRepostedUserIdList(List<String> repostedUserIdList) throws Exception {
        excel.setExcelFile(excelFilePath, "Reposted_User_Id");

        for (int i = 0; i < repostedUserIdList.size(); i++) {
            excel.setCellData(repostedUserIdList.get(i), currentUserIndex, i);
        }
    }

    @Override
    public void outputfollowingUserIdList(List<String> followingUserIdList) throws Exception {
        excel.setExcelFile(excelFilePath, "Following_User_Id");

        for (int i = 0; i < followingUserIdList.size(); i++) {
            excel.setCellData(followingUserIdList.get(i), currentUserIndex, i);
        }
    }

    @Override
    public void setCurrentUserIndex(int currentUserIndex) {
        this.currentUserIndex = currentUserIndex;
    }

}
