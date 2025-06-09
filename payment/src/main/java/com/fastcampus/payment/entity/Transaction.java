package com.fastcampus.payment.entity;

import com.fastcampus.paymentmethod.entity.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table
@Data
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;
    private String cardToken;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime expireAt;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;


    public void changePayment(Payment payment) {
        if(this.payment != payment) {
            this.payment = payment;
            payment.changeLastTransaction(this);
        }
    }

    public void checkStatusAlreadyDone() {
        if (PaymentStatus.COMPLETED.equals(this.status)) {
            throw new IllegalStateException("이미 완료된 거래입니다.");
        }
    }

    public Transaction(Payment payment) {
        if(payment == null) {
            throw new IllegalArgumentException("Payment 가 null 입니다");
        }
        this.status = payment.getStatus();
        this.changePayment(payment);
    }
}