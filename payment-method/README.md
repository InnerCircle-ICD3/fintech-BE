# Payment Method Library

## 📋 개요
Payment Method Library는 **결제 수단 관리 공유 라이브러리**입니다. 사용자, 카드 정보, 결제 수단 등 결제 관련 핵심 엔티티와 리포지토리를 제공하여 다른 모듈에서 공통으로 사용할 수 있도록 설계되었습니다.

## 🎯 주요 기능
- **엔티티 정의**: User, CardInfo, PaymentMethod 등 핵심 엔티티
- **리포지토리 제공**: JPA 기반 데이터 액세스 레이어
- **공통 데이터 모델**: 결제 관련 도메인 객체 표준화
- **타입 안전성**: Enum을 통한 결제 수단 및 상태 관리

## 🏗️ 기술 스택
- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Data JPA**
- **PostgreSQL** (지원 데이터베이스)
- **Hibernate** (ORM 구현체)

## 📦 모듈 구조

```
payment-method/
├── src/main/java/com/fastcampus/paymentmethod/
│   ├── entity/           # 핵심 엔티티 클래스
│   │   ├── User.java
│   │   ├── CardInfo.java
│   │   ├── PaymentMethod.java
│   │   ├── ReadOnlyPayment.java
│   │   └── enums/        # 열거형 정의
│   └── repository/       # JPA 리포지토리
│       ├── UserRepository.java
│       ├── CardInfoRepository.java
│       ├── PaymentMethodRepository.java
│       └── ReadOnlyTransactionRepository.java
└── build.gradle.kts     # 빌드 설정
```

## 🔧 의존성 추가

다른 모듈에서 이 라이브러리를 사용하려면:

```kotlin
// build.gradle.kts
dependencies {
    implementation(project(":payment-method"))
}
```

## 📊 핵심 엔티티

### 👤 User 엔티티
사용자 기본 정보를 관리합니다.

```java
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;  // BCrypt 암호화
    
    @Column(nullable = false)
    private String name;
    
    private String phone;
    
    @Column(nullable = false)
    private String status;  // ACTIVE, INACTIVE
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

**주요 메서드:**
- `updateInfo(String name, String phone)`: 사용자 정보 업데이트
- `updatePassword(String password)`: 비밀번호 변경
- `deactivate()`: 계정 비활성화

### 💳 CardInfo 엔티티
카드 정보를 안전하게 관리합니다.

```java
@Entity
@Table(name = "card_info")
public class CardInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;
    
    @Column(unique = true, nullable = false)
    private String cardToken;  // 보안 토큰
    
    @Column(nullable = false)
    private String cardNumber;  // 암호화된 카드번호
    
    @Column(nullable = false)
    private String maskedCardNumber;  // 마스킹된 번호
    
    @Column(nullable = false)
    private String cardHolderName;
    
    @Column(nullable = false)
    private String expiryDate;  // MM/YY 형식
    
    @Enumerated(EnumType.STRING)
    private CardType cardType;  // CREDIT, DEBIT
    
    @Enumerated(EnumType.STRING)
    private UseYn useYn;  // Y, N
}
```

**보안 특징:**
- 실제 카드번호는 암호화 저장
- 마스킹된 번호만 화면 표시
- 카드 토큰을 통한 안전한 참조

### 🏪 PaymentMethod 엔티티
사용자의 결제 수단을 관리합니다.

```java
@Entity
@Table(name = "payment_method")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethodType type;  // CARD, BANK_TRANSFER
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private CardInfo cardInfo;
    
    @Enumerated(EnumType.STRING)
    private UseYn useYn;
}
```

## 🗃️ 리포지토리 인터페이스

### UserRepository
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

### CardInfoRepository
```java
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {
    Optional<CardInfo> findByCardToken(String cardToken);
    List<CardInfo> findByUserUserIdAndUseYn(Long userId, UseYn useYn);
}
```

### PaymentMethodRepository
```java
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.type = :type and pm.user.userId = :userId")
    List<PaymentMethod> findByUserIdAndMethodType(@Param("userId") Long userId, @Param("type") PaymentMethodType type);
}
```

## 🏷️ 열거형 (Enums)

### PaymentMethodType
```java
public enum PaymentMethodType {
    CARD,           // 카드 결제
    BANK_TRANSFER,  // 계좌이체
    DIGITAL_WALLET  // 디지털 지갑
}
```

### CardType
```java
public enum CardType {
    CREDIT,  // 신용카드
    DEBIT,   // 체크카드
    PREPAID  // 선불카드
}
```

### UseYn
```java
public enum UseYn {
    Y,  // 사용 가능
    N   // 사용 불가
}
```

## 📋 사용 예시

### 1️⃣ 의존성 주입
```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CardInfoRepository cardInfoRepository;
    private final PaymentMethodRepository paymentMethodRepository;
}
```

### 2️⃣ 사용자 생성
```java
User user = User.builder()
    .email("user@example.com")
    .password(encodedPassword)
    .name("홍길동")
    .phone("010-1234-5678")
    .status("ACTIVE")
    .build();

