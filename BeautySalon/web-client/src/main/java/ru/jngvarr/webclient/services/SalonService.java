package ru.jngvarr.webclient.services;

import dao.people.Employee;
import feign_clients.ClientFeignClient;
import feign_clients.ServiceFeignClient;
import feign_clients.StaffFeignClient;
import feign_clients.VisitFeignClient;
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


    public List<Client> getClients() {
        return clientFeignClient.getClients();
    }

    public List<Employee> getEmployees() {
        return staffFeignClient.getEmployees();
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

    public Client addClient(Client client) {
        log.debug("create {}", client);
        return clientFeignClient.addClient(client);
    }
    public Employee addEmployee(Employee employee) {
        log.debug("create {}", employee);
        return staffFeignClient.addEmployee(employee);
    }

    public Client getClientByContact(String contact) {
        return clientFeignClient.getClientByContact(contact);
    }

    public Client update(Client newData, Long id) {
        return clientFeignClient.update(newData, id);
    }
    public Employee update(Employee newData, Long id) {
        return staffFeignClient.update(newData, id);
    }

    public void deleteClient(Long id) {
        clientFeignClient.deleteClient(id);
    }
    public void deleteEmployee(Long id) {        staffFeignClient.deleteEmployee(id);
    }

    public void clear() {
        clientFeignClient.clearAllData();
    }
}
