import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Service} from "../../model/entities/service";
import {ServiceForServices} from "../../services/service-for-services.service";
import {Consumable} from "../../model/entities/consumable";
import {StorageService} from "../../services/storage.service";

@Component({
  selector: 'app-service-form-component',
  templateUrl: './service-form.component.html',
  styleUrl: './service-form.component.scss'
})
export class ServiceFormComponent implements OnInit {
  service: Service = new Service();
  isEdit: boolean = false;
  consumables: Consumable[] = [];
  selectedConsumables: Consumable[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private serviceService: ServiceForServices,
    private storageService: StorageService
  ) {}

  ngOnInit(): void {
    // Выполнение запроса к сервису для получения данных о расходных материалах
    this.storageService.findAll().subscribe(
      (data: Consumable[]) => {
        this.consumables = data;
      },
      (error: any) => {
        console.error('Ошибка при загрузке данных о расходных материалах:', error);
      }
    );
    this.route.params.subscribe(params => {
      const serviceId = params['id'];
      if (serviceId) {
        this.serviceService.findById(serviceId).subscribe(data => {
          this.service = data;
          this.isEdit = true;
        });
      }
    });
  }

  onSubmit() {
    this.service.consumables = this.selectedConsumables;
    this.serviceService.save(this.service).subscribe(result => this.gotoServiceList());
  }

  gotoServiceList() {
    this.router.navigate(['/services']);
  }
}
