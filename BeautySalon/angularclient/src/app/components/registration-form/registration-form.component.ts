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
      // Действия при успешной регистрации
      console.log('Form Submitted!', this.user);
    } else {
      // Дополнительная обработка ошибки, если требуется
      console.error('Form is invalid or passwords do not match');
    }
  }

  gotoUserList() {
    this.router.navigate(['/users']);
  }

  passwordsMatch(): boolean {
    return this.user.password === this.confirmPassword;
  }

  emailFormatValid() {
    return this.user.email?.includes("@") && this.user.email?.includes(".");
  }
}
