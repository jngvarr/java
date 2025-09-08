package jngvarr.ru.pto_ackye_rzhd.telegram;

import jngvarr.ru.pto_ackye_rzhd.application.services.ExcelFileService;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.*;
import jngvarr.ru.pto_ackye_rzhd.domain.repositories.others.*;
import jngvarr.ru.pto_ackye_rzhd.domain.services.*;
import jngvarr.ru.pto_ackye_rzhd.domain.value.EntityType;
import jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static jngvarr.ru.pto_ackye_rzhd.application.util.DateUtils.DATE_FORMATTER_DDMMYYYY;

@Slf4j
@Component
@RequiredArgsConstructor
//@Transactional
public class TBotService {
    private final MeterService meterService;
    private final DcService dcService;
    private final ExcelFileService excelFileService;
    private final MeteringPointService meteringPointService;
    private final RegionRepository regionRepository;
    private final StructuralSubdivisionRepository subdivisionRepository;
    private final PowerSupplyEnterpriseRepository powerSupplyEnterpriseRepository;
    private final PowerSupplyDistrictRepository powerSupplyDistrictRepository;
    private final StationRepository stationRepository;
    private final SubstationService substationService;

}
