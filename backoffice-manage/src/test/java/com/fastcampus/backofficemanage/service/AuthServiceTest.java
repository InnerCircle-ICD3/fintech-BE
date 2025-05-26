package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.dto.login.request.MerchantLoginRequest;
import com.fastcampus.backofficemanage.dto.login.response.MerchantLoginResponse;
import com.fastcampus.backofficemanage.dto.signup.request.MerchantSignUpRequest;
import com.fastcampus.backofficemanage.dto.signup.response.MerchantSignUpResponse;
import com.fastcampus.backofficemanage.entity.Merchant;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import com.fastcampus.backofficemanage.security.JwtProvider;
import com.fastcampus.common.exception.exception.DuplicateKeyException;
import com.fastcampus.common.exception.exception.NotFoundException;
import com.fastcampus.common.exception.exception.UnauthorizedException;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@DisplayName("AuthService 유닛 테스트")
class AuthServiceTest {

    @Mock private MerchantRepository merchantRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private JwtProvider jwtProvider;

    @InjectMocks private AuthService authService;

    private AutoCloseable closeable;

    private static final String LOGIN_ID = "merchant123";
    private static final String RAW_PW = "pw123";
    private static final String ENCODED_PW = "encodedPw";

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Nested
    @DisplayName("회원가입 (signup)")
    class SignupTests {

        @Test
        @DisplayName("정상 회원가입 시 응답 DTO가 반환된다")
        void givenValidSignupRequest_whenSignup_thenReturnsResponse() {
            // given
            MerchantSignUpRequest request = createSignupRequest();
            given(merchantRepository.existsByLoginId(LOGIN_ID)).willReturn(false);
            given(passwordEncoder.encode(RAW_PW)).willReturn(ENCODED_PW);
            given(merchantRepository.save(any(Merchant.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            MerchantSignUpResponse response = authService.signup(request);

            // then
            assertEquals(LOGIN_ID, response.getLoginId());
            assertEquals("가맹점", response.getName());
            assertEquals("ACTIVE", response.getStatus());
        }

        @Test
        @DisplayName("중복된 loginId로 회원가입 시 DuplicateKeyException이 발생한다")
        void givenDuplicateLoginId_whenSignup_thenThrowsException() {
            // given
            MerchantSignUpRequest request = createSignupRequest();
            given(merchantRepository.existsByLoginId(LOGIN_ID)).willReturn(true);

            // expect
            assertThrows(DuplicateKeyException.class, () -> authService.signup(request));
        }
    }

    @Nested
    @DisplayName("로그인 (login)")
    class LoginTests {

        @Test
        @DisplayName("유효한 로그인 정보로 로그인 시 토큰이 반환된다")
        void givenValidLoginCredentials_whenLogin_thenReturnsTokens() {
            // given
            Merchant merchant = createMerchant(LOGIN_ID, ENCODED_PW);
            given(merchantRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(merchant));
            given(passwordEncoder.matches(RAW_PW, ENCODED_PW)).willReturn(true);
            given(jwtProvider.generateAccessToken(LOGIN_ID)).willReturn("access-token");
            given(jwtProvider.generateRefreshToken(LOGIN_ID)).willReturn("refresh-token");

            MerchantLoginRequest request = MerchantLoginRequest.builder()
                    .loginId(LOGIN_ID)
                    .loginPw(RAW_PW)
                    .build();

            // when
            MerchantLoginResponse response = authService.login(request);

            // then
            assertEquals("access-token", response.getAccessToken());
            assertEquals("refresh-token", response.getRefreshToken());
        }

        @Test
        @DisplayName("존재하지 않는 loginId로 로그인 시 NotFoundException이 발생한다")
        void givenInvalidLoginId_whenLogin_thenThrowsNotFoundException() {
            // given
            MerchantLoginRequest request = MerchantLoginRequest.builder()
                    .loginId("wrongId")
                    .loginPw(RAW_PW)
                    .build();
            given(merchantRepository.findByLoginId("wrongId")).willReturn(Optional.empty());

            // expect
            assertThrows(NotFoundException.class, () -> authService.login(request));
        }

        @Test
        @DisplayName("비밀번호가 틀린 경우 UnauthorizedException이 발생한다")
        void givenInvalidPassword_whenLogin_thenThrowsUnauthorizedException() {
            // given
            Merchant merchant = createMerchant(LOGIN_ID, ENCODED_PW);
            given(merchantRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(merchant));
            given(passwordEncoder.matches("wrongPw", ENCODED_PW)).willReturn(false);

            MerchantLoginRequest request = MerchantLoginRequest.builder()
                    .loginId(LOGIN_ID)
                    .loginPw("wrongPw")
                    .build();

            // expect
            assertThrows(UnauthorizedException.class, () -> authService.login(request));
        }
    }

    // === Fixtures ===
    private MerchantSignUpRequest createSignupRequest() {
        return MerchantSignUpRequest.builder()
                .loginId(LOGIN_ID)
                .loginPw(RAW_PW)
                .name("가맹점")
                .businessNumber("123-456")
                .contactName("홍길동")
                .contactEmail("email@test.com")
                .contactPhone("010-1234-5678")
                .build();
    }

    private Merchant createMerchant(String loginId, String encodedPw) {
        return Merchant.builder()
                .loginId(loginId)
                .loginPw(encodedPw)
                .status("ACTIVE")
                .build();
    }
}
