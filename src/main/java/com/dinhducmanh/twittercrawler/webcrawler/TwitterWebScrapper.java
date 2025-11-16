package com.dinhducmanh.twittercrawler.webcrawler;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.dinhducmanh.twittercrawler.Main_Class;
import com.dinhducmanh.twittercrawler.webcrawler.Interface.WebScrapper;
import com.google.common.annotations.VisibleForTesting;

public final class TwitterWebScrapper implements WebScrapper {

    // Thời gian chờ người dùng nhập thủ công tối đa (phút)
    private static final int MAX_WAIT_MINUTES = 5;

    private static final String BROWSER_TYPE = "chrome";

    //logging
    private static final Logger logger = LogManager.getLogger(Main_Class.class);

    //  driver session bao gồm cả driver và profile session tương ứng
    //cho nên tài nguyên của driver session sẽ được giải phóng khi 
    //gọi quit() của driver session
    private final DriverSession driverSession;
    private final WebDriver currentDriver;
    private final JavascriptExecutor js;

    /**
     * CONSTRUCTOR Default (Dùng cho Test) Chỉ khởi tạo session, không đăng
     * nhập. Đây là mấu chốt để Giai đoạn 1 (tuần tự) của bạn hoạt động.
     */
    public TwitterWebScrapper() {
        logger.debug("Creating new DriverSession...");
        this.driverSession = DriverFactory.createDriverSession(BROWSER_TYPE);
        this.currentDriver = driverSession.getDriver();
        this.js = (JavascriptExecutor) this.currentDriver;
        setTimeOut(50); // Thiết lập timeout cơ bản
        logger.debug("DriverSession created.");
    }

    //constructor đăng nhập vào 1 tài khoản twitter bằng tài khoản được cho
    //các tham số chủ yếu để có thể tích hợp với giao diện code cũ
    public TwitterWebScrapper(String email, String password, String userId) throws InterruptedException {
        // 1. Tạo DriverSession mới
        this();
        setTimeOut(50);

        // Nếu các hàm này thất bại, Exception sẽ tự động ném ra
        // và Main_Class (ở khối finally) sẽ bắt được và gọi quit().
        performLogin(email, password, userId);
        logger.info("TwitterScrapper initialized successfully.");
    }

    /**
     * CONSTRUCTOR DÙNG ĐỂ TEST (hoặc nội bộ) Cho phép tiêm (inject) một
     * DriverSession (thật hoặc giả)
     */
    @VisibleForTesting // Đánh dấu đây là constructor dùng cho test
    TwitterWebScrapper(DriverSession driverSession) {
        this.driverSession = driverSession;
        this.currentDriver = driverSession.getDriver();
        this.js = (this.currentDriver != null) ? (JavascriptExecutor) this.currentDriver : null;
    }

    /*-------------------------Tổ hợp method cho việc login--------------------------------------*/
    /**
     * HÀM LOGIN Thực hiện đăng nhập vào Twitter/X.
     *
     * @param email Email/SĐT đăng nhập
     * @param password Mật khẩu
     * @param userId ID tài khoản (dùng để log + để phòng trường hợp cần xác
     * định tài khoản)
     */
    public void performLogin(String email, String password, String userId) throws InterruptedException {
        logger.info("Performing login for user: {}", userId);
        get("https://x.com/search?q=%23Blockchain&src=recent_search_click&f=user");
        LoginHandler(email, password, userId);
        logger.info("Login successful for user: {}", userId);
    }

    public void LoginHandler(String email, String password, String userId) {
        if (!ensureLoggedIn()) {
            throw new RuntimeException("Login failed for user: " + userId);
        }
    }

