import {Component, Injectable} from '@angular/core';
import {AuthService} from "./services/auth.service";
@Injectable({
  providedIn: 'root'
})
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  // @ViewChild(LoginFormComponent) loginForm!: LoginFormComponent; // Использование ViewChild для получения ссылки на компонент
  title: string | undefined;
  image = '../assets/images/background.jpg';
  logged: boolean = false;
  username: string | undefined;

  constructor(protected authService: AuthService) {
  }

  // ngOnInit(): void {
  //   if (typeof window !== 'undefined') {  // Проверка, что код выполняется в браузере
  //     const token = sessionStorage.getItem('token');
  //     if (token) {
  //       this.isLoggedIn();
  //     }
  //   }
  // }
}
