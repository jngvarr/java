package ru.jngvarr.appointmentmanagement.services;

import dao.entities.Servize;
import dao.entities.Visit;
import dao.entities.people.Client;
import dao.entities.people.Employee;
import feign_clients.ClientFeignClient;
import feign_clients.ServiceFeignClient;
import feign_clients.StaffFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.jngvarr.appointmentmanagement.model.VisitData;
import ru.jngvarr.appointmentmanagement.repositories.VisitRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class VisitService {
    public final VisitRepository visitRepository;
    private final ClientFeignClient clientFeignClient;
    private final ServiceFeignClient serviceFeignClient;
    private final StaffFeignClient employeeFeignClient;
    private final TimeAvailabilityService timeAvailabilityService;

    public Visit getVisitFromVisitData(VisitData vd) {
        Servize service = serviceFeignClient.getService(vd.getServiceId());
        Client client = clientFeignClient.getClient(vd.getClientId());
        Employee master = employeeFeignClient.getEmployee(vd.getEmployeeId());
        return new Visit(vd.getId(), vd.getVisitDate(), vd.getStartTime(), service, client, master);
    }

    public List<Visit> getVisits() {
        log.debug("getVisits - VisitService");
        List<Visit> visits = new ArrayList<>();
        List<VisitData> visitData = visitRepository.findAll();
        for (VisitData vd : visitData) {
            Visit visit = getVisitFromVisitData(vd);
            visits.add(visit);
        }
        return visits;
    }

    public Visit getVisit(Long id) {
        Optional<VisitData> neededVisit = visitRepository.findById(id);
        if (neededVisit.isPresent()) return getVisitFromVisitData(neededVisit.get());
        else throw new IllegalArgumentException("Visit not found");
    }

    public Visit create(Visit visit) {
        VisitData newVisit = new VisitData();
        if (!isClientExist(visit.getClient())) {
            Client c = clientFeignClient.addClient(visit.getClient());
            visit.setClient(c);
        }
        int serviceDuration = serviceFeignClient.getServiceDuration(visit.getService().getId());
        if (visit.getVisitDate() != null && visit.getClient() != null && visit.getService() != null) {
//            if (timeAvailabilityService.isTimeAvailable(visit.getVisitDate().atStartOfDay(), visit.getStartTime(),
//                    visit.getStartTime().plusMinutes(serviceDuration))) {
//                    log.debug("точка 1");
            newVisit.setVisitDate(visit.getVisitDate());
            newVisit.setStartTime(visit.getStartTime());
            newVisit.setServiceId(visit.getService().getId());
            newVisit.setClientId(visit.getClient().getId());
            newVisit.setEmployeeId(visit.getMaster().getId());
            visitRepository.save(newVisit);
            log.debug("точка 2");
//            } else throw new IllegalArgumentException("Not enough visit data");
        }
        return getVisitFromVisitData(newVisit);
    }

    private boolean isClientExist(Client client) {
        return clientFeignClient.getClients().stream()
                .anyMatch(existingClient -> existingClient.getContact().equals(client.getContact()));
    }

    public Visit update(Visit newData, Long id) {
        Optional<VisitData> oldVisit = visitRepository.findById(id);
        if (oldVisit.isPresent()) {
            VisitData newVisit = oldVisit.get();
            if (newData.getVisitDate() != null) newVisit.setVisitDate(newData.getVisitDate());
            if (newData.getStartTime() != null) newVisit.setStartTime(newData.getStartTime());
            if (newData.getClient().getId() != null) newVisit.setClientId(newData.getClient().getId());
            if (newData.getService().getId() != null) newVisit.setServiceId(newData.getService().getId());
            if (newData.getMaster().getId() != null) newVisit.setEmployeeId(newData.getMaster().getId());
            return getVisitFromVisitData(visitRepository.save(newVisit));
        } else throw new IllegalArgumentException("Visit not found");
    }

    public void delete(Long id) {
        visitRepository.deleteById(id);
    }

    public List<Client> getClients() {
        log.debug("getClients-VisitService");
        return clientFeignClient.getClients();
    }

    public List<Servize> getServices() {
        return serviceFeignClient.getServices();
    }

    public List<Employee> getEmployees() {
        return employeeFeignClient.getEmployees();
    }

    public List<Visit> getVisitsByDate(LocalDate searchDate) {
        return visitRepository.findAllByVisitDate(searchDate).stream()
                .map(this::getVisitFromVisitData)
                .collect(Collectors.toList());
    }
}
