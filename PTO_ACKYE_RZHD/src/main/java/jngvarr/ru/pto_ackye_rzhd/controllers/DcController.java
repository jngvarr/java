package jngvarr.ru.pto_ackye_rzhd.controllers;

import jngvarr.ru.pto_ackye_rzhd.dto.DcDTO;
import jngvarr.ru.pto_ackye_rzhd.dto.MeterDTO;
import jngvarr.ru.pto_ackye_rzhd.entities.Dc;
import jngvarr.ru.pto_ackye_rzhd.repositories.DcRepository;
import jngvarr.ru.pto_ackye_rzhd.services.DcService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@Slf4j
@RestController
@RequestMapping("/dc")
public class DcController {

    private final DcService service;

    @GetMapping
    public List<DcDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/byId/{id}")
    public DcDTO getDcById(@PathVariable Long id) {
        return service.getDcById(id);
    }

    @GetMapping("/byDcNum/{num}")
    public DcDTO getDcDTOByNumber(@PathVariable String num) {
        return service.getDcDTOByNumber(num);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public DcDTO createDc(@RequestBody DcDTO dc) {
        return service.createDc(dc);
    }

    @PutMapping("/update/{id}")
    public DcDTO updateMeter(@RequestBody DcDTO dc, @PathVariable Long id) {
        return service.updateDc(dc, id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteMeter(@PathVariable Long id) {
        service.delete(id);
    }
}
