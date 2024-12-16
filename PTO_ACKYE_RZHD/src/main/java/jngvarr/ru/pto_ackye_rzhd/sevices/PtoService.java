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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PtoService {
    private final IikService iikService;
    private final IvkeService ivkeService;
    private static final DateTimeFormatter DATE_FORMATTER_DDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String PLAN_OTO_PATH = "d:\\Downloads\\Контроль ПУ РРЭ (Задания на ОТО РРЭ)demo — копия.xlsx";
    private final long startTime = System.currentTimeMillis();

    public void processFile() {
        try (Workbook planOTOWorkbook = new XSSFWorkbook(new FileInputStream(PLAN_OTO_PATH))
        ) {
            Map<String, Substation> substationMap;
            substationMap = fillDbWithIikData(planOTOWorkbook.getSheet("ИИК"));
            fillDbWithIvkeData(planOTOWorkbook.getSheet("ИВКЭ"), substationMap);
//            fillIikStatusData(planOTOWorkbook.getSheet("ИИК"));
            planOTOWorkbook.close();

            log.info("Data filled successfully!");

        } catch (IOException ex) {
            log.error("Error processing workbook", ex);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Execution time: " + duration / 1000 + " seconds");
    }

    private void fillDbWithIvkeData(Sheet sheet, Map<String, Substation> substationMap) {
        List<DcComplex> ivkes = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                // Пропускаем первую строку, это заголовок
                continue;
            }
            DcComplex newIvke = new DcComplex();
            DC newDc = new DC();
            newIvke.setInstallationDate(LocalDate.parse(getCellStringValue(row.getCell(10)), DATE_FORMATTER_DDMMYYYY));
            newIvke.setBusSection(Integer.parseInt(getCellStringValue(row.getCell(7))));
            try {
                newIvke.setSubstation(substationMap.get(getStringMapKey(row)));
            } catch (NullPointerException e) {
                log.error("There is no such substation in DB {}: ", e.getMessage());
            }
            newDc.setDcComplex(newIvke);
            newDc.setDcModel("DC-1000/SL");
            newDc.setDcNumber(getCellStringValue(row.getCell(9)));
            ivkes.add(newIvke);
        }
        ivkeService.createAll(ivkes);
    }

    private String getStringMapKey(Row row) {
        return new StringBuilder()
                .append(getCellStringValue(row.getCell(1)))
                .append("_")
                .append(getCellStringValue(row.getCell(2)))
                .append("_")
                .append(getCellStringValue(row.getCell(3)))
                .append("_")
                .append(getCellStringValue(row.getCell(4)))
                .append("_")
                .append(getCellStringValue(row.getCell(5)))
                .append("_")
                .append(getCellStringValue(row.getCell(6)))
                .toString();
    }

    private Map<String, Substation> fillDbWithIikData(Sheet sheet) {
        List<MeteringPoint> iiks = new ArrayList<>();
        Map<String, Substation> substationMap = new HashMap<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            Region newRegion = new Region();
            StructuralSubdivision newIikSubdivision = new StructuralSubdivision();
            PowerSupplyEnterprise newIikPowerSupplyEnterprise = new PowerSupplyEnterprise();
            PowerSupplyDistrict newIikPowerSupplyDistrict = new PowerSupplyDistrict();
            Station newIikStation = new Station();
            Substation newIikSubstation = new Substation();
            MeteringPoint newIik = new MeteringPoint();
            Meter newIikMeter = new Meter();

            newRegion.setName(getCellStringValue(row.getCell(2)));
            newIikSubdivision.setName(getCellStringValue(row.getCell(3)));
            newIikSubdivision.setRegion(newRegion);
            newIikPowerSupplyEnterprise.setName(getCellStringValue(row.getCell(4)));
            newIikPowerSupplyEnterprise.setStructuralSubdivision(newIikSubdivision);
            newIikPowerSupplyDistrict.setName(getCellStringValue(row.getCell(5)));
            newIikPowerSupplyDistrict.setPowerSupplyEnterprise(newIikPowerSupplyEnterprise);
            newIikStation.setName(getCellStringValue(row.getCell(6)));
            newIikStation.setPowerSupplyDistrict(newIikPowerSupplyDistrict);
            newIikSubstation.setName(getCellStringValue(row.getCell(7)));
            newIikSubstation.setStation(newIikStation);
            newIik.setConnection(getCellStringValue(row.getCell(8)));
            newIik.setName(getCellStringValue(row.getCell(9)));
            newIik.setMeterPlacement(getCellStringValue(row.getCell(10)));
            newIik.setMeteringPointAddress(getCellStringValue(row.getCell(11)));
            newIikMeter.setMeterModel(getCellStringValue(row.getCell(12)));
            newIikMeter.setMeterNumber(getCellStringValue(row.getCell(13)));
            newIik.setInstallationDate(LocalDate.parse(getCellStringValue(row.getCell(15)), DATE_FORMATTER_DDMMYYYY));
            iiks.add(newIik);
            substationMap.putIfAbsent(buildSubstationMapKey(newIikSubstation), newIikSubstation);
        }
        iikService.createAll(iiks);
        return substationMap;
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
                append(substation.getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getStructuralSubdivision().getRegion()).
                append("_").
                append(substation.getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise().getStructuralSubdivision()).
                append("_").
                append(substation.getStation().getPowerSupplyDistrict().getPowerSupplyEnterprise()).
                append("_").
                append(substation.getStation().getPowerSupplyDistrict()).
                append("_").
                append(substation.getStation().getName()).
                append("_").
                append(substation.getName()).toString();

    }
}
