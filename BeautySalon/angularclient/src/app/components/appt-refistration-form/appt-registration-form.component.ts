import {Component} from '@angular/core';
import {User} from "../../model/entities/user";

@Component({
  selector: 'app-appt-refistration-form',
  templateUrl: './appt-registration-form.component.html',
  styleUrl: './appt-registration-form.component.scss'
})
export class ApptRegistrationFormComponent {
  protected user: User | undefined;
  editMode: boolean;

  onSubmit() {

  }
}
