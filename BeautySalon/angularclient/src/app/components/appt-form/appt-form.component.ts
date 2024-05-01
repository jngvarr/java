import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Visit} from "../../model/entities/visit";
import {Client} from "../../model/entities/client";
import {Employee} from "../../model/entities/employee";
import {Service} from "../../model/entities/service";
import {ApptService} from "../../services/appt.service";
import {StaffService} from "../../services/staff.service";
import {ServiceForServices} from "../../services/service-for-services.service";
import {ClientService} from "../../services/client.service";

@Component({
  selector: 'app-appt-form',
  templateUrl: './appt-form.component.html',
  styleUrl: './appt-form.component.scss'
})
export class ApptFormComponent implements OnInit {
  appt: Visit = new Visit();
  appts: Visit[] = [];
  editMode = false;
  client: Client = new Client();
  masters: Employee[] | undefined;
  master: Employee = new Employee();
  service: Service = new Service();
  services: Service[] | undefined;
  clients: Client[] | undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    protected apptService: ApptService,
    private staffService: StaffService,
    private serviceService: ServiceForServices,
    private clientService: ClientService,
  ) {
    this.staffService.findAll().subscribe(
      (data: Employee[]) => {
        this.masters = data;
      },
      (error) => {
        console.error('Error fetching masters:', error);
      });
    this.serviceService.findAll().subscribe(
      (data: Service[]) => {
        this.services = data;
      },
      (error) => {
        console.error('Error fetching masters:', error);
      });
    this.clientService.findAll().subscribe(
      (data: Client[]) => {
        this.clients = data;
      },
      (error) => {
        console.error('Error fetching masters:', error);
      });
  }

  ngOnInit() {

    this.route.params.subscribe(params => {
      const serviceId = params['id'];
      if (serviceId) {
        this.apptService.findById(serviceId).subscribe(data => {
          this.appt = data;
          console.log(data);
          this.editMode = true;
          this.client = this.appt.client;
          this.service = this.appt.service;
          this.master = this.appt.master;

          // // Установка значения master
          // this.appt.master = data.master;
          //
          // // Установка значения service
          // this.appt.service = data.service;
        });
      }
    });
  }

  onSubmit() {
    this.appt.client = this.client;
    this.appt.master = this.master;
    this.appt.service = this.service;
    if (!this.editMode) {
      this.saveAppointment(this.appt);
    } else {
      console.log('Submitted appointment:', this.appt);
      this.updateAppointment(this.appt);
    }
  }

  private saveAppointment(appt: Visit) {
    this.apptService.save(appt).subscribe(result => this.gotoAppointmentList());
  }

  private updateAppointment(appt: Visit) {
    this.apptService.update(appt).subscribe(result => this.gotoAppointmentList());
  }

  gotoAppointmentList() {
    this.router.navigate(['/visits']);
  }

  deleteAppt(apptId: number | undefined) {
    this.apptService.delete(apptId).subscribe(() => {
      this.gotoAppointmentList();
    });
  }
}
