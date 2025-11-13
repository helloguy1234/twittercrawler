package com.dinhducmanh.twittercrawler.scrap_info_from_twitter.scrap_kol_info;

import java.util.zip.DataFormatException;

import com.dinhducmanh.twittercrawler.output_function.ExcelOutput_Kol_Infor;
import com.dinhducmanh.twittercrawler.output_function.Interface.OutPut_Kol_Infor;
import com.dinhducmanh.twittercrawler.webcrawler.Interface.WebScrapper;
import com.dinhducmanh.twittercrawler.webcrawler.TwitterWebScrapper;

public class Scrap_Kol_Info_Twitter {
    private final WebScrapper postScrapper;
    private final WebScrapper commentScrapper;
    private final WebScrapper followScrapper;
    private ScrapPostThread scrapPostThread;
    private ScrapFollowingThread scrapFollowingUserIdThread;

    private final OutPut_Kol_Infor outputToFile ;


    public Scrap_Kol_Info_Twitter(WebScrapper postScrapper,WebScrapper commentScrapper,WebScrapper followScrapper) throws Exception{
        this.postScrapper = postScrapper;
        this.commentScrapper = commentScrapper;
        this.followScrapper = followScrapper;
        outputToFile = new ExcelOutput_Kol_Infor();
    }
    
    public Scrap_Kol_Info_Twitter(String email, String password, String yourId) throws Exception{
    
        postScrapper = new TwitterWebScrapper(email, password, yourId);
        commentScrapper = new TwitterWebScrapper(email, password, yourId);
        followScrapper = new TwitterWebScrapper(email, password, yourId);
        outputToFile = new ExcelOutput_Kol_Infor();
    }

    //return false nếu user không đủ điều kiện để scrap (tức không là kol)
    @SuppressWarnings("CallToPrintStackTrace")
    public void ScrapKolInfor(String userId, int userIndex) throws InterruptedException, DataFormatException {
        //Xác định là kol thì bắt đầu search post với follower
        //scrap post thì có comment và repost nếu cần
        scrapPostThread = new ScrapPostThread(postScrapper, commentScrapper, userId);
        //scrap follower
        scrapFollowingUserIdThread = new ScrapFollowingThread(followScrapper, userId);
        
        scrapPostThread.start();
        scrapFollowingUserIdThread.start();

        scrapPostThread.join();
        scrapFollowingUserIdThread.join();

        try {
            outputToFile.setCurrentUserIndex(userIndex);

            System.out.println("OutPut numberOfFollower");
            outputToFile.outputListNumberOfFollower(scrapPostThread.getNumberOfFollower());
            
            System.out.println("OutPut commented");
            outputToFile.outputCommentedUserIdList(scrapPostThread.getCommentedUserIdList());
    
            System.out.println("OutPut reposted");
            outputToFile.outputRepostedUserIdList(scrapPostThread.getRepostedUserIdList());

            System.out.println("OutPut following");   
            outputToFile.outputfollowingUserIdList( scrapFollowingUserIdThread.getfollowingUserIdList() );

        } catch (Exception e) {
            System.out.println("Fail to output to file");
            e.printStackTrace();
        }
        
        System.out.println("finished");
    }

    public void quitWebScrapper(){
        postScrapper.quit();
        commentScrapper.quit();
        followScrapper.quit();
    }
}   
