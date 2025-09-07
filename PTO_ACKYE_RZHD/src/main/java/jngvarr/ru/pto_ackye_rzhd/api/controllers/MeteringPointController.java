package jngvarr.ru.pto_ackye_rzhd.api.controllers;

import jngvarr.ru.pto_ackye_rzhd.dto.MeteringPointDTO;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.domain.services.MeteringPointService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@Slf4j
@RestController
@RequestMapping("/iiks")
public class MeteringPointController {
    private final MeteringPointService meteringPointService;

    @GetMapping
    public List<MeteringPointDTO> getAll() {
        return meteringPointService.getAll();
    }

    @GetMapping("/{id}")
    public MeteringPointDTO getMeteringPoint(@PathVariable Long id) {
        return meteringPointService.getIik(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public MeteringPointDTO createMeteringPoint(@RequestBody MeteringPointDTO iik) {
        return meteringPointService.create(iik);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/createPack")
    public List<MeteringPoint> createSomeMeteringPoints(@RequestBody List<MeteringPoint> iiks) {
        return meteringPointService.createIiks  (iiks);
    }

    @PutMapping("/update/{id}")
    public MeteringPoint updateMeteringPoint(@RequestBody MeteringPoint newData, @PathVariable Long id) {
        return meteringPointService.update(newData, id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteMeteringPoint(@PathVariable Long id){
        meteringPointService.delete(id);
    }
}
