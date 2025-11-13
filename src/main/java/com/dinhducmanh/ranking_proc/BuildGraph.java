package com.dinhducmanh.ranking_proc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

public class BuildGraph {

    private Map<Person, List<Person>> graphMap;

    public BuildGraph(Map<Person, List<Person>> graphMap) {
        this.graphMap = graphMap;
    }

    public void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2, int r1, int r2) {
        double angl1 = Math.atan2((y2 - y1), (x2 - x1));

        x1 += Math.cos(angl1) * r1;
        x2 -= Math.cos(angl1) * r2;

        if ((y2 - y1) * angl1 > 0) {
            y1 += Math.sin(angl1) * r1;
            y2 -= Math.sin(angl1) * r2;
        } else {
            y1 -= Math.sin(angl1) * r1;
            y2 += Math.sin(angl1) * r2;
        }
        // Vẽ đường thẳng
        g2d.drawLine(x1, y1, x2, y2);

        // Tính toán góc
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowLength = 10; // Độ dài của mũi tên
        // int arrowWidth = 5; // Độ rộng của mũi tên

        // Tính tọa độ của các điểm mũi tên
        int xArrow1 = x2 - (int) (arrowLength * Math.cos(angle - Math.PI / 6));
        int yArrow1 = y2 - (int) (arrowLength * Math.sin(angle - Math.PI / 6));
        int xArrow2 = x2 - (int) (arrowLength * Math.cos(angle + Math.PI / 6));
        int yArrow2 = y2 - (int) (arrowLength * Math.sin(angle + Math.PI / 6));

        // Vẽ mũi tên
        g2d.fillPolygon(new int[]{x2, xArrow1, xArrow2}, new int[]{y2, yArrow1, yArrow2}, 3);
    }

    public void setToaDoChoCacDiem() {
        Set<String> usedCoordinates = new HashSet<>();
        Set<String> usedCoordinates2 = new HashSet<>();
        Random random = new Random();
        int x, y;
        int ranx, rany;
        String coordinate;
        for (Person person : graphMap.keySet()) {
            if (person instanceof KOL) {
                do {
                    ranx = random.nextInt(14) + 1;
                    rany = random.nextInt(14) + 1;
                } while (usedCoordinates2.contains(ranx + "," + rany));
                usedCoordinates2.add(ranx + "," + rany);
                do {
                    x = random.nextInt((ranx + 1) * 300 - 75) + ranx * 300 + 75;
                    y = random.nextInt((rany + 1) * 300 - 75) + rany * 300 + 75;
                    coordinate = x + "," + y; // Tạo chuỗi tọa độ để kiểm tra
                } while (usedCoordinates.contains(coordinate)); // Kiểm tra xem tọa độ đã tồn tại chưa

                usedCoordinates.add(coordinate); // Thêm tọa độ vào tập hợp
                person.setX(x);
                person.setY(y);
                for (Entry<Person, List<Person>> entry : graphMap.entrySet()) {
                    if (entry.getValue().contains(person) && !(entry.getKey() instanceof KOL)) {
                        boolean check = true;
                        do {

                            x = random.nextInt((ranx + 1) * 300) + ranx * 300;
                            y = random.nextInt((rany + 1) * 300) + rany * 300;
                            if (Math.pow(x - person.getX(), 2) + Math.pow(y - person.getY(), 2) > person.getR() * person.getR()) {
                                check = false;
                            }
                            coordinate = x + "," + y; // Tạo chuỗi tọa độ để kiểm tra
                        } while (usedCoordinates.contains(coordinate) || check); // Kiểm tra xem tọa độ đã tồn tại chưa
                        entry.getKey().setX(x);
                        entry.getKey().setY(y);
                        usedCoordinates.add(coordinate); // Thêm tọa độ vào tập hợp
                    }
                }
            }
        }
    }

    public void drawGraph(String filePath) throws IOException {
        // Tạo hình ảnh
        int width = 4500;
        int height = 4500;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        // Thiết lập nền trắng
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        this.setToaDoChoCacDiem();
        for (Person person : graphMap.keySet()) {
            if (person instanceof KOL) {
                g2d.setColor(Color.RED);
                g2d.drawString(person.getId(), person.getX(), person.getY());
                g2d.setColor(Color.GREEN);
            } else {
                g2d.setColor(Color.BLACK);
            }
            g2d.fillOval(person.getX() - person.getR() / 2, person.getY() - person.getR() / 2, person.getR(), person.getR());
        }

        for (Map.Entry<Person, List<Person>> entry : graphMap.entrySet()) {
            Person source = entry.getKey();
            int sourceX = source.getX();
            int sourceY = source.getY();

            for (Person destination : entry.getValue()) {
                int destX = destination.getX();
                int destY = destination.getY();

                // Vẽ cạnh
                g2d.setColor(Color.BLACK);
                drawArrow(g2d, sourceX, sourceY, destX, destY, source.getR() / 2, destination.getR() / 2);
            }

        }

        for (Person person : graphMap.keySet()) {
            if (person instanceof KOL) {
                g2d.setColor(Color.RED);
                g2d.drawString(person.getId(), person.getX() - person.getR() / 2, person.getY());
            }
        }

        ImageIO.write(img, "png", new File(filePath));
        g2d.dispose();
    }
}
