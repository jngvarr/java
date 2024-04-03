package ru.jngvarr.appointmentmanagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.jngvarr.appointmentmanagement.feign_clients.ServiceFeignClient;
import ru.jngvarr.appointmentmanagement.model.TimeSlot;
import ru.jngvarr.appointmentmanagement.repositories.TimeSlotRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeAvailabilityService {
    private final TimeSlotRepository timeSlotRepository;
    private final ServiceFeignClient serviceFeignClient;

    public boolean isTimeAvailable(LocalDateTime date, LocalDateTime startTime, LocalDateTime endTime) {
        List<TimeSlot> busySlots = timeSlotRepository.findBusySlots(date);

        for (TimeSlot slot : busySlots) {
            if (startTime.isAfter(slot.getStartTime()) && startTime.isBefore(slot.getEndTime()) ||
                    endTime.isAfter(slot.getStartTime()) && endTime.isBefore(slot.getEndTime()) ||
                    startTime.isBefore(slot.getStartTime()) && endTime.isAfter(slot.getEndTime())) {
                // Если хотя бы одно из условий выполняется, то временной интервал перекрывается с занятым слотом
                return false; // Время занято
            }
        timeSlotRepository.save(slot);
        }
        return true; // Время свободно
    }
}
