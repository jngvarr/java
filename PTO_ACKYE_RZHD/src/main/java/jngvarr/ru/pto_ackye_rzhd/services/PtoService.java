package jngvarr.ru.pto_ackye_rzhd.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private static final Map<String, Substation> SUBSTATION_MAP = new HashMap<>();
    private static final Map<EntityType, Map<String, Object>> entityMaps = new EnumMap<>(EntityType.class);
    private static final Map<String, Region> REGION_MAP = new HashMap<>();
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
        for (EntityType type : EntityType.values()) {
            entityMaps.put(type, new HashMap<>());
        }


        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                // Пропускаем первую строку, это заголовок
                continue;
            }
            String regionName = getCellStringValue(row.getCell(CELL_NUMBER_REGION_NAME));
            String subdivisionName = getCellStringValue(row.getCell(CELL_NUMBER_STRUCTURAL_SUBDIVISION_NAME));
            String powerSupplyEnterpriseName = getCellStringValue(row.getCell(CELL_NUMBER_POWER_SUPPLY_ENTERPRISE_NAME));
            String districtName = getCellStringValue(row.getCell(CELL_NUMBER_POWER_SUPPLY_DISTRICT_NAME));
            String stationName = getCellStringValue(row.getCell(CELL_NUMBER_STATION_NAME));
            String substationName = getCellStringValue(row.getCell(CELL_NUMBER_SUBSTATION_NAME));
            String dcNumber = getCellStringValue(row.getCell(CELL_NUMBER_DC_NUMBER));
            Substation substation = createSubstationIfNotExists(regionName, subdivisionName, powerSupplyEnterpriseName, districtName, stationName, substationName);

            Dc newIvke = createDc(substation, dcNumber, row);

            DC_MAP.putIfAbsent(dcNumber, newIvke);
        }
        for (
                Dc dc : DC_MAP.values()) {
            dcService.createDc(dc);
        }
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

    public Substation createSubstationIfNotExists(String regionName,
                                                  String subdivisionName,
                                                  String powerSupplyEnterpriseName,
                                                  String districtName,
                                                  String stationName,
                                                  String substationName) {

        Region region = em.createQuery("SELECT r FROM Region r WHERE r.name = :name", Region.class)
                .setParameter("name", regionName)
                .getResultStream()
                .findFirst()
                .orElseGet(() -> {
                    Region r = new Region();
                    r.setName(regionName);
                    em.persist(r);
                    return r;
                });

        StructuralSubdivision subdivision = em.createQuery(
                        "SELECT s FROM StructuralSubdivision s WHERE s.name = :name AND s.region = :region", StructuralSubdivision.class)
                .setParameter("name", subdivisionName)
                .setParameter("region", region)
                .getResultStream()
                .findFirst()
                .orElseGet(() -> {
                    StructuralSubdivision s = new StructuralSubdivision();
                    s.setName(subdivisionName);
                    s.setRegion(region);
                    em.persist(s);
                    return s;
                });
        PowerSupplyEnterprise enterprise = em.createQuery(
                        "SELECT e FROM PowerSupplyEnterprise e WHERE e.name = :name AND e.structuralSubdivision = :subdivision", PowerSupplyEnterprise.class)
                .setParameter("name", powerSupplyEnterpriseName)
                .setParameter("subdivision", subdivision)
                .getResultStream()
                .findFirst()
                .orElseGet(() -> {
                    PowerSupplyEnterprise e = new PowerSupplyEnterprise();
                    e.setName(powerSupplyEnterpriseName);
                    e.setStructuralSubdivision(subdivision);
                    em.persist(e);
                    return e;
                });

        PowerSupplyDistrict district = em.createQuery(
                        "SELECT d FROM PowerSupplyDistrict d WHERE d.name = :name AND d.powerSupplyEnterprise = :enterprise", PowerSupplyDistrict.class)
                .setParameter("name", districtName)
                .setParameter("enterprise", enterprise)
                .getResultStream()
                .findFirst()
                .orElseGet(() -> {
                    PowerSupplyDistrict d = new PowerSupplyDistrict();
                    d.setName(districtName);
                    d.setPowerSupplyEnterprise(enterprise);
                    em.persist(d);
                    return d;
                });

        Station station = em.createQuery(
                        "SELECT s FROM Station s WHERE s.name = :name AND s.powerSupplyDistrict = :district", Station.class)
                .setParameter("name", stationName)
                .setParameter("district", district)
                .getResultStream()
                .findFirst()
                .orElseGet(() -> {
                    Station s = new Station();
                    s.setName(stationName);
                    s.setPowerSupplyDistrict(district);
                    em.persist(s);
                    return s;
                });

        Substation substation = em.createQuery(
                        "SELECT s FROM Substation s WHERE s.name = :name AND s.station = :station", Substation.class)
                .setParameter("name", substationName)
                .setParameter("station", station)
                .getResultStream()
                .findFirst()
                .orElseGet(() -> {
                    Substation s = new Substation();
                    s.setName(substationName);
                    s.setStation(station);
                    em.persist(s);
                    return s;
                });
        SUBSTATION_MAP.putIfAbsent(buildSubstationMapKey(substation), substation);
        return substation;
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

    private void fillDbWithIikData(Sheet sheet) {
        List<MeteringPoint> meteringPoints = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            MeteringPoint newIik = new MeteringPoint();
            Meter newMeter = new Meter();
            try {
                String mapKey = getStringMapKey(row);
                newIik.setSubstation(SUBSTATION_MAP.get(mapKey));
            } catch (NullPointerException e) {
                log.error("There is no such substation in DB {}: ", e.getMessage());
            }
            newIik.setId(Long.parseLong(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_ID))));
            newIik.setConnection(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_CONNECTION)));
            newIik.setName(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_NAME)));
            newIik.setMeterPlacement(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_PLACEMENT)));
            newIik.setMeteringPointAddress(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_ADDRESS)));
            newIik.setInstallationDate(LocalDate.parse(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_INSTALLATION_DATE))
                    , DATE_FORMATTER_DDMMYYYY));
            newMeter.setMeterModel(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_METER_NUMBER)));
            newMeter.setMeterNumber(getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_METER_MODEL)));
            String dcNumber = getCellStringValue(row.getCell(CELL_NUMBER_METERING_POINT_DC_NUMBER));
            Dc dcToAdd = addMeterToDc(newMeter, dcNumber);
            newIik.setMeter(newMeter);
            newMeter.setDc(dcToAdd);
            meteringPoints.add(newIik);
        }
        for (MeteringPoint point : meteringPoints) {
            service.create(point);
        }
//            meterService.saveAll(meters);
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
