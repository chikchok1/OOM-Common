/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common.model;

public class MembershipModel {
    private String name;
    private String studentId;
    private String password;

    // 기본 생성자
    public MembershipModel() {
        this.name = null;
        this.studentId = null;
        this.password = null;
    }

    // 기존 생성자
    public MembershipModel(String name, String studentId, String password) {
        if (name != null && name.trim().isEmpty()) {
            throw new IllegalArgumentException("name은 공백일 수 없습니다.");
        }
        if (studentId != null && studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("studentId는 공백일 수 없습니다.");
        }
        if (password != null && password.trim().isEmpty()) {
            throw new IllegalArgumentException("password는 공백일 수 없습니다.");
        }
        
        this.name = name;
        this.studentId = studentId;
        this.password = password;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && name.trim().isEmpty()) {
            throw new IllegalArgumentException("name은 공백일 수 없습니다.");
        }
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        if (studentId != null && studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("studentId는 공백일 수 없습니다.");
        }
        this.studentId = studentId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null && password.trim().isEmpty()) {
            throw new IllegalArgumentException("password는 공백일 수 없습니다.");
        }
        this.password = password;
    }

    // null 체크 헬퍼 메서드
    public boolean isComplete() {
        return name != null && studentId != null && password != null;
    }

    @Override
    public String toString() {
        return "MembershipModel{" +
                "name='" + name + '\'' +
                ", studentId='" + studentId + '\'' +
                '}';
    }
}
