package com.dinhducmanh.twittercrawler.scrap_info_from_twitter.scrap_kol_info;

import java.util.List;
import java.util.zip.DataFormatException;

import com.dinhducmanh.twittercrawler.input_function.ExcelInput_Account_Information;
import com.dinhducmanh.twittercrawler.input_function.ExcelInput_KolId;
import com.dinhducmanh.twittercrawler.input_function.Interface.Input_Account_Information;
import com.dinhducmanh.twittercrawler.input_function.Interface.Input_KolId;

public class Get_Kol_Info_FromTwitter {

    public static void main(String[] args) throws Exception {
        Input_Account_Information inputInit = new ExcelInput_Account_Information();
        Input_KolId inputKolId = new ExcelInput_KolId();

        String email = inputInit.inputEmail();
        String password = inputInit.inputPassword();
        String yourId = inputInit.inputTwitterAccountId();

        List<String> kolIdList = inputKolId.inputKolIdList();

        Scrap_Kol_Info_Twitter scrap_Kol_Info_Twitter = new Scrap_Kol_Info_Twitter(email, password, yourId);

        for (int i = 0; i < kolIdList.size(); i++) {
            try {
                System.out.println(i);
                scrap_Kol_Info_Twitter.ScrapKolInfor(kolIdList.get(i), i);
            } catch (InterruptedException | DataFormatException e) {
                System.out.println("can't scrapKolInfo");
            }
        }

        scrap_Kol_Info_Twitter.quitWebScrapper();

        System.out.println("finished scrap kol info");
    }
}
