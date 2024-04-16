package ru.jngvarr.webclient.services;

import dao.entities.Consumable;
import dao.entities.ServiceDto;
import dao.entities.Servize;
import dao.entities.people.Employee;
import feign_clients.*;
import dao.entities.Visit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import dao.entities.people.Client;

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

    public List<Consumable> getConsumables() {
        return storageFeignClient.getConsumables();
    }

    public List<String> getStringConsumables() {
        return storageFeignClient.getConsumables().stream().map(Consumable::getTitle).toList();
    }

    public List<Visit> getVisits() {
        log.debug("salonService getVisits");
        return visitFeignClient.getVisits();
    }


    public Client getClient(Long id) {
        return clientFeignClient.getClient(id);
    }

    public Employee getEmployee(Long id) {
        return staffFeignClient.getEmployee(id);
    }

    public Servize getService(Long id) {
        return serviceFeignClient.getService(id);
    }

    public Consumable getConsumable(Long id) {
        return storageFeignClient.getConsumable(id);
    }

    public void addClient(Client client) {
        clientFeignClient.addClient(client);
    }

    public void addEmployee(Employee employee) {
        staffFeignClient.addEmployee(employee);
    }

    public void addConsumable(Consumable consumable) {
        storageFeignClient.addConsumable(consumable);
    }

    public void addService(ServiceDto service) {
        convertServiceDtoToServize(service);
        serviceFeignClient.addService(convertServiceDtoToServize(service));
    }

    public Client getClientByContact(String contact) {
        return clientFeignClient.getClientByContact(contact);
    }

    public Client update(Client newData, Long id) {
        return clientFeignClient.updateClient(newData, id);
    }

    public void updateEmployees(Employee newData, Long id) {
        staffFeignClient.update(newData, id);
    }

    public void updateService(Servize newData, Long id) {
        serviceFeignClient.updateService(newData, id);
    }

    public void updateConsumable(Consumable newData, Long id) {
        storageFeignClient.updateConsumable(newData, id);
    }


    public void deleteClient(Long id) {
        clientFeignClient.deleteClient(id);
    }

    public void deleteEmployee(Long id) {
        staffFeignClient.deleteEmployee(id);
    }

    public void deleteService(Long id) {
        serviceFeignClient.deleteService(id);
    }

    public void deleteConsumable(Long id) {
        storageFeignClient.deleteConsumable(id);
    }

    public void clear() {
        clientFeignClient.clearAllData();
    }


    public Servize convertServiceDtoToServize(ServiceDto data) {
        Servize updatedServize = new Servize();
        updatedServize.setId(data.getId());
        updatedServize.setDescription(data.getDescription());
        updatedServize.setTitle(data.getTitle());
        updatedServize.setPrice(data.getPrice());
        updatedServize.setServiceDurationInMinutes(data.getServiceDurationInMinutes());
        updatedServize.setConsumables(new ArrayList<>());
        data.getConsumables().forEach(cons -> updatedServize.getConsumables()
                .add(storageFeignClient.getConsumableByTitle(cons)));
//        for (String cons : data.getConsumables()) {
//            updatedServize.getConsumables().add(storageFeignClient.getConsumableByTitle(cons));
//        }
        return updatedServize;
    }
}
