package com.example.smart_car_park.service;

import com.example.smart_car_park.models.dto.request.PaymentRequestDTO;
import com.example.smart_car_park.models.dto.response.PaymentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentResponseDTO processPayment(PaymentRequestDTO request);
    PaymentResponseDTO getPaymentById(Long id);
    PaymentResponseDTO getPaymentByBookingId(Long bookingId);
    Page<PaymentResponseDTO> getAllPayments(Pageable pageable);
    PaymentResponseDTO refundPayment(Long paymentId);
}
