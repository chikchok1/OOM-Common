package common.manager;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * ClassroomManager 싱글턴 패턴 테스트
 */
class ClassroomManagerTest {
    
    private ClassroomManager manager;
    private static final String TEST_FILE = "test_classrooms.txt";
    
    @BeforeEach
    void setUp() {
        manager = ClassroomManager.getInstance();
        // 각 테스트 전에 데이터 초기화
        clearAllData();
    }
    
    @AfterEach
    void tearDown() {
        // 테스트 파일 정리
        File testFile = new File(TEST_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
    }
    
    /**
     * 모든 데이터 초기화 (private 필드 접근을 위한 헬퍼 메서드)
     */
    private void clearAllData() {
        // 기존 데이터 제거를 위해 새로운 매니저로 테스트
        manager.getAllClassrooms().forEach(c -> manager.removeClassroom(c.name));
        manager.getAllLabs().forEach(l -> manager.removeClassroom(l.name));
    }
    
    /**
     * 테스트 1: 싱글턴 인스턴스가 동일한지 검증
     */
    @Test
    @DisplayName("싱글턴 패턴: 항상 같은 인스턴스 반환")
    void testSingletonInstance() {
        ClassroomManager instance1 = ClassroomManager.getInstance();
        ClassroomManager instance2 = ClassroomManager.getInstance();
        
        assertSame(instance1, instance2, "같은 인스턴스를 반환해야 함");
    }
    
    /**
     * 테스트 2: 강의실 추가 및 조회
     */
    @Test
    @DisplayName("강의실 추가 및 조회 테스트")
    void testAddAndGetClassroom() {
        // Given
        String name = "908호";
        int capacity = 30;
        
        // When
        manager.addClassroom(name, capacity);
        ClassroomManager.Classroom classroom = manager.getClassroom(name);
        
        // Then
        assertNotNull(classroom, "추가한 강의실을 조회할 수 있어야 함");
        assertEquals(name, classroom.name);
        assertEquals("CLASS", classroom.type);
        assertEquals(capacity, classroom.capacity);
        assertTrue(classroom.isClassroom());
        assertFalse(classroom.isLab());
    }
    
    /**
     * 테스트 3: 실습실 추가 및 조회
     */
    @Test
    @DisplayName("실습실 추가 및 조회 테스트")
    void testAddAndGetLab() {
        // Given
        String name = "911호";
        int capacity = 40;
        
        // When
        manager.addLab(name, capacity);
        ClassroomManager.Classroom lab = manager.getClassroom(name);
        
        // Then
        assertNotNull(lab, "추가한 실습실을 조회할 수 있어야 함");
        assertEquals(name, lab.name);
        assertEquals("LAB", lab.type);
        assertEquals(capacity, lab.capacity);
        assertFalse(lab.isClassroom());
        assertTrue(lab.isLab());
    }
    
    /**
     * 테스트 4: 강의실 삭제
     */
    @Test
    @DisplayName("강의실 삭제 테스트")
    void testRemoveClassroom() {
        // Given
        String name = "912호";
        manager.addClassroom(name, 30);
        
        // When
        boolean removed = manager.removeClassroom(name);
        ClassroomManager.Classroom classroom = manager.getClassroom(name);
        
        // Then
        assertTrue(removed, "강의실이 삭제되어야 함");
        assertNull(classroom, "삭제된 강의실은 조회되지 않아야 함");
    }
    
    /**
     * 테스트 5: 모든 강의실 목록 조회
     */
    @Test
    @DisplayName("모든 강의실 목록 조회 테스트")
    void testGetAllClassrooms() {
        // Given
        manager.addClassroom("908호", 30);
        manager.addClassroom("912호", 35);
        manager.addLab("911호", 40);
        
        // When
        List<ClassroomManager.Classroom> classrooms = manager.getAllClassrooms();
        
        // Then
        assertEquals(2, classrooms.size(), "강의실만 2개 조회되어야 함");
        assertTrue(classrooms.stream().allMatch(c -> c.isClassroom()));
    }
    
    /**
     * 테스트 6: 모든 실습실 목록 조회
     */
    @Test
    @DisplayName("모든 실습실 목록 조회 테스트")
    void testGetAllLabs() {
        // Given
        manager.addClassroom("908호", 30);
        manager.addLab("911호", 40);
        manager.addLab("915호", 45);
        
        // When
        List<ClassroomManager.Classroom> labs = manager.getAllLabs();
        
        // Then
        assertEquals(2, labs.size(), "실습실만 2개 조회되어야 함");
        assertTrue(labs.stream().allMatch(l -> l.isLab()));
    }
    
    /**
     * 테스트 7: 수용 인원 50% 제한 체크
     */
    @Test
    @DisplayName("수용 인원 50% 제한 체크 테스트")
    void testCheckCapacity() {
        // Given
        String name = "908호";
        int capacity = 30;
        manager.addClassroom(name, capacity);
        
        // When & Then
        assertTrue(manager.checkCapacity(name, 15), "50% 이하는 허용되어야 함");
        assertFalse(manager.checkCapacity(name, 16), "50% 초과는 거부되어야 함");
        assertFalse(manager.checkCapacity(name, 30), "100%는 거부되어야 함");
    }
    
    /**
     * 테스트 8: getAllowedCapacity 계산 검증
     */
    @Test
    @DisplayName("허용 인원 계산 테스트")
    void testGetAllowedCapacity() {
        // Given
        manager.addClassroom("908호", 30);
        manager.addClassroom("912호", 40);
        
        // When
        ClassroomManager.Classroom classroom1 = manager.getClassroom("908호");
        ClassroomManager.Classroom classroom2 = manager.getClassroom("912호");
        
        // Then
        assertEquals(15, classroom1.getAllowedCapacity(), "30명의 50%는 15명");
        assertEquals(20, classroom2.getAllowedCapacity(), "40명의 50%는 20명");
    }
    
    /**
     * 테스트 9: 파일 저장 및 로드
     */
    @Test
    @DisplayName("파일 저장 및 로드 테스트")
    void testSaveAndLoadFromFile() {
        // Given
        manager.addClassroom("908호", 30);
        manager.addLab("911호", 40);
        
        // When
        manager.saveToFile(TEST_FILE);
        
        // 새로운 매니저로 로드
        clearAllData();
        manager.loadFromFile(TEST_FILE);
        
        // Then
        assertNotNull(manager.getClassroom("908호"), "저장된 강의실을 로드할 수 있어야 함");
        assertNotNull(manager.getClassroom("911호"), "저장된 실습실을 로드할 수 있어야 함");
        
        ClassroomManager.Classroom classroom = manager.getClassroom("908호");
        assertEquals(30, classroom.capacity);
        assertTrue(classroom.isClassroom());
        
        ClassroomManager.Classroom lab = manager.getClassroom("911호");
        assertEquals(40, lab.capacity);
        assertTrue(lab.isLab());
    }
    
    /**
     * 테스트 10: 존재하지 않는 강의실 조회
     */
    @Test
    @DisplayName("존재하지 않는 강의실 조회 시 null 반환")
    void testGetNonExistentClassroom() {
        // When
        ClassroomManager.Classroom classroom = manager.getClassroom("존재하지않는강의실");
        
        // Then
        assertNull(classroom, "존재하지 않는 강의실은 null을 반환해야 함");
    }
    
    /**
     * 테스트 11: 존재하지 않는 강의실 삭제 시도
     */
    @Test
    @DisplayName("존재하지 않는 강의실 삭제 시 false 반환")
    void testRemoveNonExistentClassroom() {
        // When
        boolean removed = manager.removeClassroom("존재하지않는강의실");
        
        // Then
        assertFalse(removed, "존재하지 않는 강의실 삭제는 false를 반환해야 함");
    }
    
    /**
     * 테스트 12: 멀티스레드 환경에서 싱글턴 패턴 검증
     */
    @Test
    @DisplayName("멀티스레드 환경에서 싱글턴 인스턴스 일관성")
    void testSingletonInMultiThreadEnvironment() throws InterruptedException {
        final int THREAD_COUNT = 10;
        ClassroomManager[] instances = new ClassroomManager[THREAD_COUNT];
        Thread[] threads = new Thread[THREAD_COUNT];
        
        // When: 여러 스레드에서 동시에 getInstance 호출
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                instances[index] = ClassroomManager.getInstance();
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
     * 테스트 13: Classroom toString 메서드
     */
    @Test
    @DisplayName("Classroom toString 메서드 테스트")
    void testClassroomToString() {
        // Given
        manager.addClassroom("908호", 30);
        manager.addLab("911호", 40);
        
        // When
        ClassroomManager.Classroom classroom = manager.getClassroom("908호");
        ClassroomManager.Classroom lab = manager.getClassroom("911호");
        
        // Then
        assertTrue(classroom.toString().contains("908호"));
        assertTrue(classroom.toString().contains("강의실"));
        assertTrue(classroom.toString().contains("30"));
        
        assertTrue(lab.toString().contains("911호"));
        assertTrue(lab.toString().contains("실습실"));
        assertTrue(lab.toString().contains("40"));
    }
    
    /**
     * 테스트 14: 파일 형식 변환 테스트
     */
    @Test
    @DisplayName("Classroom toFileFormat 메서드 테스트")
    void testToFileFormat() {
        // Given
        manager.addClassroom("908호", 30);
        manager.addLab("911호", 40);
        
        // When
        ClassroomManager.Classroom classroom = manager.getClassroom("908호");
        ClassroomManager.Classroom lab = manager.getClassroom("911호");
        
        // Then
        assertEquals("908호,CLASS,30", classroom.toFileFormat());
        assertEquals("911호,LAB,40", lab.toFileFormat());
    }
}
