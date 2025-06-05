package com.fastcampus.payment.controller;

import com.fastcampus.payment.dto.PaymentExecutionRequest;
import com.fastcampus.payment.dto.PaymentExecutionResponse;
import com.fastcampus.payment.dto.PaymentReadyRequest;
import com.fastcampus.payment.dto.PaymentReadyResponse;
import com.fastcampus.payment.entity.Transaction;
import com.fastcampus.payment.service.PaymentExecutionService;
import com.fastcampus.payment.service.PaymentProgressService;
import com.fastcampus.payment.service.PaymentReadyService;
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

    /**
     * 1. 결제 요청 처리
     * - 거래 생성 및 QR Token 반환
     */
    @PostMapping("/transactions/ready")
    public PaymentReadyResponse initiateTransaction(@RequestBody @Valid PaymentReadyRequest request) {

        Transaction transaction = paymentReadyService.readyPayment(request.convertToTransaction());
        return new PaymentReadyResponse(transaction);
    }

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
    public PaymentExecutionResponse executePayment(@RequestBody @Valid PaymentExecutionRequest request) {
        PaymentExecutionResponse response = paymentExecutionService.execute(request);
        return response;
    }
}
