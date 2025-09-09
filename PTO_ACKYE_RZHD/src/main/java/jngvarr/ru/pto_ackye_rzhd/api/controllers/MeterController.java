package jngvarr.ru.pto_ackye_rzhd.api.controllers;

import jngvarr.ru.pto_ackye_rzhd.domain.dto.MeterDTO;
import jngvarr.ru.pto_ackye_rzhd.domain.services.MeterService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@Slf4j
@RestController
@RequestMapping("/meters")
public class MeterController {
    private final MeterService meterService;

    @GetMapping
    public List<MeterDTO> getAll() {

        return meterService.getAll();
    }

    @GetMapping("/{id}")
    public MeterDTO getMeter(@PathVariable Long id) {
        return meterService.getMeterById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public MeterDTO createMeter(@RequestBody MeterDTO meter) {
        return meterService.create(meter);
    }

    @PutMapping("/update/{id}")
    public MeterDTO updateMeter(@RequestBody MeterDTO meter, @PathVariable Long id){
        return meterService.updateMeter(meter, id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteMeter(@PathVariable Long id){meterService.delete(id);
    }
}


