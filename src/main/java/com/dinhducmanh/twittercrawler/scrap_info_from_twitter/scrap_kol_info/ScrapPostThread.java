package com.dinhducmanh.twittercrawler.scrap_info_from_twitter.scrap_kol_info;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dinhducmanh.twittercrawler.webcrawler.Interface.WebScrapper;

public class ScrapPostThread extends Thread {

    private final int maxSizeOfOfPostList = 50;
    private final int maxSizeOfCommentList = 20;

    private final String listPostUrlPath = "a[class='css-146c3p1 r-bcqeeo r-1ttztb7 r-qvutc0 r-1qd0xha r-a023e6 r-rjixqe r-16dba41 r-xoduu5 r-1q142lx r-1w6e6rj r-9aw3ui r-3s2u2q r-1loqt21']";//cái đường link nó nằm trong cái thời gian của post ý
    // chú ý là dấu cách (space) trong css selector có nghĩa là chọn tất cả thành phần nằm trong 1 thẻ (decendent combine)
    private final StringBuffer postUrl = new StringBuffer();

    private final WebScrapper userProfileWebScrapper;
    private final WebScrapper postWebScrapper;
    private final String userUrl;
    private final String userId;

    private final List<String> commentedUserIdList = new ArrayList<>();
    private final List<String> repostedUserIdList = new ArrayList<>();
    private int numberOfFollower;

    public ScrapPostThread(WebScrapper userProfileWebScrapper, WebScrapper postWebScrapper, String userId) {
        this.userProfileWebScrapper = userProfileWebScrapper;
        this.postWebScrapper = postWebScrapper;
        this.userUrl = "https://x.com/" + userId;
        this.userId = userId;
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void run() {
        try {
            System.out.println("user Id : " + userId);
            userProfileWebScrapper.get(userUrl);
            Thread.sleep(3000);

            findNumberOfFollower();

            Elements listOfPostUrElement = userProfileWebScrapper.findingListOfElement(listPostUrlPath, maxSizeOfOfPostList);

            for (Element postUrlElement : listOfPostUrElement) {
                //lấy postUrl để kết nối tới post đó
                postUrl.replace(0, postUrl.length(), "https://x.com" + postUrlElement.attr("href"));

                //lấy danh sách user đã comment vào 1 post
                postWebScrapper.get(postUrl.toString());
                Thread.sleep(3000);

                findRepostedUserId();
                findCommentedUserId();
            }

        } catch (InterruptedException e) {
            System.out.println("thread got interupted while sleeping");
            e.printStackTrace();
        } catch (DataFormatException e) {
            System.out.println("xau numberOfFollower sai format");
            e.printStackTrace();
        }
    }

    public void findNumberOfFollower() throws DataFormatException, InterruptedException {
        final String numOfFollowerPath = "div[class='css-175oi2r r-13awgt0 r-18u37iz r-1w6e6rj']>div:nth-child(2)>a>span:first-child>span";
        Elements theNumberOfFollowerStringList = userProfileWebScrapper.findingListOfElement(numOfFollowerPath, 1);

        if (theNumberOfFollowerStringList.isEmpty()) {
            numberOfFollower = 0;
            return;
        }

        numberOfFollower = (int) numberStringToFloat(theNumberOfFollowerStringList.text());
    }

    private float numberStringToFloat(String a) throws DataFormatException {
        char heSoDangChu = a.charAt(a.length() - 1);
        char what = 32;

        a = a.replaceAll(",", "");

        if ('0' <= heSoDangChu && heSoDangChu <= '9') {
            return Float.parseFloat(a);
        }

        a = a.replace(heSoDangChu, what);

        if (heSoDangChu == 'B') {
            return Float.parseFloat(a) * 1000000000;
        }
        if (heSoDangChu == 'M') {
            return Float.parseFloat(a) * 1000000;
        }
        if (heSoDangChu == 'K') {
            return Float.parseFloat(a) * 1000;
        }

        return Float.parseFloat(a);
    }

    public void findRepostedUserId() throws InterruptedException {
        String postCreatorIdPath = "div:nth-child(2)>div>div>a>div>span";
        Elements postCreatorIdElement = postWebScrapper.findingListOfElement(postCreatorIdPath, 1);

        if (userId.equals(postCreatorIdElement.text())) {
            return;
        }
        if (repostedUserIdList.contains(postCreatorIdElement.text())) {
            return;
        }

        repostedUserIdList.add(postCreatorIdElement.text());
    }

    public void findCommentedUserId() throws InterruptedException {
        String listCommentPath = "div[class='css-175oi2r r-18u37iz r-1wbh5a2 r-1ez5h0i'] a>div>span[class='css-1jxf684 r-bcqeeo r-1ttztb7 r-qvutc0 r-poiln3']";
        Elements listOfCommentedUserElements = postWebScrapper.findingListOfElement(listCommentPath, maxSizeOfCommentList);
        StringBuilder commentedUser_Id = new StringBuilder();

        for (Element commentedUserElement : listOfCommentedUserElements) {
            commentedUser_Id.replace(0, commentedUser_Id.length(), commentedUserElement.text());
            //output
            if (userId.equals(commentedUser_Id.toString())) {
                continue;
            }
            if (commentedUserIdList.contains(commentedUser_Id.toString())) {
                continue;
            }

            commentedUserIdList.add(commentedUser_Id.toString());
        }
    }

    public List<String> getRepostedUserIdList() {
        return repostedUserIdList;
    }

    public List<String> getCommentedUserIdList() {
        return commentedUserIdList;
    }

    public int getNumberOfFollower() {
        return numberOfFollower;
    }

}
