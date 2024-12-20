package jngvarr.ru.pto_ackye_rzhd.controllers;

import jngvarr.ru.pto_ackye_rzhd.entities.Meter;
import jngvarr.ru.pto_ackye_rzhd.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.services.MeterService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Data
@Slf4j
@RestController
@RequestMapping("/meters")
public class MeterController {
    private final MeterService meterService;

    @GetMapping
    public List<Meter> getAll() {
        return meterService.getAll();
    }

    @GetMapping("/{id}")
    public Meter getMeter(@PathVariable Long id) {
        return meterService.getMeter(id);
    }
}
