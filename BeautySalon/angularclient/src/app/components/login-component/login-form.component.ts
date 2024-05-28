import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {catchError, map} from 'rxjs/operators';
import {of} from 'rxjs';
import {User} from "../../model/entities/user";


@Component({
  selector: 'login',
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.scss'
})
export class LoginFormComponent implements OnInit {
  @Output() loginSuccess: EventEmitter<any> = new EventEmitter<any>(); // Добавляем EventEmitter уведомлении родителя о событии
  user: User = new User();
  loading: boolean = false;
  apiUrl: string = 'http://localhost:8765/login';


  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {
  }

  ngOnInit() {
    sessionStorage.setItem('token', '');
  }

  login() {
    // this.loading = true;
    this.http.post(this.apiUrl, {
      username: this.user.username,
      password: this.user.password
    }).pipe(
      map((res: any) => res.token),
      catchError(error => {
        console.error('Login failed', error);
        return of(null);
      })
    ).subscribe(token => {
      // this.loading = false;
      if (token) {
        sessionStorage.setItem('token', token);
        this.router.navigate(['']);
        this.loginSuccess.emit(true); // Оповещаем родительский компонент об успешном входе
      } else {
        alert("Authentication failed. Please check your username and password.");
      }
    });
  }

  logout() {
    sessionStorage.removeItem('token'); // Удаление токена из sessionStorage
    this.router.navigate(['/login']); // Перенаправление на страницу входа или другую страницу
    this.loginSuccess.emit(false); // Оповещаем родительский компонент о выходе из системы
  }

  loadedData() {
    return
  }
}
