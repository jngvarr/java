package jngvarr.ru.pto_ackye_rzhd.sevices;

import jngvarr.ru.pto_ackye_rzhd.entities.IikStatusData;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NeededObjectNotFound;
import jngvarr.ru.pto_ackye_rzhd.exceptions.NotEnoughData;
import jngvarr.ru.pto_ackye_rzhd.repositories.IikStatusDataRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class IikStatusDataService {
    private final IikStatusDataRepository dataRepository;

    public List<IikStatusData> getAllData() {
        return dataRepository.findAll();
    }

    public IikStatusData getData(Long id) {
        Optional<IikStatusData> neededData = dataRepository.findById(id);
        return neededData.orElseThrow(() -> new NeededObjectNotFound("IikStatusData not found: " + id));
    }

    public IikStatusData createData(IikStatusData dataToCreate) {
        if ((dataToCreate.getStatus() != null &&
                dataToCreate.getCurrentStatus() != null &&
                dataToCreate.getNotOrNotNot() != null &&
                dataToCreate.getDispatcherTask() != null &&
                dataToCreate.getTeamReport() != null
        )) {
            return dataRepository.save(dataToCreate);
        } else throw new NotEnoughData("Not enough status data");
    }

    public List<IikStatusData> createAll(List<IikStatusData> iiks) {
        return dataRepository.saveAll(iiks);
    }

    public IikStatusData updateIikStatusData(IikStatusData newData, Long iikDataId) {
        Optional<IikStatusData> oldIikData = dataRepository.findById(iikDataId);
        if (oldIikData.isPresent()) {
            IikStatusData newIik = oldIikData.get();
            if (newData.getStatus() != null) newIik.setStatus(newData.getStatus());
            if (newData.getCurrentStatus() != null) newIik.setCurrentStatus(newData.getCurrentStatus());
            if (newData.getNotOrNotNot() != null) newIik.setNotOrNotNot(newData.getNotOrNotNot());
            if (newData.getDispatcherTask() != null) newIik.setDispatcherTask(newData.getDispatcherTask());
            if (newData.getTeamReport() != null) newIik.setTeamReport(newData.getTeamReport());


            return dataRepository.save(newIik);
        } else throw new IllegalArgumentException("IikStatusData not found");
    }

    public void delete(Long iikId) {
        dataRepository.deleteById(iikId);
    }

}
