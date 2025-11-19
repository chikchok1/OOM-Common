package common.dto;

import java.io.Serializable;

/**
 * 강의실/실습실 데이터 전송 객체 (DTO)
 * 서버와 클라이언트 간 데이터 전송에 사용
 */
public class ClassroomDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public final String name;
    public final String type;
    public final int capacity;
    
    public ClassroomDTO(String name, String type, int capacity) {
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
    
    public String toProtocol() {
        return String.format("%s,%s,%d", name, type, capacity);
    }
    
    public static ClassroomDTO fromProtocol(String protocol) {
        String[] parts = protocol.split(",");
        if (parts.length >= 3) {
            return new ClassroomDTO(
                parts[0].trim(),
                parts[1].trim(),
                Integer.parseInt(parts[2].trim())
            );
        }
        return null;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s, 수용: %d명, 예약가능: %d명)",
            name, isClassroom() ? "강의실" : "실습실",
            capacity, getAllowedCapacity());
    }
}
