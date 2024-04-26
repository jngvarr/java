import {Component, OnInit} from '@angular/core';
import {Service} from "../../model/entities/service";
import {ServiceForServices} from "../../services/service-for-services.service";
import {ClientService} from "../../services/client.service";
import {Router} from "@angular/router";
import {Consumable} from "../../model/entities/consumable";

@Component({
  selector: 'app-service-list',
  templateUrl: './service-list.component.html',
  styleUrl: './service-list.component.scss'
})
export class ServiceListComponent implements OnInit {
  services: Service[] | undefined;
  service: Service | undefined;
  isSearching: boolean = false;

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
    if (confirm('Вы уверены, что хотите удалить услугу?')) {
      this.serviceService.delete(service.id).subscribe(() => {
        this.loadServices();
      });
    }
  }

  searchByDescription(value: string) {
    this.serviceService.findByDescription(value).subscribe((data: Service[]) => {
      this.services = data;
      this.isSearching=true;
    });
  }

  searchByTitle(title: string) {
    this.serviceService.findByTitle(title).subscribe((data: Service[]) => {
      this.services = data;
      this.isSearching=true;
    });
  }

  resetSearch() {
    this.isSearching=false;
    this.loadServices();
  }
}

