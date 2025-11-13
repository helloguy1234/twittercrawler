package com.dinhducmanh.twittercrawler.scrap_info_from_twitter.scrap_kol_id;

import java.util.List;

import com.dinhducmanh.twittercrawler.input_function.ExcelInput_Account_Information;
import com.dinhducmanh.twittercrawler.input_function.ExcelInput_For_GerKolId;
import com.dinhducmanh.twittercrawler.input_function.Interface.Input_Account_Information;
import com.dinhducmanh.twittercrawler.input_function.Interface.Input_For_GetKolId;
import com.dinhducmanh.twittercrawler.output_function.ExcelOutput_Kol_Id;
import com.dinhducmanh.twittercrawler.output_function.Interface.OutPut_Kol_Id;

public class Get_User_Id_FromTwitter{
    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) throws InterruptedException, Exception {
    
        try {
            
            Input_Account_Information inputAcc = new ExcelInput_Account_Information();
            Input_For_GetKolId input_For_GetKolId = new ExcelInput_For_GerKolId();

            String email = inputAcc.inputEmail();
            String password = inputAcc.inputPassword();
            String twitterAccId = inputAcc.inputTwitterAccountId();

            String searchString = input_For_GetKolId.inputSearchString();
            
            int numberOfKolId = input_For_GetKolId.inputNumberOfKol();
            List<String> kol_Id_List ;

            OutPut_Kol_Id excelOutput = new ExcelOutput_Kol_Id();
            Scrap_User_Id_Twitter scrap_Kol_Id = new Scrap_User_Id_Twitter(email,password,twitterAccId, searchString);
            
            kol_Id_List = scrap_Kol_Id.scrapKolId(numberOfKolId);

            excelOutput.outputlistOfKolId(kol_Id_List);

            scrap_Kol_Id.quit();

            System.out.println("finished scrap user id");
        } 
        catch (InterruptedException e) {
            System.out.println("thread got interupted");
            e.printStackTrace();
        }
    }
    
}
