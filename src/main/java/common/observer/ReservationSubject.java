package common.observer;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Subject - 예약 상태 변경을 클라이언트에게 알림 (서버-클라이언트 통신 버전)
 */
public class ReservationSubject {
    
    // Singleton 패턴
    private static ReservationSubject instance;
    
    // userId별로 PrintWriter(소켓 출력 스트림) 관리
    private final Map<String, List<PrintWriter>> clientWriters;
    
    private ReservationSubject() {
        this.clientWriters = new ConcurrentHashMap<>();
    }
    
    public static synchronized ReservationSubject getInstance() {
        if (instance == null) {
            instance = new ReservationSubject();
        }
        return instance;
    }
    
    /**
     * 클라이언트 PrintWriter 등록 (서버에서 호출)
     * @param userId 사용자 ID
     * @param writer 클라이언트로 전송할 PrintWriter
     */
    public synchronized void registerClient(String userId, PrintWriter writer) {
        clientWriters.computeIfAbsent(userId, k -> Collections.synchronizedList(new ArrayList<>()))
                     .add(writer);
        System.out.println("[Observer] " + userId + " 클라이언트 등록 완료");
    }
    
    /**
     * 클라이언트 PrintWriter 제거 (서버에서 호출)
     * @param userId 사용자 ID
     * @param writer 제거할 PrintWriter
     */
    public synchronized void unregisterClient(String userId, PrintWriter writer) {
        List<PrintWriter> writers = clientWriters.get(userId);
        if (writers != null) {
            writers.remove(writer);
            if (writers.isEmpty()) {
                clientWriters.remove(userId);
            }
            System.out.println("[Observer] " + userId + " 클라이언트 제거 완료");
        }
    }
    
    /**
     * 특정 사용자에게 알림 전송 (서버에서 호출)
     * @param notification 알림 정보
     */
    public void notifyUser(ReservationNotification notification) {
        String userId = notification.getUserId();
        List<PrintWriter> writers = clientWriters.get(userId);
        
        if (writers != null && !writers.isEmpty()) {
            System.out.println("[Observer] " + userId + "에게 알림 전송: " + notification.getMessage());
            
            // 알림 메시지를 소켓으로 전송
            String notificationMessage = createNotificationMessage(notification);
            
            synchronized (writers) {
                for (PrintWriter writer : writers) {
                    try {
                        writer.println(notificationMessage);
                        writer.flush();
                    } catch (Exception e) {
                        System.err.println("[Observer] 알림 전송 실패: " + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("[Observer] " + userId + "에게 등록된 클라이언트가 없습니다.");
        }
    }
    
    /**
     * 알림 메시지 생성
     */
    private String createNotificationMessage(ReservationNotification notification) {
        // 프로토콜: NOTIFICATION,타입,메시지,강의실,날짜,요일,시간
        return String.format("NOTIFICATION,%s,%s,%s,%s,%s,%s",
            notification.getType(),
            notification.getMessage(),
            notification.getRoom(),
            notification.getDate(),
            notification.getDay(),
            notification.getTime()
        );
    }
    
    /**
     * 등록된 모든 클라이언트 제거 (테스트용)
     */
    public synchronized void clearAll() {
        clientWriters.clear();
        System.out.println("[Observer] 모든 클라이언트 제거 완료");
    }
    
    /**
     * 특정 사용자의 클라이언트 개수 반환
     */
    public int getClientCount(String userId) {
        List<PrintWriter> writers = clientWriters.get(userId);
        return writers != null ? writers.size() : 0;
    }
}
