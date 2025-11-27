package common.builder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Builder Pattern 테스트 클래스
 */
public class ReservationRequestTest {

    @Test
    @DisplayName("Builder Pattern - 정상적인 예약 요청 생성")
    void testBuilderPatternSuccess() {
        // Given & When
        ReservationRequest request = new ReservationRequest.Builder("홍길동", "908호", "2025-01-15")
            .day("수")
            .time("1교시")
            .endTime("2교시")
            .purpose("스터디 모임")
            .userRole("학생")
            .studentCount(5)
            .userId("user123")
            .build();
        
        // Then
        assertNotNull(request);
        assertEquals("홍길동", request.getUserName());
        assertEquals("908호", request.getRoom());
        assertEquals("2025-01-15", request.getDate());
        assertEquals("수", request.getDay());
        assertEquals("1교시", request.getTime());
        assertEquals("2교시", request.getEndTime());
        assertEquals("스터디 모임", request.getPurpose());
        assertEquals("학생", request.getUserRole());
        assertEquals(5, request.getStudentCount());
        assertEquals("user123", request.getUserId());
        
        System.out.println("✅ Builder Pattern 테스트 성공!");
        System.out.println("생성된 객체: " + request);
    }

    @Test
    @DisplayName("Builder Pattern - 프로토콜 문자열 생성")
    void testToProtocolString() {
        // Given
        ReservationRequest request = new ReservationRequest.Builder("김철수", "911호", "2025-01-20")
            .day("금")
            .time("3교시")
            .purpose("프로젝트 회의")
            .userRole("교수")
            .studentCount(10)
            .userId("prof001")
            .build();
        
        // When
        String protocol = request.toProtocolString();
        
        // Then
        System.out.println("✅ 프로토콜 문자열: " + protocol);
        assertTrue(protocol.startsWith("RESERVE_REQUEST,"));
        assertTrue(protocol.contains("김철수"));
        assertTrue(protocol.contains("911호"));
        assertTrue(protocol.contains("2025-01-20"));
        assertTrue(protocol.contains("prof001"));
    }

    @Test
    @DisplayName("Builder Pattern - 필수 파라미터 누락 시 예외 발생")
    void testBuilderMissingRequiredParams() {
        // Given & When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new ReservationRequest.Builder(null, "908호", "2025-01-15");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new ReservationRequest.Builder("홍길동", "", "2025-01-15");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new ReservationRequest.Builder("홍길동", "908호", null);
        });
        
        System.out.println("✅ 필수 파라미터 검증 테스트 성공!");
    }

    @Test
    @DisplayName("Builder Pattern - 목적 누락 시 예외 발생")
    void testBuilderMissingPurpose() {
        // Given & When & Then
        assertThrows(IllegalStateException.class, () -> {
            new ReservationRequest.Builder("홍길동", "908호", "2025-01-15")
                .day("월")
                .time("1교시")
                .userRole("학생")
                .studentCount(3)
                // purpose 누락!
                .build();
        });
        
        System.out.println("✅ 목적 검증 테스트 성공!");
    }

    @Test
    @DisplayName("Builder Pattern - 학생 수 0 이하 시 예외 발생")
    void testBuilderInvalidStudentCount() {
        // Given & When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new ReservationRequest.Builder("홍길동", "908호", "2025-01-15")
                .studentCount(0);  // 0명은 불가
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new ReservationRequest.Builder("홍길동", "908호", "2025-01-15")
                .studentCount(-1);  // 음수는 불가
        });
        
        System.out.println("✅ 학생 수 검증 테스트 성공!");
    }

    @Test
    @DisplayName("Builder Pattern - 메서드 체이닝 확인")
    void testMethodChaining() {
        // Given & When - 한 줄로 체이닝
        ReservationRequest request = new ReservationRequest.Builder("이영희", "912호", "2025-02-01")
            .day("화").time("4교시").endTime("5교시").purpose("세미나").userRole("교수").studentCount(15).userId("prof002").build();
        
        // Then
        assertNotNull(request);
        assertEquals("이영희", request.getUserName());
        assertEquals("세미나", request.getPurpose());
        
        System.out.println("✅ 메서드 체이닝 테스트 성공!");
        System.out.println("체이닝으로 생성: " + request);
    }

    @Test
    @DisplayName("Builder Pattern - toString() 가독성 확인")
    void testToStringReadability() {
        // Given
        ReservationRequest request = new ReservationRequest.Builder("박민수", "915호", "2025-03-10")
            .day("목")
            .time("2교시")
            .endTime("4교시")
            .purpose("실습 수업")
            .userRole("조교")
            .studentCount(20)
            .userId("ta001")
            .build();
        
        // When
        String str = request.toString();
        
        // Then
        System.out.println("✅ toString() 출력:");
        System.out.println(str);
        
        // 현재 toString() 형식에 맞게 검증
        assertTrue(str.contains("915호"));  // 방 이름 확인
        assertTrue(str.contains("2025-03-10"));  // 날짜 확인
        assertTrue(str.contains("20"));  // 학생 수 확인
    }
}
