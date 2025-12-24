# Reservation Rush

[![Java 21](https://img.shields.io/badge/Java-21-orange?style=flat-square)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.12-brightgreen?style=flat-square)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](LICENSE)

동시 요청시 한정된 수량내에서 안정적인 예약 처리를 위한 환경을 테스트합니다.

## Why Reservation Rush?

- **동시성 제어** - Redis 분산 락, Optimistic/Pessimistic Lock을 활용한 재고 관리
- **고성능 처리** - Java 21 Virtual Threads로 효율적인 동시 요청 처리
- **재시도 메커니즘** - Spring Retry를 통한 트랜잭션 충돌 해결
- **API 문서화** - Swagger UI로 즉시 테스트 가능한 REST API

## Quick Start

### Prerequisites

- Java 21+
- Gradle 8.x+
- Redis (선택사항 - 분산 락 테스트 시)

### Installation

1. Clone the repository
```bash
git clone https://github.com/yourusername/reservation-rush.git
cd reservation-rush
```

2. Run the application
```bash
./gradlew bootRun
```

3. Access the application
```
Application: http://localhost:7010
API Docs: http://localhost:7010/swagger-ui.html
H2 Console: http://localhost:7010/h2-console
```

## Features

### Concurrency Control

프로젝트는 동시성 문제 해결을 위한 여러 전략을 구현합니다:

- **Optimistic Lock** - JPA `@Version`을 이용한 낙관적 잠금
- **Pessimistic Lock** - 데이터베이스 레벨의 비관적 잠금
- **Distributed Lock** - Redis를 활용한 분산 환경 잠금

### API Endpoints

- `POST /api/bookings` - 예약 생성
- `GET /api/bookings` - 예약 목록 조회
- `GET /api/packages` - 여행 패키지 조회
- `POST /api/packages` - 여행 패키지 생성

기본적인 API 명세는 [Swagger UI](http://localhost:7010/swagger-ui.html)에서 확인할 수 있습니다.

## Project Structure

```
src/main/java/dev/reservation/rush/
├── controller/       # REST API 컨트롤러
├── service/          # 비즈니스 로직
├── repository/       # 데이터 액세스 계층
├── entity/           # JPA 엔티티
├── dto/              # 요청/응답 DTO
├── config/           # 설정 클래스
└── common/           # 공통 모듈 (예외처리, 공통 응답 처리)
```

## Tech Stack

**Backend**
- Java 21 (Virtual Threads)
- Spring Boot 3.4
- Spring Data JPA
- Spring Retry

**Database**
- H2 (In-Memory)
- Redis (Distributed Lock)

**Documentation**
- Swagger/OpenAPI 3.0

**Build Tool**
- Gradle 8.x

## Testing

### Run all tests
```bash
./gradlew test
```

### Run concurrency tests
```bash
./gradlew test --tests "*concurrency*"
```

테스트에는 동시성 제어를 검증하는 시나리오가 포함되어 있습니다:
- Race Condition 테스트
- Optimistic Lock 테스트
- Pessimistic Lock 테스트
- Redis Distributed Lock 테스트

## Configuration

주요 설정은 `src/main/resources/application.yml`에서 관리됩니다:

```yaml
server:
  port: 7010

spring:
  datasource:
    url: jdbc:h2:mem:reservationdb

  data:
    redis:
      host: localhost
      port: 6479
```


