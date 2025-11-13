package com.dinhducmanh.twittercrawler.scrap_info_from_twitter.scrap_kol_info;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dinhducmanh.twittercrawler.webcrawler.Interface.WebScrapper;

public class ScrapFollowingThread extends Thread {
    private final int maxSizeOfFollowingList = 50;
    private final String followingListUrl;
    private final String listFollowingPath = "div[class='css-146c3p1 r-dnmrzs r-1udh08x r-3s2u2q r-bcqeeo r-1ttztb7 r-qvutc0 r-1qd0xha r-a023e6 r-rjixqe r-16dba41 r-18u37iz r-1wvb978'] span";
    private final WebScrapper followingWebScrapper;

    private final List<String> followingUserIdList = new ArrayList<>();
    

    public ScrapFollowingThread (WebScrapper followingWebScrapper, String userId){
        this.followingWebScrapper = followingWebScrapper;
        this.followingListUrl = "https://x.com/" + userId + "/following";
    }


    @Override
    @SuppressWarnings({"CallToPrintStackTrace", "UseSpecificCatch"})
    public void run() {
        try{
            followingWebScrapper.get(followingListUrl);
            Thread.sleep(3000);

            Elements listOfElement = followingWebScrapper.findingListOfElement(listFollowingPath, maxSizeOfFollowingList);
            for(Element item : listOfElement){
                //output
                followingUserIdList.add(item.text());
            }

        }catch(Exception e){
            System.out.println("ScrapVerifiedFollowingThread got error");
            e.printStackTrace();
        }

    }
    
    public List<String> getfollowingUserIdList(){
        return followingUserIdList;
    }
}