    @Override
    /**
     * Phương thức chính để đảm bảo phiên đăng nhập hợp lệ.
     *
     * @param currentDriver Webdriver đã khởi tạo (có Profile).
     * @return true nếu đã đăng nhập xong, false nếu thất bại/timeout.
     */
    public boolean ensureLoggedIn() {
        System.out.println(">> [LoginHandler] Checking login status...");
        logger.info("[LoginHandler] Start Checking login status.");

        // 1. Mở trang chủ X (nếu chưa ở đó)
        String currentUrl = currentDriver.getCurrentUrl();
        if (!currentUrl.contains("twitter.com") && !currentUrl.contains("x.com")) {
            currentDriver.get("https://x.com/home");
        }

        // 2. Check nhanh: Nếu cookie cũ còn sống -> OK luôn
        if (isSessionActive()) {
            System.out.println(">> [LoginHandler] ✅ Detect old login session (valid Cookie).");
            logger.info("[LoginHandler] Detect old login session (valid Cookie).");
            return true;
        }

        // 3. Nếu chưa -> Chờ người dùng nhập tay
        System.out.println(">> [LoginHandler] ⚠️ Not yet login or expired Cookie.");
        System.out.println(">> [ACTION REQUIRED] Please login on the browser manually...");

        return waitForManualLogin();
    }

