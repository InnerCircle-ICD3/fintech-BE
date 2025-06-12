package com.fastcampus.payment.entity;


import com.fastcampus.paymentmethod.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
@Table
@Data
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // FK 컬럼명. DB에서 이 이름으로 FK 생성됨
    private User user;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "merchant_id") // FK 컬럼명. DB에서 이 이름으로 FK 생성됨
//    private Merchant merchantId; // TODO - backoffice 쪽 merchant 참초하기 / 아니 id만 넣어놔도 되나? 어차피 내용 열어볼 일도 없을 것 같고
    private Long merchantId;

    private String merchantOrderId;

    private String paymentToken;


    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Long totalAmount;

    // 아직 save 되지 않은 transaction 을 payment 에 참조한 채로 payment 를 저장하려고 할 경우 "save the transient instance before flushing" 에러가 나므로 `@ManyToOne`에 cascade 설정 넣음
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "last_transaction_id") // FK 컬럼명. DB에서 이 이름으로 FK 생성됨
    private Transaction lastTransaction;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;


    public void changeLastTransaction(Transaction transaction) {
        if(transaction == null) {
            throw new IllegalArgumentException("transaction 이 null 입니다");
        }
        if(this.lastTransaction != transaction) {
            this.lastTransaction = transaction;
            transaction.changePayment(this);
        }
    }

    public void nullCheckRequiredParam() {
        List<Object> targetList = Arrays.asList(totalAmount, merchantId, merchantOrderId);

        boolean isNull = targetList.stream().anyMatch(obj -> Objects.isNull(obj));
        if (isNull) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다: totalAmount, merchantId, merchantOrderId");
        }
    }
}
