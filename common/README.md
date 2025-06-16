# Common Library

## 📋 개요
Common Library는 **PassionPay 플랫폼의 공통 라이브러리**입니다. JWT 처리, 예외 처리, 유틸리티 함수, 상수 정의 등 모든 모듈에서 공통으로 사용되는 기능들을 제공합니다.

## 🎯 주요 기능
- **JWT 관리**: JWT 토큰 생성, 검증, 파싱 유틸리티
- **예외 처리**: 공통 예외 클래스 및 에러 코드 정의
- **상수 관리**: Redis 키, 시스템 설정값 등 공통 상수
- **유틸리티**: 일반적인 헬퍼 함수들
- **보안 도구**: 암호화, 복호화 유틸리티

## 🏗️ 기술 스택
- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Security**
- **JWT (JSON Web Token)**
- **Jackson** (JSON 처리)

## 📦 모듈 구조

```
common/
├── src/main/java/com/fastcampus/common/
│   ├── constant/         # 상수 정의
│   │   └── RedisKeys.java
│   ├── exception/        # 예외 처리
│   │   ├── code/         # 에러 코드 정의
│   │   ├── exception/    # 커스텀 예외 클래스
│   │   └── response/     # 에러 응답 포맷
│   ├── jwt/              # JWT 관련 (예정)
│   │   ├── JwtProvider.java
│   │   └── JwtUtils.java
│   └── util/             # 유틸리티 클래스
│       ├── CryptoUtils.java
│       └── DateUtils.java
└── build.gradle.kts      # 빌드 설정
```

## 🔧 의존성 추가

다른 모듈에서 이 라이브러리를 사용하려면:

```kotlin
// build.gradle.kts
dependencies {
    implementation(project(":common"))
}
```

## 🔑 JWT 관리 (예정)

### JwtProvider 클래스 (추후 구현)
```java
@Component
public class JwtProvider {
    
    public String generateAccessToken(String subject) {
        return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    public String generateRefreshToken(String subject) {
        return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    public String getSubject(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
```

## ⚠️ 예외 처리 시스템

### 🏷️ 에러 코드 정의

#### AuthErrorCode
```java
public enum AuthErrorCode implements ErrorCode {
    // 인증 관련 에러
    INVALID_CREDENTIALS(401, "AUTH_001", "아이디 또는 비밀번호가 올바르지 않습니다"),
    INVALID_PASSWORD(401, "AUTH_002", "비밀번호가 올바르지 않습니다"),
    ACCOUNT_INACTIVE(403, "AUTH_003", "비활성화된 계정입니다"),
    DUPLICATE_LOGIN_ID(409, "AUTH_004", "이미 사용 중인 로그인 ID입니다"),
    
    // JWT 관련 에러
    MISSING_ACCESS_TOKEN(401, "JWT_001", "Access Token이 없습니다"),
    MISSING_REFRESH_TOKEN(401, "JWT_002", "Refresh Token이 없습니다"),
    INVALID_ACCESS_TOKEN(401, "JWT_003", "유효하지 않은 Access Token입니다"),
    INVALID_REFRESH_TOKEN(401, "JWT_004", "유효하지 않은 Refresh Token입니다"),
    EXPIRED_TOKEN(401, "JWT_005", "토큰이 만료되었습니다");
    
    private final int status;
    private final String code;
    private final String message;
    
    AuthErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
```

#### CardErrorCode
```java
public enum CardErrorCode implements ErrorCode {
    INVALID_CARD_NUMBER(400, "CARD_001", "유효하지 않은 카드 번호입니다"),
    INVALID_EXPIRY_DATE(400, "CARD_002", "유효하지 않은 만료일입니다"),
    CARD_NOT_FOUND(404, "CARD_003", "카드 정보를 찾을 수 없습니다"),
    CARD_ALREADY_REGISTERED(409, "CARD_004", "이미 등록된 카드입니다"),
    INVALID_CARD_TOKEN(400, "CARD_005", "유효하지 않은 카드 토큰입니다");
}
```

### 🚨 커스텀 예외 클래스

#### BaseException
```java
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    
    protected BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public int getStatus() {
        return errorCode.getStatus();
    }
    
    public String getCode() {
        return errorCode.getCode();
    }
}
```

#### 구체적인 예외 클래스들
```java
// 인증 관련 예외
public class UnauthorizedException extends BaseException {
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}

// 리소스 없음 예외
public class NotFoundException extends BaseException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

// 중복 데이터 예외
public class DuplicateKeyException extends BaseException {
    public DuplicateKeyException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public static DuplicateKeyException of(ErrorCode errorCode) {
        return new DuplicateKeyException(errorCode);
    }
}
```

### 📋 에러 응답 포맷

