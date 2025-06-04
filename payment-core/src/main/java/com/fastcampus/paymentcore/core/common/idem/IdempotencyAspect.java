package com.fastcampus.paymentcore.core.common.idem;

import com.fastcampus.paymentcore.core.dto.IdempotencyDto;
import com.fastcampus.paymentcore.core.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private static final Logger logger = LoggerFactory.getLogger(IdempotencyAspect.class);

    private final IdempotencyService idempotencyService;

    // @Around 에다가 어떤 method 들을 대상으로 aspect 를 적용할지 지정할 수 있음. 특정 annotation 이 붙은 method 들, 또는 이름이 어떤 패턴인 method 들 등등
    @Around("@annotation(com.fastcampus.paymentcore.core.common.idem.Idempotent)")
    public Object aspectIdempotency(ProceedingJoinPoint joinPoint) throws Throwable {
        // 이 메소드 안에서 return 하는 값이 target 메소드가 return 하는 값이 됨.
        // decorator 패턴으로 구현하는 듯?
        // reflection 으로 껍데기 class 만들어 두고 client 가 target method 호출하면 그 껍데기 class 가 대신 호출 되는 듯?
        // 그 껍데기 class 가 바로 이 class 니까 여기서 return 하는 값이 사실 client method 가 return 받는 값이지
        // 하지만 보통은 target method 가 실행한 결과를 return 하는 게 보통일 테니 여기서도 return joinPoint.proceed(); 를 하는 거고.
        logger.info(" =========== IdempotencyAspect > aspectIdempotency :", joinPoint.getSignature().getName());

        String idemKey = extractIdemKey(joinPoint);

        // 멱등성 기능은 원래 없어도 되지만 혼자서 공부 삼아 만들어 보는 기능이기 때문에 idemkey 없으면 넘어가기
        // TODO - @Idempotent 키가 붙은 메소드에 멱등성 키를 파라미터로 넣어주도록 강제하려면 아래 if문 지우기
        if(idemKey == null || idemKey.length() < 1) {
            return joinPoint.proceed();
        }

        // 이미 동일한 요청이 처리된 경우 기존 결과 반환
        Optional<IdempotencyDto> idempotencyOptional = idempotencyService.checkIdempotency(idemKey);
        if (idempotencyOptional.isPresent()) {
            logger.info(" =========== IdempotencyAspect > response already exists: ", joinPoint.getSignature().getName());
            return idempotencyOptional.get().getResponseData();
        }

        logger.info(" =========== IdempotencyAspect > request passed :", joinPoint.getSignature().getName());

        // 최초 요청인 경우 target method 를 그대로 진행하고 response data 를 db에 저장
        Object result = joinPoint.proceed();
        // Class<?> returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType();
        // returnType.cast(result);  // class 를 인스턴스로 받았을 때 casting 하기
        // 아니, 그냥 casting 안 하고 entity 계층에서 object mapper 써서 json 으로 넣기, 꺼낼 때는 역직렬화 하기로 함.
        // db와 entity 간에 직렬화, 역직렬화를 자동으로 해주는 jpa 기능이 있음.
        // @Convert(converter = ObjectStringConverter.class)
        IdempotencyDto idempotencyDto = new IdempotencyDto(null, idemKey, result);
        idempotencyService.saveIdempotency(idempotencyDto);
        // 원래 target method 가 return 하려던 거 반환
        return result;
    }

    private String extractIdemKey(ProceedingJoinPoint joinPoint) {
        // IdempotencyDto를 상속한 파라미터 찾기
        Object[] args = joinPoint.getArgs();
        String idempotencyKey = "";
        for (Object arg : args) {
            if (arg instanceof IdempotencyDto) {
                idempotencyKey = ((IdempotencyDto) arg).getIdempotencyKey();  // 멱등성 키 추출
            }
        }
        // 멱등성 키 없으면 exception 던지기
        if(idempotencyKey == null || idempotencyKey.length() < 1) {
            // 멱등성 기능은 원래 없어도 되지만 혼자서 공부 삼아 만들어 보는 기능이기 때문에 exception 없이 넘어가기
            // TODO - @Idempotent 키가 붙은 메소드에 멱등성 키를 파라미터로 넣어주도록 강제하려면 아래 throw line 주석 풀기
            // throw new IdempotentKeyNotFound("멱등성 키가 없습니다 : " + joinPoint.getSignature().getName());
        }
        return idempotencyKey;
    }


}
