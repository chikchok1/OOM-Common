package common.observer;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 오프라인 알림 관리자
 * 사용자가 로그아웃 상태일 때 알림을 저장했다가 로그인 시 전달
 */
public class OfflineNotificationManager {
    
    private static OfflineNotificationManager instance;
    private final String notificationDir;
    
    private OfflineNotificationManager(String baseDir) {
        this.notificationDir = baseDir + "/notifications";
        ensureNotificationDirectory();
    }
    
    public static synchronized OfflineNotificationManager getInstance(String baseDir) {
        if (instance == null) {
            instance = new OfflineNotificationManager(baseDir);
        }
        return instance;
    }
    
    public static synchronized OfflineNotificationManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("OfflineNotificationManager가 초기화되지 않았습니다.");
        }
        return instance;
    }
    
    /**
     * 알림 디렉토리 생성
     */
    private void ensureNotificationDirectory() {
        try {
            Path path = Paths.get(notificationDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("[OfflineNotification] 알림 디렉토리 생성: " + notificationDir);
            }
        } catch (IOException e) {
            System.err.println("[OfflineNotification] 디렉토리 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 오프라인 알림 저장
     * @param userId 사용자 ID
     * @param notification 알림 정보
     */
    public void saveNotification(String userId, ReservationNotification notification) {
        String filePath = getNotificationFilePath(userId);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // 형식: 타입|메시지|강의실|날짜|요일|시간|타임스탬프
            String line = String.format("%s|%s|%s|%s|%s|%s|%d",
                notification.getType(),
                notification.getMessage(),
                notification.getRoom(),
                notification.getDate(),
                notification.getDay(),
                notification.getTime(),
                System.currentTimeMillis()
            );
            
            writer.write(line);
            writer.newLine();
            
            System.out.println("[OfflineNotification] 알림 저장: " + userId + " - " + notification.getMessage());
        } catch (IOException e) {
            System.err.println("[OfflineNotification] 알림 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * 사용자의 모든 오프라인 알림 조회
     * @param userId 사용자 ID
     * @return 알림 목록
     */
    public List<ReservationNotification> getNotifications(String userId) {
        List<ReservationNotification> notifications = new ArrayList<>();
        String filePath = getNotificationFilePath(userId);
        File file = new File(filePath);
        
        if (!file.exists()) {
            return notifications;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ReservationNotification notification = parseNotification(userId, line);
                if (notification != null) {
                    notifications.add(notification);
                }
            }
            
            System.out.println("[OfflineNotification] 알림 조회: " + userId + " - " + notifications.size() + "개");
        } catch (IOException e) {
            System.err.println("[OfflineNotification] 알림 조회 실패: " + e.getMessage());
        }
        
        return notifications;
    }
    
    /**
     * 사용자의 모든 오프라인 알림 삭제
     * @param userId 사용자 ID
     */
    public void clearNotifications(String userId) {
        String filePath = getNotificationFilePath(userId);
        File file = new File(filePath);
        
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("[OfflineNotification] 알림 삭제 완료: " + userId);
            } else {
                System.err.println("[OfflineNotification] 알림 삭제 실패: " + userId);
            }
        }
    }
    
    /**
     * 사용자의 알림 개수 조회
     * @param userId 사용자 ID
     * @return 알림 개수
     */
    public int getNotificationCount(String userId) {
        return getNotifications(userId).size();
    }
    
    /**
     * 알림 파일 경로 생성
     */
    private String getNotificationFilePath(String userId) {
        return notificationDir + "/" + userId + "_notifications.txt";
    }
    
    /**
     * 알림 문자열 파싱
     */
    private ReservationNotification parseNotification(String userId, String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 6) {
                ReservationNotification.NotificationType type = 
                    ReservationNotification.NotificationType.valueOf(parts[0]);
                String message = parts[1];
                String room = parts[2];
                String date = parts[3];
                String day = parts[4];
                String time = parts[5];
                
                return new ReservationNotification(
                    userId,
                    "", // userName은 필요 없음
                    room,
                    date,
                    day,
                    time,
                    type,
                    message
                );
            }
        } catch (Exception e) {
            System.err.println("[OfflineNotification] 알림 파싱 실패: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 오래된 알림 정리 (7일 이상)
     */
    public void cleanupOldNotifications() {
        File dir = new File(notificationDir);
        if (!dir.exists()) {
            return;
        }
        
        long sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L);
        int deletedCount = 0;
        
        File[] files = dir.listFiles((d, name) -> name.endsWith("_notifications.txt"));
        if (files != null) {
            for (File file : files) {
                if (file.lastModified() < sevenDaysAgo) {
                    if (file.delete()) {
                        deletedCount++;
                    }
                }
            }
        }
        
        if (deletedCount > 0) {
            System.out.println("[OfflineNotification] 오래된 알림 " + deletedCount + "개 정리 완료");
        }
    }
}
