package common.observer;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Subject - 예약 상태 변경을 클라이언트에게 알림 (서버-클라이언트 통신 버전)
 * ✅ 오프라인 알림 저장 기능 추가
 */
public class ReservationSubject {
    
    // Singleton 패턴
    private static ReservationSubject instance;
    
    // userId별로 PrintWriter(소켓 출력 스트림) 관리
    private final Map<String, List<PrintWriter>> clientWriters;
    
    // ✅ 오프라인 알림 관리자
    private OfflineNotificationManager offlineManager;
    
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
     * ✅ 오프라인 알림 관리자 초기화 (서버 시작 시 호출)
     * @param baseDir 데이터 디렉토리
     */
    public void initializeOfflineManager(String baseDir) {
        this.offlineManager = OfflineNotificationManager.getInstance(baseDir);
        System.out.println("[오프라인 알림] 관리자 초기화 완료");
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
     * ✅ 온라인 사용자에게는 즉시 전송, 오프라인은 저장
     * @param notification 알림 정보
     */
    public void notifyUser(ReservationNotification notification) {
        String userId = notification.getUserId();
        List<PrintWriter> writers = clientWriters.get(userId);
        
        if (writers != null && !writers.isEmpty()) {
            // ✅ 온라인: 즉시 전송
            System.out.println("[Observer] " + userId + "에게 알림 전송: " + notification.getMessage());
            
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
            System.out.println("[Observer 패턴] " + userId + "에게 알림 전송 완료");
        } else {
            // ✅ 오프라인: 파일로 저장
            System.out.println("[Observer] " + userId + "에게 등록된 클라이언트가 없습니다. ➡️ 오프라인 알림 저장");
            
            if (offlineManager != null) {
                offlineManager.saveNotification(userId, notification);
                System.out.println("[오프라인 알림] " + userId + "의 알림 저장 완료");
            } else {
                System.err.println("[오프라인 알림] 관리자가 초기화되지 않았습니다.");
            }
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