User savedUser = userRepository.save(user);
```

### 3️⃣ 카드 정보 저장
```java
CardInfo cardInfo = CardInfo.builder()
    .cardToken(generateCardToken())
    .cardNumber(encryptedCardNumber)
    .maskedCardNumber("1234-****-****-5678")
    .cardHolderName("홍길동")
    .expiryDate("12/26")
    .cardType(CardType.CREDIT)
    .useYn(UseYn.Y)
    .build();

CardInfo savedCard = cardInfoRepository.save(cardInfo);
```

### 4️⃣ 결제 수단 등록
```java
PaymentMethod paymentMethod = PaymentMethod.builder()
    .user(user)
    .type(PaymentMethodType.CARD)
    .cardInfo(cardInfo)
    .useYn(UseYn.Y)
    .build();

PaymentMethod savedMethod = paymentMethodRepository.save(paymentMethod);
```

### 5️⃣ 조회 쿼리 사용
```java
// 사용자 이메일로 조회
Optional<User> user = userRepository.findByEmail("user@example.com");

// 사용자의 활성 카드 조회
List<CardInfo> activeCards = cardInfoRepository
    .findByUserUserIdAndUseYn(userId, UseYn.Y);

// 사용자의 카드 결제 수단 조회
List<PaymentMethod> cardMethods = paymentMethodRepository
    .findByUserIdAndMethodType(userId, PaymentMethodType.CARD);
```

## 🔗 연동 모듈

### 📥 이 라이브러리를 사용하는 모듈

#### **App User Manage** (appuser-manage)
```java
// 사용자 관리 서비스에서 User, CardInfo 엔티티 사용
@EntityScan("com.fastcampus.paymentmethod.entity")
@EnableJpaRepositories("com.fastcampus.paymentmethod.repository")
```

#### **Payment** (payment)
```java
// 결제 서비스에서 PaymentMethod, CardInfo 참조
dependencies {
    implementation(project(":payment-method"))
}
```

#### **Backoffice API** (backoffice-api)
```java
// 백오피스에서 결제 내역 조회 시 관련 엔티티 사용
@EntityScan(basePackages = {
    "com.fastcampus.backoffice.entity",
    "com.fastcampus.payment.entity",
    "com.fastcampus.paymentmethod.entity"
})
```

## 📊 데이터베이스 스키마

### 테이블 관계도
```
User (1) ─── (N) PaymentMethod (1) ─── (1) CardInfo
  │
  └─── (N) Payment ─── (N) Transaction
