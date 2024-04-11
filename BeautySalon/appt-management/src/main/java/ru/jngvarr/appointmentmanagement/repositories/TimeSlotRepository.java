package ru.jngvarr.appointmentmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.jngvarr.appointmentmanagement.model.TimeSlot;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    @Query("SELECT t FROM TimeSlot t WHERE t.startTime >= ?1 AND t.endTime <= ?2")
    List<TimeSlot> findBusySlots(LocalDateTime date);
}
