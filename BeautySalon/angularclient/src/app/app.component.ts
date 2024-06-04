import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {LoginFormComponent} from "./components/login-component/login-form.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  @ViewChild(LoginFormComponent) loginForm!: LoginFormComponent; // Использование ViewChild для получения ссылки на компонент
  log: boolean = false;
  title: string | undefined;
  image = '../assets/images/background.jpg';
  logged: boolean = false;
  // username : String = sessionStorage.getItem('token');


  logout() {
    if (this.loginForm) {
      this.logged = false;
      this.loginForm.logout();
    } else {
      console.error('LoginFormComponent не найден');
    }
  }

  loginSuccessfull() {
    if (sessionStorage.length !== 0) this.logged = true;
  }
}
