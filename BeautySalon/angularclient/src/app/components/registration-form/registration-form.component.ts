import {Component} from '@angular/core';
import {User} from "../../model/entities/user";
import {ActivatedRoute, Router} from "@angular/router";
import {RegistrationService} from "../../services/registration.service";
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-registration-form',
  templateUrl: './registration-form.component.html',
  styleUrl: './registration-form.component.scss'
})
export class RegistrationFormComponent {
  user: User = new User();
  confirmPassword: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private registrationService: RegistrationService,
  ) {
  }

  onSubmit(form: NgForm) {
    if (form.valid && this.passwordsMatch()) {
      this.registration(this.user);

      console.log('Form Submitted!', this.user);
    } else {
      console.error('Form is invalid or passwords do not match');
      this.router.navigate(['redirect/registration']);
    }
  }

  gotoUserList() {
    this.router.navigate(['/users']);
  }

  passwordsMatch(): boolean {
    return this.user.password === this.confirmPassword;
  }

  registration(user: User){
    this.registrationService.save(user).subscribe(result => this.regSuccess());
  }

  emailFormatValid() {
    return this.user.email?.includes("@") && this.user.email?.includes(".");
  }

  private regSuccess() {
    this.router.navigate(['/login']);
  }
}
