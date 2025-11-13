package com.dinhducmanh.ranking_proc;

public abstract class Person {

    private int x;
    private int y;
    private String id;

    /**
     * @param id
     * @param pageRank
     */
    public Person(String id) {
        super();
        this.id = id;
    }

    /**
     * Tính giá trị PageRank từ User Này đến KOL A
     *
     * @param KOL A
     * @return
     */
    public abstract double tinhGiaTriPageRankDenKOLA(KOL A);

    public abstract int getR();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
