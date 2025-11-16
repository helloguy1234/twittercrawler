package com.dinhducmanh;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dinhducmanh.twittercrawler.webcrawler.TwitterWebScrapper;

/**
 * Kịch bản test:
 * 1. TẠO HÀNG LOẠT (Tuần tự): Mở 5 trình duyệt và giữ chúng trong một List.
 * 2. ĐÓNG HÀNG LOẠT (Tuần tự): Đóng cả 5 trình duyệt đã mở.
 *
 * Mục tiêu: Kiểm tra rò rỉ tài nguyên khi giữ nhiều session cùng lúc.
 * KHÔNG login, KHÔNG đa luồng.
 */
public class BatchLifecycleTest {

    private static final Logger logger = LogManager.getLogger(BatchLifecycleTest.class);
    private static final int NUMBER_OF_TESTS = 5; // Số lượng object

    public static void main(String[] args) {
        
        logger.info("Bắt đầu Test vòng đời HÀNG LOẠT (Tạo {} -> Đóng {})...", NUMBER_OF_TESTS, NUMBER_OF_TESTS);
        
        // List để "giữ" các scrapper, ngăn chúng bị 'Garbage Collector' (trình dọn rác)
        List<TwitterWebScrapper> scrapperList = new ArrayList<>();

        // -----------------------------------------------------------------
        // --- GIAI ĐOẠN 1: TẠO {} SCRAPPERS (TUẦN TỰ) ---
        // -----------------------------------------------------------------
        logger.info("--- GIAI ĐOẠN 1: TẠO {} SCRAPPERS (TUẦN TỰ) ---", NUMBER_OF_TESTS);
        try {
            for (int i = 1; i <= NUMBER_OF_TESTS; i++) {
                logger.info("[Tạo {}] Đang gọi new TwitterWebScrapper()... (Mở trình duyệt...)", i);
                
                // Sử dụng constructor không tham số, KHÔNG login
                TwitterWebScrapper scrapper = new TwitterWebScrapper(); 
                
                scrapperList.add(scrapper); // Thêm vào danh sách để giữ
                logger.info("[Tạo {}] Tạo THÀNH CÔNG. (Trình duyệt {} đã mở và đang chạy).", i, i);

                // Tạm dừng 1 giây giữa mỗi lần tạo để dễ quan sát
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
            }
        } catch (Exception e) {
            logger.error("Gặp lỗi nghiêm trọng trong Giai đoạn 1 (Tạo). Dừng test.", e);
            // Dọn dẹp những cái đã lỡ mở (nếu có)
            for (TwitterWebScrapper scrapper : scrapperList) {
                if (scrapper != null) scrapper.quit();
            }
            return; // Dừng test
        }

        logger.info("--- GIAI ĐOẠN 1: HOÀN TẤT ({} trình duyệt đang chạy) ---\n", scrapperList.size());
        
        // Tạm dừng 5 giây để bạn kiểm tra Task Manager
        logger.info("Tạm dừng 5 giây... (Bạn có thể kiểm tra Task Manager ngay bây giờ)");
        try { Thread.sleep(5000); } catch (InterruptedException e) {}
        

        // -----------------------------------------------------------------
        // --- GIAI ĐOẠN 2: ĐÓNG {} SCRAPPERS (TUẦN TỰ) ---
        // -----------------------------------------------------------------
        logger.info("--- GIAI ĐOẠN 2: ĐÓNG {} SCRAPPERS (TUẦN TỰ) ---", scrapperList.size());

        for (int i = 0; i < scrapperList.size(); i++) {
            TwitterWebScrapper scrapper = scrapperList.get(i);
            int testNum = i + 1; // Số thứ tự
            
            try {
                logger.info("[Dọn dẹp {}] Đang gọi quit()... (Đóng trình duyệt...)", testNum);
                scrapper.quit();
                logger.info("[Dọn dẹp {}] Đã quit() xong.", testNum);

                // Tạm dừng 1 giây giữa mỗi lần đóng
                try { Thread.sleep(1000); } catch (InterruptedException e) {}

            } catch (Exception e) {
                // Ghi lại lỗi nếu có, nhưng vẫn tiếp tục dọn dẹp các cái khác
                logger.error("[Dọn dẹp {}] Gặp lỗi khi đang quit():", testNum, e);
            }
        }

        logger.info("--- GIAI ĐOẠN 2: HOÀN TẤT ---");
        logger.info("Test vòng đời (hàng loạt) hoàn tất.");
    }
}