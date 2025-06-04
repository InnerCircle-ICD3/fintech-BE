package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.dto.common.CommonResponse;
import com.fastcampus.backofficemanage.dto.login.response.MerchantLoginResponse;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import com.fastcampus.backofficemanage.jwt.JwtProvider;
import com.fastcampus.common.constant.RedisKeys;
import com.fastcampus.common.exception.code.AuthErrorCode;
import com.fastcampus.common.exception.exception.UnauthorizedException;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@DisplayName("AuthService - 토큰 처리 로직 테스트")
class AuthServiceTokenTest {

    @Mock private MerchantRepository merchantRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private JwtProvider jwtProvider;
    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private ValueOperations<String, String> valueOps;

    @InjectMocks private AuthService authService;

    private AutoCloseable closeable;

    private static final String ACCESS_TOKEN = "abc.def.ghi";
    private static final String REFRESH_TOKEN = "refresh.token";
    private static final String LOGIN_ID = "merchant123";

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
        given(redisTemplate.opsForValue()).willReturn(valueOps);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Nested
    @DisplayName("로그아웃")
    class LogoutTests {

        @Test
        @DisplayName("정상 로그아웃 시 블랙리스트 처리 및 성공 응답")
        void logoutSuccess() {
            String bearerToken = "Bearer " + ACCESS_TOKEN;
            long exp = 100000L;
            given(jwtProvider.getRemainingExpiration(ACCESS_TOKEN)).willReturn(exp);

            ResponseEntity<CommonResponse> response = authService.logout(bearerToken);

            then(redisTemplate.opsForValue()).should().set(
                    RedisKeys.BLOCKLIST_PREFIX + ACCESS_TOKEN, "logout", exp, TimeUnit.MILLISECONDS
            );
            assertAll(
                    () -> assertTrue(response.getBody().isSuccess()),
                    () -> assertEquals("로그아웃 완료", response.getBody().getMessage())
            );
        }

        @Test
        @DisplayName("AccessToken이 없으면 UnauthorizedException 발생")
        void logoutMissingToken() {
            UnauthorizedException ex = assertThrows(
                    UnauthorizedException.class,
                    () -> authService.logout("")
            );
            assertEquals(AuthErrorCode.MISSING_ACCESS_TOKEN, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 재발급")
    class ReissueTests {

        @Test
        @DisplayName("유효한 RefreshToken으로 새로운 AccessToken 발급")
        void reissueSuccess() {
            String bearer = "Bearer " + REFRESH_TOKEN;
            String newAccess = "new.access.token";

            given(jwtProvider.validateToken(REFRESH_TOKEN)).willReturn(true);
            given(jwtProvider.getSubject(REFRESH_TOKEN)).willReturn(LOGIN_ID);
            given(jwtProvider.generateAccessToken(LOGIN_ID)).willReturn(newAccess);

            ResponseEntity<MerchantLoginResponse> response = authService.reissue(bearer);

            assertAll(
                    () -> assertEquals(newAccess, response.getBody().getAccessToken()),
                    () -> assertEquals(REFRESH_TOKEN, response.getBody().getRefreshToken())
            );
        }

        @Test
        @DisplayName("RefreshToken 누락 시 UnauthorizedException 발생")
        void reissueMissingToken() {
            UnauthorizedException ex = assertThrows(
                    UnauthorizedException.class,
                    () -> authService.reissue("")
            );
            assertEquals(AuthErrorCode.MISSING_REFRESH_TOKEN, ex.getErrorCode());
        }

        @Test
        @DisplayName("유효하지 않은 RefreshToken 시 UnauthorizedException 발생")
        void reissueInvalidToken() {
            String bearer = "Bearer invalid.token";
            given(jwtProvider.validateToken("invalid.token")).willReturn(false);

            UnauthorizedException ex = assertThrows(
                    UnauthorizedException.class,
                    () -> authService.reissue(bearer)
            );
            assertEquals(AuthErrorCode.INVALID_REFRESH_TOKEN, ex.getErrorCode());
        }
    }
}
