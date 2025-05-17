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

    private static Logger logger = LoggerFactory.getLogger(IdempotencyAspect.class);

    @Autowired
    private IdempotencyService idempotencyService;

    // @Around 에다가 어떤 method 들을 대상으로 aspect 를 적용할지 지정할 수 있음. 특정 annotation 이 붙은 method 들, 또는 이름이 어떤 패턴인 method 들 등등
    @Around("@annotation(com.fastcampus.paymentcore.core.common.idem.Idempotent)")
    public Object aspectIdempotency(ProceedingJoinPoint joinPoint) throws Throwable {
        int idemkey = extractIdemKey(joinPoint);

        // 이미 동일한 요청이 처리된 경우, 기존 결과 반환
        Optional<IdempotencyDto> idempotencyOptional = idempotencyService.checkIdempotency(idemkey);
        if (idempotencyOptional.isPresent()) {
            logger.info(" =========== IdempotencyService > response already exists: ", joinPoint.getSignature().getName());
            return idempotencyOptional.get().getResponseData();
        }
        logger.info(" =========== IdempotencyService > request passed :", joinPoint.getSignature().getName());

        // 실제 메서드 실행 및 결과 저장
        Object result = joinPoint.proceed();    // @Idempotent 어노테이션이 붙은 method 를 실행하고 result 를 받아옴
        IdempotencyDto idempotencyResult = new IdempotencyDto();
        String responseData = "";
        if(result != null) {
            responseData = result.toString();   // TODO - data format 어떻게 저장할지 - null 일 경우 / null 아닐 경우 모두 넣기
        }
        idempotencyResult.setResponseData(responseData);
        idempotencyService.saveIdempotency(idempotencyResult);

        return result;
    }

    private int extractIdemKey(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof Map<?, ?>) { // 인자가 Map 타입인지 확인
                Map<?, ?> paramMap = (Map<?, ?>) arg;
                if (paramMap.containsKey("idemKey")) {
                    return Integer.valueOf((String)paramMap.get("idemKey")); // "idenKey" 값 반환
                }
            }
        }

        return 0; // 해당 키가 없으면 null 반환
    }
}
