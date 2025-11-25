package common.manager;

import java.io.*;
import java.util.*;

/**
 * 강의실/실습실 관리자 (Singleton Pattern)
 * 서버와 클라이언트 공통으로 사용
 */
public class ClassroomManager {
    
    private static ClassroomManager instance;
    
    private Map<String, Classroom> classrooms;
    private Map<String, Classroom> labs;
    
    private ClassroomManager() {
        this.classrooms = new HashMap<>();
        this.labs = new HashMap<>();
    }
    
    public static synchronized ClassroomManager getInstance() {
        if (instance == null) {
            instance = new ClassroomManager();
        }
        return instance;
    }
    
    /**
     * 강의실 추가
     */
    public synchronized void addClassroom(String name, int capacity) {
        classrooms.put(name, new Classroom(name, "CLASS", capacity));
    }
    
    /**
     * 실습실 추가
     */
    public synchronized void addLab(String name, int capacity) {
        labs.put(name, new Classroom(name, "LAB", capacity));
    }
    
    /**
     * 강의실 조회
     */
    public Classroom getClassroom(String name) {
        Classroom room = classrooms.get(name);
        if (room == null) {
            room = labs.get(name);
        }
        return room;
    }
    
    /**
     * 강의실 삭제
     */
    public synchronized boolean removeClassroom(String name) {
        boolean removed = classrooms.remove(name) != null;
        if (!removed) {
            removed = labs.remove(name) != null;
        }
        return removed;
    }
    
    /**
     * 모든 강의실 목록
     */
    public List<Classroom> getAllClassrooms() {
        List<Classroom> result = new ArrayList<>(classrooms.values());
        Collections.sort(result, Comparator.comparing(c -> c.name));
        return result;
    }
    
    /**
     * 모든 실습실 목록
     */
    public List<Classroom> getAllLabs() {
        List<Classroom> result = new ArrayList<>(labs.values());
        Collections.sort(result, Comparator.comparing(c -> c.name));
        return result;
    }
    
    /**
     * 파일에서 로드
     */
    public synchronized void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String name = parts[0].trim();
                    String type = parts[1].trim();
                    int capacity = Integer.parseInt(parts[2].trim());
                    
                    if ("CLASS".equals(type)) {
                        addClassroom(name, capacity);
                    } else if ("LAB".equals(type)) {
                        addLab(name, capacity);
                    }
                }
            }
            System.out.println("[ClassroomManager] 파일에서 로드 완료: " + filePath);
        } catch (IOException e) {
            System.err.println("[ClassroomManager] 파일 로드 실패: " + e.getMessage());
        }
    }
    
    /**
     * 파일에 저장
     */
    public synchronized void saveToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Classroom classroom : getAllClassrooms()) {
                writer.write(classroom.toFileFormat());
                writer.newLine();
            }
            for (Classroom lab : getAllLabs()) {
                writer.write(lab.toFileFormat());
                writer.newLine();
            }
            System.out.println("[ClassroomManager] 파일에 저장 완료: " + filePath);
        } catch (IOException e) {
            System.err.println("[ClassroomManager] 파일 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * 수용 인원 체크 (50% 제한)
     */
    public boolean checkCapacity(String roomName, int requestedCount) {
        Classroom room = getClassroom(roomName);
        if (room == null) {
            return false;
        }
        return requestedCount <= room.getAllowedCapacity();
    }
    
    /**
     * 강의실 내부 클래스
     */
    public static class Classroom {
        public final String name;
        public final String type;
        public final int capacity;
        
        public Classroom(String name, String type, int capacity) {
            this.name = name;
            this.type = type;
            this.capacity = capacity;
        }
        
        public int getAllowedCapacity() {
            return (int) (capacity * 0.5);
        }
        
        public boolean isClassroom() {
            return "CLASS".equals(type);
        }
        
        public boolean isLab() {
            return "LAB".equals(type);
        }
        
        public String toFileFormat() {
            return String.format("%s,%s,%d", name, type, capacity);
        }
        
        @Override
        public String toString() {
            return String.format("%s (%s, 수용: %d명)", name, 
                isClassroom() ? "강의실" : "실습실", capacity);
        }
    }
}
