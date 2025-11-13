package com.dinhducmanh.twittercrawler.output_function.Interface;

import java.util.List;

public interface OutPut_Kol_Infor {
    public void outputListNumberOfFollower(int numberOfFollower) throws Exception;

    public void outputCommentedUserIdList(List<String> commentedUserIdList) throws Exception;

    public void outputRepostedUserIdList(List<String> repostedUserIdList) throws Exception;

    public void outputfollowingUserIdList(List<String> followingUserIdList) throws Exception;

    public void setCurrentUserIndex(int currentUserIndex);
}
