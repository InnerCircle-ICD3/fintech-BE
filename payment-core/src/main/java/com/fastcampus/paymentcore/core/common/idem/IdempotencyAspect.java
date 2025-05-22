package com.fastcampus.paymentcore.core.common.idem;

import com.fastcampus.paymentcore.core.dto.IdempotencyDto;
import com.fastcampus.paymentcore.core.service.IdempotencyService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Aspect
@Component
public class IdempotencyAspect {

    private static final Logger logger = LoggerFactory.getLogger(IdempotencyAspect.class);

    @Autowired
    private IdempotencyService idempotencyService;

    @Around("@annotation(com.fastcampus.paymentcore.core.common.idem.Idempotent)")
    public Object aspectIdempotency(ProceedingJoinPoint joinPoint) throws Throwable {
        String idemKey = extractIdemKey(joinPoint);

        if (idemKey == null || idemKey.isEmpty()) {
            logger.warn("Idempotency key not found in method arguments: {}", joinPoint.getSignature().getName());
            return joinPoint.proceed();  // 키가 없으면 그냥 실행
        }

        Optional<IdempotencyDto> idempotencyOptional = idempotencyService.checkIdempotency(idemKey);
        if (idempotencyOptional.isPresent()) {
            logger.info("Idempotent response returned for key: {}", idemKey);
            return idempotencyOptional.get().getResponseData();
        }

        logger.info("Proceeding with method execution: {}", joinPoint.getSignature().getName());

        Object result = joinPoint.proceed(); // 실제 비즈니스 로직 실행
        String responseData = result != null ? result.toString() : "";

        IdempotencyDto idempotencyDto = new IdempotencyDto();
        idempotencyDto.setIdempotencyKey(idemKey);
        idempotencyDto.setResponseData(responseData);
        idempotencyService.saveIdempotency(idempotencyDto);

        return result;
    }

    private String extractIdemKey(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof Map<?, ?> map) {
                Object key = map.get("idemKey");
                if (key != null) {
                    return key.toString();
                }
            }
        }

        return null;
    }
}
