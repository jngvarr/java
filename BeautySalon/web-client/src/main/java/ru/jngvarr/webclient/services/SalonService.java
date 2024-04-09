package ru.jngvarr.webclient.services;

import dao.Consumable;
import dao.Servize;
import dao.people.Employee;
import feign_clients.*;
import dao.Visit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import dao.people.Client;

@Log4j2
@Service
@RequiredArgsConstructor
public class SalonService {
    private final ClientFeignClient clientFeignClient;
    private final VisitFeignClient visitFeignClient;
    private final ServiceFeignClient serviceFeignClient;
    private final StaffFeignClient staffFeignClient;
    private final StorageFeignClient storageFeignClient;


    public List<Client> getClients() {
        return clientFeignClient.getClients();
    }

    public List<Employee> getEmployees() {
        return staffFeignClient.getEmployees();
    }

    public List<Servize> getServices() {
        return serviceFeignClient.getServices();
    }

    public List<Consumable> getConsumables() {return storageFeignClient.getConsumables();
    }


    public List<Visit> getVisits() {
        return visitFeignClient.getVisits();
    }

    public Client getClient(Long id) {
        return clientFeignClient.getClient(id);
    }

    public Employee getEmployee(Long id) {
        return staffFeignClient.getEmployee(id);
    }

    public void addClient(Client client) {
        log.debug("create {}", client);
        clientFeignClient.addClient(client);
    }

    public void addEmployee(Employee employee) {
        log.debug("create {}", employee);
        staffFeignClient.addEmployee(employee);
    }

    public Client getClientByContact(String contact) {
        return clientFeignClient.getClientByContact(contact);
    }

    public Client update(Client newData, Long id) {
        return clientFeignClient.update(newData, id);
    }

    public void updateEmployees(Employee newData, Long id) {
        log.debug("salon service - update employees {}", newData);
        staffFeignClient.update(newData, id);
    }

    public void deleteClient(Long id) {
        clientFeignClient.deleteClient(id);
    }

    public void deleteEmployee(Long id) {
        staffFeignClient.deleteEmployee(id);
    }

    public void clear() {
        clientFeignClient.clearAllData();
    }
}
