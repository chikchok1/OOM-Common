package common.observer;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.List;

/**
 * OfflineNotificationManager 기능 테스트
 * 
 * 역할 분담:
 * - 싱글턴 패턴 담당: OfflineNotificationManagerSingletonTest 참조
 * - Observer 패턴 담당: 이 파일 (알림 저장/조회 등 비즈니스 로직)
 */
class OfflineNotificationManagerTest {
    
    private static final String TEST_BASE_DIR = "test_notifications";
    private static OfflineNotificationManager manager;
    
    @BeforeAll
    static void setUpClass() {
        // 테스트 디렉토리 생성
        File baseDir = new File(TEST_BASE_DIR);
        baseDir.mkdirs();
        
        File notificationDir = new File(TEST_BASE_DIR + "/notifications");
        notificationDir.mkdirs();
        
        // 싱글턴 인스턴스 초기화
        manager = OfflineNotificationManager.getInstance(TEST_BASE_DIR);
    }
    
    @BeforeEach
    void setUp() {
        // 각 테스트 전 알림 파일 정리
        cleanupNotificationFiles();
    }
    
    @AfterEach
    void tearDown() {
        // 각 테스트 후 알림 파일 정리
        cleanupNotificationFiles();
    }
    
    @AfterAll
    static void tearDownClass() {
        // 테스트 디렉토리 완전 삭제
        deleteDirectory(new File(TEST_BASE_DIR));
    }
    
