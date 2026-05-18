package com.example.smart_car_park.controller;

import com.example.smart_car_park.models.dto.request.PaymentRequestDTO;
import com.example.smart_car_park.models.dto.response.ApiResponseDTO;
import com.example.smart_car_park.models.dto.response.PaymentResponseDTO;
import com.example.smart_car_park.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payments", description = "Payment processing and history")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Process payment for a completed booking")
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> processPayment(
            @Valid @RequestBody PaymentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(
                        paymentService.processPayment(request), "Payment processed successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Get all payments - Admin/Operator only")
    public ResponseEntity<ApiResponseDTO<Page<PaymentResponseDTO>>> getAllPayments(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponseDTO.success(paymentService.getAllPayments(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(paymentService.getPaymentById(id)));
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get payment by booking ID")
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> getPaymentByBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                paymentService.getPaymentByBookingId(bookingId)));
    }

    @PatchMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Refund a payment - Admin only")
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> refundPayment(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                paymentService.refundPayment(id), "Payment refunded successfully"));
    }
}
