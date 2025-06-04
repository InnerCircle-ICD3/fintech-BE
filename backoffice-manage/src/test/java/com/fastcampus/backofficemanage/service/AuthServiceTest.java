package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.dto.login.request.MerchantLoginRequest;
import com.fastcampus.backofficemanage.dto.login.response.MerchantLoginResponse;
import com.fastcampus.backofficemanage.dto.signup.request.MerchantSignUpRequest;
import com.fastcampus.backofficemanage.dto.signup.response.MerchantSignUpResponse;
import com.fastcampus.backofficemanage.entity.Merchant;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import com.fastcampus.backofficemanage.jwt.JwtProvider;
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

@DisplayName("AuthService - 회원가입/로그인 로직 테스트")
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
        @DisplayName("정상 회원가입 - Keys 생성 포함 확인")
        void signupSuccess() {
            // given
            MerchantSignUpRequest request = createSignupRequest();
            given(merchantRepository.existsByLoginId(LOGIN_ID)).willReturn(false);
            given(passwordEncoder.encode(RAW_PW)).willReturn(ENCODED_PW);

            // merchantRepository.save(...) 호출될 때, merchant.keys도 함께 save되는지 검증 가능
            given(merchantRepository.save(any(Merchant.class)))
                    .willAnswer(invocation -> {
                        Merchant merchant = invocation.getArgument(0);
                        assertNotNull(merchant.getKeys()); // Keys가 서비스 로직에서 생성되었는지 확인
                        return merchant;
                    });

            // when
            MerchantSignUpResponse response = authService.signup(request);

            // then
            assertAll(
                    () -> assertEquals(LOGIN_ID, response.getLoginId()),
                    () -> assertEquals("가맹점", response.getName()),
                    () -> assertEquals("ACTIVE", response.getStatus())
            );
        }

        @Test
        @DisplayName("중복 loginId로 회원가입 시 DuplicateKeyException 발생")
        void signupDuplicateId() {
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
        @DisplayName("유효한 정보로 로그인 시 토큰 반환")
        void loginSuccess() {
            // given
            Merchant merchant = createMerchant(LOGIN_ID, ENCODED_PW);
            given(merchantRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(merchant));
            given(passwordEncoder.matches(RAW_PW, ENCODED_PW)).willReturn(true);
            given(jwtProvider.generateAccessToken(LOGIN_ID)).willReturn("access-token");
            given(jwtProvider.generateRefreshToken(LOGIN_ID)).willReturn("refresh-token");

            MerchantLoginRequest request = createLoginRequest(LOGIN_ID, RAW_PW);

            // when
            MerchantLoginResponse response = authService.login(request);

            // then
            assertAll(
                    () -> assertEquals("access-token", response.getAccessToken()),
                    () -> assertEquals("refresh-token", response.getRefreshToken())
            );
        }

        @Test
        @DisplayName("존재하지 않는 loginId로 로그인 시 NotFoundException 발생")
        void loginNotFound() {
            // given
            MerchantLoginRequest request = createLoginRequest("wrongId", RAW_PW);
            given(merchantRepository.findByLoginId("wrongId")).willReturn(Optional.empty());

            // expect
            assertThrows(NotFoundException.class, () -> authService.login(request));
        }

        @Test
        @DisplayName("비밀번호 틀린 경우 UnauthorizedException 발생")
        void loginInvalidPassword() {
            // given
            Merchant merchant = createMerchant(LOGIN_ID, ENCODED_PW);
            given(merchantRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(merchant));
            given(passwordEncoder.matches("wrongPw", ENCODED_PW)).willReturn(false);

            MerchantLoginRequest request = createLoginRequest(LOGIN_ID, "wrongPw");

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

    private MerchantLoginRequest createLoginRequest(String id, String pw) {
        return MerchantLoginRequest.builder()
                .loginId(id)
                .loginPw(pw)
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
