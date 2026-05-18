package com.example.smart_car_park.service.impl;

import com.example.smart_car_park.exception.BadRequestException;
import com.example.smart_car_park.exception.ResourceNotFoundException;
import com.example.smart_car_park.models.dto.request.ParkingSlotRequestDTO;
import com.example.smart_car_park.models.dto.response.ParkingSlotResponseDTO;
import com.example.smart_car_park.models.entity.ParkingLot;
import com.example.smart_car_park.models.entity.ParkingSlot;
import com.example.smart_car_park.models.enums.SlotStatus;
import com.example.smart_car_park.models.enums.VehicleType;
import com.example.smart_car_park.repository.ParkingLotRepository;
import com.example.smart_car_park.repository.ParkingSlotRepository;
import com.example.smart_car_park.service.ParkingSlotService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ParkingSlotServiceImpl implements ParkingSlotService {
    private final ParkingSlotRepository slotRepository;
    private final ParkingLotRepository lotRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ParkingSlotResponseDTO createSlot(ParkingSlotRequestDTO request) {
        ParkingLot lot = findLotById(request.getLotId());

        if (slotRepository.existsByLotIdAndSlotNo(request.getLotId(), request.getSlotNo())) {
            throw new BadRequestException("Slot number already exists in this lot: " + request.getSlotNo());
        }

        ParkingSlot slot = ParkingSlot.builder()
                .lot(lot)
                .slotNo(request.getSlotNo())
                .floor(request.getFloor())
                .zone(request.getZone())
                .slotType(request.getSlotType())
                .status(request.getStatus() != null ? request.getStatus() : SlotStatus.AVAILABLE)
                .build();

        return mapToResponse(slotRepository.save(slot));
    }

    @Override
    @Transactional
    public ParkingSlotResponseDTO updateSlot(Long id, ParkingSlotRequestDTO request) {
        ParkingSlot slot = findSlotById(id);
        ParkingLot lot = findLotById(request.getLotId());

        if (!slot.getSlotNo().equals(request.getSlotNo())
                && slotRepository.existsByLotIdAndSlotNo(request.getLotId(), request.getSlotNo())) {
            throw new BadRequestException("Slot number already exists: " + request.getSlotNo());
        }

        slot.setLot(lot);
        slot.setSlotNo(request.getSlotNo());
        slot.setFloor(request.getFloor());
        slot.setZone(request.getZone());
        slot.setSlotType(request.getSlotType());
        if (request.getStatus() != null) {
            slot.setStatus(request.getStatus());
        }

        return mapToResponse(slotRepository.save(slot));
    }

    @Override
    @Transactional
    public void deleteSlot(Long id) {
        if (!slotRepository.existsById(id)) {
            throw new ResourceNotFoundException("ParkingSlot", "id", id);
        }
        slotRepository.deleteById(id);
    }

    @Override
    public ParkingSlotResponseDTO getSlotById(Long id) {
        return mapToResponse(findSlotById(id));
    }

    @Override
    public Page<ParkingSlotResponseDTO> getSlotsByLot(Long lotId, Pageable pageable) {
        return slotRepository.findByLotId(lotId, pageable).map(this::mapToResponse);
    }

    @Override
    public List<ParkingSlotResponseDTO> getAvailableSlots(Long lotId, VehicleType type) {
        List<ParkingSlot> slots = (type != null)
                ? slotRepository.findAvailableSlots(lotId, type)
                : slotRepository.findByLotIdAndStatus(lotId, SlotStatus.AVAILABLE);
        return slots.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateSlotStatus(Long id, SlotStatus status) {
        ParkingSlot slot = findSlotById(id);
        slot.setStatus(status);
        slotRepository.save(slot);
    }

    @Override
    @Transactional
    public void bulkCreateSlots(Long lotId, int count, VehicleType type, int floor, String zone) {
        ParkingLot lot = findLotById(lotId);
        List<ParkingSlot> slots = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            String slotNo = String.format("%s-%d-%02d", zone != null ? zone : "S", floor, i);
            if (!slotRepository.existsByLotIdAndSlotNo(lotId, slotNo)) {
                slots.add(ParkingSlot.builder()
                        .lot(lot).slotNo(slotNo).floor(floor).zone(zone)
                        .slotType(type).status(SlotStatus.AVAILABLE).build());
            }
        }
        slotRepository.saveAll(slots);
    }

    private ParkingSlot findSlotById(Long id) {
        return slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ParkingSlot", "id", id));
    }

    private ParkingLot findLotById(Long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ParkingLot", "id", id));
    }

    private ParkingSlotResponseDTO mapToResponse(ParkingSlot slot) {
        ParkingSlotResponseDTO response = modelMapper.map(slot, ParkingSlotResponseDTO.class);
        response.setLotId(slot.getLot().getId());
        response.setLotName(slot.getLot().getName());
        return response;
    }
}
