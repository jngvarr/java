package jngvarr.ru.pto_ackye_rzhd.sevices;

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
    private final IikService iikService;
    private final IvkeService ivkeService;
    private final MeterService meterService;

    private static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String PLAN_OTO_PATH = "d:\\Downloads\\Контроль ПУ РРЭ (Задания на ОТО РРЭ)demo — копия.xlsx";
    private final long startTime = System.currentTimeMillis();
    private static final Map<String, Dc> DC_MAP = new HashMap<>();
    private static final Map<String, Substation> SUBSTATION_MAP = new HashMap<>();
    private static final Map<EntityType, Map<String, Object>> entityMaps = new EnumMap<>(EntityType.class);
    private static final int REGION_NAME = 1;
    private static final int STATION_NAME = 2;
    private static final int POWER_SUPPLY_ENTERPRISE_NAME = 3;
    private static final int POWER_SUPPLY_DISTRICT_NAME = 4;
    private static final int STRUCTURAL_SUBDIVISION_NAME = 5;
    private static final int SUBSTATION_NAME = 6;
    private static final int BUS_SECTION_NUM = 7;
    private static final String DC_MODEL = "DC-1000/SL";
    private static final int DC_NUMBER = 9;


    private enum EntityType {
        SUBSTATION, STATION, POWER_SUPPLY_DISTRICT, POWER_SUPPLY_ENTERPRISE, STRUCTURAL_SUBDIVISION, REGION
    }

    public void processFile() {
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH))
        ) {
            fillDbWithIvkeData(planOTOWorkbook.getSheet("ИВКЭ"));
            fillDbWithIikData(planOTOWorkbook.getSheet("ИИК"));
            planOTOWorkbook.close();

            log.info("Data filled successfully!");

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Execution time: " + duration / 1000 + " seconds");
    }

    private void fillDbWithIvkeData(Sheet sheet) {
        for (EntityType type : EntityType.values()) {
            entityMaps.put(type, new HashMap<>());
        }
        for (Row row : sheet) {
            if (row.getRowNum() == 0 || row.getRowNum() == sheet.getLastRowNum()) {
                // Пропускаем первую строку, это заголовок
                continue;
            }
            Region newRegion = new Region();
            StructuralSubdivision newIvkeSubdivision = new StructuralSubdivision();
            PowerSupplyEnterprise newIvkePowerSupplyEnterprise = new PowerSupplyEnterprise();
            PowerSupplyDistrict newIvkePowerSupplyDistrict = new PowerSupplyDistrict();
            Station newIvkeStation = new Station();
            Substation newIvkeSubstation = new Substation();

            String regionName = getCellStringValue(row.getCell(REGION_NAME));
            newRegion.setName(regionName);
            entityMaps.get(EntityType.REGION).put(regionName, newRegion);
            newIvkeSubdivision.setName(getCellStringValue(row.getCell(STRUCTURAL_SUBDIVISION_NAME)));
            newIvkeSubdivision.setRegion((Region) entityMaps.get(EntityType.REGION).get(regionName));

            newIvkePowerSupplyEnterprise.setName(getCellStringValue(row.getCell(POWER_SUPPLY_ENTERPRISE_NAME)));
            newIvkePowerSupplyEnterprise.setStructuralSubdivision(newIvkeSubdivision);
            newIvkePowerSupplyDistrict.setName(getCellStringValue(row.getCell(POWER_SUPPLY_DISTRICT_NAME)));
            newIvkePowerSupplyDistrict.setPowerSupplyEnterprise(newIvkePowerSupplyEnterprise);
            newIvkeStation.setName(getCellStringValue(row.getCell(STATION_NAME)));
            newIvkeStation.setPowerSupplyDistrict(newIvkePowerSupplyDistrict);
            newIvkeSubstation.setName(getCellStringValue(row.getCell(SUBSTATION_NAME)));
            newIvkeSubstation.setStation(newIvkeStation);
            Dc newIvke = new Dc();
            newIvke.setSubstation(newIvkeSubstation);
            newIvke.setBusSection(Integer.parseInt(getCellStringValue(row.getCell(BUS_SECTION_NUM))));
            newIvke.setInstallationDate(LocalDate.parse(getCellStringValue(row.getCell(10)), DATE_FORMATTER_DDMMYYYY));
            newIvke.setDcModel(DC_MODEL);
            String dcNumber = getCellStringValue(row.getCell(DC_NUMBER));
            newIvke.setDcNumber(dcNumber);

            SUBSTATION_MAP.putIfAbsent(buildSubstationMapKey(newIvkeSubstation), newIvkeSubstation);
            DC_MAP.putIfAbsent(dcNumber, newIvke);
        }
        ivkeService.createAll(DC_MAP.values().stream().toList());
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
        List<Meter> meters = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            MeteringPoint newIik = new MeteringPoint();
            Meter newIikMeter = new Meter();

            try {
                String mapKey = getStringMapKey(row);
                newIik.setSubstation(SUBSTATION_MAP.get(mapKey));
            } catch (NullPointerException e) {
                log.error("There is no such substation in DB {}: ", e.getMessage());
            }
            newIik.setId(Long.parseLong(getCellStringValue(row.getCell(1))));
            newIik.setConnection(getCellStringValue(row.getCell(8)));
            newIik.setName(getCellStringValue(row.getCell(9)));
            newIik.setMeterPlacement(getCellStringValue(row.getCell(10)));
            newIik.setMeteringPointAddress(getCellStringValue(row.getCell(11)));
            newIikMeter.setMeterModel(getCellStringValue(row.getCell(12)));
            newIikMeter.setMeterNumber(getCellStringValue(row.getCell(13)));
            newIik.setInstallationDate(LocalDate.parse(getCellStringValue(row.getCell(15)), DATE_FORMATTER_DDMMYYYY));
            newIikMeter.setDc(DC_MAP.get(getCellStringValue(row.getCell(14))));
            newIikMeter.setMeteringPoint(newIik);
            meters.add(newIikMeter);

        }
        meterService.saveAll(meters);
    }

//    private IikState getiikStatusData(Row row) {
//
//        IikState newStatusData = new IikState();
//
//        newStatusData.setCurrentStatus(getCellStringValue(row.getCell(16)));
//        newStatusData.setNotOrNotNot(getCellStringValue(row.getCell(17)));
//        newStatusData.setStatus(getCellStringValue(row.getCell(19)));
//        newStatusData.setDispatcherTask(getCellStringValue(row.getCell(20)));
//        newStatusData.setTeamReport(getCellStringValue(row.getCell(21)));
//
//        return iikStatusDataService.createData(newStatusData);
//    }


    //    private List<IikState> fillIikStatusData(Sheet worksheet) {
//        List<IikState> iikStatusData = new ArrayList<>();
//        for (Row row : worksheet) {
//            IikState newStatusData = new IikState();
//            if (row.getRowNum() == 0) {
//                // Пропускаем первую строку, если это заголовок
//                continue;
//            }
//            newStatusData.setCurrentStatus(getCellStringValue(row.getCell(16)));
//            newStatusData.setNotOrNotNot(getCellStringValue(row.getCell(17)));
//            newStatusData.setStatus(getCellStringValue(row.getCell(19)));
//            newStatusData.setDispatcherTask(getCellStringValue(row.getCell(20)));
//            newStatusData.setTeamReport(getCellStringValue(row.getCell(21)));
//            iikStatusData.add(newStatusData);
//        }
//        return iikStatusDataService.createAll(iikStatusData);
//    }
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
}
