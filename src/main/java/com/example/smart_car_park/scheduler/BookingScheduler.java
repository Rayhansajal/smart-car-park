package com.example.smart_car_park.scheduler;

import com.example.smart_car_park.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingScheduler {
    private final BookingService bookingService;

    /** Every 5 minutes: expire PENDING bookings whose check-in window has passed. */
    @Scheduled(fixedDelay = 300_000)
    public void expireStaleBookings() {
        log.debug("Running booking expiry job...");
        bookingService.expireBookings();
    }
}
