package com.dinhducmanh.twittercrawler.webcrawler;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.dinhducmanh.twittercrawler.webcrawler.Interface.WebScrapper;

public final class TwitterWebScrapper implements WebScrapper{
    private final WebDriver currentDriver = new ChromeDriver();
    private final JavascriptExecutor js = (JavascriptExecutor) this.currentDriver;
    


    //constructor đăng nhập vào 1 tài khoản twitter bằng tài khoản được cho
    public TwitterWebScrapper(String email, String password, String userId) throws InterruptedException {
        setTimeOut(50);
        
        try{
            get("https://x.com/search?q=%23Blockchain&src=recent_search_click&f=user");
            loginByGmail(email, password,userId);
        }
        
        catch(InterruptedException e){
            System.out.println("Fail to initialize TwitterScrapper");
            quit();
        }
    }

    //constructor kết nối tới 1 web cụ thể sau khi đăng nhập vào twitter
    @SuppressWarnings("UseSpecificCatch")
    public TwitterWebScrapper(String url, String email, String password, String userId) throws InterruptedException {
        this(email, password, userId);
        get(url);
    }


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
        if (scrollCounter >= random.nextInt(2)*5 + 15 + random.nextInt(10)) {
            Thread.sleep(random.nextInt(10)*1000 + random.nextInt(100)*10 + 27000 + fixWaitingTime);
        }
        else Thread.sleep(random.nextInt(3)*1000 + random.nextInt(10)*100 + fixWaitingTime);

        //check xem scroll tới cuối trang chưa, chưa thì trả về false
        new_Height = scroll_Height();
        if (new_Height != last_Height) {
            return false;
        }

        //kiểm tra xem có phải do bị block không, nếu không thì hết trang thật rồi
        Document doc = Jsoup.parse(currentDriver.getPageSource());
        if(doc.selectXpath("//span[text()='Thử lại']").isEmpty()) return true;

        //nếu bị block thật
        while(!currentDriver.findElements(By.xpath("//span[text()='Thử lại']")).isEmpty()){
            Thread.sleep(random.nextInt(2)*100000+ random.nextInt(9)*10000+ random.nextInt(9)*1000+ random.nextInt(9)*100 + 100000);
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
    public void loginByGmail (String email, String password, String userId) throws InterruptedException {
        String origin_Window = currentDriver.getWindowHandle();

        //bấm vào nút login by gmail
        currentDriver.findElement(By.cssSelector("span[class='nsm7Bb-HzV7m-LgbsSe-BPrWId'")).click();
        Thread.sleep(5000);
    

        //danh sách window đã mở, chuyển sang cửa sổ vừa mở
        Object[] windowHandle = currentDriver.getWindowHandles().toArray();
        currentDriver.switchTo().window((String) windowHandle[currentDriver.getWindowHandles().size() - 1]);         
        
        //thao tác đăng nhập
        WebElement userNameBox = currentDriver.findElement(By.cssSelector("input[type='email']"));
        userNameBox.sendKeys(email);
        currentDriver.findElement(By.xpath("//span[text()='Tiếp theo']")).click();
        
        Thread.sleep(4000);

        WebElement passwordBox = currentDriver.findElement(By.cssSelector("input[type='password']"));
        passwordBox.sendKeys(password);
        currentDriver.findElement(By.xpath("//span[text()='Tiếp theo']")).click();
    
        //chuyển lại cửa sổ ban đầu
        currentDriver.switchTo().window(origin_Window);
        Thread.sleep(1000);
        if( !currentDriver.findElements( By.xpath("//span[text()='Giúp chúng tôi giữ tài khoản của bạn an toàn.']") ).isEmpty() ){
            WebElement userIdBox = currentDriver.findElement(By.name("text"));
            userIdBox.sendKeys(userId);
            currentDriver.findElement(By.xpath("//span[text()='Tiếp theo']")).click();
        }

        Thread.sleep(4000);
    }

    @Override
    public void scroll_To_Bottom() throws InterruptedException {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    //trả về chiều cao hiện tại của con lăn trang
    @Override
    public Long scroll_Height() {
        return  (long) js.executeScript("return document.body.scrollHeight;");
    }

    // phương thức này trả về danh sách các element cần tìm trên web
    @SuppressWarnings({"CallToPrintStackTrace", "UseSpecificCatch"})
    @Override
    public Elements findingListOfElement(String elements_Path,int numberOfElement){
        try{
            int elementCounter = 0;
            Elements resultList = new Elements();
            Document doc = Jsoup.parse(getPageSource());
            Elements currentElementList = doc.select(elements_Path);
            Elements oldElementList = new Elements();

            while ( !currentElementList.isEmpty() ){

                for(Element item : currentElementList){

                    //check xem có trong danh sách cũ ko, nếu có rồi thì skip lần chạy này
                    if( isContain(oldElementList, item) ) {
                        continue;
                    }

                    resultList.add(item);

                    elementCounter++;
                    if (elementCounter >= numberOfElement) return resultList;
                }
                
                if(isEndOfPage()) break;
                
                oldElementList.clear();
                oldElementList.addAll(currentElementList);
                //lưu lại danh sách cũ, khởi tạo danh sách mới
                doc = Jsoup.parse(getPageSource());
                currentElementList = doc.select(elements_Path);
            }

            return resultList;
        }

        catch (Exception e){
            System.out.println("findingListOfElement got error");
            e.printStackTrace();
            quit();
            return null;
        }

    }

    public static boolean isContain (Elements listElements, Element item){
        for (Element listItem : listElements){
            if(item.hasSameValue(listItem)) return true;
        }
    
        return false;
    }

    // thoát driver
    @Override
    public void quit(){
        currentDriver.quit();
    }

    @Override
    public WebElement findElementToInteract(String cssSelector){
        return currentDriver.findElement(By.cssSelector(cssSelector));
    }

    //GETTER
    @Override
    public String getPageSource(){
        return currentDriver.getPageSource();
    }

    @Override
    public void get(String url){
        currentDriver.get(url);
    }

    //setter
    @Override
    public void setTimeOut(int amountSeconds){
        Duration duration = Duration.of(amountSeconds, ChronoUnit.SECONDS);
        currentDriver.manage().timeouts().implicitlyWait(duration);
        currentDriver.manage().timeouts().pageLoadTimeout(duration);
    }
}