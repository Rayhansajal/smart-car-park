package com.example.smart_car_park.service.impl;

import com.example.smart_car_park.exception.BadRequestException;
import com.example.smart_car_park.exception.ResourceNotFoundException;
import com.example.smart_car_park.exception.UnauthorizedException;
import com.example.smart_car_park.models.dto.request.BookingRequestDTO;
import com.example.smart_car_park.models.dto.response.BookingResponseDTO;
import com.example.smart_car_park.models.entity.Booking;
import com.example.smart_car_park.models.entity.ParkingSlot;
import com.example.smart_car_park.models.entity.User;
import com.example.smart_car_park.models.entity.Vehicle;
import com.example.smart_car_park.models.enums.BookingStatus;
import com.example.smart_car_park.models.enums.SlotStatus;
import com.example.smart_car_park.repository.BookingRepository;
import com.example.smart_car_park.repository.ParkingSlotRepository;
import com.example.smart_car_park.repository.UserRepository;
import com.example.smart_car_park.repository.VehicleRepository;
import com.example.smart_car_park.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ParkingSlotRepository slotRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(Long userId, BookingRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        if (!vehicle.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Vehicle does not belong to this user");
        }

        ParkingSlot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("ParkingSlot", "id", request.getSlotId()));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new BadRequestException("Slot is not available: " + slot.getSlotNo());
        }

        List<Booking> active = bookingRepository.findBySlotIdAndStatusIn(
                slot.getId(), List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN));
        if (!active.isEmpty()) {
            throw new BadRequestException("Slot already has an active booking");
        }

        Booking booking = Booking.builder()
                .bookingRef(generateRef())
                .user(user).vehicle(vehicle).slot(slot)
                .scheduledCheckIn(request.getScheduledCheckIn())
                .scheduledCheckOut(request.getScheduledCheckOut())
                .status(BookingStatus.CONFIRMED)
                .notes(request.getNotes())
                .build();

        slot.setStatus(SlotStatus.RESERVED);
        slotRepository.save(slot);

        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDTO getBookingById(Long id) {
        return mapToResponse(findById(id));
    }

    @Override
    public BookingResponseDTO getBookingByRef(String ref) {
        return mapToResponse(bookingRepository.findByBookingRef(ref)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "ref", ref)));
    }

    @Override
    public Page<BookingResponseDTO> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public Page<BookingResponseDTO> getBookingsByUser(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public BookingResponseDTO checkIn(Long bookingId) {
        Booking booking = findById(bookingId);
        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Cannot check-in. Status: " + booking.getStatus());
        }
        booking.setActualCheckIn(LocalDateTime.now());
        booking.setStatus(BookingStatus.CHECKED_IN);

        ParkingSlot slot = booking.getSlot();
        slot.setStatus(SlotStatus.OCCUPIED);
        slotRepository.save(slot);

        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDTO checkOut(Long bookingId) {
        Booking booking = findById(bookingId);
        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new BadRequestException("Booking is not checked in. Status: " + booking.getStatus());
        }

        LocalDateTime checkOut = LocalDateTime.now();
        booking.setActualCheckOut(checkOut);
        booking.setStatus(BookingStatus.CHECKED_OUT);

        long minutes = Duration.between(booking.getActualCheckIn(), checkOut).toMinutes();
        long hours = Math.max(1, (long) Math.ceil(minutes / 60.0));
        BigDecimal rate = booking.getSlot().getLot().getHourlyRate();
        booking.setTotalAmount(rate.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP));

        ParkingSlot slot = booking.getSlot();
        slot.setStatus(SlotStatus.AVAILABLE);
        slotRepository.save(slot);

        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Long userId, Long bookingId) {
        Booking booking = findById(bookingId);
        if (!booking.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to cancel this booking");
        }
        if (booking.getStatus() == BookingStatus.CHECKED_IN || booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new BadRequestException("Cannot cancel a booking that is already checked-in or completed");
        }
        booking.setStatus(BookingStatus.CANCELLED);

        ParkingSlot slot = booking.getSlot();
        if (slot.getStatus() == SlotStatus.RESERVED) {
            slot.setStatus(SlotStatus.AVAILABLE);
            slotRepository.save(slot);
        }
        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void expireBookings() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(30);
        List<Booking> expired = bookingRepository.findExpiredPendingBookings(expiryTime);
        for (Booking booking : expired) {
            booking.setStatus(BookingStatus.EXPIRED);
            ParkingSlot slot = booking.getSlot();
            if (slot.getStatus() == SlotStatus.RESERVED) {
                slot.setStatus(SlotStatus.AVAILABLE);
                slotRepository.save(slot);
            }
            bookingRepository.save(booking);
        }
        if (!expired.isEmpty()) log.info("Expired {} stale bookings", expired.size());
    }

    private Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
    }

    private String generateRef() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BookingResponseDTO mapToResponse(Booking booking) {
        BookingResponseDTO response = modelMapper.map(booking, BookingResponseDTO.class);
        response.setUserId(booking.getUser().getId());
        response.setUserName(booking.getUser().getName());
        response.setVehicleId(booking.getVehicle().getId());
        response.setVehiclePlate(booking.getVehicle().getPlateNo());
        response.setSlotId(booking.getSlot().getId());
        response.setSlotNo(booking.getSlot().getSlotNo());
        response.setLotName(booking.getSlot().getLot().getName());
        return response;
    }
}
