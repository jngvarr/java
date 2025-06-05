package jngvarr.ru.pto_ackye_rzhd.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jngvarr.ru.pto_ackye_rzhd.entities.*;
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
import java.util.*;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class PtoService {
    @PersistenceContext
    private EntityManager em;
    private final DcService dcService;
    private final MeteringPointService service;
    private final SubstationService substationService;
    private static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final long startTime = System.currentTimeMillis();
    private static final Map<String, Dc> DC_MAP = new HashMap<>();
    private final Map<String, Substation> SUBSTATION_MAP = new HashMap<>();
    private final Map<EntityType, Map<String, Object>> entityCache = new EnumMap<>(EntityType.class);
    private static final int CELL_NUMBER_REGION_NAME = 1;
    private static final int CELL_NUMBER_STATION_NAME = 2;
    private static final int CELL_NUMBER_POWER_SUPPLY_ENTERPRISE_NAME = 3;
    private static final int CELL_NUMBER_POWER_SUPPLY_DISTRICT_NAME = 4;
    private static final int CELL_NUMBER_STRUCTURAL_SUBDIVISION_NAME = 5;
    private static final int CELL_NUMBER_SUBSTATION_NAME = 6;
    private static final int CELL_NUMBER_BUS_SECTION_NUM = 7;
    private static final String DC_MODEL = "DC-1000/SL";
    private static final int CELL_NUMBER_DC_NUMBER = 9;
    private static final int CELL_NUMBER_DC_INSTALLATION_DATE = 10;
    private static final int CELL_NUMBER_METERING_POINT_ID = 1;
    private static final int CELL_NUMBER_METERING_POINT_CONNECTION = 8;
    private static final int CELL_NUMBER_METERING_POINT_NAME = 9;
    private static final int CELL_NUMBER_METERING_POINT_PLACEMENT = 10;
    private static final int CELL_NUMBER_METERING_POINT_ADDRESS = 11;
    private static final int CELL_NUMBER_METERING_POINT_METER_NUMBER = 12;
    private static final int CELL_NUMBER_METERING_POINT_METER_MODEL = 13;
    private static final int CELL_NUMBER_METERING_POINT_DC_NUMBER = 14;
    private static final int CELL_NUMBER_METERING_POINT_INSTALLATION_DATE = 15;


    private enum EntityType {
        SUBSTATION, STATION, POWER_SUPPLY_DISTRICT, POWER_SUPPLY_ENTERPRISE, STRUCTURAL_SUBDIVISION, REGION
    }

    public void addDataFromExcelFile(String dataFilePath) {
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(dataFilePath))
        ) {
            for (EntityType type : EntityType.values()) {
                entityCache.put(type, new HashMap<>());
            }
            fillDbWithIvkeData(planOTOWorkbook.getSheet("ИВКЭ"));
            fillDbWithIikData(planOTOWorkbook.getSheet("ИИК"));
//            planOTOWorkbook.close();

            log.info("Data filled successfully!");

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Execution time: " + duration / 1000 + " seconds");
    }

    @Transactional
    protected void fillDbWithIvkeData(Sheet sheet) {

        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                // Пропускаем первую строку, это заголовок
                continue;
            }

            String dcNumber = getCellStringValue(row.getCell(CELL_NUMBER_DC_NUMBER));
            if (!DC_MAP.containsKey(dcNumber)) {
                Substation substation = createSubstationIfNotExists(row);
                Dc newIvke = createDc(substation, dcNumber, row);
                dcService.createDc(newIvke);
                DC_MAP.putIfAbsent(dcNumber, newIvke);
            }
        }
