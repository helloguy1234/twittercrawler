package com.dinhducmanh.twittercrawler;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dinhducmanh.twittercrawler.input_function.ExcelInput_Account_Information;
import com.dinhducmanh.twittercrawler.input_function.ExcelInput_For_GerKolId;
import com.dinhducmanh.twittercrawler.input_function.Interface.Input_Account_Information;
import com.dinhducmanh.twittercrawler.input_function.Interface.Input_For_GetKolId;
import com.dinhducmanh.twittercrawler.output_function.ExcelOutput_Kol_Id;
import com.dinhducmanh.twittercrawler.output_function.Interface.OutPut_Kol_Id;
import com.dinhducmanh.twittercrawler.scrap_info_from_twitter.scrap_kol_id.Scrap_User_Id_Twitter;

// Lớp chính (Main class) để điều phối tác vụ quét (scraping) User ID từ Twitter.
public class Main_Class {

    // 1. Khởi tạo Logger
    private static final Logger logger = LogManager.getLogger(Main_Class.class);

    public static void main(String[] args) throws InterruptedException, Exception {
        //initialize input và output
        Input_Account_Information inputAcc;
        Input_For_GetKolId input_For_GetKolId;
        OutPut_Kol_Id excelOutput;

        // là phần scraping chính
        Scrap_User_Id_Twitter scrap_Kol_Id = null;

        try {
            // --- Phần nhập dữ liệu ---
            inputAcc = new ExcelInput_Account_Information();
            input_For_GetKolId = new ExcelInput_For_GerKolId();

            String email = inputAcc.inputEmail();
            String password = inputAcc.inputPassword();
            String twitterAccId = inputAcc.inputTwitterAccountId();

            String searchString = input_For_GetKolId.inputSearchString();

            int numberOfKolId = input_For_GetKolId.inputNumberOfKol();

            logger.debug("Input loaded successfully. Searching for: '{}'", searchString);

            // --- Phần xử lý chính ---
            excelOutput = new ExcelOutput_Kol_Id();
            scrap_Kol_Id = new Scrap_User_Id_Twitter(email, password, twitterAccId, searchString);

            List<String> kol_Id_List = scrap_Kol_Id.scrapKolId(numberOfKolId);
            logger.info("Successfully scraped {} user IDs.", kol_Id_List.size());

            //--- Phần output dữ liệu ---
            excelOutput.outputlistOfKolId(kol_Id_List);
            logger.info("Output successfully written");

        } catch (InterruptedException e) {
            logger.error("An error occurred during the scraping task:", e);
        } finally {
            if (scrap_Kol_Id != null) {
                logger.info("Task finished. Closing the browser...");
                try {
                    scrap_Kol_Id.quit();   // Đóng trình duyệt
                } catch (Exception e) {
                    logger.error("Error while quitting the WebDriver instance:", e);
                }
            } else {
                logger.warn("Scraper was not initialized, nothing to quit.");
            }
        }

        logger.info("Task completed.");
    }

}
