package jngvarr.ru.pto_ackye_rzhd.controllers;

import jakarta.ws.rs.DELETE;
import jngvarr.ru.pto_ackye_rzhd.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.services.MeteringPointService;
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
    private final MeteringPointService meteringPointservice;

    @GetMapping
    public List<MeteringPoint> getAll() {
        return meteringPointservice.getAll();
    }

    @GetMapping("/{id}")
    public MeteringPoint getMeteringPoint(@PathVariable Long id) {
        return meteringPointservice.getIik(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public MeteringPoint createMeteringPoint(@RequestBody MeteringPoint iik) {
        return meteringPointservice.create(iik);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/createPack")
    public List<MeteringPoint> createSomeMeteringPoints(@RequestBody List<MeteringPoint> iiks) {
        return meteringPointservice.createIiks  (iiks);
    }

    @PutMapping("/update/{id}")
    public MeteringPoint updateMeteringPoint(@RequestBody MeteringPoint newData, @PathVariable Long id) {
        return meteringPointservice.update(newData, id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteMeteringPoint(@PathVariable Long id){
        meteringPointservice.delete(id);
    }
}
