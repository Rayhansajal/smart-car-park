package com.example.smart_car_park.service.impl;

import com.example.smart_car_park.exception.ResourceNotFoundException;
import com.example.smart_car_park.models.dto.request.ParkingLotRequestDTO;
import com.example.smart_car_park.models.dto.response.ParkingLotResponseDTO;
import com.example.smart_car_park.models.entity.ParkingLot;
import com.example.smart_car_park.models.enums.SlotStatus;
import com.example.smart_car_park.repository.ParkingLotRepository;
import com.example.smart_car_park.repository.ParkingSlotRepository;
import com.example.smart_car_park.service.ParkingLotService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ParkingLotServiceImpl implements ParkingLotService {
    private final ParkingLotRepository lotRepository;
    private final ParkingSlotRepository slotRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ParkingLotResponseDTO createLot(ParkingLotRequestDTO request) {
        ParkingLot lot = modelMapper.map(request, ParkingLot.class);
        lot.setId(null);
        return mapToResponse(lotRepository.save(lot));
    }

    @Override
    @Transactional
    public ParkingLotResponseDTO updateLot(Long id, ParkingLotRequestDTO request) {
        ParkingLot lot = findById(id);
        modelMapper.map(request, lot);
        lot.setId(id);
        return mapToResponse(lotRepository.save(lot));
    }

    @Override
    @Transactional
    public void deleteLot(Long id) {
        if (!lotRepository.existsById(id)) {
            throw new ResourceNotFoundException("ParkingLot", "id", id);
        }
        lotRepository.deleteById(id);
    }

    @Override
    public ParkingLotResponseDTO getLotById(Long id) {
        return mapToResponse(findById(id));
    }

    @Override
    public Page<ParkingLotResponseDTO> getAllLots(Pageable pageable) {
        return lotRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public List<ParkingLotResponseDTO> getActiveLots() {
        return lotRepository.findByEnabledTrue()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public Page<ParkingLotResponseDTO> searchLots(String query, Pageable pageable) {
        return lotRepository.searchLots(query, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void toggleLotStatus(Long id) {
        ParkingLot lot = findById(id);
        lot.setEnabled(!lot.isEnabled());
        lotRepository.save(lot);
    }

    private ParkingLot findById(Long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ParkingLot", "id", id));
    }

    private ParkingLotResponseDTO mapToResponse(ParkingLot lot) {
        ParkingLotResponseDTO response = modelMapper.map(lot, ParkingLotResponseDTO.class);
        long total = slotRepository.countByLotId(lot.getId());
        long available = slotRepository.countByLotIdAndStatus(lot.getId(), SlotStatus.AVAILABLE);
        response.setTotalSlots((int) total);
        response.setAvailableSlots((int) available);
        return response;
    }

}
