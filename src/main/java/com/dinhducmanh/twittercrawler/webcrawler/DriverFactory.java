package com.dinhducmanh.twittercrawler.webcrawler;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * class có nhiệm vụ khởi tạo DriverSession với profile rảnh
 */
public final class DriverFactory {

    private static final Logger logger = LogManager.getLogger(DriverFactory.class);

    /**
     * Class này là 1 dạng class Ultility để cấp phát profile rảnh cho
     * DriverFactory
     */
    private class ProfileAllocator {

        //     đây là file lock dùng để kiểm tra nếu profile đang được sử dụng hay không
        // nếu lock thành công thì profile rảnh, ngược lại thì profile đang bận
        // sau khi kiểm tra thành công thì sẽ nhả lock ngay 
        // (để DriverFactory lấy lại khóa khi tạo DriverSession)
        private static final String LOCK_FILE_NAME = "tool_lock.lock";

        /**
         * Tự động tìm một profile rảnh trong thư mục gốc.
         *
         * @param rootPath Thư mục chứa các profile (VD: C:/SeleniumData)
         * @param baseName tên thể hiện loại trình duyệt (chrome_profile,
         * edge_profile, firefox_profile)
         * @return ProfileSession chứa đường dẫn và khóa.
         */
        public static ProfileSession acquireFreeProfile(String rootPath, String baseName) {
            int index = 0; //khởi tạo bắt đầu từ đầu danh sách

            // Đảm bảo thư mục gốc tồn tại
            new File(rootPath).mkdirs();

            while (true) {
                // Tạo đường dẫn: .../chrome_profile_1, .../chrome_profile_2
                String currentProfilePath = rootPath + File.separator + baseName + "_" + index;
                File profileDir = new File(currentProfilePath);

                // Nếu thư mục chưa có -> Tạo mới -> Chắc chắn rảnh -> Lấy luôn
                if (!profileDir.exists()) {
                    profileDir.mkdirs();
                    return lockProfile(currentProfilePath); // Lấy luôn
                }

                // Nếu thư mục đã có -> Thử khóa xem có ai đang dùng không
                ProfileSession session = lockProfile(currentProfilePath);
                if (session != null) {
                    System.out.println(">> [ProfileManager] Found free profile: " + profileDir.getName());
                    return session; // Khóa thành công -> Lấy dùng
                }

                // Nếu khóa thất bại (đang bận) -> Tăng index để thử cái tiếp theo
                index++;

                // Safety break: Tránh vòng lặp vô tận nếu lỗi hệ thống (tùy chỉnh số lượng tối đa)
                if (index >= 100) {
                    logger.error("Quá tải: Tất cả 100 profile đều đang bận!");
                    throw new RuntimeException("Quá tải: Tất cả 100 profile đều đang bận!");

                }
            }
        }

        /**
         * Hàm core: Thử tạo file lock
         *
         * @param path đường dẫn tới profile
         * @return ProfileSession nếu khóa thành công, null nếu thất bại
         */
        private static ProfileSession lockProfile(String path) {
            try {
                File lockFile = new File(path + File.separator + LOCK_FILE_NAME);
                RandomAccessFile file = new RandomAccessFile(lockFile, "rw");
                FileChannel channel = file.getChannel();

                // tryLock() là non-blocking: Nếu khóa được thì trả về lock, không thì trả về null (không chờ)
                FileLock lock = channel.tryLock();

                if (lock != null) {
                    return new ProfileSession(path, lock, file, channel);
                } else {
                    // Đóng resource nếu không khóa được
                    channel.close();
                    file.close();
                }
            } catch (IOException e) {
                // Lỗi IO, coi như không lấy được
            }
            return null;
        }
    }

    /**
     * Tạo một phiên làm việc mới. Tự động tìm profile rảnh.
     *
     * @param browserType "chrome", "edge", "firefox"
     * @param rootDataPath Thư mục chứa data (VD: C:/SeleniumData)
     * @return DriverSession (Chứa Driver + Khóa)
     */
    static DriverSession createDriverSession(String browserType) {
        // thư mục gốc chứa profile, phải là đường dẫn tuyệt đối nếu không driver sẽ lỗi
        String rootDataPath
                = System.getProperty("user.dir") + File.separator + "selenium_profiles";

        // 1. Tìm và khóa một profile rảnh
        ProfileSession session = ProfileAllocator.acquireFreeProfile(rootDataPath, browserType);
        String profilePath = session.getProfilePath();

        System.out.println(">> [Factory] Đang khởi tạo " + browserType + " với profile: " + profilePath);

        WebDriver driver = null;

        try {
            switch (browserType.toLowerCase()) {
                case "chrome" -> {
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOps = new ChromeOptions();
                    chromeOps.addArguments("user-data-dir=" + profilePath);
                    chromeOps.addArguments("profile-directory=Default");
                    configureChromium(chromeOps); // Anti-bot
                    driver = new ChromeDriver(chromeOps);
                }

                case "edge" -> {
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions edgeOps = new EdgeOptions();
                    edgeOps.addArguments("user-data-dir=" + profilePath);
                    edgeOps.addArguments("profile-directory=Default");
                    configureChromium(edgeOps); // Anti-bot
                    driver = new EdgeDriver(edgeOps);
                }

                case "firefox" -> {
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOps = new FirefoxOptions();
                    // Firefox dùng tham số -profile
                    firefoxOps.addArguments("-profile");
                    firefoxOps.addArguments(profilePath);

                    // Anti-bot cho Firefox
                    firefoxOps.addPreference("dom.webdriver.enabled", false);
                    firefoxOps.addPreference("useAutomationExtension", false);

                    driver = new FirefoxDriver(firefoxOps);
                }

                default -> throw new IllegalArgumentException("Browser không hỗ trợ: " + browserType);
            }
        } catch (IllegalArgumentException e) {
            // Nếu khởi tạo driver thất bại, phải nhả khóa ngay để profile không bị kẹt
            logger.error("Lỗi khi khởi tạo driver");
            session.release();
            throw e;
        }

        return new DriverSession(driver, session);
    }

    // Cấu hình chung cho nhân Chromium (Chrome/Edge)
    private static void configureChromium(ChromeOptions options) {
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        // options.addArguments("--headless"); // Bật dòng này nếu muốn chạy ẩn
    }

    private static void configureChromium(EdgeOptions options) {
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
    }
}
