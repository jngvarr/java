package jngvarr.ru.pto_ackye_rzhd.telegram.handlers;

import jngvarr.ru.pto_ackye_rzhd.domain.entities.User;
import jngvarr.ru.pto_ackye_rzhd.domain.services.UserService;
import jngvarr.ru.pto_ackye_rzhd.telegram.UpdateEvent;
import jngvarr.ru.pto_ackye_rzhd.telegram.UpdateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HandlerDispatcher {
    private final List<UpdateHandler> handlers; // сюда Spring сам положит все бины
    private final UserService userService;

    @EventListener
    public void dispatch(UpdateEvent event) {
        Update update = event.update();
       var user = userService.checkUser(update);
        if (user.isAccepted()) {
            for (UpdateHandler handler : handlers) {
                if (handler.canHandle(update)) {
                    handler.handle(update);
                    break;
                }
            }
        }
    }
}
