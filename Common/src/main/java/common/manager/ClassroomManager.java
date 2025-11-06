package common.manager;

import java.io.*;
import java.util.*;

/**
 * 강의실/실습실 통합 관리 (Singleton Pattern)
 * 수용 인원 기본값: 30명
 */
public class ClassroomManager {
    
    private static volatile ClassroomManager instance;
    
    private Map<String, Classroom> classrooms;
    private List<String> classroomNames;
    private List<String> labNames;
    
    private static final String CLASSROOM_FILE = System.getProperty("user.dir") 
            + File.separator + "data" + File.separator + "Classrooms.txt";
    
    private static final int DEFAULT_CAPACITY = 30;  // 기본 수용 인원
    
    private ClassroomManager() {
        classrooms = new HashMap<>();
        classroomNames = new ArrayList<>();
        labNames = new ArrayList<>();
        loadClassroomData();
    }
    
    public static ClassroomManager getInstance() {
        if (instance == null) {
            synchronized (ClassroomManager.class) {
                if (instance == null) {
                    instance = new ClassroomManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * 강의실 데이터 로드
     * 파일 형식: 이름,타입,수용인원
     */
    private void loadClassroomData() {
        File file = new File(CLASSROOM_FILE);
        
        if (!file.exists()) {
            createDefaultClassroomFile();
            file = new File(CLASSROOM_FILE);
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String name = parts[0].trim();
                    String type = parts[1].trim();
                    int capacity = Integer.parseInt(parts[2].trim());
                    
                    Classroom classroom = new Classroom(name, type, capacity);
                    classrooms.put(name, classroom);
                    
                    if ("CLASS".equals(type)) {
                        classroomNames.add(name);
                    } else if ("LAB".equals(type)) {
                        labNames.add(name);
                    }
                }
            }
            
            Collections.sort(classroomNames);
            Collections.sort(labNames);
            
            System.out.println("강의실 로드 완료: " + classroomNames.size() + "개");
            System.out.println("실습실 로드 완료: " + labNames.size() + "개");
            
        } catch (IOException e) {
            System.err.println("강의실 데이터 로드 실패: " + e.getMessage());
        }
    }
    
    /**
     * 기본 강의실 파일 생성 (수용 인원 기본값: 30명)
     */
    private void createDefaultClassroomFile() {
        try {
            File file = new File(CLASSROOM_FILE);
            file.getParentFile().mkdirs();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("# 강의실/실습실 정보");
                writer.println("# 형식: 이름,타입(CLASS/LAB),수용인원");
                writer.println();
                
                // 기본 강의실 (수용 인원: 30명)
                writer.println("908호,CLASS," + DEFAULT_CAPACITY);
                writer.println("912호,CLASS," + DEFAULT_CAPACITY);
                writer.println("913호,CLASS," + DEFAULT_CAPACITY);
                writer.println("914호,CLASS," + DEFAULT_CAPACITY);
                
                writer.println();
                
                // 기본 실습실 (수용 인원: 30명)
                writer.println("911호,LAB," + DEFAULT_CAPACITY);
                writer.println("915호,LAB," + DEFAULT_CAPACITY);
                writer.println("916호,LAB," + DEFAULT_CAPACITY);
                writer.println("918호,LAB," + DEFAULT_CAPACITY);
            }
            
            System.out.println("기본 강의실 파일 생성: " + CLASSROOM_FILE);
            
        } catch (IOException e) {
            System.err.println("기본 파일 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 모든 강의실 이름 목록
     */
    public String[] getClassroomNames() {
        return classroomNames.toArray(new String[0]);
    }
    
    /**
     * 모든 실습실 이름 목록
     */
    public String[] getLabNames() {
        return labNames.toArray(new String[0]);
    }
    
    /**
     * 특정 강의실 정보 조회
     */
    public Classroom getClassroom(String name) {
        return classrooms.get(name);
    }
    
    /**
     * 수용 인원 체크 (50% 제한)
     */
    public boolean checkCapacity(String roomName, int requestedCount) {
        Classroom classroom = classrooms.get(roomName);
        if (classroom == null) {
            System.err.println("알 수 없는 강의실: " + roomName);
            return false;
        }
        
        int allowedCapacity = classroom.getAllowedCapacity();
        boolean isAllowed = requestedCount <= allowedCapacity;
        
        System.out.println(String.format(
            "[수용인원체크] %s: 최대 %d명, 허용 %d명(50%%), 요청 %d명 → %s",
            roomName, classroom.capacity, allowedCapacity, requestedCount,
            isAllowed ? "승인" : "거부"
        ));
        
        return isAllowed;
    }
    
    /**
     * 강의실 존재 여부 확인
     */
    public boolean exists(String roomName) {
        return classrooms.containsKey(roomName);
    }
    
    /**
     * 수용 인원 수정 (조교용)
     */
    public synchronized boolean updateCapacity(String name, int newCapacity) {
        Classroom classroom = classrooms.get(name);
        if (classroom == null) {
            System.err.println("존재하지 않는 강의실: " + name);
            return false;
        }
        
        classroom.capacity = newCapacity;
        saveClassroomData();
        System.out.println(name + " 수용 인원 변경: " + newCapacity + "명");
        return true;
    }
    
    /**
     * 변경사항을 파일에 저장
     */
    private void saveClassroomData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CLASSROOM_FILE))) {
            writer.println("# 강의실/실습실 정보");
            writer.println("# 형식: 이름,타입,수용인원");
            writer.println();
            
            // 강의실 먼저 저장
            for (String name : classroomNames) {
                Classroom classroom = classrooms.get(name);
                writer.println(String.format("%s,%s,%d",
                    classroom.name, classroom.type, classroom.capacity));
            }
            
            writer.println();
            
            // 실습실 저장
            for (String name : labNames) {
                Classroom classroom = classrooms.get(name);
                writer.println(String.format("%s,%s,%d",
                    classroom.name, classroom.type, classroom.capacity));
            }
            
            System.out.println("강의실 데이터 저장 완료");
            
        } catch (IOException e) {
            System.err.println("데이터 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * 데이터 새로고침 (파일에서 재로드)
     */
    public void refresh() {
        classrooms.clear();
        classroomNames.clear();
        labNames.clear();
        loadClassroomData();
        System.out.println("강의실 데이터 새로고침 완료");
    }
    
    /**
     * 강의실 정보 클래스
     */
    public static class Classroom {
        public final String name;
        public final String type;
        public int capacity;  // 수정 가능
        
        public Classroom(String name, String type, int capacity) {
            this.name = name;
            this.type = type;
            this.capacity = capacity;
        }
        
        /**
         * 허용 인원 (50%)
         */
        public int getAllowedCapacity() {
            return (int) (capacity * 0.5);
        }
        
        @Override
        public String toString() {
            return String.format("%s (%s, 수용: %d명, 예약가능: %d명)",
                name, type.equals("CLASS") ? "강의실" : "실습실",
                capacity, getAllowedCapacity());
        }
    }
}
