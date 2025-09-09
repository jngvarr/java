package jngvarr.ru.pto_ackye_rzhd;

//import jngvarr.ru.pto_ackye_rzhd.sevices.IikService;
//import jngvarr.ru.pto_ackye_rzhd.sevices.PtoService;
import jngvarr.ru.pto_ackye_rzhd.telegram.TBotMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class PtoAckyeRzhdApplication {
    private final TBotMessageService TBotMessageService;

//    @PostConstruct
//    public void init() {
//        ptoService.addDataFromExcelFile("d:\\Downloads\\пто\\Контроль ПУ РРЭ (Задания на ОТО РРЭ).xlsx");
//    }

    public static void main(String[] args) {
        SpringApplication.run(PtoAckyeRzhdApplication.class, args);
    }

}
