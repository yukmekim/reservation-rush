# ⚡ Reservation Rush

[![Java 21](https://img.shields.io/badge/Java-21-orange?style=flat-square)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.12-brightgreen?style=flat-square)](https://spring.io/projects/spring-boot)
[![H2 Database](https://img.shields.io/badge/H2-In--Memory-blue?style=flat-square)](http://www.h2database.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](LICENSE)

### 주요 특징
- **대기열 시스템**: Redis 기반 공정한 대기열 관리
- **동시성 제어**: 분산 락을 통한 재고 관리
- **성능 최적화**: Virtual Threads (Java 21) 활용
- **재사용 가능**: 다양한 예약 시스템에 적용 가능한 아키텍처

## 🛠 기술 스택

### Backend
- **Java 21** - Virtual Threads를 활용한 고성능 동시 처리
- **Spring Boot 3.4** - 최신 프레임워크 기반
- **Spring Data JPA** - 데이터 액세스 추상화
- **Lombok** - 보일러플레이트 코드 제거

### Database
- **H2 Database** (개발) - 빠른 개발 환경 구축
- **PostgreSQL** (예정) - 프로덕션 환경
- **Redis** (예정) - 대기열 및 캐싱

### Tools
- **Gradle 8.x** - 빌드 자동화
- **Git** - 버전 관리

---

## 🚀 빠른 시작

### 사전 요구사항
- Java 21 이상
- Gradle 8.x 이상

### 실행 방법

1. **프로젝트 클론**
```bash
git clone https://github.com/yourusername/reservation-rush.git
cd reservation-rush
```

2. **애플리케이션 실행**
```bash
./gradlew bootRun
```

3. **접속 확인**
```
애플리케이션: http://localhost:7010
H2 Console: http://localhost:7010/h2-console
```

4. **H2 Console 연결 정보**
```
JDBC URL: jdbc:h2:mem:reservationdb
User Name: sa
Password: (비워두기)
```

---

## 📁 프로젝트 구조

```
reservation-rush/
├── src/
│   ├── main/
│   │   ├── java/dev/reservation/ruse/
│   │   │   ├── entity/          # 엔티티
│   │   │   ├── enums/            # enum 관리
│   │   │   ├── repository/      # 데이터 액세스 계층
│   │   │   ├── service/         # 비즈니스 로직
│   │   │   ├── controller/      # REST API
│   │   │   ├── dto/             # 데이터 전송 객체
│   │   │   └── config/          # 설정 클래스
│   │   └── resources/
│   │       ├── application.yml  # 애플리케이션 설정
│   │       └── data.sql         # 초기 데이터 (예정)
│   └── test/                    # 테스트 코드
├── build.gradle                 # 빌드 설정
└── README.md
```

## 🧪 테스트

### 단위 테스트 실행
```bash
./gradlew test
```

---