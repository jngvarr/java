package jngvarr.ru.pto_ackye_rzhd.services;

import jngvarr.ru.pto_ackye_rzhd.entities.*;
import jngvarr.ru.pto_ackye_rzhd.repositories.others.*;
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
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
//@Transactional
public class PtoService {
    private final MeterService meterService;
    private final DcService dcService;
    private final MeteringPointService meteringPointService;
    private final RegionRepository regionRepository;
    private final StructuralSubdivisionRepository subdivisionRepository;
    private final PowerSupplyEnterpriseRepository powerSupplyEnterpriseRepository;
    private final PowerSupplyDistrictRepository powerSupplyDistrictRepository;
    private final StationRepository stationRepository;
    private final SubstationService substationService;
    public static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter STRICT_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    .withResolverStyle(ResolverStyle.STRICT);

    public static final DateTimeFormatter FLEXIBLE_FORMATTER =
            DateTimeFormatter.ofPattern("d.M.yyyy")
                    .withResolverStyle(ResolverStyle.SMART);
    private final long startTime = System.currentTimeMillis();
    public Map<EntityType, Map<String, Object>> entityCache = new EnumMap<>(EntityType.class);
    private static final int CELL_NUMBER_REGION_NAME = 2;
    private static final int CELL_NUMBER_STATION_NAME = 6;
    private static final int CELL_NUMBER_POWER_SUPPLY_ENTERPRISE_NAME = 4;
    private static final int CELL_NUMBER_POWER_SUPPLY_DISTRICT_NAME = 5;
    private static final int CELL_NUMBER_STRUCTURAL_SUBDIVISION_NAME = 3;
    private static final int CELL_NUMBER_SUBSTATION_NAME = 7;
    private static final int CELL_NUMBER_BUS_SECTION_NUM = 8;
    private static final String DC_MODEL = "DC-1000/SL";
    private static final int CELL_NUMBER_DC_NUMBER = 14;
    //    private static final int CELL_NUMBER_DC_NUMBER = 10;
    private static final int CELL_NUMBER_DC_INSTALLATION_DATE = 11;
    private static final int CELL_NUMBER_METERING_POINT_ID = 1;
    private static final int CELL_NUMBER_METERING_POINT_CONNECTION = 8;
    private static final int CELL_NUMBER_METERING_POINT_NAME = 9;
    private static final int CELL_NUMBER_METERING_POINT_PLACEMENT = 10;
    private static final int CELL_NUMBER_METERING_POINT_ADDRESS = 11;
    private static final int CELL_NUMBER_METERING_POINT_METER_MODEL = 12;
    private static final int CELL_NUMBER_METERING_POINT_METER_NUMBER = 13;
    private static final int CELL_NUMBER_METERING_POINT_DC_NUMBER = 14;
    private static final int CELL_NUMBER_METERING_POINT_INSTALLATION_DATE = 15;


    public enum EntityType {
        SUBSTATION, STATION, POWER_SUPPLY_DISTRICT, POWER_SUPPLY_ENTERPRISE, STRUCTURAL_SUBDIVISION, REGION, METERING_POINT, METER, DC
    }

    public List<String> MeterType = Arrays.asList(
            "EM-1021", "EM-1023", "EM-2023", "KNUM-1021", "KNUM-1023", "KNUM-2023");