//        for (
//                Dc dc : DC_MAP.values()) {
//            ;
//        }
    }

    private Dc createDc(Substation substation, String dcNumber, Row row) {
        return new Dc() {{
            setSubstation(substation);
            setBusSection(Integer.parseInt(getCellStringValue(row.getCell(CELL_NUMBER_BUS_SECTION_NUM))) == 2 ? 2 : 1);
            setInstallationDate(LocalDate.parse(getCellStringValue(row.getCell(CELL_NUMBER_DC_INSTALLATION_DATE)),
                    DATE_FORMATTER_DDMMYYYY));
            setDcModel(DC_MODEL);
            setMeters(new ArrayList<>());
            setDcNumber(dcNumber);
        }};
    }

    @Transactional
    public Substation createSubstationIfNotExists(Row row) {

        String regionName = getCellStringValue(row.getCell(CELL_NUMBER_REGION_NAME));
        String subdivisionName = getCellStringValue(row.getCell(CELL_NUMBER_STRUCTURAL_SUBDIVISION_NAME));
        String powerSupplyEnterpriseName = getCellStringValue(row.getCell(CELL_NUMBER_POWER_SUPPLY_ENTERPRISE_NAME));
        String districtName = getCellStringValue(row.getCell(CELL_NUMBER_POWER_SUPPLY_DISTRICT_NAME));
        String stationName = getCellStringValue(row.getCell(CELL_NUMBER_STATION_NAME));
        String substationName = getCellStringValue(row.getCell(CELL_NUMBER_SUBSTATION_NAME));

        Region region = (Region) findOrCreateEntity(
                EntityType.REGION, regionName,
                "SELECT r FROM Region r WHERE r.name = :name",
                Map.of("name", regionName),
                () -> {
                    Region r = new Region();
                    r.setName(regionName);
                    em.persist(r);
                    log.info("Создан регион: {}", regionName);
                    return r;
                });

        StructuralSubdivision subdivision = (StructuralSubdivision) findOrCreateEntity(
                EntityType.STRUCTURAL_SUBDIVISION, subdivisionName + "_" + region.getName(),
                "SELECT s FROM StructuralSubdivision s WHERE s.name = :name AND s.region = :region",
                Map.of("name", subdivisionName, "region", region),
                () -> {
                    StructuralSubdivision s = new StructuralSubdivision();
                    s.setName(subdivisionName);
                    s.setRegion(region);
                    em.persist(s);
                    log.info("Создан линейный отдел{} (регион: {})", subdivisionName, regionName);
                    return s;
                });

        PowerSupplyEnterprise enterprise = (PowerSupplyEnterprise) findOrCreateEntity(
                EntityType.POWER_SUPPLY_ENTERPRISE, powerSupplyEnterpriseName + "_" + subdivision.getName(),
                "SELECT e FROM PowerSupplyEnterprise e WHERE e.name = :name AND e.structuralSubdivision = :subdivision",
                Map.of("name", powerSupplyEnterpriseName, "subdivision", subdivision),
                () -> {
                    PowerSupplyEnterprise e = new PowerSupplyEnterprise();
                    e.setName(powerSupplyEnterpriseName);
                    e.setStructuralSubdivision(subdivision);
                    em.persist(e);
                    log.info("Создано дистанция электроснабжения: {} (подразделение: {})", powerSupplyEnterpriseName, subdivisionName);
                    return e;
                });

        PowerSupplyDistrict district = (PowerSupplyDistrict) findOrCreateEntity(
                EntityType.POWER_SUPPLY_DISTRICT, districtName + "_" + enterprise.getName(),
                "SELECT d FROM PowerSupplyDistrict d WHERE d.name = :name AND d.powerSupplyEnterprise = :enterprise",
                Map.of("name", districtName, "enterprise", enterprise),
                () -> {
                    PowerSupplyDistrict d = new PowerSupplyDistrict();
                    d.setName(districtName);
                    d.setPowerSupplyEnterprise(enterprise);
                    em.persist(d);
                    log.info("Создан район электроснабжения: {} (предприятие: {})", districtName, powerSupplyEnterpriseName);
                    return d;
                });

        Station station = (Station) findOrCreateEntity(
                EntityType.STATION, stationName + "_" + district.getName(),
                "SELECT s FROM Station s WHERE s.name = :name AND s.powerSupplyDistrict = :district",
                Map.of("name", stationName, "district", district),
                () -> {
                    Station s = new Station();
                    s.setName(stationName);
                    s.setPowerSupplyDistrict(district);
                    em.persist(s);
                    log.info("Создана станция: {} (район: {})", stationName, districtName);
                    return s;
                });

        return (Substation) findOrCreateEntity(
                EntityType.SUBSTATION, substationName + "_" + station.getName(),
                "SELECT s FROM Substation s WHERE s.name = :name AND s.station = :station",
                Map.of("name", substationName, "station", station),
                () -> {
                    Substation s = new Substation();
                    s.setName(substationName);
                    s.setStation(station);
                    em.persist(s);
                    log.info("Создана подстанция: {} (станция: {})", substationName, stationName);
                    return s;
                });
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

    @Transactional
    protected Object findOrCreateEntity(EntityType type,
                                        String cacheKey,
                                        String jpql,
                                        Map<String, Object> params,
                                        Supplier<Object> createSupplier) {
        entityCache.putIfAbsent(type, new HashMap<>());
        Map<String, Object> map = entityCache.get(type);

        return map.computeIfAbsent(cacheKey, key -> {
            TypedQuery<Object> query = em.createQuery(jpql, Object.class);
            params.forEach(query::setParameter);

            return query.getResultStream()
                    .findFirst()
                    .orElseGet(createSupplier);
        });
    }

    private void fillDbWithIikData(Sheet sheet) {
        Map<Long, MeteringPoint> meteringPoints = new HashMap<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            Meter newMeter = createMeter(row);
            MeteringPoint newIik = createIIk(row);

            String dcNumber = getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_DC_NUMBER));
            Dc dcToAdd = addMeterToDc(newMeter, dcNumber);
            newIik.setMeter(newMeter);
            newMeter.setDc(dcToAdd);
            meteringPoints.put(newIik.getId(), newIik);
        }
        for (MeteringPoint point : meteringPoints.values()) {
            service.create(point);
        }
    }

    private Meter createMeter(Row row) {
        String meterNumber = getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_METER_NUMBER));
        String meterModel = getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_METER_MODEL));

        return Optional.ofNullable(meterNumber)
                .filter(meterNum -> !meterNum.isBlank())
                .map(meterNum -> {
                    if (meterModel == null || meterModel.isBlank()) {
                        return null;
                    }
                    Meter meter = new Meter();
                    meter.setMeterNumber(meterNumber);
                    meter.setMeterModel(meterModel);
                    return meter;
                })
                .orElse(null);
    }

    private MeteringPoint createIIk(Row row) {
        String mapKey = getStringMapKey(row);

        Substation substation = (Substation) entityCache.get(EntityType.SUBSTATION).get(mapKey);

        if (substation == null) {
            substation = createSubstationIfNotExists(row);
            entityCache.get(EntityType.SUBSTATION).put(mapKey, substation);
        }

        Substation finalSubstation = substation;

        return new MeteringPoint() {{
            setSubstation(finalSubstation);
            setId(Long.parseLong(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_ID))));
            setConnection(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_CONNECTION)));
            setName(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_NAME)));
            setMeterPlacement(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_PLACEMENT)));
            setMeteringPointAddress(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_ADDRESS)));
            setInstallationDate(LocalDate.parse(
                    getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_INSTALLATION_DATE)),
                    DATE_FORMATTER_DDMMYYYY
            ));
        }};
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

    private Dc addMeterToDc(Meter meter, String dcNum) {
        if (DC_MAP.containsKey(dcNum)) {
            Dc dc = DC_MAP.get(dcNum);
            dc.getMeters().add(meter);
            return dc;
        }
        return null;
    }
}
