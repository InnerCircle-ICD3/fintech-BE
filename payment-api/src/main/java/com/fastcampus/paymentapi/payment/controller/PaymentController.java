package com.fastcampus.paymentapi.payment.controller;

import com.fastcampus.paymentapi.payment.dto.request.InitiateTransactionRequest;
import com.fastcampus.paymentapi.payment.dto.response.GetTransactionProgressResponse;
import com.fastcampus.paymentapi.payment.dto.response.PaymentProgressResponseDto;
import com.fastcampus.paymentapi.payment.dto.request.ProgressTransactionRequest;
import com.fastcampus.paymentapi.payment.dto.response.ResponsePaymentReadyDto;
import com.fastcampus.paymentcore.core.dto.*;
import com.fastcampus.paymentcore.core.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentReadyService paymentReadyService;
    private final PaymentProgressService paymentProgressService;
    private final PaymentExecutionService paymentExecutionService;

    // Core와 API의 DTO를 분리해 놓았고, Core에서 Map, String으로 되어 있는 부분 수정되는 대로 주석 풀고 수정 예정
    /**
     * 1. 결제 요청 처리
     * - 거래 생성 및 QR Token 반환
     */
//    @PostMapping("/transactions/initiate")
//    public ResponsePaymentReadyDto initiateTransaction(
//            @RequestBody @Valid InitiateTransactionRequest request
//    ) {
//        ResponsePaymentReady internal = paymentReadyService.initiate(request);
//        return new ResponsePaymentReadyDto(
//                internal.getTransactionToken(),
//                internal.getExpiresAt()
//        );
//    }

    /**
     * 2. QR 코드 거래 상태 조회
     * - transaction_token으로 거래 상태 확인
     */
//    @GetMapping("/transactions/{token}")
//    public GetTransactionProgressResponse getTransactionProgress(
//            @Valid @ModelAttribute ProgressTransactionRequest request
//    ) {
//        PaymentProgressDto dto = paymentProgressService.progressPayment(request);
//        return new GetTransactionProgressResponse(
//                dto.getTransactionToken(),
//                dto.getStatus().name(),
//                dto.getAmount(),
//                dto.getCreatedAt()
//        );
//    }

    /**
     * 3. 결제 실행
     * - transaction_token + card_token 이용해 결제 처리
     */
    @PostMapping("/payments/execute")
    public PaymentProgressResponseDto executePayment(@RequestBody @Valid PaymentProgressRequest request) {
        PaymentProgressResponse response = paymentExecutionService.execute(request);
        return new PaymentProgressResponseDto(
                response.getTransactionToken(),
                response.getStatus()
        );
    }
}
