package com.fastcampus.backoffice.controller;

import com.fastcampus.backoffice.dto.ApiKeyDto;
import com.fastcampus.backoffice.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchants/api-keys")
@RequiredArgsConstructor
@Tag(name = "API Key Management", description = "API Key management endpoints")
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    @PostMapping("/{merchantId}")
    @Operation(summary = "Generate new API key for merchant")
    public ResponseEntity<ApiKeyDto> generateApiKey(@PathVariable Long merchantId) {
        return ResponseEntity.ok(apiKeyService.generateApiKey(merchantId));
    }

    @PostMapping("/{merchantId}/reissue")
    @Operation(summary = "Reissue API key for merchant")
    public ResponseEntity<ApiKeyDto> reissueApiKey(
        @PathVariable("merchantId") Long merchantId,
        @RequestParam("currentKey ") String currentKey
    ) {
        return ResponseEntity.ok(apiKeyService.reissueApiKey(merchantId, currentKey));
    }

    @GetMapping("/{merchantId}")
    @Operation(summary = "Get all API keys for merchant")
    public ResponseEntity<List<ApiKeyDto>> getApiKeys(@PathVariable("merchantId") Long merchantId) {
        return ResponseEntity.ok(apiKeyService.getApiKeys(merchantId));
    }

    @DeleteMapping("/{key}")
    @Operation(summary = "Deactivate API key")
    public ResponseEntity<Void> deactivateApiKey(@PathVariable String key) {
        apiKeyService.deactivateApiKey(key);
        return ResponseEntity.ok().build();
    }
} 