package com.dinhducmanh.twittercrawler.webcrawler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * class wrapper chứa WebDriver và ProfileSession, có thể hiểu là mỗi webdriver
 * sẽ có profile riêng ứng với tài khoản sử dụng cho login (có thể giống nhau)
 */
class DriverSession {
    private static final Logger logger = LogManager.getLogger(DriverSession.class);

    private final WebDriver driver;
    private final ProfileSession profileSession;

    /**
     * @param driver WebDriver đã được khởi tạo
     * @param profileSession Profile ứng với Session hiện tại của WebDriver,
     * chứa các thông tin về tài khoản và
     */
    public DriverSession(WebDriver driver, ProfileSession profileSession) {
        this.driver = driver;
        this.profileSession = profileSession;
    }

    /**
     * Hàm thoát driver và giải phóng profile
     */
    public void quit() {
        if (driver != null) {
            try {
                driver.quit(); // Tắt trình duyệt trước
            } catch (Exception e) {
                logger.error("lỗi khi tắt driver", e);
            }
        }
        if (profileSession != null) {
            profileSession.release(); // Mở khóa file sau
        }
    }

    /**
     * Lấy WebDriver ứng với DriverSession này
     *
     * @return WebDriver
     */
    public WebDriver getDriver() {
        return driver;
    }
}
