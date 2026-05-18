package com.example.smart_car_park.service;

import com.example.smart_car_park.models.dto.response.ParkingSlotResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlotNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    /** Pushes a real-time slot update to Angular clients subscribed to /topic/slots/{lotId} */
    public void notifySlotUpdate(Long lotId, ParkingSlotResponseDTO slot) {
        String destination = "/topic/slots/" + lotId;
        messagingTemplate.convertAndSend(destination, slot);
        log.debug("Pushed slot update -> {} slot={}", destination, slot.getSlotNo());
    }
}
