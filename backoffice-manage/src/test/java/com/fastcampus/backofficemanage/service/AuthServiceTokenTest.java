package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.dto.common.CommonResponse;
import com.fastcampus.backofficemanage.dto.login.response.MerchantLoginResponse;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import com.fastcampus.backofficemanage.security.JwtProvider;
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

@DisplayName("AuthService - Token 관련 테스트")
class AuthServiceTokenTest {

    @Mock private MerchantRepository merchantRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private JwtProvider jwtProvider;
    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks private AuthService authService;

    private AutoCloseable closeable;

    private static final String ACCESS_TOKEN = "abc.def.ghi";
    private static final String REFRESH_TOKEN = "refresh.token.value";
    private static final String LOGIN_ID = "merchant123";

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Nested
    @DisplayName("로그아웃")
    class LogoutTest {

        @Test
        @DisplayName("정상 로그아웃 시 블랙리스트에 저장되고 응답을 반환한다")
        void givenValidAccessToken_whenLogout_thenBlacklistedAndSuccess() {
            String bearerToken = "Bearer " + ACCESS_TOKEN;
            long expiration = 100000L;
            given(jwtProvider.getRemainingExpiration(ACCESS_TOKEN)).willReturn(expiration);

            ResponseEntity<CommonResponse> response = authService.logout(bearerToken);

            then(redisTemplate.opsForValue()).should().set(
                    RedisKeys.BLOCKLIST_PREFIX + ACCESS_TOKEN, "logout", expiration, TimeUnit.MILLISECONDS
            );

            assertTrue(response.getBody().isSuccess());
            assertEquals("로그아웃 완료", response.getBody().getMessage());
        }

        @Test
        @DisplayName("AccessToken이 누락되면 예외를 던진다")
        void givenEmptyAccessToken_whenLogout_thenThrowsUnauthorized() {
            UnauthorizedException ex = assertThrows(
                    UnauthorizedException.class,
                    () -> authService.logout("")
            );
            assertEquals(AuthErrorCode.MISSING_ACCESS_TOKEN, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("토큰 재발급")
    class ReissueTest {

        @Test
        @DisplayName("유효한 RefreshToken이면 새로운 AccessToken을 발급한다")
        void givenValidRefreshToken_whenReissue_thenReturnsNewAccessToken() {
            String bearer = "Bearer " + REFRESH_TOKEN;
            String newAccess = "new.access.token";

            given(jwtProvider.validateToken(REFRESH_TOKEN)).willReturn(true);
            given(jwtProvider.getSubject(REFRESH_TOKEN)).willReturn(LOGIN_ID);
            given(jwtProvider.generateAccessToken(LOGIN_ID)).willReturn(newAccess);

            ResponseEntity<MerchantLoginResponse> response = authService.reissue(bearer);

            assertEquals(newAccess, response.getBody().getAccessToken());
            assertEquals(REFRESH_TOKEN, response.getBody().getRefreshToken());
        }

        @Test
        @DisplayName("RefreshToken이 누락되면 예외를 던진다")
        void givenEmptyRefreshToken_whenReissue_thenThrowsUnauthorized() {
            UnauthorizedException ex = assertThrows(
                    UnauthorizedException.class,
                    () -> authService.reissue("")
            );
            assertEquals(AuthErrorCode.MISSING_REFRESH_TOKEN, ex.getErrorCode());
        }

        @Test
        @DisplayName("RefreshToken 검증에 실패하면 예외를 던진다")
        void givenInvalidRefreshToken_whenReissue_thenThrowsUnauthorized() {
            String bearer = "Bearer invalid.token";
            String actual = "invalid.token";

            given(jwtProvider.validateToken(actual)).willReturn(false);

            UnauthorizedException ex = assertThrows(
                    UnauthorizedException.class,
                    () -> authService.reissue(bearer)
            );
            assertEquals(AuthErrorCode.INVALID_REFRESH_TOKEN, ex.getErrorCode());
        }
    }
}
