/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common.model;

/**
 *
 * @author YangJinWon
 */
public class User {
    private final String userId;
    private final String password;
    private final String name;
    
    public User(String userId, String password, String name) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("userId는 null이거나 공백일 수 없습니다.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("password는 null이거나 공백일 수 없습니다.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name은 null이거나 공백일 수 없습니다.");
        }
        
        this.userId = userId;
        this.password = password;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
