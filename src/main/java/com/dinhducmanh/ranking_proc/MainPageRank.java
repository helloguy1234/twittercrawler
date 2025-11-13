package com.dinhducmanh.ranking_proc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainPageRank {

    public void pagerank() throws Exception {
        //Tạo danh sách KOL
        List<KOL> listkol1 = null;
        List<KOL> listkol = new ArrayList<>();
        //Danh sách ID của KOL
        List<String> ListKolId = new ArrayList<>();
        //Danh sách ID của người dùng thông thường
        List<String> listUserID = new ArrayList<>();
        // file là đường dẫn tương đối đến file excel cần đọc
        String file = "Big-Assignment\\src\\main\\java\\ohhello\\Resource\\Kol.xlsx";
        ReaderExcelData reader = new ReaderExcelData(file);
        //đọc listkol và trả về danh sách kol
        int soKOL = 100;
        listkol1 = reader.readKOLData(soKOL);

        for (KOL X : listkol1) {
            if (ListKolId.contains(X.getId()) == false) {
                ListKolId.add(X.getId());
                listkol.add(X);
            }
        }

        //Danh sách các node là Person - có thể là NormalUser , có thể là KOL
        List<Person> listPerson = new ArrayList<>();

        //Thêm các KOL vào List<Person>
        for (KOL A : listkol) {
            listPerson.add(A);

            // Với mỗi KOL, thêm các node là NormalUser - không phải người dùng
            // Những Người dùng được KOL follow, reposted nhưng không nằm 
            // trong các KOL cần xếp PageRank sẽ không xét đến và không nằm trong đồ thị
            for (String Id_User : A.getCommented_User_ID()) {
                boolean checkkol = false;
                for (KOL B : listkol) {
                    if (B.getId().equals(Id_User)) {
                        checkkol = true;
                        B.addComment_to_User_ID(A.getId());
                        break;
                    }
                }
                if (!checkkol) {
                    if (listUserID.contains(Id_User) == false) {
                        listUserID.add(Id_User);
                    }
                }
            }

        }

        //Thêm các NormalUser vào list<Person>
        for (String UserID : listUserID) {
            listPerson.add(new NormalUser(UserID));
        }

        //Khởi tạo lớp để tính PageRank
        PageRank pgr = new PageRank();
        // Thêm các cạnh từ Person UserX đến KOL 
        // nếu UserX có trỏ đến KOL
        // Tức giá trị trỏ đến KOL là khác 0
        for (Person UserX : listPerson) {
            for (KOL KOL : listkol) {
                if (UserX.tinhGiaTriPageRankDenKOLA(KOL) != 0) {
                    pgr.addEdge(UserX, KOL);
                }

            }
        }
        System.out.print(listPerson.size());
        //i là số lần lặp cho thuật toán
        int i = 50;
        pgr.calculatePageRank(i);

        //Sử dụng lớp anonymous
        //tạo lớp anonymous cho Comparator
        //So sánh PageRank giữa KOL A và KOL A
        Collections.sort(listkol, new Comparator<KOL>() {
            @Override
            public int compare(KOL A, KOL B) {
                //Trả về danh sách theo thứ tự PageRank giảm dần
                //Để trả về tăng dần --> đổi chỗ A và B
                return Double.compare(B.getPageRank(), A.getPageRank());
            }
        });

        //setRank cho KOL trong list
        for (int rank = 0; rank < listkol.size(); rank++) {
            listkol.get(rank).setRank(rank + 1);
        }

        //Ghi dữ liệu ra file Excel
        String outputFilePath = "Big-Assignment\\src\\main\\java\\part2\\outputPagerank\\output.xlsx";

        SupportExcel spe = new SupportExcel();
        spe.setExcelFile(outputFilePath, "Output");

        spe.setCellData("ID", 0, 0);
        spe.setCellData("Rank", 0, 1);
        spe.setCellData("PageRank", 0, 2);
        for (KOL kol : listkol) {
            spe.setCellData(kol.getId(), kol.getRank(), 0);
            spe.setCellData(String.valueOf(kol.getRank()), kol.getRank(), 1);
            spe.setCellData(String.valueOf(kol.getPageRank()), kol.getRank(), 2);
        }

        //Ghi dữ liệu ra file csv
        /*
		*String filePath = "output\\Output.csv";
		*try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
		*	
		*	writer.println("ID,Rank,Point");
		*
		*	
		*	for(KOL kolX :listkol) {
		*		writer.println(kolX.getId()+","+kolX.getRank()+","+kolX.getPageRank());
		*	}
	  	*
		*} 
		*catch (IOException e) {
		*	e.printStackTrace();
		*}
         */
        //Vẽ đồ thị
        //pgr.buildGraph();
    }
}
