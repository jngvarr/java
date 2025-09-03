package jngvarr.ru.pto_ackye_rzhd.telegram.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Data
@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
public class PendingPhoto {
    private final String type; // "counter" или "concentrator"  или "tt"
    private final Path tempFilePath;
    private String deviceNumber;
    private String additionalInfo; //показания счетчика или номер трансформатора тока

    public PendingPhoto(String type, Path tempFilePath, String deviceNumber) {
        this.type = type;
        this.tempFilePath = tempFilePath;
        this.deviceNumber = deviceNumber;
    }
}
//
////    public String getType() {
////        return type;
////    }
////
////    public Path getTempFilePath() {
////        return tempFilePath;
////    }
////
////    public String getScannedBarcode() {
////        return scannedBarcode;
////    }
//
//    public void setScannedBarcode(String scannedBarcode) {
//        this.scannedBarcode = scannedBarcode;
//    }
//}

