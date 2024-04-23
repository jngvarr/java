import {Component, OnInit} from '@angular/core';
import {Service} from "../../model/entities/service";
import {ServiceForServices} from "../../services/service-for-services.service";
import {ClientServiceService} from "../../services/client-service.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-service-list',
  templateUrl: './service-list.component.html',
  styleUrl: './service-list.component.scss'
})
export class ServiceListComponent implements OnInit {
  services: Service[] | undefined;
  servcice: Service | undefined;

  constructor(private serviceService: ServiceForServices, private router: Router) {
  }

  ngOnInit(): void {
    this.loadServices();
  }

  loadServices() {
    this.serviceService.findAll().subscribe(data => {
      this.services = data;
    });
  }

  deleteService(service: Service) {

  }

  searchByDescription(value: string) {
  }

  searchByTitle(value: string) {

  }

}
