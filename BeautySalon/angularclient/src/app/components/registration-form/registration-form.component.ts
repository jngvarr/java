import {Component} from '@angular/core';
import {User} from "../../model/entities/user";
import {ActivatedRoute, Router} from "@angular/router";
import {RegistrationService} from "../../services/registration.service";

@Component({
  selector: 'app-registration-form',
  templateUrl: './registration-form.component.html',
  styleUrl: './registration-form.component.scss'
})
export class RegistrationFormComponent {
  user: User = new User();
  log: boolean | undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private registrationService: RegistrationService,
  ) {
  }

  onSubmit() {
    this.registrationService.save(this.user).subscribe(result => this.gotoUserList());
  }

  gotoUserList() {
    this.router.navigate(['/users']);
  }
}
