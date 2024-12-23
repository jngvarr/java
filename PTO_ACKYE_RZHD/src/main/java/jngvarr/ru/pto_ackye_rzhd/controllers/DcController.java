package jngvarr.ru.pto_ackye_rzhd.controllers;

import jngvarr.ru.pto_ackye_rzhd.dto.DcDTO;
import jngvarr.ru.pto_ackye_rzhd.dto.MeterDTO;
import jngvarr.ru.pto_ackye_rzhd.repositories.DcRepository;
import jngvarr.ru.pto_ackye_rzhd.services.DcService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Data
@Slf4j
@RestController
@RequestMapping
public class DcController {

    private final DcService service;

    @GetMapping
    public List<DcDTO> getAll() {
        return service.getAll();
    }
}