#### ErrorResponse
```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final String error;
    private final String message;
    private final String code;
    private final LocalDateTime timestamp;
    private final String path;
    
    public ErrorResponse(String error, String message, String code, String path) {
        this.error = error;
        this.message = message;
        this.code = code;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }
    
    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return new ErrorResponse(
            errorCode.name(),
            errorCode.getMessage(),
            errorCode.getCode(),
            path
        );
    }
}
```

## 🗂️ 상수 관리

### RedisKeys
```java
public class RedisKeys {
    // JWT 블랙리스트
    public static final String BLOCKLIST_PREFIX = "blocklist:";
    
    // 세션 관리
    public static final String SESSION_PREFIX = "session:";
    public static final String USER_SESSION_PREFIX = "user_session:";
    public static final String MERCHANT_SESSION_PREFIX = "merchant_session:";
    
    // 캐시 키
    public static final String PAYMENT_CACHE_PREFIX = "payment:";
    public static final String USER_CACHE_PREFIX = "user:";
    public static final String CARD_CACHE_PREFIX = "card:";
    
    // 설정값
    public static final String CONFIG_PREFIX = "config:";
    
    // TTL 상수 (초)
    public static final long ACCESS_TOKEN_TTL = 3600;      // 1시간
    public static final long REFRESH_TOKEN_TTL = 604800;   // 7일
    public static final long SESSION_TTL = 1800;           // 30분
    public static final long PAYMENT_TTL = 3600;           // 1시간
    
    private RedisKeys() {
        // 유틸리티 클래스이므로 인스턴스 생성 방지
    }
}
```

## 🛠️ 유틸리티 클래스

### CryptoUtils (예정)
```java
@Component
public class CryptoUtils {
    
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    
    @Value("${encryption.secret-key}")
    private String secretKey;
    
    /**
     * 문자열을 AES로 암호화합니다.
     */
    public String encrypt(String plainText) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    /**
     * AES로 암호화된 문자열을 복호화합니다.
     */
    public String decrypt(String encryptedText) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decrypted);
    }
    
    /**
     * 카드 번호를 마스킹합니다.
     */
    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return cardNumber;
        }
        
        String cleaned = cardNumber.replaceAll("-", "");
        if (cleaned.length() != 16) {
            return cardNumber;
        }
        
        return cleaned.substring(0, 4) + "-****-****-" + cleaned.substring(12);
    }
}
```

### DateUtils
```java
public class DateUtils {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 현재 시간을 문자열로 반환합니다.
     */
    public static String now() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }
    
    /**
     * 현재 날짜를 문자열로 반환합니다.
     */
    public static String today() {
        return LocalDate.now().format(DATE_FORMATTER);
    }
    
    /**
     * 지정된 일수 후의 날짜를 반환합니다.
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, int days) {
        return dateTime.plusDays(days);
    }
    
    /**
     * 지정된 시간 후의 시간을 반환합니다.
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, int hours) {
        return dateTime.plusHours(hours);
    }
    
    /**
     * 두 날짜 사이의 일수를 계산합니다.
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }
    
    /**
     * Unix timestamp를 LocalDateTime으로 변환합니다.
     */
    public static LocalDateTime fromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
    
    /**
     * LocalDateTime을 Unix timestamp로 변환합니다.
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    
    private DateUtils() {
        // 유틸리티 클래스이므로 인스턴스 생성 방지
    }
}
```

## 📋 사용 예시

### 1️⃣ 예외 처리 사용
```java
@Service
public class UserService {
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(AuthErrorCode.NOT_FOUND_ID));
    }
    
    public void validatePassword(String password, String encodedPassword) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new UnauthorizedException(AuthErrorCode.INVALID_PASSWORD);
        }
    }
    
    public void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw DuplicateKeyException.of(AuthErrorCode.DUPLICATE_LOGIN_ID);
        }
    }
}
```

### 2️⃣ Redis 키 사용
```java
@Service
public class TokenService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    public void blacklistToken(String token) {
        String key = RedisKeys.BLOCKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", RedisKeys.ACCESS_TOKEN_TTL, TimeUnit.SECONDS);
    }
    
    public boolean isTokenBlacklisted(String token) {
        String key = RedisKeys.BLOCKLIST_PREFIX + token;
        return redisTemplate.hasKey(key);
    }
    
    public void cacheUserSession(Long userId, String sessionData) {
        String key = RedisKeys.USER_SESSION_PREFIX + userId;
        redisTemplate.opsForValue().set(key, sessionData, RedisKeys.SESSION_TTL, TimeUnit.SECONDS);
    }
}
```

### 3️⃣ 암호화 유틸리티 사용
```java
@Service
public class CardService {
    
    private final CryptoUtils cryptoUtils;
    
    public void saveCard(String cardNumber, String holderName) {
        try {
            String encryptedCardNumber = cryptoUtils.encrypt(cardNumber);
            String maskedCardNumber = cryptoUtils.maskCardNumber(cardNumber);
            
            CardInfo cardInfo = CardInfo.builder()
                .cardNumber(encryptedCardNumber)
                .maskedCardNumber(maskedCardNumber)
                .cardHolderName(holderName)
                .build();
                
            cardInfoRepository.save(cardInfo);
        } catch (Exception e) {
            throw new RuntimeException("카드 정보 암호화 중 오류 발생", e);
        }
    }
}
```

