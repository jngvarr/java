package jngvarr.ru.pto_ackye_rzhd.sevices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PtoService {
    private  final IikService iikService;
    private final IvkeService ivkeService;

}
