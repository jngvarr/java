package jngvarr.ru.pto_ackye_rzhd.domain.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Data
@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
public class PendingPhoto {
    private final String type; // "counter" или "concentrator"  или "tt"
    private final Path tempFilePath;
    private String deviceNumber;
    private LocalDateTime creationDate;
    private String additionalInfo; //показания счетчика или номер трансформатора тока

    public PendingPhoto(String type, Path tempFilePath, String deviceNumber, LocalDateTime date) {
        this.type = type;
        this.tempFilePath = tempFilePath;
        this.deviceNumber = deviceNumber;
        this.creationDate = date;
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

