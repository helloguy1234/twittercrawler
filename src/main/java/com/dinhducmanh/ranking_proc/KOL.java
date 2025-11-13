package com.dinhducmanh.ranking_proc;

import java.util.ArrayList;
import java.util.List;

/**
 * KOL
 */
public class KOL extends Person {

    private int nFollowers;
    /**
     * Danh sách Id của những người đã comment vào bài viết của KOL này
     */
    private List<String> commented_User_ID;
    /**
     * Danh sách Id của những người được KOL này reposted
     */
    private List<String> reposted_User_ID;
    /**
     * Danh sách Id của những người được KOL này follow
     */
    private List<String> Following_User_ID;
    /**
     * Danh sách Id của những người được KOL này comment tới
     */
    private List<String> comment_to_User_ID;

    private int countOf_Rep_Fol_Com;
    private int rank;
    private double pageRank;

    /**
     * @param id
     */
    public KOL(String id) {
        super(id);
        // TODO Auto-generated constructor stub
    }

    /**
     * Khởi tạo KOL với Id, số lượng followers
     *
     * @param id
     * @param nFollowers
     */
    public KOL(String id, int nFollowers) {
        super(id);
        this.nFollowers = nFollowers;
        setPageRankFirstTime();
        this.comment_to_User_ID = new ArrayList<>();
    }

    /**
     * đặt giá trị ban đầu cho KOL với từng mức followers
     */
    public void setPageRankFirstTime() {
        if (this.getnFollowers() <= 0) {
            this.setPageRank(0);
        } else {
            this.setPageRank(Math.log10(getnFollowers()) / 10);
        }

    }

    /**
     * Giá trị trỏ đến KOL A từ KOL hiện tại
     */
    @Override
    public double tinhGiaTriPageRankDenKOLA(KOL A) {
        this.setCountOf_Rep_Fol_Com();
        double point = 0;
        int count = 0;
        for (String Aid : this.reposted_User_ID) {
            if (Aid.equals(A.getId())) {
                count += 2;
            }
        }
        for (String Aid : this.comment_to_User_ID) {
            if (Aid.equals(A.getId())) {
                count += 1;
            }
        }
        for (String Aid : this.Following_User_ID) {
            if (Aid.equals(A.getId())) {
                count += 3;
            }
        }
        point = this.pageRank * count / this.countOf_Rep_Fol_Com;
        return point;
    }

    @Override
    public int getR() {
        return (int) Math.round(400 * this.getPageRank());
    }

    public double getPageRank() {
        return pageRank;
    }

    public void setPageRank(double pageRank) {
        this.pageRank = pageRank;
    }

    public List<String> getComment_to_User_ID() {
        return comment_to_User_ID;
    }

    public void setComment_to_User_ID(List<String> comment_to_User_ID) {
        this.comment_to_User_ID = comment_to_User_ID;
    }

    public int getCountOf_Rep_Fol_Com() {
        return countOf_Rep_Fol_Com;
    }

    /**
     * Tính tổng số lượng Reposted, comment, following từ KOL này
     */
    public void setCountOf_Rep_Fol_Com() {
        countOf_Rep_Fol_Com = this.reposted_User_ID.size() * 2 + this.Following_User_ID.size() * 3 + this.comment_to_User_ID.size();
    }

    public void addComment_to_User_ID(String X) {
        this.comment_to_User_ID.add(X);
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getnFollowers() {
        return nFollowers;
    }

    public List<String> getCommented_User_ID() {
        return commented_User_ID;
    }

    public void setCommented_User_ID(List<String> commented_User_ID) {
        this.commented_User_ID = commented_User_ID;
    }

    public List<String> getFollowing_User_ID() {
        return Following_User_ID;
    }

    public void setFollowing_User_ID(List<String> following_User_ID) {
        Following_User_ID = following_User_ID;
    }

    public List<String> getReposted_User_ID() {
        return reposted_User_ID;
    }

    public void setReposted_User_ID(List<String> reposted_User_ID) {
        this.reposted_User_ID = reposted_User_ID;
    }

}
