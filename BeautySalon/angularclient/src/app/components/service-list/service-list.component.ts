import {Component, OnInit} from '@angular/core';
import {Service} from "../../model/entities/service";
import {ServiceForServices} from "../../services/service-for-services.service";
import {ClientService} from "../../services/client.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-service-list',
  templateUrl: './service-list.component.html',
  styleUrl: './service-list.component.scss'
})
export class ServiceListComponent implements OnInit {
  services: Service[] | undefined;
  service: Service| undefined;

  constructor(private serviceService: ServiceForServices, private router: Router) {
  }

  ngOnInit(): void {
    this.loadServices();
  }

  loadServices() {
    this.serviceService.findAll().subscribe(data => {
        console.log("Получены данные:", data); // Вывод полученных данных в консоль
        this.services = data;
      },
      error => {
        console.error("Ошибка при загрузке данных:", error); // Вывод ошибки в консоль

      });
  }

  deleteService(service: Service) {

  }

  searchByDescription(value: string) {
  }

  searchByTitle(value: string) {

  }

}
