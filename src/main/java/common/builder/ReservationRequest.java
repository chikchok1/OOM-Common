package common.builder;

/**
 * 예약 요청 데이터를 위한 Builder Pattern
 */
public class ReservationRequest {
    private final String userName;
    private final String room;
    private final String date;
    private final String day;
    private final String time;
    private final String endTime;
    private final String purpose;
    private final String userRole;
    private final int studentCount;
    private final String userId;

    private ReservationRequest(Builder builder) {
        this.userName = builder.userName;
        this.room = builder.room;
        this.date = builder.date;
        this.day = builder.day;
        this.time = builder.time;
        this.endTime = builder.endTime;
        this.purpose = builder.purpose;
        this.userRole = builder.userRole;
        this.studentCount = builder.studentCount;
        this.userId = builder.userId;
    }

    public String toProtocolString() {
        return String.format("RESERVE_REQUEST,%s,%s,%s,%s,%s,%s,%s,%d,%s",
            userName, room, date, day, time, purpose, userRole, studentCount, userId);
    }

    @Override
    public String toString() {
        return String.format("ReservationRequest [name=%s, room=%s, date=%s, time=%s, count=%d]", 
            userName, room, date, time, studentCount);
    }

    public static class Builder {
        private final String userName;
        private final String room;
        private final String date;
        
        // 선택적 파라미터 - 기본값으로 초기화
        private String day = "";
        private String time = "1교시";
        private String endTime = "1교시";
        private String purpose = "";
        private String userRole = "학생";
        private int studentCount = 1;
        private String userId = "";  // 기본값 빈 문자열
        
        /**
         * 필수 파라미터만 받는 생성자
         */
        public Builder(String userName, String room, String date) {
            if (userName == null || userName.isEmpty()) {
                throw new IllegalArgumentException("사용자 이름은 필수입니다.");
            }
            if (room == null || room.isEmpty()) {
                throw new IllegalArgumentException("강의실은 필수입니다.");
            }
            if (date == null || date.isEmpty()) {
                throw new IllegalArgumentException("날짜는 필수입니다.");
            }
            
            this.userName = userName;
            this.room = room;
            this.date = date;
        }
        
        // 선택적 파라미터 설정 메서드들 (메서드 체이닝)
        public Builder day(String day) {
            this.day = day;
            return this;
        }
        
        public Builder time(String time) {
            this.time = time;
            return this;
        }
        
        public Builder endTime(String endTime) {
            this.endTime = endTime;
            return this;
        }
        
        public Builder purpose(String purpose) {
            this.purpose = purpose;
            return this;
        }
        
        public Builder userRole(String userRole) {
            this.userRole = userRole;
            return this;
        }
        
        public Builder studentCount(int studentCount) {
            if (studentCount <= 0) {
                throw new IllegalArgumentException("학생 수는 1명 이상이어야 합니다.");
            }
            this.studentCount = studentCount;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        
        /**
         * ReservationRequest 객체 생성
         */
        public ReservationRequest build() {
            // 최종 검증 (필요시)
            if (purpose.isEmpty()) {
                throw new IllegalStateException("사용 목적을 입력해주세요.");
            }
            
            return new ReservationRequest(this);
        }
    }
}