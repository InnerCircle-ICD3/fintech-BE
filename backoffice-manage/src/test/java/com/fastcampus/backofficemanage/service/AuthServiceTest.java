package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.dto.login.request.MerchantLoginRequest;
import com.fastcampus.backofficemanage.dto.login.response.MerchantLoginResponse;
import com.fastcampus.backofficemanage.dto.signup.request.MerchantSignUpRequest;
import com.fastcampus.backofficemanage.dto.signup.response.MerchantSignUpResponse;
import com.fastcampus.backofficemanage.entity.Merchant;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import com.fastcampus.backofficemanage.security.JwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class AuthServiceTest {

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void signup_shouldSucceed_whenValidRequest() {
        // given
        MerchantSignUpRequest request = MerchantSignUpRequest.builder()
                .loginId("merchant123")
                .loginPw("pw123")
                .name("가맹점")
                .businessNumber("123-456")
                .contactName("홍길동")
                .contactEmail("email@test.com")
                .contactPhone("010-1234-5678")
                .build();

        given(merchantRepository.existsByLoginId("merchant123")).willReturn(false);
        given(passwordEncoder.encode("pw123")).willReturn("encodedPw");
        given(merchantRepository.save(any(Merchant.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        MerchantSignUpResponse response = authService.signup(request);

        // then
        assertEquals("merchant123", response.getLoginId());
        assertEquals("가맹점", response.getName());
        assertEquals("ACTIVE", response.getStatus());
    }

    @Test
    void login_shouldReturnTokens_whenValidCredentials() {
        // given
        Merchant merchant = Merchant.builder()
                .loginId("merchant123")
                .loginPw("encodedPw")
                .status("ACTIVE")
                .build();

        given(merchantRepository.findByLoginId("merchant123"))
                .willReturn(Optional.of(merchant));
        given(passwordEncoder.matches("pw123", "encodedPw")).willReturn(true);
        given(jwtProvider.generateAccessToken("merchant123")).willReturn("access-token");
        given(jwtProvider.generateRefreshToken("merchant123")).willReturn("refresh-token");

        // when
        MerchantLoginResponse response = authService.login(
                MerchantLoginRequest.builder()
                        .loginId("merchant123")
                        .loginPw("pw123")
                        .build()
        );

        // then
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }
}