    /**
     * Kiểm tra xem trình duyệt có đang ở trạng thái đã login không. Dấu hiệu:
     * Thấy nút Account (Avatar nhỏ góc dưới trái) hoặc nút Home.
     */
    private boolean isSessionActive() {
        try {
            // Chỉ chờ 7 giây để check, không chờ lâu
            WebDriverWait wait = new WebDriverWait(currentDriver, Duration.ofSeconds(7));

            // Xpath tìm nút Avatar người dùng (SideNav_AccountSwitcher_Button)
            // Hoặc tìm tab Home (AppTabBar_Home_Link)
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-testid='SideNav_AccountSwitcher_Button']")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@data-testid='AppTabBar_Home_Link']"))
            ));
            return true;
        } catch (Exception e) {
            logger.error("Waiting check login condition time out", e);
            return false;
        }
    }

    /**
     * Vòng lặp chờ người dùng thao tác thủ công.
     */
    private boolean waitForManualLogin() {
        long endTime = System.currentTimeMillis() + (MAX_WAIT_MINUTES * 60 * 1000);
        
        // In ra hướng dẫn
        System.out.println("----------------------------------------------------");
        System.out.println("   Manual Login Instructions:");
        System.out.println("   1. Login into the X website using the opened browser.");
        System.out.println("   2. Wait for the homepage to load completely.");
        System.out.println("   3. Input 2FA (if there is any) manually.");
        System.out.println("   -> The program will automatically run after login successfully.");
        System.out.println("   (Timeout: " + MAX_WAIT_MINUTES + " minutes)");
        System.out.println("----------------------------------------------------");

        while (System.currentTimeMillis() < endTime) {
            // hàm sẽ kiểm tra xem đã đăng nhập được chưa, thời gian đợi nằm ở isSessionActive()
            if (isSessionActive()) {
                System.out.println(">> [LoginHandler] Detect login success! Saving profile...");
            }
        }

        System.err.println(">> [LoginHandler] Fail: login waiting time time out.");
        logger.error(">> [LoginHandler] Thất bại: Quá thời gian chờ đăng nhập.");
        return false;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    

    //METHOD
    @Override
    public boolean isEndOfPage() throws InterruptedException {
        Random random = new Random();
        long new_Height;
        long last_Height = scroll_Height();
        int fixWaitingTime = 5000; // thời gian chờ cho cái việc kéo trang xuống
        int attemptCounter = 0;
        int scrollCounter = 0;

        scroll_To_Bottom();

        //tạo thời gian chờ để tránh bị block, đợi trang tải xong, tuỳ chỉnh theo hoàn cảnh
        scrollCounter++;
        if (scrollCounter >= random.nextInt(2) * 5 + 15 + random.nextInt(10)) {
            Thread.sleep(random.nextInt(10) * 1000 + random.nextInt(100) * 10 + 27000 + fixWaitingTime);
        } else {
            Thread.sleep(random.nextInt(3) * 1000 + random.nextInt(10) * 100 + fixWaitingTime);
        }

        //check xem scroll tới cuối trang chưa, chưa thì trả về false
        new_Height = scroll_Height();
        if (new_Height != last_Height) {
            return false;
        }

        //kiểm tra xem có phải do bị block không, nếu không thì hết trang thật rồi
        Document doc = Jsoup.parse(currentDriver.getPageSource());
        if (doc.selectXpath("//span[text()='Thử lại']").isEmpty()) {
            return true;
        }

        //nếu bị block thật
        while (!currentDriver.findElements(By.xpath("//span[text()='Thử lại']")).isEmpty()) {
            Thread.sleep(random.nextInt(2) * 100000 + random.nextInt(9) * 10000 + random.nextInt(9) * 1000 + random.nextInt(9) * 100 + 100000);
            currentDriver.findElement(By.xpath("//span[text()='Thử lại']")).click();
            Thread.sleep(5000);

            if (attemptCounter >= 12) {
                throw new NoSuchElementException("this website already have retry button, you should check it at the first place");
            }
            attemptCounter++;
        }

        return false;
    }

    @Override
    public void scroll_To_Bottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    //trả về chiều cao hiện tại của con lăn trang
    @Override
    public Long scroll_Height() {
        return (long) js.executeScript("return document.body.scrollHeight;");
    }

    /**
     * phương thức này trả về danh sách các element cần tìm trên web
     *
     * @param elements_Path: đường dẫn tới các element cần tìm (css selector)
     * @param numberOfElement: số lượng element cần tìm
     * @return danh sách các element tìm được
     */
    @Override
    public Elements findingListOfElement(String elements_Path, int numberOfElement) throws InterruptedException {
        int elementCounter = 0;
        Elements resultList = new Elements();
        Document doc = Jsoup.parse(getPageSource());
        Elements currentElementList = doc.select(elements_Path);
        Elements oldElementList = new Elements();

        while (!currentElementList.isEmpty()) {

            for (Element item : currentElementList) {

                //check xem có trong danh sách cũ ko, nếu có rồi thì skip lần chạy này
                if (isContain(oldElementList, item)) {
                    continue;
                }

                resultList.add(item);

                elementCounter++;
                if (elementCounter >= numberOfElement) {
                    return resultList;
                }
            }

            if (isEndOfPage()) {
                break;
            }

            oldElementList.clear();
            oldElementList.addAll(currentElementList);
            //lưu lại danh sách cũ, khởi tạo danh sách mới
            doc = Jsoup.parse(getPageSource());
            currentElementList = doc.select(elements_Path);
        }

        return resultList;
    }

    /**
     * method kiểm tra 1 element có trong danh sách element hay không (chủ yếu
     * dùng trong class thôi)
     *
     * @param listElements
     * @param item
     * @return
     */
    public static boolean isContain(Elements listElements, Element item) {
        for (Element listItem : listElements) {
            if (item.hasSameValue(listItem)) {
                return true;
            }
        }

        return false;
    }

    /**
     * thoát driver + session hiện tại việc quit có để lại zombie process, tuy
     * nhiên không nghiêm trọng lắm
     */
    @Override
    public void quit() {
        driverSession.quit();
    }

    @Override
    public WebElement findElementToInteract(String cssSelector) {
        return currentDriver.findElement(By.cssSelector(cssSelector));
    }

    //GETTER
    @Override
    public String getPageSource() {
        return currentDriver.getPageSource();
    }

    /**
     * mở 1 url mới trên trình duyệt
     *
     * @param url đường dẫn url cần mở
     */
    @Override
    public void get(String url) {
        currentDriver.get(url);
    }

    //setter
    /**
     * thiết lập thời gian chờ cho driver
     *
     * @param amountSeconds số giây chờ
     */
    @Override
    public void setTimeOut(int amountSeconds) {
        Duration duration = Duration.of(amountSeconds, ChronoUnit.SECONDS);
        currentDriver.manage().timeouts().implicitlyWait(duration);
        currentDriver.manage().timeouts().pageLoadTimeout(duration);
    }

}
