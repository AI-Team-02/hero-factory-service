package com.herofactory.payment.dto.response;

import com.herofactory.payment.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String paymentId;
    private PaymentStatus status;
    private String errorMessage;
    private String tid;
    private String nextRedirectPcUrl;
    private String nextRedirectMobileUrl;
}

