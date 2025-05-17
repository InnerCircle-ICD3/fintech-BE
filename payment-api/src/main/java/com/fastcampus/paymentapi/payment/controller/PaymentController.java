//package com.fastcampus.paymentapi.payment.controller;
//
//import com.fastcampus.paymentcore.core.dto.PaymentProgressDto;
//import com.fastcampus.paymentcore.core.service.PaymentProgressService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/payments")
//@RequiredArgsConstructor
//public class PaymentController {
//
//    private final PaymentProgressService progressService;
//
//    @GetMapping("/progress")
//    public PaymentProgressDto getPaymentProgress(@RequestParam("token") String qrToken) {
//        return progressService.progressPayment(qrToken);
//    }
//}
