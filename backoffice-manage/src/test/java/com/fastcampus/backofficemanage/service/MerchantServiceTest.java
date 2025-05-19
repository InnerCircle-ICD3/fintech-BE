package com.fastcampus.backofficemanage.service;

import com.fastcampus.backofficemanage.dto.common.CommonResponse;
import com.fastcampus.backofficemanage.dto.info.MerchantInfoResponse;
import com.fastcampus.backofficemanage.dto.update.request.MerchantUpdateRequest;
import com.fastcampus.backofficemanage.entity.Merchant;
import com.fastcampus.backofficemanage.repository.MerchantRepository;
import com.fastcampus.common.exception.code.MerchantErrorCode;
import com.fastcampus.common.exception.exception.DuplicateKeyException;
import com.fastcampus.common.exception.exception.NotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@DisplayName("MerchantService 유닛 테스트")
class MerchantServiceTest {

    @Mock private MerchantRepository merchantRepository;
    @Mock private Clock clock;

    @InjectMocks private MerchantService merchantService;

    private AutoCloseable closeable;
    private final static String LOGIN_ID = "merchant123";
    private final static LocalDateTime NOW = LocalDateTime.of(2025, 5, 20, 12, 0);

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
        given(clock.instant()).willReturn(NOW.atZone(ZoneId.systemDefault()).toInstant());
        given(clock.getZone()).willReturn(ZoneId.systemDefault());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Nested
    @DisplayName("가맹점 정보 조회 (getMyInfo)")
    class GetMyInfoTests {

        @Test
        @DisplayName("정상적인 loginId로 조회 시 가맹점 정보 반환")
        void givenValidLoginId_whenGetMyInfo_thenReturnsInfo() {
            Merchant merchant = createMerchant();
            given(merchantRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(merchant));

            MerchantInfoResponse response = merchantService.getMyInfo(LOGIN_ID);

            assertEquals("가맹점", response.getName());
            assertEquals("123-456", response.getBusinessNumber());
        }

        @Test
        @DisplayName("존재하지 않는 loginId로 조회 시 NotFoundException 발생")
        void givenInvalidLoginId_whenGetMyInfo_thenThrows() {
            given(merchantRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> merchantService.getMyInfo(LOGIN_ID));
        }
    }

    @Nested
    @DisplayName("가맹점 정보 수정 (updateMyInfo)")
    class UpdateMyInfoTests {

        @Test
        @DisplayName("정상적으로 수정 요청 시 성공 응답 반환")
        void givenValidUpdateRequest_whenUpdate_thenSuccessResponse() {
            Merchant merchant = spy(createMerchant());
            given(merchantRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(merchant));

            MerchantUpdateRequest request = MerchantUpdateRequest.builder()
                    .name("변경된이름")
                    .businessNumber("999-999")
                    .contactName("이몽룡")
                    .contactEmail("new@email.com")
                    .contactPhone("010-9999-8888")
                    .build();

            CommonResponse response = merchantService.updateMyInfo(LOGIN_ID, request);

            assertTrue(response.isSuccess());
            assertEquals("가맹점 정보가 성공적으로 수정되었습니다.", response.getMessage());
            verify(merchant).updateInfo(anyString(), anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("수정 시 중복 사업자번호 오류 발생 시 DuplicateKeyException 발생")
        void givenDuplicateBusinessNumber_whenUpdate_thenThrows() {
            Merchant merchant = createMerchant();
            given(merchantRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(merchant));
            willThrow(DataIntegrityViolationException.class).given(merchantRepository).flush();

            MerchantUpdateRequest request = MerchantUpdateRequest.builder()
                    .name("변경된이름")
                    .businessNumber("999-999")
                    .contactName("이몽룡")
                    .contactEmail("new@email.com")
                    .contactPhone("010-9999-8888")
                    .build();

            assertThrows(DuplicateKeyException.class, () -> merchantService.updateMyInfo(LOGIN_ID, request));
        }

        @Test
        @DisplayName("존재하지 않는 loginId로 수정 시 NotFoundException 발생")
        void givenInvalidLoginId_whenUpdate_thenThrows() {
            given(merchantRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.empty());

            MerchantUpdateRequest request = MerchantUpdateRequest.builder().build();
            assertThrows(NotFoundException.class, () -> merchantService.updateMyInfo(LOGIN_ID, request));
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 (deleteMyAccount)")
    class DeleteMyAccountTests {

        @Test
        @DisplayName("정상적으로 회원 탈퇴 요청 시 상태값 변경 및 응답 반환")
        void givenValidLoginId_whenDelete_thenSuccessResponse() {
            Merchant merchant = createMerchant();
            given(merchantRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(merchant));

            CommonResponse response = merchantService.deleteMyAccount(LOGIN_ID);

            assertTrue(response.isSuccess());
            assertEquals("회원 탈퇴가 완료되었습니다.", response.getMessage());
            assertEquals("DELETED", merchant.getStatus());
        }

        @Test
        @DisplayName("존재하지 않는 loginId로 탈퇴 요청 시 NotFoundException 발생")
        void givenInvalidLoginId_whenDelete_thenThrows() {
            given(merchantRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> merchantService.deleteMyAccount(LOGIN_ID));
        }
    }

    // === Fixtures ===
    private Merchant createMerchant() {
        return Merchant.builder()
                .loginId(LOGIN_ID)
                .loginPw("encodedPw")
                .name("가맹점")
                .businessNumber("123-456")
                .contactName("홍길동")
                .contactEmail("email@test.com")
                .contactPhone("010-1234-5678")
                .status("ACTIVE")
                .build();
    }
}
