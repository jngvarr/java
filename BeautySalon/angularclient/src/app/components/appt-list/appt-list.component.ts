import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {Visit} from "../../model/entities/visit";
import {ApptService} from "../../services/appt.service";
import {Time} from "@angular/common";
import {Client} from "../../model/entities/client";
import {Employee} from "../../model/entities/employee";

@Component({
  selector: 'app-appt-list',
  templateUrl: './appt-list.component.html',
  styleUrl: './appt-list.component.scss'
})
export class ApptListComponent implements OnInit {
  appts: Visit[] | undefined;
  appt: Visit | undefined;
  isSearching: boolean = false;
  searchDate: Date | undefined;
  searchTime: Time | undefined;
  searchClient: Client | undefined;
  searchMaster: Employee | undefined;

  constructor(private apptService: ApptService, private router: Router) {
  }

  ngOnInit() {
    this.loadAppts();
  }

  loadAppts() {
    this.apptService.findAll().subscribe(data => {
      this.appts = data;
    });
  }

  searchByVisitDate(date: Date) {
    this.apptService.findByDate(date).subscribe((data: Visit[]) => {
      this.appts = data;
      this.isSearching = true;
    });
  }

  // searchByTime(time: Time) {
  //   this.apptService.findByStartTime(time).subscribe((data: Visit[]) => {
  //     this.appts = data;
  //     this.isSearching = true;
  //   });
  // }

  deleteAppt(appt: Visit) {
    if (confirm('Вы уверены, что хотите удалить запись?')) {
      this.apptService.delete(appt.id).subscribe(() => {
        this.loadAppts();
      });
    }
  }

  resetSearch() {
    this.isSearching = false;
    this.loadAppts();
  }

  updateAppt(appt: Visit) {
    this.router.navigate(['/visits/update', appt.id]);
  }

  searchByService(value: string) {
    this.isSearching = true;
    this.apptService.findByService(value).subscribe((data: Visit[]) => {
      this.appts = data;
    });
  }

  searchByClient(value: string) {
    this.isSearching = true;
    this.apptService.findByClient(value).subscribe((data: Visit[]) => {
      this.appts = data;
    });
  }

  searchByMaster(value: string) {
    this.isSearching = true;
    this.apptService.findByMaster(value).subscribe((data: Visit[]) => {
      this.appts = data;
    });
  }
}
