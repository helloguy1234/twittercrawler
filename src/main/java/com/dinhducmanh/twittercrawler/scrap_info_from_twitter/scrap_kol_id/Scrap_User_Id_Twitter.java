package com.dinhducmanh.twittercrawler.scrap_info_from_twitter.scrap_kol_id;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import org.jsoup.nodes.Element;
import org.openqa.selenium.WebElement;

import com.dinhducmanh.twittercrawler.webcrawler.Interface.WebScrapper;
import com.dinhducmanh.twittercrawler.webcrawler.TwitterWebScrapper;

public final class Scrap_User_Id_Twitter{

    private final String userNamePath = "div[class='css-175oi2r r-1awozwy r-18u37iz r-1wbh5a2'] span[class='css-1jxf684 r-bcqeeo r-1ttztb7 r-qvutc0 r-poiln3']";
    private final WebScrapper webScrapper_Kol_Id;
    
    public Scrap_User_Id_Twitter(WebScrapper webScrapper_Kol_Id, String searchString) throws InterruptedException{
        this.webScrapper_Kol_Id = webScrapper_Kol_Id;

        search(searchString);
    }

    public Scrap_User_Id_Twitter(String email, String password, String yourId, String searchString) throws InterruptedException{
        this( new TwitterWebScrapper(email, password, yourId), searchString);
    }


    public List<String> scrapKolId(int numberOfUserName) throws InterruptedException, DataFormatException{
        List<String> listOfUserName = new ArrayList<>();
        
        for( Element userIdElement : webScrapper_Kol_Id.findingListOfElement(userNamePath, numberOfUserName) ){
            listOfUserName.add(userIdElement.text());
        }

        return listOfUserName;
    }



    public void search(String searchString) throws InterruptedException{
        webScrapper_Kol_Id.get("https://x.com/home");
        WebElement searchBox = webScrapper_Kol_Id.findElementToInteract("input[aria-label='Search query']");
        searchBox.sendKeys(searchString);
        searchBox.submit();
        webScrapper_Kol_Id.findElementToInteract("#react-root > div > div > div.css-175oi2r.r-1f2l425.r-13qz1uu.r-417010.r-18u37iz > main > div > div > div > div > div > div.css-175oi2r.r-aqfbo4.r-gtdqiz.r-1gn8etr.r-1g40b8q > div.css-175oi2r.r-1e5uvyk.r-5zmot > div:nth-child(2) > nav > div > div.css-175oi2r.r-1adg3ll.r-16y2uox.r-1wbh5a2.r-1pi2tsx > div > div:nth-child(3) > a > div > div > span").click();
        Thread.sleep(5000);
    }

    public void quit(){
        webScrapper_Kol_Id.quit();
    }
}
