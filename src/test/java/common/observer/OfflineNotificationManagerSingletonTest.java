package common.observer;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * OfflineNotificationManager 싱글턴 패턴 테스트
 * 
 * 역할 분담:
 * - 싱글턴 패턴 담당: getInstance() 및 멀티스레드 일관성 테스트
 * - Observer 패턴 담당: 알림 저장/조회 등 비즈니스 로직 테스트
 */
class OfflineNotificationManagerSingletonTest {
    
    private static final String TEST_BASE_DIR = "test_notifications";
    
    @BeforeAll
    static void setUpClass() {
        // 테스트 디렉토리 생성
        new java.io.File(TEST_BASE_DIR).mkdirs();
    }
    
    @AfterAll
    static void tearDownClass() {
        // 테스트 디렉토리 삭제
        deleteDirectory(new java.io.File(TEST_BASE_DIR));
    }
    
    private static void deleteDirectory(java.io.File directory) {
        if (directory.exists()) {
            java.io.File[] files = directory.listFiles();
            if (files != null) {
                for (java.io.File file : files) {
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
     * 테스트 1: 싱글턴 인스턴스가 동일한지 검증
     */
    @Test
    @DisplayName("싱글턴 패턴: 항상 같은 인스턴스 반환")
    void testSingletonInstance() {
        // Given
        OfflineNotificationManager instance1 = OfflineNotificationManager.getInstance(TEST_BASE_DIR);
        
        // When
        OfflineNotificationManager instance2 = OfflineNotificationManager.getInstance();
        
        // Then
        assertSame(instance1, instance2, "같은 인스턴스를 반환해야 함");
    }
    
    /**
     * 테스트 2: 멀티스레드 환경에서 싱글턴 인스턴스 일관성
     * Double-Checked Locking 패턴 검증
     */
    @Test
    @DisplayName("멀티스레드 환경에서 싱글턴 인스턴스 일관성")
    void testSingletonInMultiThreadEnvironment() throws InterruptedException {
        final int THREAD_COUNT = 20;
        OfflineNotificationManager[] instances = new OfflineNotificationManager[THREAD_COUNT];
        Thread[] threads = new Thread[THREAD_COUNT];
        
        // When: 여러 스레드에서 동시에 getInstance 호출
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                instances[index] = OfflineNotificationManager.getInstance();
            });
            threads[i].start();
        }
        
        // 모든 스레드 대기
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Then: 모든 인스턴스가 동일해야 함
        for (int i = 1; i < THREAD_COUNT; i++) {
            assertSame(instances[0], instances[i], 
                "멀티스레드 환경에서도 같은 인스턴스를 반환해야 함");
        }
    }
    
    /**
     * 테스트 3: 초기화 이후 getInstance() 호출
     */
    @Test
    @DisplayName("초기화 이후 getInstance() 호출 시 동일 인스턴스 반환")
    void testGetInstanceAfterInitialization() {
        // Given
        OfflineNotificationManager initialInstance = 
            OfflineNotificationManager.getInstance(TEST_BASE_DIR);
        
        // When: 다른 스레드에서 getInstance() 호출
        OfflineNotificationManager subsequentInstance = 
            OfflineNotificationManager.getInstance();
        
        // Then
        assertSame(initialInstance, subsequentInstance, 
            "초기화 이후에도 같은 인스턴스를 반환해야 함");
    }
    
    /**
     * 테스트 4: synchronized 메서드를 통한 thread-safe 보장 검증
     * 동시에 getInstance()를 호출해도 하나의 인스턴스만 생성됨을 검증
     */
    @Test
    @DisplayName("동시 getInstance() 호출 시 thread-safe 보장")
    void testThreadSafeInstanceCreation() throws InterruptedException {
        final int THREAD_COUNT = 50;
        final OfflineNotificationManager[] instances = new OfflineNotificationManager[THREAD_COUNT];
        final Thread[] threads = new Thread[THREAD_COUNT];
        
        // 카운트다운 래치로 모든 스레드가 동시에 시작하도록 함
        final java.util.concurrent.CountDownLatch startLatch = 
            new java.util.concurrent.CountDownLatch(1);
        final java.util.concurrent.CountDownLatch doneLatch = 
            new java.util.concurrent.CountDownLatch(THREAD_COUNT);
        
        // When: 모든 스레드가 동시에 getInstance() 호출
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    startLatch.await(); // 모든 스레드가 대기
                    instances[index] = OfflineNotificationManager.getInstance();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
            threads[i].start();
        }
        
        startLatch.countDown(); // 모든 스레드 동시 시작
        doneLatch.await(); // 모든 스레드 완료 대기
        
        // Then: 모든 인스턴스가 동일해야 함
        for (int i = 1; i < THREAD_COUNT; i++) {
            assertSame(instances[0], instances[i], 
                "동시 호출에도 모두 같은 인스턴스를 반환해야 함");
        }
    }
    
    /**
     * 테스트 5: 싱글턴 인스턴스의 유일성 보장
     */
    @Test
    @DisplayName("프로그램 전체에서 단 하나의 인스턴스만 존재")
    void testSingleInstanceAcrossProgram() {
        // Given
        OfflineNotificationManager manager1 = OfflineNotificationManager.getInstance(TEST_BASE_DIR);
        
        // When: 여러 번 호출
        OfflineNotificationManager manager2 = OfflineNotificationManager.getInstance();
        OfflineNotificationManager manager3 = OfflineNotificationManager.getInstance();
        OfflineNotificationManager manager4 = OfflineNotificationManager.getInstance();
        
        // Then: 모두 같은 인스턴스
        assertSame(manager1, manager2);
        assertSame(manager2, manager3);
        assertSame(manager3, manager4);
    }
}
