package com.fastcampus.appusermanage.controller;

import com.fastcampus.appusermanage.dto.CommonResponse;
import com.fastcampus.appusermanage.dto.card.UserCardRegisterRequest;
import com.fastcampus.appusermanage.dto.card.UserCardResponse;
import com.fastcampus.appusermanage.service.UserCardService;
import com.fastcampus.common.util.SwaggerDocs.StandardResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app-users/cards")
@RequiredArgsConstructor
public class CardController {

    private final UserCardService userCardService;

    @Operation(summary = "카드 등록")
    @StandardResponses
    @PostMapping("/register")
    public ResponseEntity<CommonResponse> registerCard(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid UserCardRegisterRequest request) {

        userCardService.registerCard(authorization, request);
        return ResponseEntity.ok(CommonResponse.success("카드 등록 완료"));
    }

    @Operation(summary = "카드 삭제")
    @StandardResponses
    @DeleteMapping("/{cardToken}")
    public ResponseEntity<CommonResponse> deleteCard(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @PathVariable String cardToken) {

        userCardService.deleteCard(authorization, cardToken);
        return ResponseEntity.ok(CommonResponse.success("카드 삭제 완료"));
    }

    @Operation(summary = "카드 목록 조회")
    @StandardResponses
    @GetMapping
    public ResponseEntity<List<UserCardResponse>> getMyCards(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization) {

        return ResponseEntity.ok(userCardService.getMyCards(authorization));
    }

    @Operation(summary = "단일 카드 상세 조회")
    @StandardResponses
    @GetMapping("/{cardToken}")
    public ResponseEntity<UserCardResponse> getMyCardByToken(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @PathVariable String cardToken) {

        return ResponseEntity.ok(userCardService.getMyCardByToken(authorization, cardToken));
    }

    @Operation(summary = "결제 비밀번호 등록/변경")
    @StandardResponses
    @PutMapping("/{cardToken}/payment-password")
    public ResponseEntity<CommonResponse> updatePaymentPassword(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @PathVariable String cardToken,
            @RequestParam String newPaymentPassword) {

        userCardService.updatePaymentPassword(authorization, cardToken, newPaymentPassword);
        return ResponseEntity.ok(CommonResponse.success("결제 비밀번호가 변경되었습니다."));
    }

    @Operation(summary = "카드 유효성 검사")
    @StandardResponses
    @GetMapping("/{cardToken}/valid")
    public ResponseEntity<CommonResponse> validateCard(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @PathVariable String cardToken) {

        boolean valid = userCardService.isValidCard(authorization, cardToken);
        return ResponseEntity.ok(CommonResponse.success(valid ? "카드가 유효합니다." : "카드가 유효하지 않습니다."));
    }
}
