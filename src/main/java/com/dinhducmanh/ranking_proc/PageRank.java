package com.dinhducmanh.ranking_proc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PageRank {

    /**
     * Giá trị d là độ giảm...
     */
    private double d = 0.85;

    public PageRank() {
        super();
    }

    private Map<Person, List<Person>> graph = new HashMap<>();

    /**
     * Thêm cạnh vào đồ thị : Từ Person "from" đến Person "to"
     *
     * @param from
     * @param to
     */
    public void addEdge(Person from, Person to) {
        graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
    }

    /**
     * Thuật toán Page Rank , a là số lần lặp cho thuật toán
     *
     * @param a
     */
    public void calculatePageRank(int a) {

        for (int i = 0; i < a; i++) {
            // Tạo Map với Key là các KOL, Value là giá trị PageRank của KOL đó-Double
            Map<KOL, Double> newPageRank = new HashMap<>();
            // Chạy vòng lặp cho tất cả các Person trong Map
            for (Person person : graph.keySet()) {
                // Chỉ xét nếu person là KOL
                if (person instanceof KOL) {
                    //rankSum là tổng giá trị mà các person khác trỏ đến KOL hiện tại
                    double rankSum = 0;
                    //entry là một đối tượng khác trong Map
                    //Chạy với tất cả các thành phần khác trong Map
                    for (Entry<Person, List<Person>> entry : graph.entrySet()) {
                        //lấy ra các phần của entry
                        Person otherPerson = entry.getKey();
                        List<Person> outlinks = entry.getValue();

                        //Nếu entry có trỏ đến person-là KOL đang tính PageRank
                        //rankSum+= giá trị mà otherPerson này trỏ đến KOL person
                        if (outlinks.contains(person)) {
                            double otherRank = otherPerson.tinhGiaTriPageRankDenKOLA((KOL) person);
                            rankSum += otherRank;
                        }
                    }
                    //Tính giá trị Rank mới theo công thức PageRank
                    double newRank = (1 - d) + d * rankSum;
                    //đặt giá trị rank mới vào VALUE cho person-KEY trong map
                    newPageRank.put((KOL) person, newRank);
                }
            }

            //Đặt giá trị PageRank mới cho các KOL trong list sau mỗi lần lặp
            for (KOL kol : newPageRank.keySet()) {
                kol.setPageRank(newPageRank.get(kol));
            }
        }
    }

    public void buildGraph() throws IOException {
        BuildGraph buildgrp = new BuildGraph(graph);
        buildgrp.drawGraph("Big-Assignment\\src\\main\\java\\part2\\outputPagerank\\MinhHoa.png");
    }
}
