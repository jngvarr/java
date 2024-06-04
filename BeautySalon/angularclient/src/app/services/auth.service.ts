import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {User} from "../model/entities/user";
import {ApiService} from "./api-service";
import {catchError, map} from "rxjs/operators";
import {of} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {AppComponent} from "../app.component";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private usersUrl: string;
  loading: boolean = false;
  apiUrl: string = this.apiService.apiUrl + '/users/login';

  constructor(private route: ActivatedRoute,
              private router: Router,
              private http: HttpClient,
              private apiService: ApiService,
              private appComp: AppComponent) {
    this.usersUrl = apiService.apiUrl + '/users';
  }

  save(user: User) {
    return this.http.post<User>(this.usersUrl + "/registration", user);
  }

  login(user: User) {
    this.http.post(this.usersUrl + '/login', {
      username: user.username,
      password: user.password
    }).pipe(
      map((res: any) => {
        if (res && res.accessToken) {
          return res.accessToken;
        } else {
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
          this.appComp.username = user.username;
        } else {
          console.error("sessionStorage is not available");
        }
      } else {
        alert("Authentication failed. Please check your username and password.");
      }
    });
  }

  logout() {
    this.appComp.logged = false;
    this.http.post(`${this.usersUrl}/logout`, { token: sessionStorage.getItem('token') }).subscribe({
      next: () => {
        sessionStorage.removeItem('token');
        this.router.navigate(['/login']);
      },
      error: err => console.error('Logout failed', err)
    });
  }
}