### 4️⃣ 날짜 유틸리티 사용
```java
@Service
public class PaymentService {
    
    public void createPayment(PaymentRequest request) {
        // 결제 만료 시간 설정 (1시간 후)
        LocalDateTime expiresAt = DateUtils.addHours(LocalDateTime.now(), 1);
        
        Payment payment = Payment.builder()
            .amount(request.getAmount())
            .merchantId(request.getMerchantId())
            .expiresAt(expiresAt)
            .createdAt(LocalDateTime.now())
            .build();
            
        paymentRepository.save(payment);
    }
    
    public List<Payment> getPaymentHistory(LocalDate startDate, LocalDate endDate) {
        long daysBetween = DateUtils.daysBetween(startDate, endDate);
        
        if (daysBetween > 365) {
            throw new IllegalArgumentException("조회 기간은 1년을 초과할 수 없습니다");
        }
        
        return paymentRepository.findByDateRange(
            startDate.atStartOfDay(),
            endDate.atTime(23, 59, 59)
        );
    }
}
```

## 🔗 연동 모듈

### 📤 이 라이브러리를 사용하는 모듈

모든 서비스 모듈에서 공통으로 사용:

- **backoffice-api**: 예외 처리, JWT 검증
- **backoffice-manage**: JWT 생성, 예외 처리, 암호화
- **payment**: 예외 처리, Redis 키 관리, 날짜 유틸리티
- **appuser-manage**: JWT 관리, 예외 처리, 암호화
- **payment-method**: 예외 처리, 상수 사용

## 🧪 테스트

### 예외 처리 테스트
```java
@ExtendWith(MockitoExtension.class)
class ExceptionTest {
    
    @Test
    void shouldThrowNotFoundException() {
        // Given
        ErrorCode errorCode = AuthErrorCode.NOT_FOUND_ID;
        
        // When & Then
        assertThatThrownBy(() -> {
            throw new NotFoundException(errorCode);
        })
        .isInstanceOf(NotFoundException.class)
        .hasMessage(errorCode.getMessage());
    }
}
```

### 유틸리티 테스트
```java
class DateUtilsTest {
    
    @Test
    void shouldCalculateDaysBetween() {
        // Given
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 10);
        
        // When
        long days = DateUtils.daysBetween(start, end);
        
        // Then
        assertThat(days).isEqualTo(9);
    }
    
    @Test
    void shouldMaskCardNumber() {
        // Given
        String cardNumber = "1234567890123456";
        
        // When
        String masked = CryptoUtils.maskCardNumber(cardNumber);
        
        // Then
        assertThat(masked).isEqualTo("1234-****-****-3456");
    }
}
```

## ⚙️ 설정

### application.yml 추가 설정
```yaml
# 암호화 키 설정
encryption:
  secret-key: "your-32-character-secret-key-here"

# JWT 설정 (각 모듈에서 개별 설정)
jwt:
  secret: "your-jwt-secret-key"
  access-token-expiration: 3600000   # 1시간
  refresh-token-expiration: 604800000 # 7일

# Redis 설정
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-wait: -1ms
          max-idle: 8
          min-idle: 0
```

## 🔒 보안 고려사항

### 1. 암호화 키 관리
- 환경변수 또는 외부 설정 파일에서 관리
- 프로덕션과 개발 환경의 키 분리
- 정기적인 키 로테이션

### 2. 에러 정보 노출 방지
- 스택 트레이스 숨김
- 민감한 정보 마스킹
- 적절한 에러 메시지 제공

### 3. 로깅 보안
- 개인정보 로그 출력 방지
- 카드 정보 마스킹
- 토큰 정보 부분 마스킹

## 📈 성능 고려사항

### 1. Redis 연결 풀링
- Lettuce 커넥션 풀 설정 최적화
- 타임아웃 설정으로 무한 대기 방지

### 2. 암호화 성능
- 필요한 경우에만 암호화 수행
- 비동기 처리 고려

### 3. 예외 처리 성능
- 예외 생성 비용 최소화
- 스택 트레이스 깊이 제한

## 📞 지원

### 개발 가이드
- **새로운 에러 코드 추가**: 적절한 Enum에 추가 후 문서 업데이트
- **유틸리티 함수 추가**: 테스트 코드와 함께 작성
- **상수 추가**: 네이밍 컨벤션 준수

### 문의사항
- **기술 지원**: tech-common@passionpay.com
- **버그 리포트**: [GitHub Issues](../../issues)
- **기능 요청**: tech-enhancement@passionpay.com 