package com.dinhducmanh.ranking_proc;

/**
 * Người dùng thông thường
 */
public class NormalUser extends Person {

    /**
     * @param id
     */
    public NormalUser(String id) {
        super(id);
    }

    /**
     * Nếu là Normal User, giá trị trỏ đến cho KOL A là 0.0001 với comment
     */
    @Override
    public double tinhGiaTriPageRankDenKOLA(KOL A) {
        double point = 0;
        if (A.getCommented_User_ID().contains(this.getId())) {
            point = 0.0001;
        }
        return point;
    }

    @Override
    public int getR() {
        return 4;
    }
}
