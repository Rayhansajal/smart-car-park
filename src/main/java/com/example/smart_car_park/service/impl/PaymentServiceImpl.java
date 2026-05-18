package com.example.smart_car_park.service.impl;

import com.example.smart_car_park.exception.BadRequestException;
import com.example.smart_car_park.exception.ResourceNotFoundException;
import com.example.smart_car_park.models.dto.request.PaymentRequestDTO;
import com.example.smart_car_park.models.dto.response.PaymentResponseDTO;
import com.example.smart_car_park.models.entity.Booking;
import com.example.smart_car_park.models.entity.Payment;
import com.example.smart_car_park.models.enums.BookingStatus;
import com.example.smart_car_park.models.enums.PaymentStatus;
import com.example.smart_car_park.repository.BookingRepository;
import com.example.smart_car_park.repository.PaymentRepository;
import com.example.smart_car_park.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.getBookingId()));

        if (booking.getStatus() != BookingStatus.CHECKED_OUT) {
            throw new BadRequestException("Payment can only be processed after checkout. Status: " + booking.getStatus());
        }

        paymentRepository.findByBookingId(booking.getId()).ifPresent(p -> {
            if (p.getStatus() == PaymentStatus.COMPLETED) {
                throw new BadRequestException("Booking is already paid");
            }
        });

        if (booking.getTotalAmount() == null) {
            throw new BadRequestException("Booking total amount has not been calculated yet");
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalAmount())
                .method(request.getMethod())
                .status(PaymentStatus.COMPLETED)
                .transactionId(request.getTransactionId() != null
                        ? request.getTransactionId()
                        : "TXN-" + System.currentTimeMillis())
                .paidAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment processed for booking {}: amount={}", booking.getBookingRef(), saved.getAmount());
        return mapToResponse(saved);
    }

    @Override
    public PaymentResponseDTO getPaymentById(Long id) {
        return mapToResponse(findById(id));
    }

    @Override
    public PaymentResponseDTO getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "bookingId", bookingId));
        return mapToResponse(payment);
    }

    @Override
    public Page<PaymentResponseDTO> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public PaymentResponseDTO refundPayment(Long paymentId) {
        Payment payment = findById(paymentId);
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BadRequestException("Only completed payments can be refunded");
        }
        payment.setStatus(PaymentStatus.REFUNDED);
        log.info("Payment {} refunded", paymentId);
        return mapToResponse(paymentRepository.save(payment));
    }

    private Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
    }

    private PaymentResponseDTO mapToResponse(Payment payment) {
        PaymentResponseDTO response = modelMapper.map(payment, PaymentResponseDTO.class);
        response.setBookingId(payment.getBooking().getId());
        response.setBookingRef(payment.getBooking().getBookingRef());
        return response;
    }
}
