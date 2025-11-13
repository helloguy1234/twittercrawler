package com.dinhducmanh.ranking_proc;

import java.util.ArrayList;
import java.util.List;

/**
 * Đọc dữ liệu từ File Excel
 */
public class ReaderExcelData {

    private String file;
    SupportExcel spe0 = new SupportExcel();
    SupportExcel spe1 = new SupportExcel();
    SupportExcel spe2 = new SupportExcel();
    SupportExcel spe3 = new SupportExcel();

    /**
     * @param file
     */
    public ReaderExcelData(String file) {
        super();
        this.file = file;
    }

    public List<KOL> readKOLData(int a) throws Exception {
        // số lượng kol
        List<KOL> listkol = new ArrayList<>();

        spe0.setExcelFile(file, "Kol_Id and Kol_numberOfFollower");
        spe1.setExcelFile(file, "Commented_User_Id");
        spe2.setExcelFile(file, "Reposted_User_Id");
        spe3.setExcelFile(file, "Following_User_Id");
        for (int i = 0; i < a; i++) {
            KOL A = new KOL(spe0.getCellData(i, 0), Integer.parseInt(spe0.getCellData(i, 1)));

            List<String> cmt = new ArrayList<>();
            int ii = 0;
            while (spe1.getCellData(i, ii) != null && spe1.getCellData(i, ii) != "") {
                cmt.add(spe1.getCellData(i, ii));
                ii++;
            }
            ii = 0;

            List<String> rep = new ArrayList<>();
            while (spe2.getCellData(i, ii) != null && spe2.getCellData(i, ii) != "") {
                rep.add(spe2.getCellData(i, ii));
                ii++;
            }
            ii = 0;
            List<String> flwing = new ArrayList<>();
            while (spe3.getCellData(i, ii) != null && spe3.getCellData(i, ii) != "") {
                rep.add(spe3.getCellData(i, ii));
                ii++;
            }
            A.setCommented_User_ID(cmt);
            A.setFollowing_User_ID(flwing);
            A.setReposted_User_ID(rep);
            listkol.add(A);
        }
        //for(KOL X : listkol) {
        //for(KOL Y :listkol ) {
        //if(X.equals(Y)==false) {
        //for(String Yid : X.getCommented_User_ID()) {
        //if(Yid.equals(Y.getId())) {
        //Y.addComment_to_User_ID(X.getId());
        //}
        //}
        //}
        //}
        //}
        return listkol;
    }

}
