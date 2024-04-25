import {Component} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Service} from "../../model/entities/service";
import {ServiceForServices} from "../../services/service-for-services.service";
import {Consumable} from "../../model/entities/consumable";

@Component({
  selector: 'app-service-form-component',
  templateUrl: './service-form.component.html',
  styleUrl: './service-form.component.scss'
})
export class ServiceFormComponent {
  service: Service;
  isEdit: boolean = false;
  consumables: Consumable[] | undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private serviceService: ServiceForServices) {
    this.service = new Service();
  }

  ngOnInit() {
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
    this.serviceService.save(this.service).subscribe(result => this.gotoServiceList());
  }

  gotoServiceList() {
    this.router.navigate(['/services']);
  }
}
