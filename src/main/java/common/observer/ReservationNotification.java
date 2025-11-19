package common.observer;

import java.time.LocalDateTime;

/**
 * 예약 알림 정보 클래스
 */
public class ReservationNotification {
    
    public enum NotificationType {
        APPROVED,           // 예약 승인
        REJECTED,           // 예약 거절
        CHANGE_APPROVED,    // 변경 승인
        CHANGE_REJECTED,    // 변경 거절
        CANCELLED           // 예약 취소
    }
    
    private final String userId;
    private final String userName;
    private final String room;
    private final String date;
    private final String day;
    private final String time;
    private final NotificationType type;
    private final LocalDateTime timestamp;
    private final String message;
    
    public ReservationNotification(String userId, String userName, String room, 
                                   String date, String day, String time, 
                                   NotificationType type, String message) {
        this.userId = userId;
        this.userName = userName;
        this.room = room;
        this.date = date;
        this.day = day;
        this.time = time;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }
    
    // Getters
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getRoom() { return room; }
    public String getDate() { return date; }
    public String getDay() { return day; }
    public String getTime() { return time; }
    public NotificationType getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s님의 %s %s(%s) %s 예약이 %s되었습니다.",
            timestamp, type, userName, room, date, day, time, getTypeMessage());
    }
    
    private String getTypeMessage() {
        switch (type) {
            case APPROVED: return "승인";
            case REJECTED: return "거절";
            case CHANGE_APPROVED: return "변경 승인";
            case CHANGE_REJECTED: return "변경 거절";
            case CANCELLED: return "취소";
            default: return "처리";
        }
    }
}