    /**
     * 알림 파일만 정리 (디렉토리는 유지)
     */
    private void cleanupNotificationFiles() {
        File notificationDir = new File(TEST_BASE_DIR + "/notifications");
        if (notificationDir.exists()) {
            File[] files = notificationDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }
    
    /**
     * 디렉토리 재귀적 삭제 헬퍼 메서드
     */
    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
    
    /**
     * 테스트 1: 알림 저장 및 조회
     */
    @Test
    @DisplayName("알림 저장 및 조회 테스트")
    void testSaveAndGetNotifications() {
        // Given
        String userId = "testUser";
        ReservationNotification notification = new ReservationNotification(
            userId, 
            "홍길동",
            "908호",
            "2025-01-15",
            "월요일",
            "09:00-10:00",
            ReservationNotification.NotificationType.APPROVED,
            "예약이 승인되었습니다."
        );
        
        // When
        manager.saveNotification(userId, notification);
        List<ReservationNotification> notifications = manager.getNotifications(userId);
        
        // Then
        assertEquals(1, notifications.size(), "저장한 알림 1개를 조회할 수 있어야 함");
        ReservationNotification retrieved = notifications.get(0);
        assertEquals(userId, retrieved.getUserId());
        assertEquals("908호", retrieved.getRoom());
        assertEquals("2025-01-15", retrieved.getDate());
    }
    
    /**
     * 테스트 2: 여러 알림 저장 및 조회
     */
    @Test
    @DisplayName("여러 알림 저장 및 조회 테스트")
    void testSaveMultipleNotifications() {
        // Given
        String userId = "testUser";
        
        ReservationNotification notification1 = createNotification(
            userId, "908호", "2025-01-15", ReservationNotification.NotificationType.APPROVED
        );
        ReservationNotification notification2 = createNotification(
            userId, "912호", "2025-01-16", ReservationNotification.NotificationType.REJECTED
        );
        ReservationNotification notification3 = createNotification(
            userId, "911호", "2025-01-17", ReservationNotification.NotificationType.CANCELLED
        );
        
        // When
        manager.saveNotification(userId, notification1);
        manager.saveNotification(userId, notification2);
        manager.saveNotification(userId, notification3);
        
        List<ReservationNotification> notifications = manager.getNotifications(userId);
        
        // Then
        assertEquals(3, notifications.size(), "저장한 알림 3개를 조회할 수 있어야 함");
    }
    
    /**
     * 테스트 3: 알림 삭제
     */
    @Test
    @DisplayName("알림 삭제 테스트")
    void testClearNotifications() {
        // Given
        String userId = "testUser";
        ReservationNotification notification = createNotification(
            userId, "908호", "2025-01-15", ReservationNotification.NotificationType.APPROVED
        );
        manager.saveNotification(userId, notification);
        
        // When
        manager.clearNotifications(userId);
        List<ReservationNotification> notifications = manager.getNotifications(userId);
        
        // Then
        assertEquals(0, notifications.size(), "삭제 후 알림이 없어야 함");
    }
    
    /**
     * 테스트 4: 알림 개수 조회
     */
    @Test
    @DisplayName("알림 개수 조회 테스트")
    void testGetNotificationCount() {
        // Given
        String userId = "testUser";
        manager.saveNotification(userId, createNotification(
            userId, "908호", "2025-01-15", ReservationNotification.NotificationType.APPROVED
        ));
        manager.saveNotification(userId, createNotification(
            userId, "912호", "2025-01-16", ReservationNotification.NotificationType.APPROVED
        ));
        
        // When
        int count = manager.getNotificationCount(userId);
        
        // Then
        assertEquals(2, count, "저장한 알림 개수가 2개여야 함");
    }
    
    /**
     * 테스트 5: 존재하지 않는 사용자의 알림 조회
     */
    @Test
    @DisplayName("존재하지 않는 사용자의 알림 조회 시 빈 리스트 반환")
    void testGetNotificationsForNonExistentUser() {
        // When
        List<ReservationNotification> notifications = manager.getNotifications("nonExistentUser");
        
        // Then
        assertNotNull(notifications, "null이 아닌 빈 리스트를 반환해야 함");
        assertEquals(0, notifications.size(), "빈 리스트를 반환해야 함");
    }
    
    /**
     * 테스트 6: 여러 사용자의 알림 독립성
     */
    @Test
    @DisplayName("여러 사용자의 알림 독립성 테스트")
    void testMultipleUsersNotifications() {
        // Given
        String user1 = "user1";
        String user2 = "user2";
        
        manager.saveNotification(user1, createNotification(
            user1, "908호", "2025-01-15", ReservationNotification.NotificationType.APPROVED
        ));
        manager.saveNotification(user2, createNotification(
            user2, "912호", "2025-01-16", ReservationNotification.NotificationType.APPROVED
        ));
        
        // When
        List<ReservationNotification> user1Notifications = manager.getNotifications(user1);
        List<ReservationNotification> user2Notifications = manager.getNotifications(user2);
        
        // Then
        assertEquals(1, user1Notifications.size(), "user1은 알림 1개");
        assertEquals(1, user2Notifications.size(), "user2는 알림 1개");
        assertEquals("908호", user1Notifications.get(0).getRoom());
        assertEquals("912호", user2Notifications.get(0).getRoom());
    }
    
    /**
     * 테스트 7: 알림 타입별 저장 및 조회
     */
    @Test
    @DisplayName("알림 타입별 저장 및 조회 테스트")
    void testDifferentNotificationTypes() {
        // Given
        String userId = "testUser";
        
        manager.saveNotification(userId, createNotification(
            userId, "908호", "2025-01-15", ReservationNotification.NotificationType.APPROVED
        ));
        manager.saveNotification(userId, createNotification(
            userId, "912호", "2025-01-16", ReservationNotification.NotificationType.REJECTED
        ));
        manager.saveNotification(userId, createNotification(
            userId, "911호", "2025-01-17", ReservationNotification.NotificationType.CANCELLED
        ));
        manager.saveNotification(userId, createNotification(
            userId, "913호", "2025-01-18", ReservationNotification.NotificationType.CHANGE_APPROVED
        ));
        
        // When
        List<ReservationNotification> notifications = manager.getNotifications(userId);
        
        // Then
        assertEquals(4, notifications.size(), "4가지 타입의 알림이 모두 저장되어야 함");
        
        long approvedCount = notifications.stream()
            .filter(n -> n.getType() == ReservationNotification.NotificationType.APPROVED)
            .count();
        long rejectedCount = notifications.stream()
            .filter(n -> n.getType() == ReservationNotification.NotificationType.REJECTED)
            .count();
        long cancelledCount = notifications.stream()
            .filter(n -> n.getType() == ReservationNotification.NotificationType.CANCELLED)
            .count();
        long changeApprovedCount = notifications.stream()
            .filter(n -> n.getType() == ReservationNotification.NotificationType.CHANGE_APPROVED)
            .count();
        
        assertEquals(1, approvedCount, "승인 알림 1개");
        assertEquals(1, rejectedCount, "거부 알림 1개");
        assertEquals(1, cancelledCount, "취소 알림 1개");
        assertEquals(1, changeApprovedCount, "변경 승인 알림 1개");
    }
    
    /**
     * 테스트 8: 알림 디렉토리 자동 생성
     */
    @Test
    @DisplayName("알림 디렉토리 자동 생성 테스트")
    void testNotificationDirectoryCreation() {
        // When
        File notificationDir = new File(TEST_BASE_DIR + "/notifications");
        
        // Then
        assertTrue(notificationDir.exists(), "알림 디렉토리가 자동으로 생성되어야 함");
        assertTrue(notificationDir.isDirectory(), "디렉토리여야 함");
    }
    
    /**
     * 테스트 9: 대량의 알림 저장 및 조회
     */
    @Test
    @DisplayName("대량의 알림 저장 및 조회 테스트")
    void testLargeNumberOfNotifications() {
        // Given
        String userId = "testUser";
        int notificationCount = 100;
        
        // When
        for (int i = 0; i < notificationCount; i++) {
            manager.saveNotification(userId, createNotification(
                userId, "908호", "2025-01-" + String.format("%02d", (i % 28 + 1)), 
                ReservationNotification.NotificationType.APPROVED
            ));
        }
        
        List<ReservationNotification> notifications = manager.getNotifications(userId);
        
        // Then
        assertEquals(notificationCount, notifications.size(), 
            "대량의 알림도 모두 저장 및 조회되어야 함");
    }
    
    /**
     * 테스트 10: 오래된 알림 정리 (7일 이상)
     */
    @Test
    @DisplayName("오래된 알림 정리 테스트")
    void testCleanupOldNotifications() throws Exception {
        // Given
        String userId = "oldUser";
        manager.saveNotification(userId, createNotification(
            userId, "908호", "2025-01-15", ReservationNotification.NotificationType.APPROVED
        ));
        
        // 파일의 수정 시간을 8일 전으로 변경
        File notificationFile = new File(TEST_BASE_DIR + "/notifications/" + userId + "_notifications.txt");
        long eightDaysAgo = System.currentTimeMillis() - (8L * 24 * 60 * 60 * 1000);
        notificationFile.setLastModified(eightDaysAgo);
        
        // When
        manager.cleanupOldNotifications();
        
        // Then
        assertFalse(notificationFile.exists(), "7일 이상된 알림 파일은 삭제되어야 함");
    }
    
    /**
     * 헬퍼 메서드: 테스트용 알림 생성
     */
    private ReservationNotification createNotification(
            String userId, String room, String date, 
            ReservationNotification.NotificationType type) {
        return new ReservationNotification(
            userId, 
            "테스트유저",
            room,
            date,
            "월요일",
            "09:00-10:00",
            type,
            "테스트 메시지"
        );
    }
}