```

### DDL 스크립트
```sql
-- User 테이블
CREATE TABLE "user" (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL,
    name VARCHAR(10) NOT NULL,
    phone VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CardInfo 테이블
CREATE TABLE card_info (
    card_id BIGSERIAL PRIMARY KEY,
    card_token VARCHAR(255) UNIQUE NOT NULL,
    card_number TEXT NOT NULL,  -- 암호화된 데이터
    masked_card_number VARCHAR(20) NOT NULL,
    card_holder_name VARCHAR(100) NOT NULL,
    expiry_date VARCHAR(5) NOT NULL,
    card_type VARCHAR(20) NOT NULL,
    use_yn VARCHAR(1) NOT NULL DEFAULT 'Y',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PaymentMethod 테이블
CREATE TABLE payment_method (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(user_id),
    type VARCHAR(20) NOT NULL,
    card_id BIGINT REFERENCES card_info(card_id),
    use_yn VARCHAR(1) NOT NULL DEFAULT 'Y',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔧 설정 방법

### Spring Boot 애플리케이션에서 사용

#### 1️⃣ JPA 설정
```java
@SpringBootApplication
@EntityScan("com.fastcampus.paymentmethod.entity")
@EnableJpaRepositories("com.fastcampus.paymentmethod.repository")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

#### 2️⃣ 데이터베이스 설정
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/testdb
    username: testuser
    password: testpass
  jpa:
    hibernate:
      ddl-auto: update  # 개발환경에서만 사용
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

## 🧪 테스트

### 리포지토리 테스트
```java
@DataJpaTest
class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testFindByEmail() {
        // Given
        User user = User.builder()
            .email("test@example.com")
            .password("encoded_password")
            .name("테스트")
            .status("ACTIVE")
            .build();
        userRepository.save(user);
        
        // When
        Optional<User> found = userRepository.findByEmail("test@example.com");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("테스트");
    }
}
```

## 🔒 보안 고려사항

### 1. 카드 정보 보안
- **암호화**: 실제 카드번호는 AES 암호화 저장
- **토큰화**: 카드번호 대신 토큰을 통한 참조
- **마스킹**: UI에서는 마스킹된 번호만 표시

### 2. 개인정보 보호
- **Soft Delete**: 사용자 삭제 시 status 변경
- **데이터 최소화**: 필요한 정보만 저장
- **접근 제어**: 본인 데이터만 접근 가능

### 3. 데이터 무결성
- **외래키 제약**: 참조 무결성 보장
- **Unique 제약**: 중복 데이터 방지
- **Not Null 제약**: 필수 데이터 보장

## 📈 성능 최적화

### 1. 인덱스 설정
```sql
-- 자주 조회되는 컬럼에 인덱스 생성
CREATE INDEX idx_user_email ON "user"(email);
CREATE INDEX idx_card_token ON card_info(card_token);
CREATE INDEX idx_payment_method_user_type ON payment_method(user_id, type);
```

### 2. Lazy Loading
```java
// 연관 관계는 Lazy Loading으로 설정
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;
```

### 3. 쿼리 최적화
```java
// N+1 문제 해결을 위한 Join Fetch 사용
@Query("SELECT pm FROM PaymentMethod pm " +
       "JOIN FETCH pm.user " +
       "JOIN FETCH pm.cardInfo " +
       "WHERE pm.user.userId = :userId")
List<PaymentMethod> findByUserIdWithDetails(@Param("userId") Long userId);
```

## 🚨 마이그레이션 가이드

### 기존 데이터 마이그레이션
```sql
-- 기존 테이블에서 새 구조로 데이터 이전
INSERT INTO "user" (email, password, name, status)
SELECT email, password, name, 'ACTIVE'
FROM legacy_user_table;

-- 카드 정보 마이그레이션 (암호화 필요)
INSERT INTO card_info (card_token, card_number, masked_card_number, card_holder_name, expiry_date, card_type)
SELECT 
    CONCAT('card_', id),
    encrypt_card_number(card_number),
    mask_card_number(card_number),
    holder_name,
    expiry,
    'CREDIT'
FROM legacy_card_table;
```

## 📞 지원

### 개발 가이드
- **엔티티 수정**: 새로운 필드 추가 시 마이그레이션 스크립트 작성 필요
- **리포지토리 확장**: 커스텀 쿼리 메서드 추가 가능
- **성능 이슈**: 쿼리 실행 계획 확인 및 인덱스 최적화

### 문의사항
- **기술 지원**: tech-library@passionpay.com
- **버그 리포트**: [GitHub Issues](../../issues)
- **기능 요청**: tech-enhancement@passionpay.com 