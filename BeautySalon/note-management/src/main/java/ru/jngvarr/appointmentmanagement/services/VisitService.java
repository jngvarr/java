package ru.jngvarr.appointmentmanagement.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import dao.Visit;
import ru.jngvarr.appointmentmanagement.repositories.VisitRepository;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class VisitService {
    public final VisitRepository visitRepository;

    public List<Visit> getVisits() {
        log.debug("getVisits - Sercice");
        return visitRepository.findAll();
    }

//    public List<Visit> getVisitsByContact(Client client) {
//        return visitRepository.findByContact(client.getContact());
//    }
//
//    public List<Visit> getVisitsByLastName(Client client) {
//        return visitRepository.findByLastName(client.getLastName());
//    }

    public Visit getVisit(Long id) {
        Optional<Visit> neededVisit = visitRepository.findById(id);
        if (neededVisit.isPresent()) return neededVisit.get();
        else throw new IllegalArgumentException("Visit not found");
    }

    public Visit create(Visit visit) {
        if (visit.getServiceId() != null && visit.getClientId() != null && visit.getVisitDate() != null)
            return visitRepository.save(visit);
        else throw new IllegalArgumentException("Not enough visit data");
    }

    public Visit update(Visit newData, Long id) {
        Optional<Visit> oldVisit = visitRepository.findById(id);
        if (oldVisit.isPresent()) {
            Visit newVisit = oldVisit.get();
            if (newData.getVisitDate() != null) newVisit.setVisitDate(newData.getVisitDate());
            if (newData.getClientId() != null) newVisit.setClientId(newData.getClientId());
            if (newData.getServiceId() != null) newVisit.setServiceId(newData.getServiceId());
            if (newData.getEmployeeId() != null) newVisit.setEmployeeId(newData.getEmployeeId());
            return visitRepository.save(newVisit);
        } else throw new IllegalArgumentException("Visit not found");
    }

    public void delete (Long id){
        visitRepository.deleteById(id);
    }

}
