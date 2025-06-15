package com.fastcampus.appusermanage.controller;

import com.fastcampus.appusermanage.dto.CommonResponse;
import com.fastcampus.appusermanage.dto.card.UserCardRegisterRequest;
import com.fastcampus.appusermanage.dto.card.UserCardResponse;
import com.fastcampus.appusermanage.service.UserCardService;
import com.fastcampus.common.exception.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "등록 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "UnauthorizedError",
                                    summary = "인증 실패 예시",
                                    value = """
                {
                  "code": "UNAUTHORIZED",
                  "message": "Access Token이 없거나 유효하지 않습니다.",
                  "status": 401,
                  "path": "/app-users/cards/register",
                  "timestamp": "2025-06-15T13:00:00"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복 등록된 카드",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "DuplicateCardError",
                                    summary = "중복 카드 등록 예시",
                                    value = """
                {
                  "code": "DUPLICATE_CARD",
                  "message": "이미 등록된 카드입니다.",
                  "status": 409,
                  "path": "/app-users/cards/register",
                  "timestamp": "2025-06-15T13:00:00"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "InternalServerError",
                                    summary = "서버 내부 오류 예시",
                                    value = """
                {
                  "code": "INTERNAL_SERVER_ERROR",
                  "message": "예상치 못한 서버 오류가 발생했습니다.",
                  "status": 500,
                  "path": "/app-users/cards/register",
                  "timestamp": "2025-06-15T13:00:00"
                }
                """
                            )
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<CommonResponse> registerCard(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid UserCardRegisterRequest request) {
        userCardService.registerCard(authorization, request);
        return ResponseEntity.ok(CommonResponse.success("카드 등록 완료"));
    }

    @Operation(summary = "카드 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "UnauthorizedError",
                                    summary = "인증 실패 예시",
                                    value = """
                {
                  "code": "UNAUTHORIZED",
                  "message": "Access Token이 없거나 유효하지 않습니다.",
                  "status": 401,
                  "path": "/app-users/cards/abcd1234",
                  "timestamp": "2025-06-15T13:00:00"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 (타인의 카드)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ForbiddenError",
                                    summary = "타인의 카드 접근 예시",
                                    value = """
                {
                  "code": "UNAUTHORIZED_CARD_ACCESS",
                  "message": "해당 카드에 대한 권한이 없습니다.",
                  "status": 403,
                  "path": "/app-users/cards/abcd1234",
                  "timestamp": "2025-06-15T13:00:00"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 카드",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "CardNotFoundError",
                                    summary = "존재하지 않는 카드 예시",
                                    value = """
                {
                  "code": "NOT_FOUND_CARD",
                  "message": "해당 카드를 찾을 수 없습니다.",
                  "status": 404,
                  "path": "/app-users/cards/abcd1234",
                  "timestamp": "2025-06-15T13:00:00"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "InternalServerError",
                                    summary = "서버 오류 예시",
                                    value = """
                {
                  "code": "INTERNAL_SERVER_ERROR",
                  "message": "예상치 못한 오류가 발생했습니다.",
                  "status": 500,
                  "path": "/app-users/cards/abcd1234",
                  "timestamp": "2025-06-15T13:00:00"
                }
                """
                            )
                    )
            )
    })
    @DeleteMapping("/{cardToken}")
    public ResponseEntity<CommonResponse> deleteCard(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @PathVariable String cardToken) {
        userCardService.deleteCard(authorization, cardToken);
        return ResponseEntity.ok(CommonResponse.success("카드 삭제 완료"));
    }

    @Operation(summary = "카드 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 정보 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<UserCardResponse>> getMyCards(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(userCardService.getMyCards(authorization));
    }

    @Operation(summary = "단일 카드 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음 (타인의 카드)", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "카드 정보 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{cardToken}")
    public ResponseEntity<UserCardResponse> getMyCardByToken(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @PathVariable String cardToken) {
        return ResponseEntity.ok(userCardService.getMyCardByToken(authorization, cardToken));
    }

    @Operation(summary = "결제 비밀번호 등록/변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음 (타인의 카드)", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "카드 정보 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{cardToken}/payment-password")
    public ResponseEntity<CommonResponse> updatePaymentPassword(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @PathVariable String cardToken,
            @RequestParam String newPaymentPassword) {
        userCardService.updatePaymentPassword(authorization, cardToken, newPaymentPassword);
        return ResponseEntity.ok(CommonResponse.success("결제 비밀번호가 변경되었습니다."));
    }

    @Operation(summary = "카드 유효성 검사")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검증 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음 (타인의 카드)", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "카드 정보 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{cardToken}/valid")
    public ResponseEntity<CommonResponse> validateCard(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @PathVariable String cardToken) {
        boolean valid = userCardService.isValidCard(authorization, cardToken);
        return ResponseEntity.ok(CommonResponse.success(valid ? "카드가 유효합니다." : "카드가 유효하지 않습니다."));
    }
}
