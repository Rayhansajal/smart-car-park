package com.example.smart_car_park.service;

import com.example.smart_car_park.models.dto.request.BookingRequestDTO;
import com.example.smart_car_park.models.dto.response.BookingResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingResponseDTO createBooking(Long userId, BookingRequestDTO request);
    BookingResponseDTO getBookingById(Long id);
    BookingResponseDTO getBookingByRef(String ref);
    Page<BookingResponseDTO> getAllBookings(Pageable pageable);
    Page<BookingResponseDTO> getBookingsByUser(Long userId, Pageable pageable);
    BookingResponseDTO checkIn(Long bookingId);
    BookingResponseDTO checkOut(Long bookingId);
    BookingResponseDTO cancelBooking(Long userId, Long bookingId);
    void expireBookings();
}
