package com.dinhducmanh.twittercrawler.webcrawler.Interface;

import org.jsoup.select.Elements;
import org.openqa.selenium.WebElement;

public interface WebScrapper {

    public boolean isEndOfPage() throws Exception;

    public boolean ensureLoggedIn();

    public void scroll_To_Bottom() throws Exception;

    public Long scroll_Height();

    public Elements findingListOfElement(String elements_Path, int numberOfElement) throws InterruptedException;

    public void quit();

    public WebElement findElementToInteract(String cssSelector);

    //getter
    public String getPageSource();

    public void get(String url);

    //setter
    public void setTimeOut(int amountSeconds);
}
