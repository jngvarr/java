package jngvarr.ru.pto_ackye_rzhd.util;

import jngvarr.ru.pto_ackye_rzhd.telegram.domain.OtoType;
import jngvarr.ru.pto_ackye_rzhd.telegram.domain.ProcessState;
import jngvarr.ru.pto_ackye_rzhd.telegram.services.TBotConversationStateService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.STRINGS_BY_ACTION_TYPE;

@Component
@Data
@RequiredArgsConstructor
public class TBotConversationUtils {
    private final TBotConversationStateService conversationStateService;

    public String actionConfirmation(Long userId) {
        StringBuilder resultStr = new StringBuilder("Выполнены следующие действия:\n");
        int lineCounter = 0;

        for (Map.Entry<String, String> entry : conversationStateService.getOtoLog().entrySet()) {
            String key = entry.getKey();
            String[] str = entry.getValue().split("_");
            str[4] = str[4].toUpperCase();
            String actionType = str[0];
            List<String> strings = STRINGS_BY_ACTION_TYPE.get(actionType);

            resultStr.append(++lineCounter).append(". ").append(strings.get(2));
            switch (actionType) {
                case "meterChange" -> resultStr.append(String.format(
                        "%s с показаниями: %s\n на прибор учета № %s с показаниями: %s. Причина: %s.", key, str[1], str[2], str[3], str[4]));
                case "ttChange" -> resultStr.append(String.format(
                        "%s, номиналом %s, с классом точности %s, %sг.в. №АВС = %s, %s, %s. Причина: %s.",
                        str[1], str[2], str[3], str[4], str[5], str[6], str[7], str[8]));
                case "dcChange" -> resultStr.append(String.format(
                        "%s на концентратор №%s. Причина: %s.", key, str[1], str[2]));
                case "iikMount" -> resultStr.append(String.format(
                        "\nНаименование ТУ: %s, \nПрибор учёта: %s №: %s. \nСтанция: %s, \nТП/КТП: %s, \nАдрес: %s, \nДата монтажа: %s.",
                        str[5], str[4], str[3], str[1], str[2], str[6], str[10]));
                default -> {
                    String device = ProcessState.IIK_WORKS.equals(conversationStateService.getProcessState(userId)) ? " ПУ" : " Концентратор";
                    resultStr.append(String.format(device + " № %s - ", key));
                    if (str.length > 1) resultStr.append(" ").append(str[str.length - 1]).append(".");
                }
            }
            resultStr.append("\n");
        }
        return resultStr.toString();
    }

    public void formingOtoLog(String deviceInfo, Object typeIndicator, Long userId) {
        String deviceNumber = deviceInfo.substring(0, deviceInfo.indexOf("_"));
        String workType = setWorkType(typeIndicator, deviceNumber);
        conversationStateService.getOtoLog().put(deviceNumber, workType + deviceInfo.substring(deviceInfo.indexOf("_")));
        conversationStateService.clearProcessInfo(userId);
        conversationStateService.resetSequence(userId);
    }
    private String setWorkType(Object typeIndicator, String deviceNumber) {
        if (typeIndicator instanceof OtoType otoType) {
            return switch (otoType) {
                case METER_CHANGE -> "meterChange";
                case TT_CHANGE -> "ttChange";
                case DC_CHANGE -> "dcChange";
                case SET_NOT -> "NOT";
                case SUPPLY_RESTORING ->
                        deviceNumber.contains("LW") || deviceNumber.contains("LJ") ? "dcSupply" : "meterSupply";
                default -> "unknown";
            };
        } else if (typeIndicator instanceof ProcessState processState) {
            return switch (processState) {
                case IIK_MOUNT -> "iikMount";
                case DC_MOUNT -> "dcMount";
                default -> "unknown";
            };
        }
        return "unknown";
    }
}
