package jngvarr.ru.pto_ackye_rzhd.telegram;

import jngvarr.ru.pto_ackye_rzhd.application.services.ExcelFileService;
import jngvarr.ru.pto_ackye_rzhd.domain.repositories.others.*;
import jngvarr.ru.pto_ackye_rzhd.domain.services.DcService;
import jngvarr.ru.pto_ackye_rzhd.domain.services.MeterService;
import jngvarr.ru.pto_ackye_rzhd.domain.services.MeteringPointService;
import jngvarr.ru.pto_ackye_rzhd.domain.services.SubstationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.ERROR_TEXT;

@Slf4j
@Component
@RequiredArgsConstructor
//@Transactional
public class TBotMessageService {
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