    public void addDataFromExcelFile(String dataFilePath) {
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(dataFilePath))
        ) {
            for (EntityType type : EntityType.values()) {
                entityCache.put(type, new HashMap<>());
            }
            fillDbWithData(planOTOWorkbook.getSheet("ИВКЭ"), planOTOWorkbook.getSheet("ИИК"));

            log.info("Data filled successfully!");

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Execution time: " + duration / 1000 + " seconds");
    }

    protected void fillDbWithData(Sheet ivkeSheet, Sheet iikSheet) {
        fillDbWithIvkeData(ivkeSheet);
        saveDcFromMap();
        fillDbWithIikData(iikSheet);
        saveDcFromMap();
    }

    private void saveDcFromMap() {
        for (
                Object dc : entityCache.get(EntityType.DC).values()) {
            dcService.createDc((Dc) dc);
        }
    }

    protected void fillDbWithIvkeData(Sheet sheet) {
        getAllDcMap(dcService.getAllDc());
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                // Пропускаем первую строку, это заголовок
                continue;
            }
            String dcNumber = getCellStringValue(row.getCell(CELL_NUMBER_DC_NUMBER));
            if (!entityCache.get(EntityType.DC).containsKey(dcNumber)) {
                Substation substation = createSubstationIfNotExists(row);
                createDc(substation, dcNumber, row);
            }
        }
    }

    private void createDc(Substation substation, String dcNumber, Row row) {
        Dc newDc = new Dc();
        newDc.setSubstation(substation);
        log.info("{}", row.getRowNum());
        String sectionNumber = getCellStringValue(row.getCell(CELL_NUMBER_BUS_SECTION_NUM));
        if (!sectionNumber.isBlank()) {
            newDc.setBusSection(Integer.parseInt(sectionNumber) == 2 ? 2 : 1);
        } else newDc.setBusSection(1);

        String installationDateStr = getCellStringValue(row.getCell(CELL_NUMBER_DC_INSTALLATION_DATE));
        if (installationDateStr != null && !installationDateStr.isBlank()) {
            newDc.setInstallationDate(LocalDate.parse(installationDateStr, DATE_FORMATTER_DDMMYYYY));
        }
//
//        newDc.setInstallationDate(LocalDate.parse(getCellStringValue(row.getCell(CELL_NUMBER_DC_INSTALLATION_DATE)),
//                DATE_FORMATTER_DDMMYYYY));
        newDc.setDcModel(DC_MODEL);
        newDc.setMeters(new ArrayList<>());
        newDc.setDcNumber(dcNumber);
        dcService.createDc(newDc);
        entityCache.get(EntityType.DC).putIfAbsent(dcNumber, newDc);
    }

    private Dc createVirtualDc(Substation substation, String dcNumber) {
        Dc newDc = new Dc();
        newDc.setSubstation(substation);
        newDc.setDcModel("Virtual");
        newDc.setMeters(new ArrayList<>());
        newDc.setDcNumber(dcNumber);
        dcService.createDc(newDc);
        entityCache.get(EntityType.DC).putIfAbsent(dcNumber, newDc);
        return newDc;
    }

    public Substation createSubstationIfNotExists(Row row) {

        String regionName = getCellStringValue(row.getCell(CELL_NUMBER_REGION_NAME));
        String subdivisionName = getCellStringValue(row.getCell(CELL_NUMBER_STRUCTURAL_SUBDIVISION_NAME));
        String powerSupplyEnterpriseName = getCellStringValue(row.getCell(CELL_NUMBER_POWER_SUPPLY_ENTERPRISE_NAME));
        String districtName = getCellStringValue(row.getCell(CELL_NUMBER_POWER_SUPPLY_DISTRICT_NAME));
        String stationName = getCellStringValue(row.getCell(CELL_NUMBER_STATION_NAME));
        String substationName = getCellStringValue(row.getCell(CELL_NUMBER_SUBSTATION_NAME));

        Region region = regionRepository.findByName(regionName)
                .orElseGet(() -> {
                    Region r = new Region();
                    r.setName(regionName);
                    log.info("Создан регион: {}", regionName);
                    return regionRepository.save(r);
                });

        StructuralSubdivision subdivision = subdivisionRepository.findByName(subdivisionName)
                .orElseGet(() -> {
                    StructuralSubdivision s = new StructuralSubdivision();
                    s.setName(subdivisionName);
                    s.setRegion(region);
                    log.info("Создано подразделение: {}", subdivisionName);
                    return subdivisionRepository.save(s);
                });

        PowerSupplyEnterprise enterprise = powerSupplyEnterpriseRepository.findByName(powerSupplyEnterpriseName)
                .orElseGet(() -> {
                    PowerSupplyEnterprise e = new PowerSupplyEnterprise();
                    e.setName(powerSupplyEnterpriseName);
                    e.setStructuralSubdivision(subdivision);
                    log.info("Создан участок электроснабжения: {}", powerSupplyEnterpriseName);
                    return powerSupplyEnterpriseRepository.save(e);
                });

        PowerSupplyDistrict district = powerSupplyDistrictRepository.findByName(districtName)
                .orElseGet(() -> {
                    PowerSupplyDistrict d = new PowerSupplyDistrict();
                    d.setName(districtName);
                    d.setPowerSupplyEnterprise(enterprise);
                    log.info("Создан район электроснабжения: {} (предприятие: {})", districtName, powerSupplyEnterpriseName);
                    return powerSupplyDistrictRepository.save(d);
                });

        Station station = stationRepository.findByNameAndPowerSupplyDistrict(stationName, district)
                .orElseGet(() -> {
                    Station s = new Station();
                    s.setName(stationName);
                    s.setPowerSupplyDistrict(district);
                    log.info("Создана станция: {} (район: {})", stationName, districtName);
                    return stationRepository.save(s);
                });

        return substationService.findByName(substationName, station.getName())
                .orElseGet(() -> {
                    Substation s = new Substation();
                    s.setName(substationName);
                    s.setStation(station);
                    log.info("Создана подстанция: {} (станция: {})", substationName, stationName);
                    return substationService.create(s);
                });
    }


    protected void fillDbWithIikData(Sheet sheet) {
        Map<Long, MeteringPoint> meteringPoints = new HashMap<>();
        for (MeteringPoint mp : meteringPointService.getAllIik()) {
            meteringPoints.put(mp.getId(), mp);
        }

        Map<String, Meter> meters = new HashMap<>();
        for (Meter m : meterService.getAllMeters()) {
            meters.put(m.getMeterNumber(), m);
        }

        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            Meter newMeter = constructMeter(row);
            MeteringPoint newIik = createIIk(row);
//            if (newMeter == null || newIik == null) {
//                log.warn("Не удалось создать Meter или MeteringPoint из строки {}", row.getRowNum() + 1);
//                continue;
//            }

            if (!meteringPoints.containsKey(newIik.getId())) {
                String dcNumber = getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_DC_NUMBER));
                if (dcNumber == null) {
                    if (newIik.getMeteringPointAddress() == null) newIik.setMeteringPointAddress("");
                } else {
                    Dc dcByNumber = dcService.getDcByNumber(dcNumber);
                    if (dcByNumber == null && !dcNumber.isBlank()) {
                        dcByNumber = createVirtualDc(newIik.getSubstation(), dcNumber);
                    }

                    if (dcByNumber != null) {
                        newMeter.setDc(dcByNumber);
                        if (!meters.containsKey(newMeter.getMeterNumber())) {
                            newMeter = meterService.create(newMeter);
                        }
//                    newMeter = meterService.getMeterByNumber(newMeter.getMeterNumber());
                        dcByNumber.getMeters().add(newMeter);
                        entityCache.get(EntityType.DC).put(dcByNumber.getDcNumber(), dcByNumber);
                    }
                }
                newIik.setMeter(newMeter);
                meteringPoints.put(newIik.getId(), newIik);
            }
        }
        for (MeteringPoint point : meteringPoints.values()) {
            meteringPointService.create(point);
        }
    }

    public Meter constructMeter(Row row) {
        String meterNumber = getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_METER_NUMBER));
        String meterModel = getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_METER_MODEL));
        String dcNum = getCellStringValue(row.getCell(CELL_NUMBER_DC_NUMBER));

        // Dc ищем либо в карте, либо в сервисе
        Dc foundDc = Optional.ofNullable((Dc) entityCache.get(EntityType.DC).get(dcNum))
                .orElseGet(() -> dcService.getDcByNumber(dcNum));

        return Optional.ofNullable(meterNumber)
                .filter(num -> !num.isBlank())                // проверка, что номер не пустой
                .filter(num -> meterModel != null && !meterModel.isBlank()) // проверка модели
                .map(num -> {
                    Meter meter = new Meter();
                    meter.setMeterNumber(num);
                    meter.setMeterModel(meterModel);
                    meter.setDc(foundDc);
                    return meter;
                })
                .orElse(null); // если номер или модель не прошли проверки — null
    }

    public Meter constructMeter(String meterNum, String meterModel, String dcNum) {
        Dc foundDc = dcService.getDcByNumber(dcNum);
        Meter meter = new Meter();
        meter.setMeterNumber(meterNum);
        meter.setMeterModel(meterModel);
        foundDc.getMeters().add(meter);
        meter.setDc(foundDc);
        return meter;
    }

    public Meter getOrCreateMeter(String mountingMeterNumber, String meterType, String dcNum) {

       return Optional.ofNullable(
                        meterService.getMeterByNumber(mountingMeterNumber)
                )
                .orElseGet(() -> {
                    Meter created = constructMeter(mountingMeterNumber, meterType, dcNum);
                    return meterService.create(created);
                });
    }

    private MeteringPoint createIIk(Row row) {
        String mapKey = getStringMapKey(row);

        Substation substation = (Substation) entityCache.get(EntityType.SUBSTATION).get(mapKey);

        if (substation == null) {
            substation = createSubstationIfNotExists(row);
            entityCache.get(EntityType.SUBSTATION).put(mapKey, substation);
        }

        Substation finalSubstation = substation;
        MeteringPoint newMeteringPoint = new MeteringPoint();

        newMeteringPoint.setSubstation(finalSubstation);
        newMeteringPoint.setId(Long.parseLong(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_ID))));
        newMeteringPoint.setConnection(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_CONNECTION)));
        newMeteringPoint.setName(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_NAME)));
        newMeteringPoint.setMeterPlacement(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_PLACEMENT)));
        newMeteringPoint.setMeteringPointAddress(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_ADDRESS)));

        String installationDateStr = getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_INSTALLATION_DATE));
        if (installationDateStr != null && !installationDateStr.isBlank()) {
            newMeteringPoint.setInstallationDate(LocalDate.parse(installationDateStr, DATE_FORMATTER_DDMMYYYY));
        }
        return newMeteringPoint;
    }

    private String getStringMapKey(Row row) {
        return new StringBuilder()
                .append(getCellStringValue(row.getCell(2)))
                .append("_")
                .append(getCellStringValue(row.getCell(3)))
                .append("_")
                .append(getCellStringValue(row.getCell(4)))
                .append("_")
                .append(getCellStringValue(row.getCell(5)))
                .append("_")
                .append(getCellStringValue(row.getCell(6)))
                .append("_")
                .append(getCellStringValue(row.getCell(7)))
                .toString();
    }

    private static String getCellStringValue(Cell cell) {
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return new SimpleDateFormat("dd.MM.yyyy").format(cell.getDateCellValue());
                    } else {
                        return new DecimalFormat("0").format(cell.getNumericCellValue());
                    }
                case FORMULA:
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    return evaluator.evaluate(cell).getStringValue();
                default:
                    return null;
            }
        }
        return null;
    }

    private String buildSubstationMapKey(Substation substation) {
        return new StringBuilder().
                append(substation.getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getStructuralSubdivision().getRegion().getName()).
                append("_").
                append(substation.getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getStructuralSubdivision().getName()).
                append("_").
                append(substation.getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getName()).
                append("_").
                append(substation.getStation().getPowerSupplyDistrict().getName()).
                append("_").
                append(substation.getStation().getName()).
                append("_").
                append(substation.getName()).toString();

    }


    protected Dc addMeterToDc(Meter meter, String dcNum) {
        if (entityCache.get(EntityType.DC).containsKey(dcNum)) {
            Dc dc = dcService.getDcByNumber(dcNum);
            dc.getMeters().add(meter);
            return dc;
        }
        return null;
    }

    private void getAllDcMap(List<Dc> dcs) {
        for (Dc dc : dcs) {
            entityCache.get(EntityType.DC).put(dc.getDcNumber(), dc);
        }
    }
}
