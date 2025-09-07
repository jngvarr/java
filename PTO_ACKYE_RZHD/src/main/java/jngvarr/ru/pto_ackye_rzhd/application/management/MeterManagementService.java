package jngvarr.ru.pto_ackye_rzhd.application.management;

import jngvarr.ru.pto_ackye_rzhd.application.util.EntityCache;
import jngvarr.ru.pto_ackye_rzhd.domain.entities.MeteringPoint;
import jngvarr.ru.pto_ackye_rzhd.domain.value.EntityType;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Data
@Component
public class MeterManagementService {
    private final EntityCache entityCache;

    private boolean isMeterInstalled(String meterNum) {
        return entityCache
                .get(EntityType.METERING_POINT)
                .values()
                .stream()
//                .map(o -> (MeteringPoint) o)
                .map(MeteringPoint::getMeter)
                .filter(Objects::nonNull)
                .anyMatch(m -> meterNum.equals(m.getMeterNumber()));
    }

}
