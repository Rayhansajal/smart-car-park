package com.example.smart_car_park.models.dto.response;

import com.example.smart_car_park.models.enums.PaymentMethod;
import com.example.smart_car_park.models.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
    private Long id;
    private Long bookingId;
    private String bookingRef;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
