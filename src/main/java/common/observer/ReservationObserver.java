package common.observer;

/**
 * Observer 인터페이스 - 예약 상태 변경 알림을 받는 객체
 */
public interface ReservationObserver {
    /**
     * 예약 상태가 변경되었을 때 호출되는 메서드
     * @param notification 알림 정보
     */
    void update(ReservationNotification notification);
}
