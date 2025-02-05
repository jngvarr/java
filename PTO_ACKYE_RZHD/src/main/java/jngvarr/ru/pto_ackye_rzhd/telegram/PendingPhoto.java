package jngvarr.ru.pto_ackye_rzhd.telegram;
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
    private final String type; // "counter" или "concentrator"
    private final Path tempFilePath;
    private String scannedBarcode;
}
//    public PendingPhoto(String type, Path tempFilePath, String scannedBarcode) {
//        this.type = type;
//        this.tempFilePath = tempFilePath;
//        this.scannedBarcode = scannedBarcode;
//    }
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

