import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {StaffService} from "../../services/staff.service";
import {Employee} from "../../model/entities/employee";

@Component({
  selector: 'app-staff-form',
  templateUrl: './staff-form.component.html',
  styleUrl: './staff-form.component.scss'
})
export class StaffFormComponent implements OnInit{
  employee: Employee = new Employee();
  isEdit: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private staffService: StaffService,
  ) {}

  ngOnInit(): void {

    this.route.params.subscribe(params => {
      const employeeId = params['id'];
      if (employeeId) {
        this.staffService.findById(employeeId).subscribe(data => {
          this.employee = data;
          this.isEdit = true;
        });
      }
    });
  }

  onSubmit() {
    this.staffService.save(this.employee).subscribe(result => this.gotoStaffList());
  }

  gotoStaffList() {
    this.router.navigate(['/staff']);
  }
}

