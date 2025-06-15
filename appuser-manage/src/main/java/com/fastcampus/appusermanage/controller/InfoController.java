package com.fastcampus.appusermanage.controller;

import com.fastcampus.appusermanage.dto.transaction.UserTransactionResponse;
import com.fastcampus.appusermanage.service.InfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/info")
@RequiredArgsConstructor
public class InfoController {

    private final InfoService infoService;

    @Operation(summary = "내 결제 내역 조회", description = "JWT 토큰을 기반으로 본인의 결제 거래 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserTransactionResponse.class)),
                    examples = @ExampleObject(value = """
                        [
                          {
                            "transactionId": 1001,
                            "amount": 12000,
                            "status": "COMPLETED",
                            "cardToken": "8b72aaca-3f6a-4ae4-8e5e-9f447d1b7f1a",
                            "createdAt": "2025-06-15T12:34:56"
                          },
                          {
                            "transactionId": 1002,
                            "amount": 8000,
                            "status": "FAILED",
                            "cardToken": "c1d92ecb-8a00-48ea-8322-7f0e2fd5f1c9",
                            "createdAt": "2025-06-14T11:20:33"
                          }
                        ]
                        """)
            )),
            @ApiResponse(responseCode = "401", description = "인증 실패 또는 토큰 누락")
    })
    @GetMapping("/transactions")
    public ResponseEntity<List<UserTransactionResponse>> getMyTransactions(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization
    ) {
        List<UserTransactionResponse> history = infoService.getMyTransactionHistory(authorization);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "카드별 결제 내역 조회", description = "카드 토큰(PaymentMethod) 기준으로 해당 카드의 결제 거래 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserTransactionResponse.class)),
                    examples = @ExampleObject(value = """
                        [
                          {
                            "transactionId": 2001,
                            "amount": 15000,
                            "status": "COMPLETED",
                            "cardToken": "8b72aaca-3f6a-4ae4-8e5e-9f447d1b7f1a",
                            "createdAt": "2025-06-12T10:10:10"
                          },
                          {
                            "transactionId": 2002,
                            "amount": 3000,
                            "status": "CANCELED",
                            "cardToken": "8b72aaca-3f6a-4ae4-8e5e-9f447d1b7f1a",
                            "createdAt": "2025-06-13T14:44:44"
                          }
                        ]
                        """)
            )),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (타인의 카드)"),
            @ApiResponse(responseCode = "404", description = "해당 카드 또는 거래 내역 없음")
    })
    @GetMapping("/transactions/by-card/{cardToken}")
    public ResponseEntity<List<UserTransactionResponse>> getTransactionsByCard(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @PathVariable String cardToken
    ) {
        List<UserTransactionResponse> history = infoService.getTransactionHistoryByCard(authorization, cardToken);
        return ResponseEntity.ok(history);
    }
}
