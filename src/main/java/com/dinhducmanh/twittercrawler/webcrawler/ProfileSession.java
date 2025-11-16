package com.dinhducmanh.twittercrawler.webcrawler;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * - profile là 1 thư mục chứa dữ liệu trình duyệt (cookies, cache, history...)
 * - đại diện cho 1 phiên làm việc ứng với 1 profile trên browser, sẽ phân biệt
 * với các profile của session khác
 */
class ProfileSession {
    private static final Logger logger = LogManager.getLogger(ProfileSession.class);

    private final String profilePath;       //đường dẫn tới profile được lưu
    private final FileLock lock;            //lock file để đánh dấu profile đang được sử dụng
    private final RandomAccessFile file;    //profile đang truy nhập, kiểu này cho phép đọc ghi file ở bất cứ đâu thay vì đọc ghi tuần tự
    private final FileChannel channel;      //channel truy nhập file hiệu quả

    /**
     * @param profilePath Đường dẫn tới profile
     * @param lock FileLock ứng với profile này
     * @param file RandomAccessFile ứng với profile này
     * @param channel FileChannel ứng với profile này
     */
    public ProfileSession(
            String profilePath,
            FileLock lock,
            RandomAccessFile file,
            FileChannel channel) {

        this.profilePath = profilePath;
        this.lock = lock;
        this.file = file;
        this.channel = channel;
    }

    /**
     * Giải phóng profile của session này (tương đương với giải phóng session)
     */
    public void release() {
        try {
            if (lock != null) {
                lock.release();
            }
            if (channel != null) {
                channel.close();
            }
            if (file != null) {
                file.close();
            }
            // Xóa file lock để nhìn cho sạch (tùy chọn)
            // chi tiết file Lock xem ở ProfileAllocator
            new File(profilePath + "/tool_lock.lock").delete();
            System.out.println(">> [System] Đã giải phóng Profile: " + new File(profilePath).getName());
        } catch (IOException e) {
            logger.error("",e);
        }
    }

    /**
     * Lấy đường dẫn tới profile đang sử dụng
     *
     * @return đường dẫn tới profile
     */
    public String getProfilePath() {
        return profilePath;
    }
}
