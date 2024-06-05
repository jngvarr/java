import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {catchError, map} from 'rxjs/operators';
import {of} from 'rxjs';
import {User} from "../../model/entities/user";
import {ApiService} from "../../services/api-service";
import {AppComponent} from "../../app.component";


@Component({
  selector: 'login',
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.scss'
})
export class LoginFormComponent implements OnInit {
  // @Output() loginSuccess: EventEmitter<any> = new EventEmitter<any>(); // Добавляем EventEmitter уведомлении родителя о событии
  user: User = new User();
  loading: boolean = false;
  apiUrl: string = this.apiService.apiUrl + '/users/login';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private apiService: ApiService,
    private appComp: AppComponent
  ) {
  }

  ngOnInit() {
    sessionStorage.setItem('token', '');
  }

  login() {
    this.http.post(this.apiUrl, {
      username: this.user.username,
      password: this.user.password
    }).pipe(
      map((res: any) => {
        if (res && res.accessToken) {
          return res.accessToken;
        } else {
          console.error("No access token found in response");
          return null;
        }
      }),
      catchError(error => {
        console.error('Login failed', error);
        return of(null);
      })
    ).subscribe(token => {
      if (token) {
        if (typeof sessionStorage !== 'undefined') {
          sessionStorage.setItem('token', token);
          this.router.navigate(['']);
          this.appComp.logged = true;
          this.appComp.username = this.user.username;
          // this.loginSuccess.emit(true); // Оповещаем родительский компонент об успешном входе
        } else {
          console.error("sessionStorage is not available");
        }
      } else {
        alert("Authentication failed. Please check your username and password.");
      }
    });
  }


  // login() {
  //   // this.loading = true;
  //   this.http.post(this.apiUrl, {
  //     username: this.user.username,
  //     password: this.user.password
  //   }).pipe(
  //     map((res: any) => res.token),
  //     catchError(error => {
  //       console.error('Login failed', error);
  //       return of(null);
  //     })
  //   ).subscribe(token => {
  //     // this.loading = false;
  //     if (token) {
  //       sessionStorage.setItem('token', token);
  //       this.router.navigate(['']);
  //       this.loginSuccess.emit(true); // Оповещаем родительский компонент об успешном входе
  //     } else {
  //       alert("Authentication failed. Please check your username and password.");
  //     }
  //   });
  // }

  logout() {
    sessionStorage.removeItem('token'); // Удаление токена из sessionStorage
    this.appComp.logged = false;
    this.router.navigate(['/login']); // Перенаправление на страницу входа или другую страницу
  }

  loadedData() {
    return
  }
}

