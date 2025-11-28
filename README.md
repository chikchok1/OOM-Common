# 📦 Classroom Reservation System - Common

> 클라이언트-서버 간 공유 라이브러리 모듈

![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square)
![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?style=flat-square)

## 🎯 개요

클라이언트와 서버 양쪽에서 사용되는 공통 모델, DTO, 유틸리티를 제공하는 라이브러리입니다. 데이터 전송 객체와 비즈니스 로직의 중복을 방지하고 일관성을 보장합니다.

## ✨ 주요 컴포넌트

| 컴포넌트        | 클래스                                   | 설명                           |
| --------------- | ---------------------------------------- | ------------------------------ |
| **도메인 모델** | User, MembershipModel, ReservedRoomModel | 불변 객체로 구현된 핵심 데이터 |
| **DTO**         | ClassroomDTO                             | 강의실 정보 전송 객체          |
| **매니저**      | ClassroomManager (추상)                  | 강의실 관리 Template Method    |
| **빌더**        | ReservationRequest                       | 예약 요청 Builder 패턴         |
| **옵저버**      | ReservationSubject, ReservationObserver  | 실시간 알림 시스템             |
| **유틸리티**    | ConfigLoader, OfflineNotificationManager | 설정 로드, 오프라인 알림       |

## 🎨 디자인 패턴

### 1. Builder Pattern

**목적**: 복잡한 예약 요청 객체를 단계적으로 생성

```java
ReservationRequest request = new ReservationRequest.Builder()
    .setUserId("S123")
    .setRoom("101호")
    .setDate("2025-11-28")
    .setTime("1교시(09:00~10:00)")
    .build();
```

### 2. Singleton Pattern

**목적**: 애플리케이션 전역 단일 인스턴스 보장

| 클래스                     | 구현 방식                         |
| -------------------------- | --------------------------------- |
| ReservationSubject         | Synchronized                      |
| OfflineNotificationManager | Double-checked locking + volatile |

### 3. Observer Pattern

**목적**: 예약 상태 변경 시 이벤트 기반 알림 전파

```
ReservationSubject (Subject)
    ├── registerObserver(observer)
    ├── removeObserver(observer)
    └── notifyObservers(notification)
        └── ReservationObserver.update()
```

**알림 타입**: APPROVED, REJECTED, CANCELLED, CHANGED

### 4. Template Method Pattern

**목적**: 강의실 관리 공통 골격 정의

```java
public abstract class ClassroomManager {
    // 추상 메서드 (하위 클래스 구현)
    public abstract boolean refreshFromServer();

    // 공통 메서드
    public List<String> getClassroomNames() { ... }
    public List<String> getLabNames() { ... }
}
```

## 📁 프로젝트 구조

```
src/main/java/common/
├── model/                    # 도메인 모델 (불변 객체)
│   ├── User.java
│   ├── MembershipModel.java
│   └── ReservedRoomModel.java
├── dto/
│   └── ClassroomDTO.java    # 강의실 DTO
├── manager/
│   └── ClassroomManager.java # Template Method (추상)
├── builder/
│   └── ReservationRequest.java # Builder 패턴
├── observer/                 # Observer 패턴
│   ├── ReservationSubject.java (Singleton)
│   ├── ReservationObserver.java
│   ├── ReservationNotification.java
│   └── OfflineNotificationManager.java (Singleton)
└── utils/
    └── ConfigLoader.java

src/test/java/               # 단위 테스트
```

## 📝 핵심 클래스

### User (Immutable)

```java
public class User {
    private final String userId;    // 불변
    private final String password;
    private final String name;

    // Getters only (Setters 없음)
}
```

### ReservationRequest (Builder)

```java
// 필수/선택 파라미터 명확히 구분
// 불변 객체 생성
// 가독성 향상
```

### ClassroomManager (Template Method)

```java
// 공통 로직: 강의실/실습실 이름 목록
// 추상 메서드: refreshFromServer() - 서버/클라이언트별 구현
```

### ReservationSubject (Observer + Singleton)

```java
// 온라인: 즉시 알림 전파
// 오프라인: 파일 저장 후 로그인 시 전송
// Thread-safe: CopyOnWriteArrayList 사용
```

## 🚀 설치 및 사용

### 빌드 및 설치

```bash
cd OOM-Common
mvn clean install
```

### 의존성 추가 (pom.xml)

```xml
<dependency>
    <groupId>cse.oop5</groupId>
    <artifactId>ClassReservationCommon</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 사용 예시

**User 생성**

```java
User user = new User("S123", "pass123", "홍길동");
```

**ReservationRequest 생성**

```java
ReservationRequest request = new ReservationRequest.Builder()
    .setUserId("S123")
    .setRoom("101호")
    .build();
```

**Observer 등록**

```java
ReservationSubject.getInstance()
    .registerObserver(notification -> {
        System.out.println("알림: " + notification.getMessage());
    });
```

**ClassroomManager 확장**

```java
public class ClientClassroomManager extends ClassroomManager {
    @Override
    public boolean refreshFromServer() {
        // 구현
    }
}
```

## 🧪 테스트

```bash
mvn test
```

**테스트 케이스**:

- ✅ Builder 패턴 검증
- ✅ Singleton 패턴 검증 (Thread-safe)
- ✅ Observer 알림 전파 테스트
- ✅ Template Method 추상화 테스트

## 🔒 불변성 보장

### User & ReservationRequest

- 모든 필드 `final`
- Setter 메서드 없음
- 생성 후 수정 불가

## 📋 설정

### config.properties

```properties
server.ip=localhost
server.port=8000
data.dir=./data
```

### 사용

```java
String ip = ConfigLoader.getProperty("server.ip");
```

## 📊 통계

- **코드**: ~2,000 lines
- **디자인 패턴**: 4개 (Builder, Singleton, Observer, Template Method)
- **외부 의존성**: 없음 (Java 21 표준만 사용)

## 🔗 관련 프로젝트

- [OOM-Client](https://github.com/chikchok1/OOM-Client) - Swing GUI 클라이언트
- [OOM-Server](https://github.com/chikchok1/OOM-Server) - 멀티스레드 서버

---

**OOM Team** | 객체지향 프로그래밍 과제
